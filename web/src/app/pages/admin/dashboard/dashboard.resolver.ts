import { inject } from '@angular/core';
import { ResolveFn } from '@angular/router';
import { forkJoin, map } from 'rxjs';
import { AdminOrderService, Order } from '../../../core/services/admin-order.service';
import { AdminUserService, StaffMember, Courier } from '../../../core/services/admin-user.service';

export interface DashboardData {
  orders: Order[];
  couriers: Courier[];
  staff: StaffMember[];
}

export const dashboardResolver: ResolveFn<DashboardData> = () => {
  const orderService = inject(AdminOrderService);
  const userService = inject(AdminUserService);

  return forkJoin({
    orders: orderService.getOrders(),
    couriers: userService.getCouriers(),
    staff: userService.getStaff()
  });
};
