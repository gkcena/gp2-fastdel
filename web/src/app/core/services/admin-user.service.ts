import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environments/environment';

export interface StaffMember {
  id: string;
  name: string;
  email: string;
  role: string;
  createdAt: string;
}

export interface Courier {
  id: string;
  name: string;
  email: string;
  vehicleType: string;
  licensePlate: string;
  createdAt: string;
}

export interface CreateUserRequest {
  name: string;
  email: string;
  password: string;
}

export interface ProfileResponse {
  name: string;
  email: string;
  role: string;
  profilePhotoUrl: string | null;
}

export interface PhotoUploadResponse {
  photoUrl: string;
}

@Injectable({ providedIn: 'root' })
export class AdminUserService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = `${environment.apiUrl}/admin`;

  getAdmins() {
    return this.http.get<StaffMember[]>(`${this.apiUrl}/admins`);
  }

  getStaff() {
    return this.http.get<StaffMember[]>(`${this.apiUrl}/staff`);
  }

  getCouriers() {
    return this.http.get<Courier[]>(`${this.apiUrl}/couriers`);
  }

  createAdmin(data: CreateUserRequest) {
    return this.http.post<StaffMember>(`${this.apiUrl}/admins`, data);
  }

  createStaff(data: CreateUserRequest) {
    return this.http.post<StaffMember>(`${this.apiUrl}/staff`, data);
  }

  createCourier(data: CreateUserRequest) {
    return this.http.post<Courier>(`${this.apiUrl}/couriers`, data);
  }

  deleteUser(id: string) {
    return this.http.delete(`${this.apiUrl}/users/${id}`);
  }

  changePassword(currentPassword: string, newPassword: string) {
    return this.http.post(`${this.apiUrl}/change-password`, { currentPassword, newPassword });
  }

  getProfile() {
    return this.http.get<ProfileResponse>(`${this.apiUrl}/profile`);
  }

  uploadPhoto(file: File) {
    const formData = new FormData();
    formData.append('photo', file);
    return this.http.post<PhotoUploadResponse>(`${this.apiUrl}/profile/photo`, formData);
  }
}
