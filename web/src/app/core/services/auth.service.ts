import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { tap, switchMap, catchError, of } from 'rxjs';
import { environment } from '../../../environments/environment';
import { ProfileResponse } from './admin-user.service';

export interface LoginResponse {
  token: string;
  expiresIn: number;
  role: string;
  name: string;
}

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly http = inject(HttpClient);
  private readonly router = inject(Router);
  private readonly apiUrl = environment.apiUrl;

  login(email: string, password: string) {
    return this.http
      .post<LoginResponse>(`${this.apiUrl}/auth/login`, { email, password })
      .pipe(
        tap(res => {
          localStorage.setItem('token', res.token);
          localStorage.setItem('role', res.role);
          localStorage.setItem('userName', res.name);
          localStorage.setItem('userEmail', email);
        }),
        switchMap(res => {
          const profileUrl = res.role === 'ADMIN'
            ? `${this.apiUrl}/admin/profile`
            : `${this.apiUrl}/staff/profile`;
          return this.http.get<ProfileResponse>(profileUrl).pipe(
            tap(profile => {
              localStorage.setItem('userName', profile.name);
              if (profile.profilePhotoUrl) {
                localStorage.setItem('profilePhotoUrl', profile.profilePhotoUrl);
              } else {
                localStorage.removeItem('profilePhotoUrl');
              }
            }),
            catchError(() => of(null))
          );
        })
      );
  }

  logout(): void {
    localStorage.removeItem('token');
    localStorage.removeItem('role');
    localStorage.removeItem('userName');
    localStorage.removeItem('userEmail');
    localStorage.removeItem('profilePhotoUrl');
    this.router.navigate(['/login']);
  }

  getToken(): string | null {
    return localStorage.getItem('token');
  }

  getRole(): string | null {
    return localStorage.getItem('role');
  }

  getUserName(): string | null {
    return localStorage.getItem('userName');
  }

  getUserEmail(): string | null {
    return localStorage.getItem('userEmail');
  }

  getProfilePhotoUrl(): string | null {
    const path = localStorage.getItem('profilePhotoUrl');
    if (!path) return null;
    return environment.backendUrl + path;
  }

  isLoggedIn(): boolean {
    return !!this.getToken();
  }
}
