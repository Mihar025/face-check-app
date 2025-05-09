/* tslint:disable */
/* eslint-disable */
/* Code generated by ng-openapi-gen DO NOT EDIT. */

import { HttpClient, HttpContext, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import { StrictHttpResponse } from '../../strict-http-response';
import { RequestBuilder } from '../../request-builder';

import { WorkSiteUpdateLocationRequest } from '../../models/work-site-update-location-request';
import { WorkSiteUpdateLocationResponse } from '../../models/work-site-update-location-response';

export interface UpdateLocation$Params {
  workSiteId: number;
      body: WorkSiteUpdateLocationRequest
}

export function updateLocation(http: HttpClient, rootUrl: string, params: UpdateLocation$Params, context?: HttpContext): Observable<StrictHttpResponse<WorkSiteUpdateLocationResponse>> {
  const rb = new RequestBuilder(rootUrl, updateLocation.PATH, 'patch');
  if (params) {
    rb.path('workSiteId', params.workSiteId, {});
    rb.body(params.body, 'application/json');
  }

  return http.request(
    rb.build({ responseType: 'json', accept: 'application/json', context })
  ).pipe(
    filter((r: any): r is HttpResponse<any> => r instanceof HttpResponse),
    map((r: HttpResponse<any>) => {
      return r as StrictHttpResponse<WorkSiteUpdateLocationResponse>;
    })
  );
}

updateLocation.PATH = '/workSite/{workSiteId}/location';
