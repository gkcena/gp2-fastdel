import { inject } from '@angular/core';
import { ResolveFn } from '@angular/router';
import { forkJoin } from 'rxjs';
import { AdminUserService, StaffMember, Courier } from '../../../core/services/admin-user.service';

export interface UsersData {
  admins: StaffMember[];
  staff: StaffMember[];
  couriers: Courier[];
}

export const usersResolver: ResolveFn<UsersData> = () => {
  const userService = inject(AdminUserService);

  return forkJoin({
    admins: userService.getAdmins(),
    staff: userService.getStaff(),
    couriers: userService.getCouriers()
  });
};
