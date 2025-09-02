import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { MainPageModule } from "./modules/main-page/main-page.module";
import { HTTP_INTERCEPTORS, HttpClientModule } from "@angular/common/http";
import { FormsModule, ReactiveFormsModule } from "@angular/forms";
import { AuthInterceptor } from "./modules/main-page/additionalServices/auth-interceptor";
import { SharedModule } from './modules/shared/shared.module'; // Импортируем SharedModule

@NgModule({
  declarations: [
    AppComponent,
    // AdminSidebarComponent убираем отсюда
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    HttpClientModule,
    FormsModule,
    ReactiveFormsModule,
    MainPageModule,
    SharedModule // Добавляем SharedModule
  ],
  providers: [
    {
      provide: HTTP_INTERCEPTORS,
      useClass: AuthInterceptor,
      multi: true
    }
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
