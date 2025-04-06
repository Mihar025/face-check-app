/* tslint:disable */
/* eslint-disable */
/* Code generated by ng-openapi-gen DO NOT EDIT. */

import { HttpClient, HttpContext, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import { StrictHttpResponse } from '../../strict-http-response';
import { RequestBuilder } from '../../request-builder';

import { UpdateWorkSiteAddress } from '../../models/update-work-site-address';
import { WorkSiteUpdateAddressResponse } from '../../models/work-site-update-address-response';

export interface UpdateAddress$Params {
  workSiteId: number;
      body: UpdateWorkSiteAddress
}

export function updateAddress(http: HttpClient, rootUrl: string, params: UpdateAddress$Params, context?: HttpContext): Observable<StrictHttpResponse<WorkSiteUpdateAddressResponse>> {
  const rb = new RequestBuilder(rootUrl, updateAddress.PATH, 'patch');
  if (params) {
    rb.path('workSiteId', params.workSiteId, {});
    rb.body(params.body, 'application/json');
  }

  return http.request(
    rb.build({ responseType: 'json', accept: 'application/json', context })
  ).pipe(
    filter((r: any): r is HttpResponse<any> => r instanceof HttpResponse),
    map((r: HttpResponse<any>) => {
      return r as StrictHttpResponse<WorkSiteUpdateAddressResponse>;
    })
  );
}

updateAddress.PATH = '/workSite/{workSiteId}/address';
