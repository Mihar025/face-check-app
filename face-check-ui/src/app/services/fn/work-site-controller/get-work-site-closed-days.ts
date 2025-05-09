/* tslint:disable */
/* eslint-disable */
/* Code generated by ng-openapi-gen DO NOT EDIT. */

import { HttpClient, HttpContext, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import { StrictHttpResponse } from '../../strict-http-response';
import { RequestBuilder } from '../../request-builder';

import { PageResponseWorkSiteClosedDaysResponse } from '../../models/page-response-work-site-closed-days-response';

export interface GetWorkSiteClosedDays$Params {
  workSiteId: number;
  page?: number;
  size?: number;
}

export function getWorkSiteClosedDays(http: HttpClient, rootUrl: string, params: GetWorkSiteClosedDays$Params, context?: HttpContext): Observable<StrictHttpResponse<PageResponseWorkSiteClosedDaysResponse>> {
  const rb = new RequestBuilder(rootUrl, getWorkSiteClosedDays.PATH, 'get');
  if (params) {
    rb.path('workSiteId', params.workSiteId, {});
    rb.query('page', params.page, {});
    rb.query('size', params.size, {});
  }

  return http.request(
    rb.build({ responseType: 'json', accept: 'application/json', context })
  ).pipe(
    filter((r: any): r is HttpResponse<any> => r instanceof HttpResponse),
    map((r: HttpResponse<any>) => {
      return r as StrictHttpResponse<PageResponseWorkSiteClosedDaysResponse>;
    })
  );
}

getWorkSiteClosedDays.PATH = '/workSite/{workSiteId}/closed-days';
