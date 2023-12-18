import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../environments/environment';
import { ProductService } from './product.service';
@Injectable({
  providedIn: 'root'
})
export class CartService {

  private cart: Map<number,number> = new Map(); //dùng map, key là id sản phẩm, value là số lượng 

  constructor(private productService: ProductService){
    //lưu đối tượng map vào trong localStorage, lần khác mở trình duyệt lên thì vẫn lưu lại thông tin giỏ hàng 
    //và mất đi khi bấm vào mua    
    const storedCart = localStorage.getItem('cart');
    if(storedCart){
        this.cart = new Map(JSON.parse(storedCart)); //kiểu dữ liệu Map 
        /* đối tượng map
      {
        "2" : 5,
        "3" : 10  
      }
        */ 
    }
  }
  //Thêm vào cart với productId là key, quantity là value, để thay đổi biến cart 
  addToCart(productId: number, quantity: number = 1): void {

    if(this.cart.has(productId)){
      //nếu sản phẩm đã có trong giỏ, tăng số lượng quantity thêm 1 
        this.cart.set(productId, this.cart.get(productId)! + quantity);
    }else{
      //nếu sản phẩm chưa có trong giỏ, với số lượng là quantity 
        this.cart.set(productId, quantity)
    }
    //sau khi thay đổi cart, lưu trữ nó vào trong localStorage 
    this.saveCartToLocalStorage();
  }

  getCart(): Map<number,number>{
    return this.cart;
  }

  private saveCartToLocalStorage():void{
    //entries() là một phương thức của đối tượng Map, trả về một Iterator chứa các cặp [key, value] cho mỗi cặp khóa-giá trị trong Map.
    //sử dụng Array.from() để chuyển đổi iterator thành một mảng, đang lặp qua từng cặp [key, value] trong iterator và đưa chúng vào một mảng mới.
    localStorage.setItem('cart',JSON.stringify(Array.from(this.cart.entries())));
  }

  clearCart():void{
    this.cart.clear();
    this.saveCartToLocalStorage();

  }

}
