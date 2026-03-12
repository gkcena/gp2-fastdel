import { Component, OnInit, inject, signal } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { DatePipe } from '@angular/common';
import { Order } from '../../../core/services/admin-order.service';

interface StatCard {
  title: string;
  value: number;
  icon: string;
  color: string;
}

interface StatusCount {
  status: string;
  label: string;
  count: number;
  color: string;
}

const STATUS_META: Record<string, { label: string; color: string }> = {
  PENDING:          { label: 'Pending',          color: '#f59e0b' },
  IN_TRANSIT:       { label: 'In Transit',       color: '#3b82f6' },
  OUT_FOR_DELIVERY: { label: 'Out for Delivery', color: '#8b5cf6' },
  DELIVERED:        { label: 'Delivered',         color: '#10b981' },
  FAILED:           { label: 'Failed',            color: '#ef4444' },
  RETURNED:         { label: 'Returned',          color: '#6b7280' }
};

@Component({
  selector: 'app-admin-dashboard',
  imports: [MatIconModule, MatProgressSpinnerModule, RouterLink, DatePipe],
  templateUrl: './admin-dashboard.component.html',
  styleUrl: './admin-dashboard.component.scss'
})
export class AdminDashboardComponent implements OnInit {
  private readonly route = inject(ActivatedRoute);

  loading = signal(true);
  stats: StatCard[] = [];
  statusBreakdown: StatusCount[] = [];
  recentOrders: Order[] = [];
  todaysDeliveries = 0;
  failedRate = 0;
  returnRate = 0;

  ngOnInit(): void {
    this.route.data.subscribe(data => {
      this.buildStats(data['dashboard']);
      this.loading.set(false);
    });
  }

  private buildStats(data: { orders: Order[]; couriers: any[]; staff: any[] }): void {
    const orders = data.orders;

    const countByStatus = (s: string) => orders.filter(o => o.status === s).length;
    const pending = countByStatus('PENDING');
    const inTransit = countByStatus('IN_TRANSIT');
    const outForDelivery = countByStatus('OUT_FOR_DELIVERY');
    const delivered = countByStatus('DELIVERED');
    const failed = countByStatus('FAILED');
    const returned = countByStatus('RETURNED');

    this.stats = [
      { title: 'Total Orders',   value: orders.length,         icon: 'inventory_2',    color: '#3b82f6' },
      { title: 'Pending Orders', value: pending,               icon: 'pending_actions', color: '#f59e0b' },
      { title: 'Active Couriers', value: data.couriers.length, icon: 'delivery_dining', color: '#10b981' },
      { title: 'Staff Count',    value: data.staff.length,     icon: 'badge',           color: '#8b5cf6' }
    ];

    const today = new Date().toISOString().split('T')[0];
    this.todaysDeliveries = orders.filter(o => o.createdAt?.startsWith(today)).length;
    this.failedRate = orders.length ? Math.round((failed / orders.length) * 100) : 0;
    this.returnRate = orders.length ? Math.round((returned / orders.length) * 100) : 0;

    this.statusBreakdown = [
      { status: 'PENDING',          label: 'Pending',          count: pending,        color: '#f59e0b' },
      { status: 'IN_TRANSIT',       label: 'In Transit',       count: inTransit,      color: '#3b82f6' },
      { status: 'OUT_FOR_DELIVERY', label: 'Out for Delivery', count: outForDelivery, color: '#8b5cf6' },
      { status: 'DELIVERED',        label: 'Delivered',         count: delivered,      color: '#10b981' },
      { status: 'FAILED',           label: 'Failed',            count: failed,         color: '#ef4444' },
      { status: 'RETURNED',         label: 'Returned',          count: returned,       color: '#6b7280' }
    ];

    this.recentOrders = orders
      .slice()
      .sort((a, b) => new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime())
      .slice(0, 5);
  }

  getStatusColor(status: string): string {
    return STATUS_META[status]?.color ?? '#6b7280';
  }

  getStatusLabel(status: string): string {
    return STATUS_META[status]?.label ?? status;
  }
}
