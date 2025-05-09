/* tslint:disable */
/* eslint-disable */
/* Code generated by ng-openapi-gen DO NOT EDIT. */

import { HttpClient, HttpContext } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

import { BaseService } from '../base-service';
import { ApiConfiguration } from '../api-configuration';
import { StrictHttpResponse } from '../strict-http-response';

import { canPunchInOut } from '../fn/work-site-controller/can-punch-in-out';
import { CanPunchInOut$Params } from '../fn/work-site-controller/can-punch-in-out';
import { countAllWorksitesRelatedToTheCompany } from '../fn/work-site-controller/count-all-worksites-related-to-the-company';
import { CountAllWorksitesRelatedToTheCompany$Params } from '../fn/work-site-controller/count-all-worksites-related-to-the-company';
import { createWorkSite } from '../fn/work-site-controller/create-work-site';
import { CreateWorkSite$Params } from '../fn/work-site-controller/create-work-site';
import { deleteWorkSiteById } from '../fn/work-site-controller/delete-work-site-by-id';
import { DeleteWorkSiteById$Params } from '../fn/work-site-controller/delete-work-site-by-id';
import { findAllWorkSites } from '../fn/work-site-controller/find-all-work-sites';
import { FindAllWorkSites$Params } from '../fn/work-site-controller/find-all-work-sites';
import { findWorkSiteAllInformation } from '../fn/work-site-controller/find-work-site-all-information';
import { FindWorkSiteAllInformation$Params } from '../fn/work-site-controller/find-work-site-all-information';
import { findWorkSiteById } from '../fn/work-site-controller/find-work-site-by-id';
import { FindWorkSiteById$Params } from '../fn/work-site-controller/find-work-site-by-id';
import { getActiveWorkers } from '../fn/work-site-controller/get-active-workers';
import { GetActiveWorkers$Params } from '../fn/work-site-controller/get-active-workers';
import { getWorkSiteClosedDays } from '../fn/work-site-controller/get-work-site-closed-days';
import { GetWorkSiteClosedDays$Params } from '../fn/work-site-controller/get-work-site-closed-days';
import { isActive } from '../fn/work-site-controller/is-active';
import { IsActive$Params } from '../fn/work-site-controller/is-active';
import { isWithinRadius } from '../fn/work-site-controller/is-within-radius';
import { IsWithinRadius$Params } from '../fn/work-site-controller/is-within-radius';
import { IsWithinRadiusResponse } from '../models/is-within-radius-response';
import { PageResponseWorkerCurrentlyWorkingInWorkSite } from '../models/page-response-worker-currently-working-in-work-site';
import { PageResponseWorkSiteClosedDaysResponse } from '../models/page-response-work-site-closed-days-response';
import { PageResponseWorkSiteResponse } from '../models/page-response-work-site-response';
import { removeInactiveDay } from '../fn/work-site-controller/remove-inactive-day';
import { RemoveInactiveDay$Params } from '../fn/work-site-controller/remove-inactive-day';
import { scheduleInactiveDay } from '../fn/work-site-controller/schedule-inactive-day';
import { ScheduleInactiveDay$Params } from '../fn/work-site-controller/schedule-inactive-day';
import { ScheduleInactiveDayResponse } from '../models/schedule-inactive-day-response';
import { selectWorkSite } from '../fn/work-site-controller/select-work-site';
import { SelectWorkSite$Params } from '../fn/work-site-controller/select-work-site';
import { SelectWorkSiteResponse } from '../models/select-work-site-response';
import { setActive } from '../fn/work-site-controller/set-active';
import { SetActive$Params } from '../fn/work-site-controller/set-active';
import { setCustomRadius } from '../fn/work-site-controller/set-custom-radius';
import { SetCustomRadius$Params } from '../fn/work-site-controller/set-custom-radius';
import { setCustomRadiusForWorkerInSpecialWorkSite } from '../fn/work-site-controller/set-custom-radius-for-worker-in-special-work-site';
import { SetCustomRadiusForWorkerInSpecialWorkSite$Params } from '../fn/work-site-controller/set-custom-radius-for-worker-in-special-work-site';
import { SetNewCustomRadiusForWorkerInSpecialWorkSiteResponse } from '../models/set-new-custom-radius-for-worker-in-special-work-site-response';
import { SetNewCustomRadiusResponse } from '../models/set-new-custom-radius-response';
import { updateAddress } from '../fn/work-site-controller/update-address';
import { UpdateAddress$Params } from '../fn/work-site-controller/update-address';
import { updateLocation } from '../fn/work-site-controller/update-location';
import { UpdateLocation$Params } from '../fn/work-site-controller/update-location';
import { updateName } from '../fn/work-site-controller/update-name';
import { UpdateName$Params } from '../fn/work-site-controller/update-name';
import { updateWorkingHours } from '../fn/work-site-controller/update-working-hours';
import { UpdateWorkingHours$Params } from '../fn/work-site-controller/update-working-hours';
import { WorkSiteAllInformationResponse } from '../models/work-site-all-information-response';
import { WorkSiteResponse } from '../models/work-site-response';
import { WorkSiteUpdateAddressResponse } from '../models/work-site-update-address-response';
import { WorkSiteUpdateLocationResponse } from '../models/work-site-update-location-response';
import { WorkSiteUpdateNameResponse } from '../models/work-site-update-name-response';
import { WorkSiteUpdateWorkingHoursResponse } from '../models/work-site-update-working-hours-response';


/**
 * API for working with Work Sites
 */
@Injectable({ providedIn: 'root' })
export class WorkSiteControllerService extends BaseService {
  constructor(config: ApiConfiguration, http: HttpClient) {
    super(config, http);
  }

  /** Path part for operation `findAllWorkSites()` */
  static readonly FindAllWorkSitesPath = '/workSite';

  /**
   * Find all work sites with pagination.
   *
   *
   *
   * This method provides access to the full `HttpResponse`, allowing access to response headers.
   * To access only the response body, use `findAllWorkSites()` instead.
   *
   * This method doesn't expect any request body.
   */
  findAllWorkSites$Response(params?: FindAllWorkSites$Params, context?: HttpContext): Observable<StrictHttpResponse<PageResponseWorkSiteResponse>> {
    return findAllWorkSites(this.http, this.rootUrl, params, context);
  }

  /**
   * Find all work sites with pagination.
   *
   *
   *
   * This method provides access only to the response body.
   * To access the full response (for headers, for example), `findAllWorkSites$Response()` instead.
   *
   * This method doesn't expect any request body.
   */
  findAllWorkSites(params?: FindAllWorkSites$Params, context?: HttpContext): Observable<PageResponseWorkSiteResponse> {
    return this.findAllWorkSites$Response(params, context).pipe(
      map((r: StrictHttpResponse<PageResponseWorkSiteResponse>): PageResponseWorkSiteResponse => r.body)
    );
  }

  /** Path part for operation `createWorkSite()` */
  static readonly CreateWorkSitePath = '/workSite';

  /**
   * Create new work site.
   *
   *
   *
   * This method provides access to the full `HttpResponse`, allowing access to response headers.
   * To access only the response body, use `createWorkSite()` instead.
   *
   * This method sends `application/json` and handles request body of type `application/json`.
   */
  createWorkSite$Response(params: CreateWorkSite$Params, context?: HttpContext): Observable<StrictHttpResponse<WorkSiteResponse>> {
    return createWorkSite(this.http, this.rootUrl, params, context);
  }

  /**
   * Create new work site.
   *
   *
   *
   * This method provides access only to the response body.
   * To access the full response (for headers, for example), `createWorkSite$Response()` instead.
   *
   * This method sends `application/json` and handles request body of type `application/json`.
   */
  createWorkSite(params: CreateWorkSite$Params, context?: HttpContext): Observable<WorkSiteResponse> {
    return this.createWorkSite$Response(params, context).pipe(
      map((r: StrictHttpResponse<WorkSiteResponse>): WorkSiteResponse => r.body)
    );
  }

  /** Path part for operation `isWithinRadius()` */
  static readonly IsWithinRadiusPath = '/workSite/{workSiteId}/within-radius';

  /**
   * Check is user in correct work site radius.
   *
   *
   *
   * This method provides access to the full `HttpResponse`, allowing access to response headers.
   * To access only the response body, use `isWithinRadius()` instead.
   *
   * This method sends `application/json` and handles request body of type `application/json`.
   */
  isWithinRadius$Response(params: IsWithinRadius$Params, context?: HttpContext): Observable<StrictHttpResponse<IsWithinRadiusResponse>> {
    return isWithinRadius(this.http, this.rootUrl, params, context);
  }

  /**
   * Check is user in correct work site radius.
   *
   *
   *
   * This method provides access only to the response body.
   * To access the full response (for headers, for example), `isWithinRadius$Response()` instead.
   *
   * This method sends `application/json` and handles request body of type `application/json`.
   */
  isWithinRadius(params: IsWithinRadius$Params, context?: HttpContext): Observable<IsWithinRadiusResponse> {
    return this.isWithinRadius$Response(params, context).pipe(
      map((r: StrictHttpResponse<IsWithinRadiusResponse>): IsWithinRadiusResponse => r.body)
    );
  }

  /** Path part for operation `scheduleInactiveDay()` */
  static readonly ScheduleInactiveDayPath = '/workSite/{workSiteId}/inactive-day';

  /**
   * Schedule vacation day.
   *
   *
   *
   * This method provides access to the full `HttpResponse`, allowing access to response headers.
   * To access only the response body, use `scheduleInactiveDay()` instead.
   *
   * This method sends `application/json` and handles request body of type `application/json`.
   */
  scheduleInactiveDay$Response(params: ScheduleInactiveDay$Params, context?: HttpContext): Observable<StrictHttpResponse<ScheduleInactiveDayResponse>> {
    return scheduleInactiveDay(this.http, this.rootUrl, params, context);
  }

  /**
   * Schedule vacation day.
   *
   *
   *
   * This method provides access only to the response body.
   * To access the full response (for headers, for example), `scheduleInactiveDay$Response()` instead.
   *
   * This method sends `application/json` and handles request body of type `application/json`.
   */
  scheduleInactiveDay(params: ScheduleInactiveDay$Params, context?: HttpContext): Observable<ScheduleInactiveDayResponse> {
    return this.scheduleInactiveDay$Response(params, context).pipe(
      map((r: StrictHttpResponse<ScheduleInactiveDayResponse>): ScheduleInactiveDayResponse => r.body)
    );
  }

  /** Path part for operation `removeInactiveDay()` */
  static readonly RemoveInactiveDayPath = '/workSite/{workSiteId}/inactive-day';

  /**
   * Remove inactive day.
   *
   *
   *
   * This method provides access to the full `HttpResponse`, allowing access to response headers.
   * To access only the response body, use `removeInactiveDay()` instead.
   *
   * This method sends `application/json` and handles request body of type `application/json`.
   */
  removeInactiveDay$Response(params: RemoveInactiveDay$Params, context?: HttpContext): Observable<StrictHttpResponse<ScheduleInactiveDayResponse>> {
    return removeInactiveDay(this.http, this.rootUrl, params, context);
  }

  /**
   * Remove inactive day.
   *
   *
   *
   * This method provides access only to the response body.
   * To access the full response (for headers, for example), `removeInactiveDay$Response()` instead.
   *
   * This method sends `application/json` and handles request body of type `application/json`.
   */
  removeInactiveDay(params: RemoveInactiveDay$Params, context?: HttpContext): Observable<ScheduleInactiveDayResponse> {
    return this.removeInactiveDay$Response(params, context).pipe(
      map((r: StrictHttpResponse<ScheduleInactiveDayResponse>): ScheduleInactiveDayResponse => r.body)
    );
  }

  /** Path part for operation `setCustomRadius()` */
  static readonly SetCustomRadiusPath = '/workSite/{workSiteId}/custom-radius';

  /**
   * Set special radius .
   *
   *
   *
   * This method provides access to the full `HttpResponse`, allowing access to response headers.
   * To access only the response body, use `setCustomRadius()` instead.
   *
   * This method sends `application/json` and handles request body of type `application/json`.
   */
  setCustomRadius$Response(params: SetCustomRadius$Params, context?: HttpContext): Observable<StrictHttpResponse<SetNewCustomRadiusResponse>> {
    return setCustomRadius(this.http, this.rootUrl, params, context);
  }

  /**
   * Set special radius .
   *
   *
   *
   * This method provides access only to the response body.
   * To access the full response (for headers, for example), `setCustomRadius$Response()` instead.
   *
   * This method sends `application/json` and handles request body of type `application/json`.
   */
  setCustomRadius(params: SetCustomRadius$Params, context?: HttpContext): Observable<SetNewCustomRadiusResponse> {
    return this.setCustomRadius$Response(params, context).pipe(
      map((r: StrictHttpResponse<SetNewCustomRadiusResponse>): SetNewCustomRadiusResponse => r.body)
    );
  }

  /** Path part for operation `setCustomRadiusForWorkerInSpecialWorkSite()` */
  static readonly SetCustomRadiusForWorkerInSpecialWorkSitePath = '/workSite/{workSiteId}/custom-radius/{workerId}';

  /**
   * Set custom radius for specific worker on work site.
   *
   *
   *
   * This method provides access to the full `HttpResponse`, allowing access to response headers.
   * To access only the response body, use `setCustomRadiusForWorkerInSpecialWorkSite()` instead.
   *
   * This method sends `application/json` and handles request body of type `application/json`.
   */
  setCustomRadiusForWorkerInSpecialWorkSite$Response(params: SetCustomRadiusForWorkerInSpecialWorkSite$Params, context?: HttpContext): Observable<StrictHttpResponse<SetNewCustomRadiusForWorkerInSpecialWorkSiteResponse>> {
    return setCustomRadiusForWorkerInSpecialWorkSite(this.http, this.rootUrl, params, context);
  }

  /**
   * Set custom radius for specific worker on work site.
   *
   *
   *
   * This method provides access only to the response body.
   * To access the full response (for headers, for example), `setCustomRadiusForWorkerInSpecialWorkSite$Response()` instead.
   *
   * This method sends `application/json` and handles request body of type `application/json`.
   */
  setCustomRadiusForWorkerInSpecialWorkSite(params: SetCustomRadiusForWorkerInSpecialWorkSite$Params, context?: HttpContext): Observable<SetNewCustomRadiusForWorkerInSpecialWorkSiteResponse> {
    return this.setCustomRadiusForWorkerInSpecialWorkSite$Response(params, context).pipe(
      map((r: StrictHttpResponse<SetNewCustomRadiusForWorkerInSpecialWorkSiteResponse>): SetNewCustomRadiusForWorkerInSpecialWorkSiteResponse => r.body)
    );
  }

  /** Path part for operation `canPunchInOut()` */
  static readonly CanPunchInOutPath = '/workSite/{workSiteId}/can-punch/{userId}';

  /**
   * Check is it correct time to punch in/out.
   *
   *
   *
   * This method provides access to the full `HttpResponse`, allowing access to response headers.
   * To access only the response body, use `canPunchInOut()` instead.
   *
   * This method sends `application/json` and handles request body of type `application/json`.
   */
  canPunchInOut$Response(params: CanPunchInOut$Params, context?: HttpContext): Observable<StrictHttpResponse<boolean>> {
    return canPunchInOut(this.http, this.rootUrl, params, context);
  }

  /**
   * Check is it correct time to punch in/out.
   *
   *
   *
   * This method provides access only to the response body.
   * To access the full response (for headers, for example), `canPunchInOut$Response()` instead.
   *
   * This method sends `application/json` and handles request body of type `application/json`.
   */
  canPunchInOut(params: CanPunchInOut$Params, context?: HttpContext): Observable<boolean> {
    return this.canPunchInOut$Response(params, context).pipe(
      map((r: StrictHttpResponse<boolean>): boolean => r.body)
    );
  }

  /** Path part for operation `selectWorkSite()` */
  static readonly SelectWorkSitePath = '/workSite/select/{workSiteId}';

  /**
   * Select work site.
   *
   *
   *
   * This method provides access to the full `HttpResponse`, allowing access to response headers.
   * To access only the response body, use `selectWorkSite()` instead.
   *
   * This method doesn't expect any request body.
   */
  selectWorkSite$Response(params: SelectWorkSite$Params, context?: HttpContext): Observable<StrictHttpResponse<SelectWorkSiteResponse>> {
    return selectWorkSite(this.http, this.rootUrl, params, context);
  }

  /**
   * Select work site.
   *
   *
   *
   * This method provides access only to the response body.
   * To access the full response (for headers, for example), `selectWorkSite$Response()` instead.
   *
   * This method doesn't expect any request body.
   */
  selectWorkSite(params: SelectWorkSite$Params, context?: HttpContext): Observable<SelectWorkSiteResponse> {
    return this.selectWorkSite$Response(params, context).pipe(
      map((r: StrictHttpResponse<SelectWorkSiteResponse>): SelectWorkSiteResponse => r.body)
    );
  }

  /** Path part for operation `updateWorkingHours()` */
  static readonly UpdateWorkingHoursPath = '/workSite/{workSiteId}/working-hours';

  /**
   * Update working hours work site.
   *
   *
   *
   * This method provides access to the full `HttpResponse`, allowing access to response headers.
   * To access only the response body, use `updateWorkingHours()` instead.
   *
   * This method sends `application/json` and handles request body of type `application/json`.
   */
  updateWorkingHours$Response(params: UpdateWorkingHours$Params, context?: HttpContext): Observable<StrictHttpResponse<WorkSiteUpdateWorkingHoursResponse>> {
    return updateWorkingHours(this.http, this.rootUrl, params, context);
  }

  /**
   * Update working hours work site.
   *
   *
   *
   * This method provides access only to the response body.
   * To access the full response (for headers, for example), `updateWorkingHours$Response()` instead.
   *
   * This method sends `application/json` and handles request body of type `application/json`.
   */
  updateWorkingHours(params: UpdateWorkingHours$Params, context?: HttpContext): Observable<WorkSiteUpdateWorkingHoursResponse> {
    return this.updateWorkingHours$Response(params, context).pipe(
      map((r: StrictHttpResponse<WorkSiteUpdateWorkingHoursResponse>): WorkSiteUpdateWorkingHoursResponse => r.body)
    );
  }

  /** Path part for operation `updateName()` */
  static readonly UpdateNamePath = '/workSite/{workSiteId}/name';

  /**
   * Update name for work site.
   *
   *
   *
   * This method provides access to the full `HttpResponse`, allowing access to response headers.
   * To access only the response body, use `updateName()` instead.
   *
   * This method sends `application/json` and handles request body of type `application/json`.
   */
  updateName$Response(params: UpdateName$Params, context?: HttpContext): Observable<StrictHttpResponse<WorkSiteUpdateNameResponse>> {
    return updateName(this.http, this.rootUrl, params, context);
  }

  /**
   * Update name for work site.
   *
   *
   *
   * This method provides access only to the response body.
   * To access the full response (for headers, for example), `updateName$Response()` instead.
   *
   * This method sends `application/json` and handles request body of type `application/json`.
   */
  updateName(params: UpdateName$Params, context?: HttpContext): Observable<WorkSiteUpdateNameResponse> {
    return this.updateName$Response(params, context).pipe(
      map((r: StrictHttpResponse<WorkSiteUpdateNameResponse>): WorkSiteUpdateNameResponse => r.body)
    );
  }

  /** Path part for operation `updateLocation()` */
  static readonly UpdateLocationPath = '/workSite/{workSiteId}/location';

  /**
   * Update location of work site.
   *
   *
   *
   * This method provides access to the full `HttpResponse`, allowing access to response headers.
   * To access only the response body, use `updateLocation()` instead.
   *
   * This method sends `application/json` and handles request body of type `application/json`.
   */
  updateLocation$Response(params: UpdateLocation$Params, context?: HttpContext): Observable<StrictHttpResponse<WorkSiteUpdateLocationResponse>> {
    return updateLocation(this.http, this.rootUrl, params, context);
  }

  /**
   * Update location of work site.
   *
   *
   *
   * This method provides access only to the response body.
   * To access the full response (for headers, for example), `updateLocation$Response()` instead.
   *
   * This method sends `application/json` and handles request body of type `application/json`.
   */
  updateLocation(params: UpdateLocation$Params, context?: HttpContext): Observable<WorkSiteUpdateLocationResponse> {
    return this.updateLocation$Response(params, context).pipe(
      map((r: StrictHttpResponse<WorkSiteUpdateLocationResponse>): WorkSiteUpdateLocationResponse => r.body)
    );
  }

  /** Path part for operation `updateAddress()` */
  static readonly UpdateAddressPath = '/workSite/{workSiteId}/address';

  /**
   * Update address for work site.
   *
   *
   *
   * This method provides access to the full `HttpResponse`, allowing access to response headers.
   * To access only the response body, use `updateAddress()` instead.
   *
   * This method sends `application/json` and handles request body of type `application/json`.
   */
  updateAddress$Response(params: UpdateAddress$Params, context?: HttpContext): Observable<StrictHttpResponse<WorkSiteUpdateAddressResponse>> {
    return updateAddress(this.http, this.rootUrl, params, context);
  }

  /**
   * Update address for work site.
   *
   *
   *
   * This method provides access only to the response body.
   * To access the full response (for headers, for example), `updateAddress$Response()` instead.
   *
   * This method sends `application/json` and handles request body of type `application/json`.
   */
  updateAddress(params: UpdateAddress$Params, context?: HttpContext): Observable<WorkSiteUpdateAddressResponse> {
    return this.updateAddress$Response(params, context).pipe(
      map((r: StrictHttpResponse<WorkSiteUpdateAddressResponse>): WorkSiteUpdateAddressResponse => r.body)
    );
  }

  /** Path part for operation `isActive()` */
  static readonly IsActivePath = '/workSite/{workSiteId}/active';

  /**
   * Check is work site active.
   *
   *
   *
   * This method provides access to the full `HttpResponse`, allowing access to response headers.
   * To access only the response body, use `isActive()` instead.
   *
   * This method doesn't expect any request body.
   */
  isActive$Response(params: IsActive$Params, context?: HttpContext): Observable<StrictHttpResponse<boolean>> {
    return isActive(this.http, this.rootUrl, params, context);
  }

  /**
   * Check is work site active.
   *
   *
   *
   * This method provides access only to the response body.
   * To access the full response (for headers, for example), `isActive$Response()` instead.
   *
   * This method doesn't expect any request body.
   */
  isActive(params: IsActive$Params, context?: HttpContext): Observable<boolean> {
    return this.isActive$Response(params, context).pipe(
      map((r: StrictHttpResponse<boolean>): boolean => r.body)
    );
  }

  /** Path part for operation `setActive()` */
  static readonly SetActivePath = '/workSite/{workSiteId}/active';

  /**
   * Change status of work site activity.
   *
   *
   *
   * This method provides access to the full `HttpResponse`, allowing access to response headers.
   * To access only the response body, use `setActive()` instead.
   *
   * This method sends `application/json` and handles request body of type `application/json`.
   */
  setActive$Response(params: SetActive$Params, context?: HttpContext): Observable<StrictHttpResponse<void>> {
    return setActive(this.http, this.rootUrl, params, context);
  }

  /**
   * Change status of work site activity.
   *
   *
   *
   * This method provides access only to the response body.
   * To access the full response (for headers, for example), `setActive$Response()` instead.
   *
   * This method sends `application/json` and handles request body of type `application/json`.
   */
  setActive(params: SetActive$Params, context?: HttpContext): Observable<void> {
    return this.setActive$Response(params, context).pipe(
      map((r: StrictHttpResponse<void>): void => r.body)
    );
  }

  /** Path part for operation `getWorkSiteClosedDays()` */
  static readonly GetWorkSiteClosedDaysPath = '/workSite/{workSiteId}/closed-days';

  /**
   * Get all closed days in special work site.
   *
   *
   *
   * This method provides access to the full `HttpResponse`, allowing access to response headers.
   * To access only the response body, use `getWorkSiteClosedDays()` instead.
   *
   * This method doesn't expect any request body.
   */
  getWorkSiteClosedDays$Response(params: GetWorkSiteClosedDays$Params, context?: HttpContext): Observable<StrictHttpResponse<PageResponseWorkSiteClosedDaysResponse>> {
    return getWorkSiteClosedDays(this.http, this.rootUrl, params, context);
  }

  /**
   * Get all closed days in special work site.
   *
   *
   *
   * This method provides access only to the response body.
   * To access the full response (for headers, for example), `getWorkSiteClosedDays$Response()` instead.
   *
   * This method doesn't expect any request body.
   */
  getWorkSiteClosedDays(params: GetWorkSiteClosedDays$Params, context?: HttpContext): Observable<PageResponseWorkSiteClosedDaysResponse> {
    return this.getWorkSiteClosedDays$Response(params, context).pipe(
      map((r: StrictHttpResponse<PageResponseWorkSiteClosedDaysResponse>): PageResponseWorkSiteClosedDaysResponse => r.body)
    );
  }

  /** Path part for operation `findWorkSiteAllInformation()` */
  static readonly FindWorkSiteAllInformationPath = '/workSite/{workSiteId}/all-information';

  /**
   * Get all information about work site by ID.
   *
   *
   *
   * This method provides access to the full `HttpResponse`, allowing access to response headers.
   * To access only the response body, use `findWorkSiteAllInformation()` instead.
   *
   * This method doesn't expect any request body.
   */
  findWorkSiteAllInformation$Response(params: FindWorkSiteAllInformation$Params, context?: HttpContext): Observable<StrictHttpResponse<WorkSiteAllInformationResponse>> {
    return findWorkSiteAllInformation(this.http, this.rootUrl, params, context);
  }

  /**
   * Get all information about work site by ID.
   *
   *
   *
   * This method provides access only to the response body.
   * To access the full response (for headers, for example), `findWorkSiteAllInformation$Response()` instead.
   *
   * This method doesn't expect any request body.
   */
  findWorkSiteAllInformation(params: FindWorkSiteAllInformation$Params, context?: HttpContext): Observable<WorkSiteAllInformationResponse> {
    return this.findWorkSiteAllInformation$Response(params, context).pipe(
      map((r: StrictHttpResponse<WorkSiteAllInformationResponse>): WorkSiteAllInformationResponse => r.body)
    );
  }

  /** Path part for operation `getActiveWorkers()` */
  static readonly GetActiveWorkersPath = '/workSite/{workSiteId}/active-workers';

  /**
   * Get list of workers whose working right now in worksite and related to special company.
   *
   *
   *
   * This method provides access to the full `HttpResponse`, allowing access to response headers.
   * To access only the response body, use `getActiveWorkers()` instead.
   *
   * This method doesn't expect any request body.
   */
  getActiveWorkers$Response(params: GetActiveWorkers$Params, context?: HttpContext): Observable<StrictHttpResponse<PageResponseWorkerCurrentlyWorkingInWorkSite>> {
    return getActiveWorkers(this.http, this.rootUrl, params, context);
  }

  /**
   * Get list of workers whose working right now in worksite and related to special company.
   *
   *
   *
   * This method provides access only to the response body.
   * To access the full response (for headers, for example), `getActiveWorkers$Response()` instead.
   *
   * This method doesn't expect any request body.
   */
  getActiveWorkers(params: GetActiveWorkers$Params, context?: HttpContext): Observable<PageResponseWorkerCurrentlyWorkingInWorkSite> {
    return this.getActiveWorkers$Response(params, context).pipe(
      map((r: StrictHttpResponse<PageResponseWorkerCurrentlyWorkingInWorkSite>): PageResponseWorkerCurrentlyWorkingInWorkSite => r.body)
    );
  }

  /** Path part for operation `findWorkSiteById()` */
  static readonly FindWorkSiteByIdPath = '/workSite/{id}';

  /**
   * Find work site by ID.
   *
   *
   *
   * This method provides access to the full `HttpResponse`, allowing access to response headers.
   * To access only the response body, use `findWorkSiteById()` instead.
   *
   * This method doesn't expect any request body.
   */
  findWorkSiteById$Response(params: FindWorkSiteById$Params, context?: HttpContext): Observable<StrictHttpResponse<WorkSiteResponse>> {
    return findWorkSiteById(this.http, this.rootUrl, params, context);
  }

  /**
   * Find work site by ID.
   *
   *
   *
   * This method provides access only to the response body.
   * To access the full response (for headers, for example), `findWorkSiteById$Response()` instead.
   *
   * This method doesn't expect any request body.
   */
  findWorkSiteById(params: FindWorkSiteById$Params, context?: HttpContext): Observable<WorkSiteResponse> {
    return this.findWorkSiteById$Response(params, context).pipe(
      map((r: StrictHttpResponse<WorkSiteResponse>): WorkSiteResponse => r.body)
    );
  }

  /** Path part for operation `countAllWorksitesRelatedToTheCompany()` */
  static readonly CountAllWorksitesRelatedToTheCompanyPath = '/workSite/work-site/sum';

  /**
   * Get sum of all Worksites.
   *
   *
   *
   * This method provides access to the full `HttpResponse`, allowing access to response headers.
   * To access only the response body, use `countAllWorksitesRelatedToTheCompany()` instead.
   *
   * This method doesn't expect any request body.
   */
  countAllWorksitesRelatedToTheCompany$Response(params?: CountAllWorksitesRelatedToTheCompany$Params, context?: HttpContext): Observable<StrictHttpResponse<number>> {
    return countAllWorksitesRelatedToTheCompany(this.http, this.rootUrl, params, context);
  }

  /**
   * Get sum of all Worksites.
   *
   *
   *
   * This method provides access only to the response body.
   * To access the full response (for headers, for example), `countAllWorksitesRelatedToTheCompany$Response()` instead.
   *
   * This method doesn't expect any request body.
   */
  countAllWorksitesRelatedToTheCompany(params?: CountAllWorksitesRelatedToTheCompany$Params, context?: HttpContext): Observable<number> {
    return this.countAllWorksitesRelatedToTheCompany$Response(params, context).pipe(
      map((r: StrictHttpResponse<number>): number => r.body)
    );
  }

  /** Path part for operation `deleteWorkSiteById()` */
  static readonly DeleteWorkSiteByIdPath = '/workSite/{workSiteId}';

  /**
   * Delete work site by ID.
   *
   *
   *
   * This method provides access to the full `HttpResponse`, allowing access to response headers.
   * To access only the response body, use `deleteWorkSiteById()` instead.
   *
   * This method doesn't expect any request body.
   */
  deleteWorkSiteById$Response(params: DeleteWorkSiteById$Params, context?: HttpContext): Observable<StrictHttpResponse<void>> {
    return deleteWorkSiteById(this.http, this.rootUrl, params, context);
  }

  /**
   * Delete work site by ID.
   *
   *
   *
   * This method provides access only to the response body.
   * To access the full response (for headers, for example), `deleteWorkSiteById$Response()` instead.
   *
   * This method doesn't expect any request body.
   */
  deleteWorkSiteById(params: DeleteWorkSiteById$Params, context?: HttpContext): Observable<void> {
    return this.deleteWorkSiteById$Response(params, context).pipe(
      map((r: StrictHttpResponse<void>): void => r.body)
    );
  }

}
