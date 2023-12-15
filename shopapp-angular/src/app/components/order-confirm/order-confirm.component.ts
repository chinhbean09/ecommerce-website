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
cartItems: {product: Product, quantity: number }[] = [];
couponCode: String = '';
totalAmount: number = 0;

constructor(
  private cartService: CartService,
  private productService: ProductService
){}

ngOnInit(): void {
  //Lấy thông tin giỏ hàng từ cartService:
    const cart = this.cartService.getCart();
    //lấy key từ trong cart (id của products )
    const productIds = Array.from(cart.keys());

    // Lấy Các Mục trong Giỏ Hàng và Chi Tiết Sản Phẩm:
    this.productService.getProductsByIds(productIds).subscribe({
      next: (products) => {
        //map() để tạo một mảng mới(this.cartItems) dựa trên mảng productIds.
        //mỗi phần tử trong mảng mới là một đối tượng có hai thuộc tính: product và quantity.
        this.cartItems = productIds.map((productId) => {
        //hàm find sẽ lọc ra những product có id tương ứng
          const product=  products.find(
            (p) => p.id === productId);
          if(product) {
            product.thumbnail = `${environment.apiBaseUrl}/products/images/${product.thumbnail}`;
          }
          return { //mỗi vòng lặp trả về đối tượng chứa product và quantity
            // '!' thông báo rằng TypeScript nên xem xét những giá trị này như là không bao giờ null hoặc undefined.
            product: product!,
            quantity: cart.get(productId)!
          };
        });
        console.log('DONEEEEEEEEE')
      },
      complete: () =>{

      },
      error: (error:any) => {
        console.log(error);
      }
        });
      
    }

    

    calculateTotal():void{

      //reduce giúp tổng hợp các phần tử ở trong cartItems và lấy
      this.totalAmount = this.cartItems.reduce(
        (total,item) => total + item.product.price * item.quantity,0
      )
    }

    applyCoupon():void{
      
    }

  }

