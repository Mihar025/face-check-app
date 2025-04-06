/* tslint:disable */
/* eslint-disable */
/* Code generated by ng-openapi-gen DO NOT EDIT. */

import { HttpClient, HttpContext, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import { StrictHttpResponse } from '../../strict-http-response';
import { RequestBuilder } from '../../request-builder';

import { CompanyTaxCalculationRequest } from '../../models/company-tax-calculation-request';
import { CompanyTaxCalculationResponse } from '../../models/company-tax-calculation-response';

export interface CalculateTaxes$Params {
      body: CompanyTaxCalculationRequest
}

export function calculateTaxes(http: HttpClient, rootUrl: string, params: CalculateTaxes$Params, context?: HttpContext): Observable<StrictHttpResponse<CompanyTaxCalculationResponse>> {
  const rb = new RequestBuilder(rootUrl, calculateTaxes.PATH, 'post');
  if (params) {
    rb.body(params.body, 'application/json');
  }

  return http.request(
    rb.build({ responseType: 'json', accept: 'application/json', context })
  ).pipe(
    filter((r: any): r is HttpResponse<any> => r instanceof HttpResponse),
    map((r: HttpResponse<any>) => {
      return r as StrictHttpResponse<CompanyTaxCalculationResponse>;
    })
  );
}

calculateTaxes.PATH = '/company/calculate-taxes';
