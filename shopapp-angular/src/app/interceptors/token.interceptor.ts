import { Injectable } from '@angular/core'; //để co the inject class nay ở chỗ ta cần 
import { HttpInterceptor, HttpRequest, HttpHandler, HttpEvent } from '@angular/common/http';
import { Observable } from 'rxjs';
import { TokenService } from '../services/token.service';

@Injectable()
export class TokenInterceptor implements HttpInterceptor {

    //đăng ký interceptor trong module (APP MODULE) - DONE 
    constructor(private tokenService: TokenService) { }
    //phương thức intercept cho phép req của chúng ta trên đường bay đi thì ta có thể chèn thêm token vào  
    intercept(
        req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
        debugger    
        const token = this.tokenService.getToken();
        //if token != null 
        if (token) {
            //lí do clone là vì req không thể sửa trực tiếp được mà ta nhân bản ra và sửa cái nhân bản 
            //xong r thì tham chiếu req sang req mới  
            req = req.clone({
                setHeaders: {
                    Authorization: `Bearer ${token}`,
                },
            });
        }   
        //đi tiếp, đón req sau khi chèn Bearer thì được đi tiếp 
        return next.handle(req);
    }

}