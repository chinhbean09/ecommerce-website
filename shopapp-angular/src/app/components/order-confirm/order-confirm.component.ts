import { environment } from './../../environments/environment';
import { Component, OnInit } from '@angular/core';
import { Product } from 'src/app/models/product';
import { CartService } from 'src/app/services/cart.service';
import { ProductService } from 'src/app/services/product.service';

@Component({
  selector: 'app-order-confirm',
  templateUrl: './order-confirm.component.html',
  styleUrls: ['./order-confirm.component.scss']
})
export class OrderConfirmComponent implements OnInit {
orderResponse: OrderResponse = {
id:0,
user_id: 0,
fullname:'',
email:'',
    phone_number:'',
    address:'',
    note:'',
    order_date: new Date(),
    status: '',
    total_money:0,
    shipping_method:'',
    shipping_address:'',
    shipping_date: new Date(),
    payment_method: '',
    order_details:[]

}

constructor(
  private orderService: OrderService,
){}

ngOnInit(): void {
  this.getOrderDetails();      
    }

    getOrderDetails():void {

      const orderId = 1;
      this.orderService.getOrderById(orderId).subscribe({
        next: (response: any) => {
          
           this.orderResponse.id = response.id;
           this.orderResponse.user_id = response.user_id;
           this.orderResponse.fullname = response.fullname;
           this.orderResponse.email = response.email;
           this.orderResponse.phone_number = response.phone_number;
           this.orderResponse.address = response.address;
           this.orderResponse.note = response.note;
           this.orderResponse.order_date = new Date(
            response.order_date[0],
            response.order_date[1] - 1,
            response.order_date[2]
           );
           this.orderResponse.order_details = response.order_details;
           .map(order_detail: OrderDetail) => {
            order_detail.product.thumbnail = `${environment.apiBaseUrl}/products/images/${order_detail.product.thumbnail}`;
            return order_detail;
           };

           this.orderResponse.paymend_method = response.paymend_method;
           this.orderResponse.shipping_date = new Date(
            response.order_date[0],
            response.order_date[1] - 1,
            response.order_date[2]
           );

           this.orderResponse.shipping_method = response.shipping_method;
           this.orderResponse.status = response.status;
           this.orderResponse.total_money = response.total_money;
           
        },
        complete: () =>{
           
        },
        error: (error:any) => {
          console.log(error);
        }
          });
        
      }
    

    
}
