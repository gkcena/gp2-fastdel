import { Component, inject, OnDestroy, OnInit, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators, AbstractControl, ValidationErrors } from '@angular/forms';
import { NavigationEnd, Router } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatDividerModule } from '@angular/material/divider';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { Subscription } from 'rxjs';
import { AuthService } from '../../../core/services/auth.service';
import { StaffOrderService } from '../../../core/services/staff-order.service';
import { environment } from '../../../../environments/environment';

@Component({
  selector: 'app-staff-profile',
  imports: [
    ReactiveFormsModule, MatCardModule, MatFormFieldModule,
    MatInputModule, MatButtonModule, MatIconModule, MatDividerModule,
    MatProgressSpinnerModule
  ],
  templateUrl: './staff-profile.component.html',
  styleUrl: './staff-profile.component.scss'
})
export class StaffProfileComponent implements OnInit, OnDestroy {
  private readonly fb = inject(FormBuilder);
  private readonly snackBar = inject(MatSnackBar);
  private readonly auth = inject(AuthService);
  private readonly staffService = inject(StaffOrderService);
  private readonly router = inject(Router);
  private profileSub?: Subscription;

  userName = '';
  userEmail = '';
  saving = false;
  loading = signal(true);
  uploadingPhoto = false;

  photoUrl: string | null = null;
  pendingPhoto: string | null = null;
  private pendingFile: File | null = null;

  get userInitial(): string {
    return this.userName ? this.userName.charAt(0).toUpperCase() : '?';
  }

  passwordForm = this.fb.nonNullable.group({
    currentPassword: ['', Validators.required],
    newPassword: ['', [Validators.required, Validators.minLength(8)]],
    confirmNewPassword: ['', Validators.required]
  }, { validators: this.passwordMatchValidator });

  private passwordMatchValidator(group: AbstractControl): ValidationErrors | null {
    const newPw = group.get('newPassword')?.value;
    const confirm = group.get('confirmNewPassword')?.value;
    return newPw && confirm && newPw !== confirm ? { passwordMismatch: true } : null;
  }

  ngOnInit(): void {
    this.loadProfile();
  }

  ngOnDestroy(): void {
    this.profileSub?.unsubscribe();
  }

  private loadProfile(): void {
    this.loading.set(true);
    this.profileSub?.unsubscribe();
    this.profileSub = this.staffService.getProfile().subscribe({
      next: (profile) => {
        this.userName = profile.name;
        this.userEmail = profile.email;
        this.photoUrl = profile.profilePhotoUrl
          ? environment.backendUrl + profile.profilePhotoUrl
          : null;
        this.loading.set(false);
      },
      error: () => {
        this.loading.set(false);
      }
    });
  }

  onFileSelected(event: Event): void {
    const file = (event.target as HTMLInputElement).files?.[0];
    if (!file) return;
    this.pendingFile = file;
    const reader = new FileReader();
    reader.onload = () => {
      this.pendingPhoto = reader.result as string;
    };
    reader.readAsDataURL(file);
  }

  savePhoto(): void {
    if (!this.pendingFile) return;
    this.uploadingPhoto = true;
    this.staffService.uploadPhoto(this.pendingFile).subscribe({
      next: (res) => {
        this.photoUrl = environment.backendUrl + res.photoUrl;
        this.pendingPhoto = null;
        this.pendingFile = null;
        this.uploadingPhoto = false;
        localStorage.setItem('profilePhotoUrl', res.photoUrl);
        this.snackBar.open('Profile photo updated', 'OK', { duration: 3000 });
      },
      error: (err: any) => {
        this.uploadingPhoto = false;
        this.snackBar.open(err.error?.message || 'Photo upload failed', 'OK', { duration: 4000 });
      }
    });
  }

  get displayPhoto(): string | null {
    return this.pendingPhoto ?? this.photoUrl;
  }

  onChangePassword(): void {
    if (this.passwordForm.invalid) return;
    this.saving = true;
    const { currentPassword, newPassword } = this.passwordForm.getRawValue();
    this.staffService.changePassword(currentPassword, newPassword).subscribe({
      next: () => {
        this.snackBar.open('Password changed successfully', 'OK', { duration: 3000 });
        this.passwordForm.reset();
        this.saving = false;
      },
      error: (err: any) => {
        this.snackBar.open(err.error?.message || 'Password change failed', 'OK', { duration: 4000 });
        this.saving = false;
      }
    });
  }
}
