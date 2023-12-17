import { CartService } from './../../services/cart.service';
// import {  Router } from '@angular/router';
import { CategoryService } from './../../services/category.service';
import { ProductService } from './../../services/product.service';
import { Component, OnInit } from '@angular/core';
import { Product } from 'src/app/models/product';
import { environment } from 'src/app/environments/environment';
import { ProductImage } from 'src/app/models/product.image';
import { ActivatedRoute, Router } from '@angular/router';

@Component({
  selector: 'app-detail-product',
  templateUrl: './detail-product.component.html',
  styleUrls: ['./detail-product.component.scss']
})
export class DetailProductComponent implements OnInit{
  //local attribute 
  product?: Product;
  productId: number = 0;
  quantity: number = 1;
  currentImageIndex: number = 0;
  constructor (
    private productService: ProductService,
    private categoryService: CategoryService,
    private cartService: CartService,
    private router: Router,
    private activatedRoute: ActivatedRoute,

    ){}

    ngOnInit(){
      //lấy product ID từ URL 
      const idParam = this.activatedRoute.snapshot.paramMap.get('id');
      
      // this.cartService.clearCart();
      // const idParam = 5;
      if(idParam !== null){
        this.productId =+ idParam;
      }

      if(!isNaN(this.productId)){
        this.productService.getDetailProduct(this.productId).subscribe({
          next: (response:any) => {
            //lấy danh sách sản phẩm và thay đổi url nếu có hình ảnh
            if(response.product_images && response.product_images.length > 0){
              response.product_images.forEach((product_images: ProductImage) => {
                product_images.imageUrl = `${environment.apiBaseUrl}/products/images/${product_images?.imageUrl}`;
                console.log(product_images.imageUrl)
              });
            }
            this.product = response
            //start with first image
            this.showImage(0);
        },
          complete: () => {

          },
          error:(error: any) => {
            console.error(error)
          }
      });
      }else{
        console.error('Invalid Product', idParam); 
      }
    }
    
    showImage(index: number): void{
      if(this.product && this.product.product_images && this.product.product_images.length > 0) {
        //đảm bảo index nằm trong khoảng hợp lệ
      if(index < 0){
        index = 0;
      } else if (index >= this.product.product_images.length) {
        index = this.product.product_images.length -1;
      }
      //gán index hiện tại và cập nhật ảnh hiển thị 
    this.currentImageIndex = index;
    }
    }
    
    thumbnailClick(index: number){
      //gọi khi một thumbnail được bấm 
      this.currentImageIndex = index; //cập nhật currentImageIndex
    }
    nextImage():void{
      this.showImage(this.currentImageIndex + 1);
    }
    previousImage(): void{
      this.showImage(this.currentImageIndex - 1);
    }

    addToCart() :void{

      if(this.product){
        this.cartService.addToCart(this.product.id, this.quantity);
      }else{
        console.error('Product là null không thể thêm sản phẩm vào cart')
      }
    }

    increaseQuantity():void{
      this.quantity++;
    }

    decreaseQuantity():void{
      if(this.quantity > 1) {
        this.quantity--;
      }
    }
    buyNow(): void {      
      this.router.navigate(['/orders']);
    }   
}
