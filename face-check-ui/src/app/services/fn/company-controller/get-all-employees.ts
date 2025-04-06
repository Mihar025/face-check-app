/* tslint:disable */
/* eslint-disable */
/* Code generated by ng-openapi-gen DO NOT EDIT. */

import { HttpClient, HttpContext, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import { StrictHttpResponse } from '../../strict-http-response';
import { RequestBuilder } from '../../request-builder';

import { PageResponseRelatedUserInCompanyResponse } from '../../models/page-response-related-user-in-company-response';

export interface GetAllEmployees$Params {
  companyId: number;
  page?: number;
  size?: number;
}

export function getAllEmployees(http: HttpClient, rootUrl: string, params: GetAllEmployees$Params, context?: HttpContext): Observable<StrictHttpResponse<PageResponseRelatedUserInCompanyResponse>> {
  const rb = new RequestBuilder(rootUrl, getAllEmployees.PATH, 'get');
  if (params) {
    rb.path('companyId', params.companyId, {});
    rb.query('page', params.page, {});
    rb.query('size', params.size, {});
  }

  return http.request(
    rb.build({ responseType: 'json', accept: 'application/json', context })
  ).pipe(
    filter((r: any): r is HttpResponse<any> => r instanceof HttpResponse),
    map((r: HttpResponse<any>) => {
      return r as StrictHttpResponse<PageResponseRelatedUserInCompanyResponse>;
    })
  );
}

getAllEmployees.PATH = '/company/{companyId}/employees';
