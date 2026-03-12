import { Component, OnInit, inject } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { MatTableModule } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { DatePipe } from '@angular/common';
import { AdminVehicleService, VehicleRequest } from '../../../core/services/admin-vehicle.service';

@Component({
  selector: 'app-admin-vehicle-requests',
  imports: [
    MatTableModule, MatButtonModule, MatIconModule,
    MatProgressSpinnerModule, DatePipe
  ],
  templateUrl: './admin-vehicle-requests.component.html',
  styleUrl: './admin-vehicle-requests.component.scss'
})
export class AdminVehicleRequestsComponent implements OnInit {
  private readonly route = inject(ActivatedRoute);
  private readonly vehicleService = inject(AdminVehicleService);
  private readonly snackBar = inject(MatSnackBar);

  requests: VehicleRequest[] = [];
  loading = true;

  columns = [
    'courierName', 'courierEmail', 'requestedVehicleType',
    'requestedLicensePlate', 'status', 'createdAt', 'actions'
  ];

  ngOnInit(): void {
    this.route.data.subscribe(data => {
      this.requests = data['vehicleRequests'];
      this.loading = false;
    });
  }

  getStatusClass(status: string): string {
    return 'status-badge status-' + status.toLowerCase();
  }

  approve(id: string): void {
    this.vehicleService.approveRequest(id).subscribe({
      next: () => {
        this.requests = this.requests.map(r =>
          r.id === id ? { ...r, status: 'APPROVED' } : r
        );
        this.snackBar.open('Request approved', 'OK', { duration: 3000 });
      },
      error: (err: any) => {
        this.snackBar.open(err.error?.message || 'Approval failed', 'OK', { duration: 4000 });
      }
    });
  }

  reject(id: string): void {
    this.vehicleService.rejectRequest(id).subscribe({
      next: () => {
        this.requests = this.requests.map(r =>
          r.id === id ? { ...r, status: 'REJECTED' } : r
        );
        this.snackBar.open('Request rejected', 'OK', { duration: 3000 });
      },
      error: (err: any) => {
        this.snackBar.open(err.error?.message || 'Rejection failed', 'OK', { duration: 4000 });
      }
    });
  }
}
