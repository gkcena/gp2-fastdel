import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environments/environment';

export interface VehicleRequest {
  id: string;
  courierName: string;
  courierEmail: string;
  requestedVehicleType: string;
  requestedLicensePlate: string;
  status: string;
  createdAt: string;
}

@Injectable({ providedIn: 'root' })
export class AdminVehicleService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = `${environment.apiUrl}/admin/vehicle-requests`;

  getVehicleRequests() {
    return this.http.get<VehicleRequest[]>(this.apiUrl);
  }

  approveRequest(id: string) {
    return this.http.post(`${this.apiUrl}/${id}/approve`, {});
  }

  rejectRequest(id: string) {
    return this.http.post(`${this.apiUrl}/${id}/reject`, {});
  }
}
