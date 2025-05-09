/* tslint:disable */
/* eslint-disable */
/* Code generated by ng-openapi-gen DO NOT EDIT. */

import { HttpClient, HttpContext, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import { StrictHttpResponse } from '../../strict-http-response';
import { RequestBuilder } from '../../request-builder';

import { UserHomeAddressResponse } from '../../models/user-home-address-response';

export interface FindWorkerHomeAddress$Params {
}

export function findWorkerHomeAddress(http: HttpClient, rootUrl: string, params?: FindWorkerHomeAddress$Params, context?: HttpContext): Observable<StrictHttpResponse<UserHomeAddressResponse>> {
  const rb = new RequestBuilder(rootUrl, findWorkerHomeAddress.PATH, 'get');
  if (params) {
  }

  return http.request(
    rb.build({ responseType: 'json', accept: 'application/json', context })
  ).pipe(
    filter((r: any): r is HttpResponse<any> => r instanceof HttpResponse),
    map((r: HttpResponse<any>) => {
      return r as StrictHttpResponse<UserHomeAddressResponse>;
    })
  );
}

findWorkerHomeAddress.PATH = '/user/address';
