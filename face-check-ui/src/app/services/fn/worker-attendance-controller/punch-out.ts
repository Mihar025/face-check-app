/* tslint:disable */
/* eslint-disable */
/* Code generated by ng-openapi-gen DO NOT EDIT. */

import { HttpClient, HttpContext, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import { StrictHttpResponse } from '../../strict-http-response';
import { RequestBuilder } from '../../request-builder';

import { PunchOutRequest } from '../../models/punch-out-request';
import { PunchOutResponse } from '../../models/punch-out-response';

export interface PunchOut$Params {
      body: PunchOutRequest
}

export function punchOut(http: HttpClient, rootUrl: string, params: PunchOut$Params, context?: HttpContext): Observable<StrictHttpResponse<PunchOutResponse>> {
  const rb = new RequestBuilder(rootUrl, punchOut.PATH, 'post');
  if (params) {
    rb.body(params.body, 'application/json');
  }

  return http.request(
    rb.build({ responseType: 'json', accept: 'application/json', context })
  ).pipe(
    filter((r: any): r is HttpResponse<any> => r instanceof HttpResponse),
    map((r: HttpResponse<any>) => {
      return r as StrictHttpResponse<PunchOutResponse>;
    })
  );
}

punchOut.PATH = '/attendance/punch-out';
