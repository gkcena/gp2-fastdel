import { inject } from '@angular/core';
import { ResolveFn } from '@angular/router';
import { AdminVehicleService, VehicleRequest } from '../../../core/services/admin-vehicle.service';

export const vehicleRequestsResolver: ResolveFn<VehicleRequest[]> = () => {
  return inject(AdminVehicleService).getVehicleRequests();
};
