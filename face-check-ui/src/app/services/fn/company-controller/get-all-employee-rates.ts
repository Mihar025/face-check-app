/* tslint:disable */
/* eslint-disable */
/* Code generated by ng-openapi-gen DO NOT EDIT. */

import { HttpClient, HttpContext, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import { StrictHttpResponse } from '../../strict-http-response';
import { RequestBuilder } from '../../request-builder';

import { EmployeeSalaryResponse } from '../../models/employee-salary-response';

export interface GetAllEmployeeRates$Params {
}

export function getAllEmployeeRates(http: HttpClient, rootUrl: string, params?: GetAllEmployeeRates$Params, context?: HttpContext): Observable<StrictHttpResponse<Array<EmployeeSalaryResponse>>> {
  const rb = new RequestBuilder(rootUrl, getAllEmployeeRates.PATH, 'get');
  if (params) {
  }

  return http.request(
    rb.build({ responseType: 'json', accept: 'application/json', context })
  ).pipe(
    filter((r: any): r is HttpResponse<any> => r instanceof HttpResponse),
    map((r: HttpResponse<any>) => {
      return r as StrictHttpResponse<Array<EmployeeSalaryResponse>>;
    })
  );
}

getAllEmployeeRates.PATH = '/company/employee-rates';
