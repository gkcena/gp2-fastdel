import { Component, OnInit, inject } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatTabsModule } from '@angular/material/tabs';
import { MatTableModule } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatDialog, MatDialogModule, MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { DatePipe } from '@angular/common';
import { AdminUserService, StaffMember, Courier, CreateUserRequest } from '../../../core/services/admin-user.service';
import { UsersData } from './users.resolver';

@Component({
  selector: 'app-add-user-dialog',
  imports: [
    ReactiveFormsModule, MatDialogModule, MatFormFieldModule,
    MatInputModule, MatButtonModule
  ],
  templateUrl: './add-user-dialog.component.html',
  styleUrl: './add-user-dialog.component.scss'
})
export class AddUserDialogComponent {
  readonly dialogRef = inject(MatDialogRef<AddUserDialogComponent>);
  readonly data = inject<{ title: string }>(MAT_DIALOG_DATA);
  private readonly fb = inject(FormBuilder);

  form = this.fb.nonNullable.group({
    name: ['', Validators.required],
    email: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required, Validators.minLength(6)]]
  });

  onSubmit(): void {
    if (this.form.valid) {
      this.dialogRef.close(this.form.getRawValue());
    }
  }
}

@Component({
  selector: 'app-admin-users',
  imports: [
    MatTabsModule, MatTableModule, MatButtonModule, MatIconModule,
    MatProgressSpinnerModule, DatePipe
  ],
  templateUrl: './admin-users.component.html',
  styleUrl: './admin-users.component.scss'
})
export class AdminUsersComponent implements OnInit {
  private readonly route = inject(ActivatedRoute);
  private readonly userService = inject(AdminUserService);
  private readonly dialog = inject(MatDialog);
  private readonly snackBar = inject(MatSnackBar);

  adminList: StaffMember[] = [];
  staffList: StaffMember[] = [];
  courierList: Courier[] = [];
  loading = true;

  adminColumns = ['name', 'email', 'createdAt', 'actions'];
  staffColumns = ['name', 'email', 'createdAt', 'actions'];
  courierColumns = ['name', 'email', 'vehicleType', 'licensePlate', 'createdAt', 'actions'];

  ngOnInit(): void {
    this.route.data.subscribe(data => {
      const resolved: UsersData = data['users'];
      this.adminList = resolved.admins;
      this.staffList = resolved.staff;
      this.courierList = resolved.couriers;
      this.loading = false;
    });
  }

  openAddDialog(type: 'admin' | 'staff' | 'courier'): void {
    const titles: Record<string, string> = {
      admin: 'Add Admin',
      staff: 'Add Staff Member',
      courier: 'Add Courier'
    };
    const ref = this.dialog.open(AddUserDialogComponent, { data: { title: titles[type] } });

    ref.afterClosed().subscribe((result: CreateUserRequest | undefined) => {
      if (!result) return;

      if (type === 'admin') {
        this.userService.createAdmin(result).subscribe({
          next: (created) => {
            this.adminList = [...this.adminList, created];
            this.snackBar.open('Admin created', 'OK', { duration: 3000 });
          },
          error: (err: any) => {
            this.snackBar.open(err.error?.message || 'Creation failed', 'OK', { duration: 4000 });
          }
        });
      } else if (type === 'staff') {
        this.userService.createStaff(result).subscribe({
          next: (created) => {
            this.staffList = [...this.staffList, created];
            this.snackBar.open('Staff created', 'OK', { duration: 3000 });
          },
          error: (err: any) => {
            this.snackBar.open(err.error?.message || 'Creation failed', 'OK', { duration: 4000 });
          }
        });
      } else {
        this.userService.createCourier(result).subscribe({
          next: (created) => {
            this.courierList = [...this.courierList, created];
            this.snackBar.open('Courier created', 'OK', { duration: 3000 });
          },
          error: (err: any) => {
            this.snackBar.open(err.error?.message || 'Creation failed', 'OK', { duration: 4000 });
          }
        });
      }
    });
  }

  deleteUser(id: string, type: 'admin' | 'staff' | 'courier'): void {
    if (!confirm('Are you sure you want to delete this user?')) return;

    this.userService.deleteUser(id).subscribe({
      next: () => {
        if (type === 'admin') {
          this.adminList = this.adminList.filter(u => u.id !== id);
        } else if (type === 'staff') {
          this.staffList = this.staffList.filter(u => u.id !== id);
        } else {
          this.courierList = this.courierList.filter(u => u.id !== id);
        }
        this.snackBar.open('User deleted', 'OK', { duration: 3000 });
      },
      error: (err: any) => {
        this.snackBar.open(err.error?.message || 'Deletion failed', 'OK', { duration: 4000 });
      }
    });
  }
}
