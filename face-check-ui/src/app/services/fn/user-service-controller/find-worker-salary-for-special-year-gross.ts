/* tslint:disable */
/* eslint-disable */
/* Code generated by ng-openapi-gen DO NOT EDIT. */

import { HttpClient, HttpContext, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import { StrictHttpResponse } from '../../strict-http-response';
import { RequestBuilder } from '../../request-builder';


export interface FindWorkerSalaryForSpecialYearGross$Params {
  year: number;
}

export function findWorkerSalaryForSpecialYearGross(http: HttpClient, rootUrl: string, params: FindWorkerSalaryForSpecialYearGross$Params, context?: HttpContext): Observable<StrictHttpResponse<number>> {
  const rb = new RequestBuilder(rootUrl, findWorkerSalaryForSpecialYearGross.PATH, 'get');
  if (params) {
    rb.query('year', params.year, {});
  }

  return http.request(
    rb.build({ responseType: 'json', accept: 'application/json', context })
  ).pipe(
    filter((r: any): r is HttpResponse<any> => r instanceof HttpResponse),
    map((r: HttpResponse<any>) => {
      return (r as HttpResponse<any>).clone({ body: parseFloat(String((r as HttpResponse<any>).body)) }) as StrictHttpResponse<number>;
    })
  );
}

findWorkerSalaryForSpecialYearGross.PATH = '/user/salary/year/gross/special';
