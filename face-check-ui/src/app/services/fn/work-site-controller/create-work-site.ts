/* tslint:disable */
/* eslint-disable */
/* Code generated by ng-openapi-gen DO NOT EDIT. */

import { HttpClient, HttpContext, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import { StrictHttpResponse } from '../../strict-http-response';
import { RequestBuilder } from '../../request-builder';

import { WorkSiteRequest } from '../../models/work-site-request';
import { WorkSiteResponse } from '../../models/work-site-response';

export interface CreateWorkSite$Params {
      body: WorkSiteRequest
}

export function createWorkSite(http: HttpClient, rootUrl: string, params: CreateWorkSite$Params, context?: HttpContext): Observable<StrictHttpResponse<WorkSiteResponse>> {
  const rb = new RequestBuilder(rootUrl, createWorkSite.PATH, 'post');
  if (params) {
    rb.body(params.body, 'application/json');
  }

  return http.request(
    rb.build({ responseType: 'json', accept: 'application/json', context })
  ).pipe(
    filter((r: any): r is HttpResponse<any> => r instanceof HttpResponse),
    map((r: HttpResponse<any>) => {
      return r as StrictHttpResponse<WorkSiteResponse>;
    })
  );
}

createWorkSite.PATH = '/workSite';
