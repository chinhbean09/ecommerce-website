import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, RouterStateSnapshot, UrlTree, CanActivateFn } from '@angular/router';
import { TokenService } from 'src/app/services/token.service';
import { Router } from '@angular/router'; // Đảm bảo bạn đã import Router ở đây.
import { inject } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class AuthGuard {
  constructor(private tokenService: TokenService, private router: Router) {}
//CanActivate là một interface được sử dụng để tạo ra một guard để kiểm tra xem 
//một người dùng có được phép truy cập vào một route hay không
  canActivate(next: ActivatedRouteSnapshot, state: RouterStateSnapshot): boolean {
    const isTokenExpired = this.tokenService.isTokenExpired();
    const isUserIdValid = this.tokenService.getUserId() > 0;
    debugger
    //nếu hết hạn và userId invalid thì trả về trang login 
    if (!isTokenExpired && isUserIdValid) {
      return true;
    } else {
      // Nếu không authenticated, bạn có thể redirect hoặc trả về một UrlTree khác.
      // Ví dụ trả về trang login:
      this.router.navigate(['/login']);
      return false;
    }
  }
}

// Sử dụng functional guard như sau:
// Đi qua cảnh vệ này để kiểm tra điều kiện là có userId chưa và token => phải login 
export const AuthGuardFn: CanActivateFn = (next: ActivatedRouteSnapshot, state: RouterStateSnapshot): boolean => {
  debugger
  return inject(AuthGuard).canActivate(next, state);
}
