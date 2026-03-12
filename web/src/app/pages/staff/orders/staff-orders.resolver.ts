import { inject } from '@angular/core';
import { ResolveFn } from '@angular/router';
import { StaffOrderService } from '../../../core/services/staff-order.service';
import { Order } from '../../../core/services/admin-order.service';

export const staffOrdersResolver: ResolveFn<Order[]> = () => {
  return inject(StaffOrderService).getOrders();
};
