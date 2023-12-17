import { UserResponse } from './../../responses/user/user.response';
import { RegisterDTO } from './../../dtos/user/register.dto';
import { LoginDTO } from '../../dtos/user/login.dto';
import { Component, ViewChild } from '@angular/core';
import { UserService } from '../../services/user.service';
import { TokenService } from '../../services/token.service';
import { RoleService } from '../../services/role.service'; // Import RoleService
import { Router } from '@angular/router';
import { NgForm } from '@angular/forms';
import { LoginResponse } from '../../responses/user/login.response';
import { Role } from 'src/app/models/role';
import { Observable } from 'rxjs';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent {
  @ViewChild('loginForm') loginForm!: NgForm;
  phoneNumber: string = '0856444000';
  password: string = '12345';

  roles: Role[] = []; // Mảng roles
  rememberMe: boolean = true;
  selectedRole: Role | undefined; // Biến để lưu giá trị được chọn từ dropdown
  userResponse?: UserResponse; 
  onPhoneNumberChange() {
    console.log(`Phone typed: ${this.phoneNumber}`);
    //how to validate ? phone must be at least 6 characters
  }

  constructor(
    private router: Router,
    private userService: UserService, 
    private tokenService: TokenService,
    private roleService: RoleService

  ) { }

  ngOnInit() {
    // Gọi API lấy danh sách roles và lưu vào biến roles
    debugger
    this.roleService.getRoles().subscribe({
      next: (roles: Role[]) => { // Sử dụng kiểu Role[]
        debugger
        this.roles = roles;
        this.selectedRole = roles.length > 0 ? roles[0] : undefined;
      },
      error: (error: any) => {
        debugger
        console.error('Error getting roles:', error);
      }
    });
  }
  // register(registerDTO: RegisterDTO): Observable<any>{

  createAccount() {
    debugger
    // Chuyển hướng người dùng đến trang đăng ký (hoặc trang tạo tài khoản)
    this.router.navigate(['/register']); 
  }
  
  
  login() {
    // const message = `phone: ${this.phoneNumber}` +
    //   `password: ${this.password}`;
    // alert(message);
    debugger
    const loginDTO: LoginDTO = {
      phone_number: this.phoneNumber,
      password: this.password,
      role_id: this.selectedRole?.id ?? 1
    };

    this.userService.login(loginDTO).subscribe({
      next: (response: LoginResponse) => {
        //muốn sử dụng token này trong các API request thì ta gắn vào các header, tức là trên đường truyền dữ liệu thì ta
        //gắn thêm vào header giá trị token, và quá trình xen ngang này được gọi là interceptor
        debugger;
        console.log(response)
        const { token } = response;
        // let token = response.token
        if (this.rememberMe) {
          //lưu token vào trong localStorage
          this.tokenService.setToken(token);

          this.userService.getUserDetail(token).subscribe({
            next: (response: any) => {
                  this.userResponse  = {
                    // id: response.id,
                    // phone_number:response.phone_number,
                    // fullname: response.fullname,
                    // address: response.address,
                    // is_active: response.is_active,
                    // facebook_account_id: response.facebook_account_id,
                    // google_account_id: response.google_account_id,
                    // role: response.role,
                    ...response,
                    date_of_birth: new Date(response.date_of_birth),
                  };
                  this.userService.saveUserResponseToLocalStorage(this.userResponse);
                  this.router.navigate(['/']);

            },
            complete: () => {
              debugger;
            },
            error: (error: any) => {
              debugger;
              alert(error.error.message);
            }
          })
        }                
      },
      complete: () => {
        debugger;
      },
      error: (error: any) => {
        debugger;
        alert(error.error.message);
      }
    });
  }
}
