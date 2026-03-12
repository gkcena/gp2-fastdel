import { Component, OnInit, inject } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { FormControl, ReactiveFormsModule } from '@angular/forms';
import { MatTableModule } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatSelectModule } from '@angular/material/select';
import { MatDialog, MatDialogModule, MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { DatePipe } from '@angular/common';
import { AdminOrderService, Order } from '../../../core/services/admin-order.service';
import { Courier } from '../../../core/services/admin-user.service';
import { OrdersData } from './orders.resolver';

@Component({
  selector: 'app-assign-courier-dialog',
  imports: [
    ReactiveFormsModule, MatDialogModule, MatFormFieldModule,
    MatSelectModule, MatButtonModule
  ],
  templateUrl: './assign-courier-dialog.component.html',
  styleUrl: './assign-courier-dialog.component.scss'
})
export class AssignCourierDialogComponent {
  readonly dialogRef = inject(MatDialogRef<AssignCourierDialogComponent>);
  readonly data = inject<{ couriers: Courier[] }>(MAT_DIALOG_DATA);
  courierControl = new FormControl('');

  onAssign(): void {
    this.dialogRef.close(this.courierControl.value);
  }
}

@Component({
  selector: 'app-admin-orders',
  imports: [
    ReactiveFormsModule, MatTableModule, MatButtonModule, MatIconModule,
    MatFormFieldModule, MatSelectModule, MatProgressSpinnerModule, DatePipe
  ],
  templateUrl: './admin-orders.component.html',
  styleUrl: './admin-orders.component.scss'
})
export class AdminOrdersComponent implements OnInit {
  private readonly route = inject(ActivatedRoute);
  private readonly orderService = inject(AdminOrderService);
  private readonly dialog = inject(MatDialog);
  private readonly snackBar = inject(MatSnackBar);

  orders: Order[] = [];
  filteredOrders: Order[] = [];
  couriers: Courier[] = [];
  selectedStatus = 'ALL';
  loading = true;

  columns = [
    'barcode', 'customerName', 'customerPhone', 'deliveryAddress',
    'status', 'assignedCourierName', 'createdByName', 'createdAt', 'actions'
  ];

  ngOnInit(): void {
    this.route.data.subscribe(data => {
      const resolved: OrdersData = data['orders'];
      this.orders = resolved.orders;
      this.couriers = resolved.couriers;
      this.applyFilter();
      this.loading = false;
    });
  }

  applyFilter(): void {
    this.filteredOrders = this.selectedStatus === 'ALL'
      ? this.orders
      : this.orders.filter(o => o.status === this.selectedStatus);
  }

  getStatusClass(status: string): string {
    return 'status-badge status-' + status.toLowerCase().replace('_', '-');
  }

  formatStatus(status: string): string {
    return status.replace('_', ' ');
  }

  openAssign(order: Order): void {
    const ref = this.dialog.open(AssignCourierDialogComponent, {
      data: { couriers: this.couriers }
    });

    ref.afterClosed().subscribe((courierId: string | undefined) => {
      if (!courierId) return;
      this.orderService.assignCourier(order.id, courierId).subscribe({
        next: () => {
          const courier = this.couriers.find(c => c.id === courierId);
          const idx = this.orders.findIndex(o => o.id === order.id);
          if (idx !== -1) {
            this.orders[idx] = {
              ...this.orders[idx],
              status: 'ASSIGNED',
              assignedCourierName: courier?.name ?? ''
            };
            this.orders = [...this.orders];
            this.applyFilter();
          }
          this.snackBar.open('Courier assigned successfully', 'OK', { duration: 3000 });
        },
        error: (err: any) => {
          this.snackBar.open(err.error?.message || 'Assignment failed', 'OK', { duration: 4000 });
        }
      });
    });
  }
}
