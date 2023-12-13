import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../environments/environment';
import { Product } from '../models/product';
import { Category } from '../models/category';
@Injectable({
  providedIn: 'root'
})
export class CategoryService {
  private apiGetCategory  = `${environment.apiBaseUrl}/categories`;

  constructor(private http: HttpClient) { }
  

    getCategories(page: number, limit: number): Observable<Category[]> {
        const params = new HttpParams()
        .set('page',page.toString())
        .set('limit',limit.toString())
        return this.http.get<Category[]>(this.apiGetCategory,{params})
    }

//   getRoles():Observable<any> {
//     return this.http.get<any[]>(this.apiGetRoles);
//   }
}
