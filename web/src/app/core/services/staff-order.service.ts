import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { Order } from './admin-order.service';
import { ProfileResponse, PhotoUploadResponse } from './admin-user.service';

export interface CreateOrderRequest {
  customerName: string;
  customerPhone: string;
  deliveryAddress: string;
}

@Injectable({ providedIn: 'root' })
export class StaffOrderService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = `${environment.apiUrl}/staff`;

  getOrders() {
    return this.http.get<Order[]>(`${this.apiUrl}/orders`);
  }

  createOrder(data: CreateOrderRequest) {
    return this.http.post<Order>(`${this.apiUrl}/orders`, data);
  }

  getByBarcode(barcode: string) {
    return this.http.get<Order>(`${this.apiUrl}/orders/barcode/${barcode}`);
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
