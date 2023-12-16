import { RouterModule, Routes } from "@angular/router";
import { HomeComponent } from "./components/home/home.component";
import { RegisterComponent } from "./components/register/register.component";
import { DetailProductComponent } from "./components/detail-product/detail-product.component";
import { LoginComponent } from "./components/login/login.component";
import { OrderComponent } from "./components/order/order.component";
import { OrderConfirmComponent } from "./components/order-confirm/order-confirm.component";
import { NgModel } from "@angular/forms";
import { NgModule } from "@angular/core";


const routes: Routes = [
    {path: '', component: HomeComponent},
    {path: 'login', component: LoginComponent},
    {path: 'register', component: RegisterComponent},
    {path: 'products/:id', component: DetailProductComponent},
    {path: 'orders', component: OrderComponent},
    {path: 'orders/:id', component: OrderConfirmComponent},

];
@NgModule({
    imports: [RouterModule.forRoot(routes)],
    exports: [RouterModule],
})

export class AppRoutingModule{}