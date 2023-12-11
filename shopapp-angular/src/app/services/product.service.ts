import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../environments/environment';
import { Product } from '../models/product';
@Injectable({
  providedIn: 'root'
})
export class ProductService {
  private apiGetProduct  = `${environment.apiBaseUrl}/products`;

  constructor(private http: HttpClient) { }
  

    getProducts(page: number, limit: number): Observable<Product[]> {
        const params = new HttpParams()
        .set('page',page.toString())
        .set('limit',limit.toString())
        return this.http.get<Product[]>(this.apiGetProduct,{params})
    }

//   getRoles():Observable<any> {
//     return this.http.get<any[]>(this.apiGetRoles);
//   }
}
