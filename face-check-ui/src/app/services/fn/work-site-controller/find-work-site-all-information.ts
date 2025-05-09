/* tslint:disable */
/* eslint-disable */
/* Code generated by ng-openapi-gen DO NOT EDIT. */

import { HttpClient, HttpContext, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import { StrictHttpResponse } from '../../strict-http-response';
import { RequestBuilder } from '../../request-builder';

import { WorkSiteAllInformationResponse } from '../../models/work-site-all-information-response';

export interface FindWorkSiteAllInformation$Params {
  workSiteId: number;
}

export function findWorkSiteAllInformation(http: HttpClient, rootUrl: string, params: FindWorkSiteAllInformation$Params, context?: HttpContext): Observable<StrictHttpResponse<WorkSiteAllInformationResponse>> {
  const rb = new RequestBuilder(rootUrl, findWorkSiteAllInformation.PATH, 'get');
  if (params) {
    rb.path('workSiteId', params.workSiteId, {});
  }

  return http.request(
    rb.build({ responseType: 'json', accept: 'application/json', context })
  ).pipe(
    filter((r: any): r is HttpResponse<any> => r instanceof HttpResponse),
    map((r: HttpResponse<any>) => {
      return r as StrictHttpResponse<WorkSiteAllInformationResponse>;
    })
  );
}

findWorkSiteAllInformation.PATH = '/workSite/{workSiteId}/all-information';
