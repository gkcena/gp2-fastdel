import { Component, OnInit, inject } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatTableModule } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatDialog, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { DatePipe } from '@angular/common';
import { MatTooltipModule } from '@angular/material/tooltip';
import { StaffOrderService, CreateOrderRequest } from '../../../core/services/staff-order.service';
import { Order } from '../../../core/services/admin-order.service';
import { QrDialogComponent, QrDialogData } from './qr-dialog.component';

@Component({
  selector: 'app-create-order-dialog',
  imports: [
    ReactiveFormsModule, MatDialogModule, MatFormFieldModule,
    MatInputModule, MatButtonModule
  ],
  templateUrl: './create-order-dialog.component.html',
  styleUrl: './create-order-dialog.component.scss'
})
export class CreateOrderDialogComponent {
  readonly dialogRef = inject(MatDialogRef<CreateOrderDialogComponent>);
  private readonly fb = inject(FormBuilder);

  form = this.fb.nonNullable.group({
    customerName: ['', Validators.required],
    customerPhone: ['', Validators.required],
    deliveryAddress: ['', Validators.required]
  });

  onSubmit(): void {
    if (this.form.valid) {
      this.dialogRef.close(this.form.getRawValue());
    }
  }
}

@Component({
  selector: 'app-staff-orders',
  imports: [
    ReactiveFormsModule, MatTableModule, MatButtonModule, MatIconModule,
    MatFormFieldModule, MatInputModule, MatProgressSpinnerModule, MatTooltipModule, DatePipe
  ],
  templateUrl: './staff-orders.component.html',
  styleUrl: './staff-orders.component.scss'
})
export class StaffOrdersComponent implements OnInit {
  private readonly route = inject(ActivatedRoute);
  private readonly orderService = inject(StaffOrderService);
  private readonly dialog = inject(MatDialog);
  private readonly snackBar = inject(MatSnackBar);

  orders: Order[] = [];
  loading = true;

  columns = [
    'barcode', 'customerName', 'customerPhone', 'deliveryAddress',
    'status', 'assignedCourierName', 'createdByName', 'createdAt', 'qr'
  ];

  ngOnInit(): void {
    this.route.data.subscribe(data => {
      this.orders = data['staffOrders'];
      this.loading = false;
    });
  }

  loadData(): void {
    this.orderService.getOrders().subscribe({
      next: (data) => {
        this.orders = data;
      },
      error: (err: any) => {
        this.snackBar.open(err.error?.message || 'Failed to load orders', 'OK', { duration: 4000 });
      }
    });
  }

  searchByBarcode(barcode: string): void {
    const trimmed = barcode.trim();
    if (!trimmed) {
      this.loadData();
      return;
    }
    
    this.orderService.getByBarcode(trimmed).subscribe({
      next: (order) => { this.orders = [order]; },
      error: () => {
        this.snackBar.open('Order not found', 'OK', { duration: 3000 });
      }
    });
  }

  getStatusClass(status: string): string {
    return 'status-badge status-' + status.toLowerCase().replace('_', '-');
  }

  formatStatus(status: string): string {
    return status.replace('_', ' ');
  }

  openQrDialog(order: Order): void {
    this.dialog.open(QrDialogComponent, {
      data: {
        barcode: order.barcode,
        customerName: order.customerName,
        deliveryAddress: order.deliveryAddress
      } as QrDialogData,
      width: '400px'
    });
  }

  openCreateDialog(): void {
    const ref = this.dialog.open(CreateOrderDialogComponent);

    ref.afterClosed().subscribe((result: CreateOrderRequest | undefined) => {
      if (!result) return;
      this.orderService.createOrder(result).subscribe({
        next: (created) => {
          this.orders = [created, ...this.orders];
          this.snackBar.open('Order created successfully', 'OK', { duration: 3000 });
        },
        error: (err: any) => {
          this.snackBar.open(err.error?.message || 'Creation failed', 'OK', { duration: 4000 });
        }
      });
    });
  }
}
