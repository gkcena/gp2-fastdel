import { inject } from '@angular/core';
import { ResolveFn } from '@angular/router';
import { forkJoin } from 'rxjs';
import { AdminOrderService, Order } from '../../../core/services/admin-order.service';
import { AdminUserService, Courier } from '../../../core/services/admin-user.service';

export interface OrdersData {
  orders: Order[];
  couriers: Courier[];
}

export const ordersResolver: ResolveFn<OrdersData> = () => {
  const orderService = inject(AdminOrderService);
  const userService = inject(AdminUserService);

  return forkJoin({
    orders: orderService.getOrders(),
    couriers: userService.getCouriers()
  });
};
