import { Component, OnInit } from '@angular/core';
import { environment } from 'src/app/environments/environment';
import { Product } from 'src/app/models/product';
import { CartService } from 'src/app/services/cart.service';
import { ProductService } from 'src/app/services/product.service';

@Component({
  selector: 'app-order',
  templateUrl: './order.component.html',
  styleUrls: ['./order.component.scss']
})
export class OrderComponent implements OnInit {
  cartItems: {product: Product, quantity: number }[] = [];
  couponCode: String = '';
  totalAmount: number = 0;

  orderData: OrderDTO = { //1
    user_id: 1,
    fullname: '',
    email:'',
    phone_number:'',
    address:'',
    note:'',
    total_money:0,
    paymend_method: 'cod',
    shipping_method:'express',
    coupon_code:'',
    cart_items:[]
  };
  
  constructor(
    private cartService: CartService,
    private productService: ProductService,
    private orderService: OrderService //2
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
  
      placeOrder(){ 
        this.orderService.placeOrder(this.orderData).subscribe({
        next: (response) => {
          console.log('Đặt hàng thành công');
        },
        complete: () => {
          this.calculateTotal()
        },
        error: (error:any) => {
          console.error('Lỗi khi đặt hàng', error)
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
