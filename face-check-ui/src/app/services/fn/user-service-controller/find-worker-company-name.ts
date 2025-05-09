/* tslint:disable */
/* eslint-disable */
/* Code generated by ng-openapi-gen DO NOT EDIT. */

import { HttpClient, HttpContext, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import { StrictHttpResponse } from '../../strict-http-response';
import { RequestBuilder } from '../../request-builder';

import { UserCompanyNameInformation } from '../../models/user-company-name-information';

export interface FindWorkerCompanyName$Params {
}

export function findWorkerCompanyName(http: HttpClient, rootUrl: string, params?: FindWorkerCompanyName$Params, context?: HttpContext): Observable<StrictHttpResponse<UserCompanyNameInformation>> {
  const rb = new RequestBuilder(rootUrl, findWorkerCompanyName.PATH, 'get');
  if (params) {
  }

  return http.request(
    rb.build({ responseType: 'json', accept: 'application/json', context })
  ).pipe(
    filter((r: any): r is HttpResponse<any> => r instanceof HttpResponse),
    map((r: HttpResponse<any>) => {
      return r as StrictHttpResponse<UserCompanyNameInformation>;
    })
  );
}

findWorkerCompanyName.PATH = '/user/company';
