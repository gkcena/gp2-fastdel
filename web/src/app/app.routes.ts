import { Routes } from '@angular/router';
import { authGuard } from './core/guards/auth.guard';
import { roleGuard } from './core/guards/role.guard';
import { dashboardResolver } from './pages/admin/dashboard/dashboard.resolver';
import { usersResolver } from './pages/admin/users/users.resolver';
import { ordersResolver } from './pages/admin/orders/orders.resolver';
import { vehicleRequestsResolver } from './pages/admin/vehicle-requests/vehicle-requests.resolver';
import { staffOrdersResolver } from './pages/staff/orders/staff-orders.resolver';

export const routes: Routes = [
  { path: '', redirectTo: '/login', pathMatch: 'full' },
  {
    path: 'login',
    loadComponent: () =>
      import('./pages/login/login.component').then(m => m.LoginComponent)
  },
  {
    path: 'admin',
    loadComponent: () =>
      import('./layouts/admin-layout/admin-layout.component').then(m => m.AdminLayoutComponent),
    canActivate: [authGuard, roleGuard],
    data: { role: 'ADMIN' },
    children: [
      { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
      {
        path: 'dashboard',
        loadComponent: () =>
          import('./pages/admin/dashboard/admin-dashboard.component').then(m => m.AdminDashboardComponent),
        resolve: { dashboard: dashboardResolver }
      },
      {
        path: 'users',
        loadComponent: () =>
          import('./pages/admin/users/admin-users.component').then(m => m.AdminUsersComponent),
        resolve: { users: usersResolver }
      },
      {
        path: 'orders',
        loadComponent: () =>
          import('./pages/admin/orders/admin-orders.component').then(m => m.AdminOrdersComponent),
        resolve: { orders: ordersResolver }
      },
      {
        path: 'vehicle-requests',
        loadComponent: () =>
          import('./pages/admin/vehicle-requests/admin-vehicle-requests.component').then(m => m.AdminVehicleRequestsComponent),
        resolve: { vehicleRequests: vehicleRequestsResolver }
      },
      {
        path: 'profile',
        loadComponent: () =>
          import('./pages/admin/profile/admin-profile.component').then(m => m.AdminProfileComponent)
      }
    ]
  },
  {
    path: 'staff',
    loadComponent: () =>
      import('./layouts/staff-layout/staff-layout.component').then(m => m.StaffLayoutComponent),
    canActivate: [authGuard, roleGuard],
    data: { role: 'STAFF' },
    children: [
      { path: '', redirectTo: 'orders', pathMatch: 'full' },
      {
        path: 'orders',
        loadComponent: () =>
          import('./pages/staff/orders/staff-orders.component').then(m => m.StaffOrdersComponent),
        resolve: { staffOrders: staffOrdersResolver }
      },
      {
        path: 'profile',
        loadComponent: () =>
          import('./pages/staff/profile/staff-profile.component').then(m => m.StaffProfileComponent)
      }
    ]
  },
  { path: '**', redirectTo: '/login' }
];
