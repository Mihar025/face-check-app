/* tslint:disable */
/* eslint-disable */
/* Code generated by ng-openapi-gen DO NOT EDIT. */

import { NgModule, ModuleWithProviders, SkipSelf, Optional } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { ApiConfiguration, ApiConfigurationParams } from './api-configuration';

import { UserServiceControllerService } from './services/user-service-controller.service';
import { CompanyControllerService } from './services/company-controller.service';
import { AuthenticationService } from './services/authentication.service';
import { AdminControllerService } from './services/admin-controller.service';
import { WorkSiteControllerService } from './services/work-site-controller.service';
import { WorkScheduleControllerService } from './services/work-schedule-controller.service';
import { FileControllerService } from './services/file-controller.service';
import { WorkerAttendanceControllerService } from './services/worker-attendance-controller.service';

/**
 * Module that provides all services and configuration.
 */
@NgModule({
  imports: [],
  exports: [],
  declarations: [],
  providers: [
    UserServiceControllerService,
    CompanyControllerService,
    AuthenticationService,
    AdminControllerService,
    WorkSiteControllerService,
    WorkScheduleControllerService,
    FileControllerService,
    WorkerAttendanceControllerService,
    ApiConfiguration
  ],
})
export class ApiModule {
  static forRoot(params: ApiConfigurationParams): ModuleWithProviders<ApiModule> {
    return {
      ngModule: ApiModule,
      providers: [
        {
          provide: ApiConfiguration,
          useValue: params
        }
      ]
    }
  }

  constructor( 
    @Optional() @SkipSelf() parentModule: ApiModule,
    @Optional() http: HttpClient
  ) {
    if (parentModule) {
      throw new Error('ApiModule is already loaded. Import in your base AppModule only.');
    }
    if (!http) {
      throw new Error('You need to import the HttpClientModule in your AppModule! \n' +
      'See also https://github.com/angular/angular/issues/20575');
    }
  }
}
