/* tslint:disable */
/* eslint-disable */
/* Code generated by ng-openapi-gen DO NOT EDIT. */

import { LocalTime } from '../models/local-time';
export interface ChangePunchInRequest {
  dateWhenWorkerDidntMakePunchIn?: string;
  newPunchInDate?: string;
  newPunchInTime?: LocalTime;
  workerId?: number;
}
