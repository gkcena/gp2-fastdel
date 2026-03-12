import { Component, inject, OnInit } from '@angular/core';
import { RouterOutlet, RouterLink, RouterLinkActive } from '@angular/router';
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatListModule } from '@angular/material/list';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { AuthService } from '../../core/services/auth.service';
import { ThemeService } from '../../core/services/theme.service';

@Component({
  selector: 'app-staff-layout',
  imports: [
    RouterOutlet, RouterLink, RouterLinkActive,
    MatSidenavModule, MatListModule, MatIconModule, MatButtonModule
  ],
  templateUrl: './staff-layout.component.html',
  styleUrl: './staff-layout.component.scss'
})
export class StaffLayoutComponent implements OnInit {
  readonly auth = inject(AuthService);
  readonly theme = inject(ThemeService);

  photoUrl: string | null = null;

  get userInitial(): string {
    const name = this.auth.getUserName();
    return name ? name.charAt(0).toUpperCase() : '?';
  }

  ngOnInit(): void {
    this.photoUrl = this.auth.getProfilePhotoUrl();
  }
}
