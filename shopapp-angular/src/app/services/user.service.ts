import { UserResponse } from './../responses/user/user.response';
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
  saveUserResponseToLocalStorage(userResponse?: UserResponse){
    try{
      if(userResponse == null || !userResponse){
        return;
      }
      const userResponseJSON = JSON.stringify(userResponse);
      localStorage.setItem('user', userResponseJSON);
      console.log('User response saved to local storage')
    }catch (error){
      console.error('Error saving user response to local storage', error)
    }
  }

  getUserResponseFromLocalStorage():UserResponse | null{
    try{
      
      const userResponseJSON = localStorage.getItem('user');
      if(userResponseJSON == null || !userResponseJSON == undefined){
        return null;
      }
      const userResponse = JSON.parse(userResponseJSON!);
      console.log('User response retrieved from local storage')
      return userResponse;
    }catch (error){
      console.error('Error retrieving user response from local storage', error)
      return null;
    }
  }
  removeUserFromLocalStorage():void {
    try {
      // Remove the user data from local storage using the key
      localStorage.removeItem('user');
      console.log('User data removed from local storage.');
    } catch (error) {
      console.error('Error removing user data from local storage:', error);
      // Handle the error as needed
    }
  }
}
