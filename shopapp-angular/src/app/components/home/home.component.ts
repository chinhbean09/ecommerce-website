import { CategoryService } from './../../services/category.service';
import { ProductService } from './../../services/product.service';
import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { environment } from 'src/app/environments/environment';
import { Category } from 'src/app/models/category';
import { Product } from 'src/app/models/product';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss']
})
export class HomeComponent implements OnInit {
  products: Product[] = [];
  categories: Category[] = []; //data từ category Service
  currentPage: number = 1;
  itemsPerPage: number = 9;
  pages: number[] = [];
  totalPages: number = 0;
  visiblePages: number[] = [];
  keyword:string ="";
  selectedCategoryId: number = 0;
  constructor(private ProductService: ProductService, 
    private categoryService: CategoryService,
    private router: Router,
    ){}

  ngOnInit() {
    // Gọi API lấy danh sách roles và lưu vào biến roles
    this.getProducts(this.keyword, this.selectedCategoryId, this.currentPage, this.itemsPerPage)
    this.getCategories(1,100);
 
  }

  getCategories(page:number,limit:number){
    this.categoryService.getCategories(page,limit).subscribe({
      next:(categories: Category[]) =>{
        this.categories = categories;
      },
      complete: () => {

      },
      error:(error: any) => {
        console.error(error); 
      }

    })


  }
  getProducts(keyword:string,selectedCategoryId: number, page: number, limit: number) {
    this.ProductService.getProducts(keyword, selectedCategoryId, page, limit).subscribe({
      next:(response:any) => {
        response.products.forEach((product: Product ) => {
          product.url = `${environment.apiBaseUrl}/products/images/${product.thumbnail}`
        });
        this.products = response.products;
        this.totalPages = response.totalPages;
        this.visiblePages = this.generateVisiblePageArray(this.currentPage,this.totalPages);
      },
      complete:() => {
        
      },
      error: (error:any) => {
        console.error(error);
      }
    });
  }

  onPageChange(page:number){
    this.currentPage = page;
    this.getProducts(this.keyword, this.selectedCategoryId, this.currentPage, this.itemsPerPage);
  }

  generateVisiblePageArray(currentPage: number, totalPages:number ){
    const MaxVisiblePage = 5;
    const halfVisiblePage = Math.floor(MaxVisiblePage / 2);

    let startPage = Math.max(currentPage - halfVisiblePage, 1);
    let endPage = Math.min(startPage + MaxVisiblePage - 1,totalPages);
  
    if(endPage - startPage + 1 < MaxVisiblePage){
      startPage = Math.max(endPage - MaxVisiblePage + 1, 1);
    }

    return new Array(endPage - startPage + 1).fill(0).map((_,index) => startPage + index);

  }
  searchProducts(){
    this.currentPage = 1;
    this.itemsPerPage = 9;
    this.getProducts(this.keyword, this.selectedCategoryId, this.currentPage, this.itemsPerPage);
  }


  onProductClick(productId:number){
    this.router.navigate(['/product', productId]);
  }
}
