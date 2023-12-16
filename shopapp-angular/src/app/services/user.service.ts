import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { RegisterDTO } from '../../app/dtos/user/register.dto';
import { HttpUtilService } from './http.util.service';
import { environment } from 'src/app/environments/environment';
import { LoginDTO } from '../../app/dtos/user/login.dto';
@Injectable({
  providedIn: 'root'
})
export class UserService {
  private apiRegister = `${environment.apiBaseUrl}/users/register`;
  private apiLogin = `${environment.apiBaseUrl}/users/login`;
  private apiUserDetail = `${environment.apiBaseUrl}/users/details`;

  private apiConfig = {
    headers: this.httpUtilService.createHeaders(),
    /*
     createHeaders(): HttpHeaders {
     return new HttpHeaders({
      'Content-Type': 'application/json',
      'Accept-Language': 'vi'});
    */
  }

  constructor(
    private http: HttpClient,
    private httpUtilService: HttpUtilService
  ) { }
//tham số là registerDTO, giả định là một đối tượng có kiểu dữ liệu RegisterDTO.
//Phương thức trả về một Observable để quản lý bất đồng bộ và theo dõi kết quả của request.
  register(registerDTO: RegisterDTO):Observable<any> {
    return this.http.post(this.apiRegister, registerDTO, this.apiConfig);
    return this.http.post(this.apiRegister, registerDTO, this.apiConfig);
  }
  login(loginDTO: LoginDTO): Observable<any> {    
    return this.http.post(this.apiLogin, loginDTO, this.apiConfig);
  }

  getUserDetail(token: string){
    return this.http.post(this.apiUserDetail, {
        headers: new HttpHeaders({
         'Content-Type': 'application/json',
         Authorization: `Bearer ${token}`}) 
        })
  }

}