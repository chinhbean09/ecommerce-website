import { OrderDetail } from 'src/app/models/order.detail';
import { environment } from '../../environments/environment';
import { Component, OnInit } from '@angular/core';
import { Product } from 'src/app/models/product';
import { OrderResponse } from 'src/app/responses/order/order.response';
import { CartService } from 'src/app/services/cart.service';
import { OrderService } from 'src/app/services/order.service';
import { ProductService } from 'src/app/services/product.service';

@Component({
  selector: 'app-order-detail',
  templateUrl: './order.detail.component.html',
  styleUrls: ['./order.detail.component.scss']
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

constructor(private orderService: OrderService){}

ngOnInit(): void {
  this.getOrderDetails();      
    }

    getOrderDetails(): void {
      debugger
      const orderId = 10; // Thay bằng ID của đơn hàng bạn muốn lấy.
      this.orderService.getOrderById(orderId).subscribe({
        next: (response: any) => {        
          debugger;       
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
          
          this.orderResponse.order_details = response.order_details
            .map((order_detail: OrderDetail) => {
            order_detail.product.thumbnail = `${environment.apiBaseUrl}/products/images/${order_detail.product.thumbnail}`;
            return order_detail;
          });        
          this.orderResponse.payment_method = response.payment_method;
          this.orderResponse.shipping_date = new Date(
            response.shipping_date[0], 
            response.shipping_date[1] - 1, 
            response.shipping_date[2]
          );
          
          this.orderResponse.shipping_method = response.shipping_method;
          
          this.orderResponse.status = response.status;
          this.orderResponse.total_money = response.total_money;
        },
        complete: () => {
          debugger;        
        },
        error: (error: any) => {
          debugger;
          console.error('Error fetching detail:', error);
        }
      });
    }
    

    
}
