/* tslint:disable */
/* eslint-disable */
/* Code generated by ng-openapi-gen DO NOT EDIT. */

import { HttpClient, HttpContext, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import { StrictHttpResponse } from '../../strict-http-response';
import { RequestBuilder } from '../../request-builder';

import { DailyEarningResponse } from '../../models/daily-earning-response';

export interface GetWeeklyEarnings$Params {
}

export function getWeeklyEarnings(http: HttpClient, rootUrl: string, params?: GetWeeklyEarnings$Params, context?: HttpContext): Observable<StrictHttpResponse<Array<DailyEarningResponse>>> {
  const rb = new RequestBuilder(rootUrl, getWeeklyEarnings.PATH, 'get');
  if (params) {
  }

  return http.request(
    rb.build({ responseType: 'json', accept: 'application/json', context })
  ).pipe(
    filter((r: any): r is HttpResponse<any> => r instanceof HttpResponse),
    map((r: HttpResponse<any>) => {
      return r as StrictHttpResponse<Array<DailyEarningResponse>>;
    })
  );
}

getWeeklyEarnings.PATH = '/attendance/week';
