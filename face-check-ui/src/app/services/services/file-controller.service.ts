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

import { deleteFile } from '../fn/file-controller/delete-file';
import { DeleteFile$Params } from '../fn/file-controller/delete-file';
import { getFile } from '../fn/file-controller/get-file';
import { GetFile$Params } from '../fn/file-controller/get-file';
import { uploadFile } from '../fn/file-controller/upload-file';
import { UploadFile$Params } from '../fn/file-controller/upload-file';
import { uploadPhoto } from '../fn/file-controller/upload-photo';
import { UploadPhoto$Params } from '../fn/file-controller/upload-photo';

@Injectable({ providedIn: 'root' })
export class FileControllerService extends BaseService {
  constructor(config: ApiConfiguration, http: HttpClient) {
    super(config, http);
  }

  /** Path part for operation `uploadFile()` */
  static readonly UploadFilePath = '/files/upload';

  /**
   * This method provides access to the full `HttpResponse`, allowing access to response headers.
   * To access only the response body, use `uploadFile()` instead.
   *
   * This method doesn't expect any request body.
   */
  uploadFile$Response(params: UploadFile$Params, context?: HttpContext): Observable<StrictHttpResponse<string>> {
    return uploadFile(this.http, this.rootUrl, params, context);
  }

  /**
   * This method provides access only to the response body.
   * To access the full response (for headers, for example), `uploadFile$Response()` instead.
   *
   * This method doesn't expect any request body.
   */
  uploadFile(params: UploadFile$Params, context?: HttpContext): Observable<string> {
    return this.uploadFile$Response(params, context).pipe(
      map((r: StrictHttpResponse<string>): string => r.body)
    );
  }

  /** Path part for operation `uploadPhoto()` */
  static readonly UploadPhotoPath = '/files/upload/photo';

  /**
   * This method provides access to the full `HttpResponse`, allowing access to response headers.
   * To access only the response body, use `uploadPhoto()` instead.
   *
   * This method sends `application/json` and handles request body of type `application/json`.
   */
  uploadPhoto$Response(params: UploadPhoto$Params, context?: HttpContext): Observable<StrictHttpResponse<string>> {
    return uploadPhoto(this.http, this.rootUrl, params, context);
  }

  /**
   * This method provides access only to the response body.
   * To access the full response (for headers, for example), `uploadPhoto$Response()` instead.
   *
   * This method sends `application/json` and handles request body of type `application/json`.
   */
  uploadPhoto(params: UploadPhoto$Params, context?: HttpContext): Observable<string> {
    return this.uploadPhoto$Response(params, context).pipe(
      map((r: StrictHttpResponse<string>): string => r.body)
    );
  }

  /** Path part for operation `getFile()` */
  static readonly GetFilePath = '/files/{fileName}';

  /**
   * This method provides access to the full `HttpResponse`, allowing access to response headers.
   * To access only the response body, use `getFile()` instead.
   *
   * This method doesn't expect any request body.
   */
  getFile$Response(params: GetFile$Params, context?: HttpContext): Observable<StrictHttpResponse<Array<string>>> {
    return getFile(this.http, this.rootUrl, params, context);
  }

  /**
   * This method provides access only to the response body.
   * To access the full response (for headers, for example), `getFile$Response()` instead.
   *
   * This method doesn't expect any request body.
   */
  getFile(params: GetFile$Params, context?: HttpContext): Observable<Array<string>> {
    return this.getFile$Response(params, context).pipe(
      map((r: StrictHttpResponse<Array<string>>): Array<string> => r.body)
    );
  }

  /** Path part for operation `deleteFile()` */
  static readonly DeleteFilePath = '/files/{fileName}';

  /**
   * This method provides access to the full `HttpResponse`, allowing access to response headers.
   * To access only the response body, use `deleteFile()` instead.
   *
   * This method doesn't expect any request body.
   */
  deleteFile$Response(params: DeleteFile$Params, context?: HttpContext): Observable<StrictHttpResponse<void>> {
    return deleteFile(this.http, this.rootUrl, params, context);
  }

  /**
   * This method provides access only to the response body.
   * To access the full response (for headers, for example), `deleteFile$Response()` instead.
   *
   * This method doesn't expect any request body.
   */
  deleteFile(params: DeleteFile$Params, context?: HttpContext): Observable<void> {
    return this.deleteFile$Response(params, context).pipe(
      map((r: StrictHttpResponse<void>): void => r.body)
    );
  }

}
