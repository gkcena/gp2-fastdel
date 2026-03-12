import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environments/environment';

export interface Order {
  id: string;
  barcode: string;
  customerName: string;
  customerPhone: string;
  deliveryAddress: string;
  status: string;
  assignedCourierName: string;
  createdByName: string;
  createdAt: string;
}

@Injectable({ providedIn: 'root' })
export class AdminOrderService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = `${environment.apiUrl}/admin`;

  getOrders() {
    return this.http.get<Order[]>(`${this.apiUrl}/orders`);
  }

  assignCourier(orderId: string, courierId: string) {
    return this.http.post(`${this.apiUrl}/orders/${orderId}/assign`, { courierId });
  }
}
