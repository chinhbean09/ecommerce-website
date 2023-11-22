import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { HomeComponent } from './home/home.component';
import { HeaderComponent } from './header/header.component';
import { FooterComponent } from './footer/footer.component';
import { DetailProductComponent } from './detail-product/detail-product.component';

@NgModule({
  declarations: [    
    HomeComponent,
    HeaderComponent,FooterComponent,DetailProductComponent
  ],
  imports: [
    BrowserModule
  ],

  providers: [],
  bootstrap: [
    // HomeComponent,
    DetailProductComponent
  ]
})
export class AppModule { }
