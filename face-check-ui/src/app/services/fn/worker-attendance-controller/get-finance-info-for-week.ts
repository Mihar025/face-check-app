/* tslint:disable */
/* eslint-disable */
/* Code generated by ng-openapi-gen DO NOT EDIT. */

import { HttpClient, HttpContext, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import { StrictHttpResponse } from '../../strict-http-response';
import { RequestBuilder } from '../../request-builder';

import { FinanceInfoForWeekInFinanceScreenResponse } from '../../models/finance-info-for-week-in-finance-screen-response';

export interface GetFinanceInfoForWeek$Params {
  weekStart: string;
}

export function getFinanceInfoForWeek(http: HttpClient, rootUrl: string, params: GetFinanceInfoForWeek$Params, context?: HttpContext): Observable<StrictHttpResponse<FinanceInfoForWeekInFinanceScreenResponse>> {
  const rb = new RequestBuilder(rootUrl, getFinanceInfoForWeek.PATH, 'get');
  if (params) {
    rb.query('weekStart', params.weekStart, {});
  }

  return http.request(
    rb.build({ responseType: 'json', accept: 'application/json', context })
  ).pipe(
    filter((r: any): r is HttpResponse<any> => r instanceof HttpResponse),
    map((r: HttpResponse<any>) => {
      return r as StrictHttpResponse<FinanceInfoForWeekInFinanceScreenResponse>;
    })
  );
}

getFinanceInfoForWeek.PATH = '/attendance/finance-info';
