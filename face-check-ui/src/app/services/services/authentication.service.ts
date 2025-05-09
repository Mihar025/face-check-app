/* tslint:disable */
/* eslint-disable */
/* Code generated by ng-openapi-gen DO NOT EDIT. */

import { HttpClient, HttpContext } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

import { BaseService } from '../base-service';
import { ApiConfiguration } from '../api-configuration';
import { StrictHttpResponse } from '../strict-http-response';

import { authenticate } from '../fn/authentication/authenticate';
import { Authenticate$Params } from '../fn/authentication/authenticate';
import { confirm } from '../fn/authentication/confirm';
import { Confirm$Params } from '../fn/authentication/confirm';
import { register } from '../fn/authentication/register';
import { Register$Params } from '../fn/authentication/register';
import { registerAdmin } from '../fn/authentication/register-admin';
import { RegisterAdmin$Params } from '../fn/authentication/register-admin';
import { registerCompany } from '../fn/authentication/register-company';
import { RegisterCompany$Params } from '../fn/authentication/register-company';
import { registerForeman } from '../fn/authentication/register-foreman';
import { RegisterForeman$Params } from '../fn/authentication/register-foreman';
import { resetPassword } from '../fn/authentication/reset-password';
import { ResetPassword$Params } from '../fn/authentication/reset-password';
import { sendResetCode } from '../fn/authentication/send-reset-code';
import { SendResetCode$Params } from '../fn/authentication/send-reset-code';
import { setPaymentData } from '../fn/authentication/set-payment-data';
import { SetPaymentData$Params } from '../fn/authentication/set-payment-data';
import { SettingsEmailResponse } from '../models/settings-email-response';
import { SettingsPhoneNumberResponse } from '../models/settings-phone-number-response';
import { updateEmail1 } from '../fn/authentication/update-email-1';
import { UpdateEmail1$Params } from '../fn/authentication/update-email-1';
import { updatePassword1 } from '../fn/authentication/update-password-1';
import { UpdatePassword1$Params } from '../fn/authentication/update-password-1';
import { updatePhoneNumber } from '../fn/authentication/update-phone-number';
import { UpdatePhoneNumber$Params } from '../fn/authentication/update-phone-number';
import { verifyCode } from '../fn/authentication/verify-code';
import { VerifyCode$Params } from '../fn/authentication/verify-code';

@Injectable({ providedIn: 'root' })
export class AuthenticationService extends BaseService {
  constructor(config: ApiConfiguration, http: HttpClient) {
    super(config, http);
  }

  /** Path part for operation `updatePhoneNumber()` */
  static readonly UpdatePhoneNumberPath = '/auth/phone';

  /**
   * This method provides access to the full `HttpResponse`, allowing access to response headers.
   * To access only the response body, use `updatePhoneNumber()` instead.
   *
   * This method sends `application/json` and handles request body of type `application/json`.
   */
  updatePhoneNumber$Response(params: UpdatePhoneNumber$Params, context?: HttpContext): Observable<StrictHttpResponse<SettingsPhoneNumberResponse>> {
    return updatePhoneNumber(this.http, this.rootUrl, params, context);
  }

  /**
   * This method provides access only to the response body.
   * To access the full response (for headers, for example), `updatePhoneNumber$Response()` instead.
   *
   * This method sends `application/json` and handles request body of type `application/json`.
   */
  updatePhoneNumber(params: UpdatePhoneNumber$Params, context?: HttpContext): Observable<SettingsPhoneNumberResponse> {
    return this.updatePhoneNumber$Response(params, context).pipe(
      map((r: StrictHttpResponse<SettingsPhoneNumberResponse>): SettingsPhoneNumberResponse => r.body)
    );
  }

  /** Path part for operation `updatePassword1()` */
  static readonly UpdatePassword1Path = '/auth/password';

  /**
   * This method provides access to the full `HttpResponse`, allowing access to response headers.
   * To access only the response body, use `updatePassword1()` instead.
   *
   * This method sends `application/json` and handles request body of type `application/json`.
   */
  updatePassword1$Response(params: UpdatePassword1$Params, context?: HttpContext): Observable<StrictHttpResponse<void>> {
    return updatePassword1(this.http, this.rootUrl, params, context);
  }

  /**
   * This method provides access only to the response body.
   * To access the full response (for headers, for example), `updatePassword1$Response()` instead.
   *
   * This method sends `application/json` and handles request body of type `application/json`.
   */
  updatePassword1(params: UpdatePassword1$Params, context?: HttpContext): Observable<void> {
    return this.updatePassword1$Response(params, context).pipe(
      map((r: StrictHttpResponse<void>): void => r.body)
    );
  }

  /** Path part for operation `updateEmail1()` */
  static readonly UpdateEmail1Path = '/auth/email';

  /**
   * This method provides access to the full `HttpResponse`, allowing access to response headers.
   * To access only the response body, use `updateEmail1()` instead.
   *
   * This method sends `application/json` and handles request body of type `application/json`.
   */
  updateEmail1$Response(params: UpdateEmail1$Params, context?: HttpContext): Observable<StrictHttpResponse<SettingsEmailResponse>> {
    return updateEmail1(this.http, this.rootUrl, params, context);
  }

  /**
   * This method provides access only to the response body.
   * To access the full response (for headers, for example), `updateEmail1$Response()` instead.
   *
   * This method sends `application/json` and handles request body of type `application/json`.
   */
  updateEmail1(params: UpdateEmail1$Params, context?: HttpContext): Observable<SettingsEmailResponse> {
    return this.updateEmail1$Response(params, context).pipe(
      map((r: StrictHttpResponse<SettingsEmailResponse>): SettingsEmailResponse => r.body)
    );
  }

  /** Path part for operation `register()` */
  static readonly RegisterPath = '/auth/register';

  /**
   * This method provides access to the full `HttpResponse`, allowing access to response headers.
   * To access only the response body, use `register()` instead.
   *
   * This method sends `application/json` and handles request body of type `application/json`.
   */
  register$Response(params: Register$Params, context?: HttpContext): Observable<StrictHttpResponse<{
}>> {
    return register(this.http, this.rootUrl, params, context);
  }

  /**
   * This method provides access only to the response body.
   * To access the full response (for headers, for example), `register$Response()` instead.
   *
   * This method sends `application/json` and handles request body of type `application/json`.
   */
  register(params: Register$Params, context?: HttpContext): Observable<{
}> {
    return this.register$Response(params, context).pipe(
      map((r: StrictHttpResponse<{
}>): {
} => r.body)
    );
  }

  /** Path part for operation `registerForeman()` */
  static readonly RegisterForemanPath = '/auth/register/foreman';

  /**
   * This method provides access to the full `HttpResponse`, allowing access to response headers.
   * To access only the response body, use `registerForeman()` instead.
   *
   * This method sends `application/json` and handles request body of type `application/json`.
   */
  registerForeman$Response(params: RegisterForeman$Params, context?: HttpContext): Observable<StrictHttpResponse<{
}>> {
    return registerForeman(this.http, this.rootUrl, params, context);
  }

  /**
   * This method provides access only to the response body.
   * To access the full response (for headers, for example), `registerForeman$Response()` instead.
   *
   * This method sends `application/json` and handles request body of type `application/json`.
   */
  registerForeman(params: RegisterForeman$Params, context?: HttpContext): Observable<{
}> {
    return this.registerForeman$Response(params, context).pipe(
      map((r: StrictHttpResponse<{
}>): {
} => r.body)
    );
  }

  /** Path part for operation `registerCompany()` */
  static readonly RegisterCompanyPath = '/auth/register/company';

  /**
   * This method provides access to the full `HttpResponse`, allowing access to response headers.
   * To access only the response body, use `registerCompany()` instead.
   *
   * This method sends `application/json` and handles request body of type `application/json`.
   */
  registerCompany$Response(params: RegisterCompany$Params, context?: HttpContext): Observable<StrictHttpResponse<void>> {
    return registerCompany(this.http, this.rootUrl, params, context);
  }

  /**
   * This method provides access only to the response body.
   * To access the full response (for headers, for example), `registerCompany$Response()` instead.
   *
   * This method sends `application/json` and handles request body of type `application/json`.
   */
  registerCompany(params: RegisterCompany$Params, context?: HttpContext): Observable<void> {
    return this.registerCompany$Response(params, context).pipe(
      map((r: StrictHttpResponse<void>): void => r.body)
    );
  }

  /** Path part for operation `registerAdmin()` */
  static readonly RegisterAdminPath = '/auth/register/admin';

  /**
   * This method provides access to the full `HttpResponse`, allowing access to response headers.
   * To access only the response body, use `registerAdmin()` instead.
   *
   * This method sends `application/json` and handles request body of type `application/json`.
   */
  registerAdmin$Response(params: RegisterAdmin$Params, context?: HttpContext): Observable<StrictHttpResponse<{
}>> {
    return registerAdmin(this.http, this.rootUrl, params, context);
  }

  /**
   * This method provides access only to the response body.
   * To access the full response (for headers, for example), `registerAdmin$Response()` instead.
   *
   * This method sends `application/json` and handles request body of type `application/json`.
   */
  registerAdmin(params: RegisterAdmin$Params, context?: HttpContext): Observable<{
}> {
    return this.registerAdmin$Response(params, context).pipe(
      map((r: StrictHttpResponse<{
}>): {
} => r.body)
    );
  }

  /** Path part for operation `resetPassword()` */
  static readonly ResetPasswordPath = '/auth/forgot-password';

  /**
   * This method provides access to the full `HttpResponse`, allowing access to response headers.
   * To access only the response body, use `resetPassword()` instead.
   *
   * This method sends `application/json` and handles request body of type `application/json`.
   */
  resetPassword$Response(params: ResetPassword$Params, context?: HttpContext): Observable<StrictHttpResponse<void>> {
    return resetPassword(this.http, this.rootUrl, params, context);
  }

  /**
   * This method provides access only to the response body.
   * To access the full response (for headers, for example), `resetPassword$Response()` instead.
   *
   * This method sends `application/json` and handles request body of type `application/json`.
   */
  resetPassword(params: ResetPassword$Params, context?: HttpContext): Observable<void> {
    return this.resetPassword$Response(params, context).pipe(
      map((r: StrictHttpResponse<void>): void => r.body)
    );
  }

  /** Path part for operation `verifyCode()` */
  static readonly VerifyCodePath = '/auth/forgot-password/verify';

  /**
   * This method provides access to the full `HttpResponse`, allowing access to response headers.
   * To access only the response body, use `verifyCode()` instead.
   *
   * This method sends `application/json` and handles request body of type `application/json`.
   */
  verifyCode$Response(params: VerifyCode$Params, context?: HttpContext): Observable<StrictHttpResponse<void>> {
    return verifyCode(this.http, this.rootUrl, params, context);
  }

  /**
   * This method provides access only to the response body.
   * To access the full response (for headers, for example), `verifyCode$Response()` instead.
   *
   * This method sends `application/json` and handles request body of type `application/json`.
   */
  verifyCode(params: VerifyCode$Params, context?: HttpContext): Observable<void> {
    return this.verifyCode$Response(params, context).pipe(
      map((r: StrictHttpResponse<void>): void => r.body)
    );
  }

  /** Path part for operation `sendResetCode()` */
  static readonly SendResetCodePath = '/auth/forgot-password/email';

  /**
   * This method provides access to the full `HttpResponse`, allowing access to response headers.
   * To access only the response body, use `sendResetCode()` instead.
   *
   * This method sends `application/json` and handles request body of type `application/json`.
   */
  sendResetCode$Response(params: SendResetCode$Params, context?: HttpContext): Observable<StrictHttpResponse<void>> {
    return sendResetCode(this.http, this.rootUrl, params, context);
  }

  /**
   * This method provides access only to the response body.
   * To access the full response (for headers, for example), `sendResetCode$Response()` instead.
   *
   * This method sends `application/json` and handles request body of type `application/json`.
   */
  sendResetCode(params: SendResetCode$Params, context?: HttpContext): Observable<void> {
    return this.sendResetCode$Response(params, context).pipe(
      map((r: StrictHttpResponse<void>): void => r.body)
    );
  }

  /** Path part for operation `setPaymentData()` */
  static readonly SetPaymentDataPath = '/auth/employees/{employeeId}/payment';

  /**
   * This method provides access to the full `HttpResponse`, allowing access to response headers.
   * To access only the response body, use `setPaymentData()` instead.
   *
   * This method sends `application/json` and handles request body of type `application/json`.
   */
  setPaymentData$Response(params: SetPaymentData$Params, context?: HttpContext): Observable<StrictHttpResponse<void>> {
    return setPaymentData(this.http, this.rootUrl, params, context);
  }

  /**
   * This method provides access only to the response body.
   * To access the full response (for headers, for example), `setPaymentData$Response()` instead.
   *
   * This method sends `application/json` and handles request body of type `application/json`.
   */
  setPaymentData(params: SetPaymentData$Params, context?: HttpContext): Observable<void> {
    return this.setPaymentData$Response(params, context).pipe(
      map((r: StrictHttpResponse<void>): void => r.body)
    );
  }

  /** Path part for operation `authenticate()` */
  static readonly AuthenticatePath = '/auth/authenticate';

  /**
   * This method provides access to the full `HttpResponse`, allowing access to response headers.
   * To access only the response body, use `authenticate()` instead.
   *
   * This method sends `application/json` and handles request body of type `application/json`.
   */
  authenticate$Response(params: Authenticate$Params, context?: HttpContext): Observable<StrictHttpResponse<{
}>> {
    return authenticate(this.http, this.rootUrl, params, context);
  }

  /**
   * This method provides access only to the response body.
   * To access the full response (for headers, for example), `authenticate$Response()` instead.
   *
   * This method sends `application/json` and handles request body of type `application/json`.
   */
  authenticate(params: Authenticate$Params, context?: HttpContext): Observable<{
}> {
    return this.authenticate$Response(params, context).pipe(
      map((r: StrictHttpResponse<{
}>): {
} => r.body)
    );
  }

  /** Path part for operation `confirm()` */
  static readonly ConfirmPath = '/auth/activate-account';

  /**
   * This method provides access to the full `HttpResponse`, allowing access to response headers.
   * To access only the response body, use `confirm()` instead.
   *
   * This method doesn't expect any request body.
   */
  confirm$Response(params: Confirm$Params, context?: HttpContext): Observable<StrictHttpResponse<void>> {
    return confirm(this.http, this.rootUrl, params, context);
  }

  /**
   * This method provides access only to the response body.
   * To access the full response (for headers, for example), `confirm$Response()` instead.
   *
   * This method doesn't expect any request body.
   */
  confirm(params: Confirm$Params, context?: HttpContext): Observable<void> {
    return this.confirm$Response(params, context).pipe(
      map((r: StrictHttpResponse<void>): void => r.body)
    );
  }

}
