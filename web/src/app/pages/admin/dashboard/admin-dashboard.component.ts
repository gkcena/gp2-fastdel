import { Component, OnInit, inject } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { Order } from '../../../core/services/admin-order.service';

interface StatCard {
  title: string;
  value: number;
  icon: string;
  color: string;
}

@Component({
  selector: 'app-admin-dashboard',
  imports: [MatCardModule, MatIconModule, MatProgressSpinnerModule],
  templateUrl: './admin-dashboard.component.html',
  styleUrl: './admin-dashboard.component.scss'
})
export class AdminDashboardComponent implements OnInit {
  private readonly route = inject(ActivatedRoute);

  loading = true;
  stats: StatCard[] = [];

  ngOnInit(): void {
    this.route.data.subscribe(data => {
      this.buildStats(data['dashboard']);
      this.loading = false;
    });
  }

  private buildStats(data: { orders: Order[]; couriers: any[]; staff: any[] }): void {
    const pending = data.orders.filter(o => o.status === 'PENDING').length;
    this.stats = [
      { title: 'Total Orders', value: data.orders.length, icon: 'inventory_2', color: '#3b82f6' },
      { title: 'Pending Orders', value: pending, icon: 'pending_actions', color: '#f97316' },
      { title: 'Active Couriers', value: data.couriers.length, icon: 'delivery_dining', color: '#10b981' },
      { title: 'Staff Count', value: data.staff.length, icon: 'badge', color: '#8b5cf6' }
    ];
  }
}
