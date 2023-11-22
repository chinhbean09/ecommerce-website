import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { HomeComponent } from './home/home.component';
import { HeaderComponent } from './header/header.component';
import { FooterComponent } from './footer/footer.component';

@NgModule({
  declarations: [    
    HomeComponent,
    HeaderComponent,FooterComponent
  ],
  imports: [
    BrowserModule
  ],

  providers: [],
  bootstrap: [
    HomeComponent
  ]
})
export class AppModule { }
