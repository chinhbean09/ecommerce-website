import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { HomeComponent } from './components/home/home.component';
import { HeaderComponent } from './components/header/header.component';
import { FooterComponent } from './components/footer/footer.component';
import { DetailProductComponent } from './components/detail-product/detail-product.component';
import { OrderComponent } from './components/order/order.component';
import { OrderConfirmComponent } from './components/detail-order/order.detail.component';
import { LoginComponent } from './components/login/login.component';
import { RegisterComponent } from './components/register/register.component';
import { FormsModule } from '@angular/forms';
import { HttpClientModule, HTTP_INTERCEPTORS } from '@angular/common/http';
import { TokenInterceptor } from './interceptors/token.interceptor';
import { ReactiveFormsModule } from '@angular/forms';
import { AppComponent } from './app/app.component';
import { AppRoutingModule } from './app-routing.module';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';

@NgModule({
  declarations: [    
    HomeComponent,
    HeaderComponent,
    FooterComponent,
    DetailProductComponent,
    OrderComponent,
    OrderConfirmComponent,
    LoginComponent,
    RegisterComponent,
    AppComponent,
  ],
  imports: [
    BrowserModule,
    FormsModule,
    HttpClientModule,
    ReactiveFormsModule,
    AppRoutingModule,
    NgbModule

  ],
  providers: [
    //cho thêm đối tượng http_interceptors và nó sử dụng class TokenInterceptor mới tạo
    {
      provide: HTTP_INTERCEPTORS,
      useClass: TokenInterceptor,
      multi: true,
    },
  ],
  bootstrap: [
    // HomeComponent,
    // DetailProductComponent,
        // OrderComponent,
    // OrderConfirmComponent,
    // LoginComponent,
    // RegisterComponent
    AppComponent

  ]
})
export class AppModule { }
