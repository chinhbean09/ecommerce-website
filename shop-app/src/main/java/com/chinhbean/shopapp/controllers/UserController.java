package com.chinhbean.shopapp.controllers;

import com.chinhbean.shopapp.components.LocalizationUtils;
import com.chinhbean.shopapp.dtos.UserDTO;
import com.chinhbean.shopapp.dtos.UserLoginDTO;
import com.chinhbean.shopapp.dtos.*;
import com.chinhbean.shopapp.models.Token;
import com.chinhbean.shopapp.models.User;
import com.chinhbean.shopapp.responses.LoginResponse;
import com.chinhbean.shopapp.responses.RegisterResponse;
import com.chinhbean.shopapp.responses.UserResponse;
import com.chinhbean.shopapp.services.token.ITokenService;
import com.chinhbean.shopapp.services.user.IUserService;
import com.chinhbean.shopapp.utils.MessageKeys;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/users")
@RequiredArgsConstructor
public class UserController {
    private final LocalizationUtils localizationUtils;
    private final IUserService userService;
    private final ITokenService tokenService;

    //    @PostMapping("/register")
//
//    public ResponseEntity<?> createUser(
//            @Valid @RequestBody UserDTO userDTO,
//            BindingResult result)
//    {
//        try{
//            if(result.hasErrors()) {
//                List<String> errorMessages = result.getFieldErrors()
//                        .stream()
//                        .map(FieldError::getDefaultMessage)
//                        .toList();
//                return ResponseEntity.badRequest().body(errorMessages);
//            }
//            if(!userDTO.getPassword().equals(userDTO.getRetypePassword())){
//                return ResponseEntity.badRequest().body("Password does not match");
//            }
//            User user = userService.createUser(userDTO);
//            //return ResponseEntity.ok("Register successfully");
//            return ResponseEntity.ok(user);
//        }  catch (Exception e) {
//            return ResponseEntity.badRequest().body(e.getMessage());
//        }
//    }
@PostMapping("/register")
//can we register an "admin" user ?
public ResponseEntity<RegisterResponse> createUser(
        @Valid @RequestBody UserDTO userDTO,
        BindingResult result
) {
    RegisterResponse registerResponse = new RegisterResponse();

    if (result.hasErrors()) {
        List<String> errorMessages = result.getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .toList();

        registerResponse.setMessage(errorMessages.toString());
        return ResponseEntity.badRequest().body(registerResponse);
    }

    if (!userDTO.getPassword().equals(userDTO.getRetypePassword())) {
        registerResponse.setMessage(localizationUtils.getLocalizedMessage(MessageKeys.PASSWORD_NOT_MATCH));
        return ResponseEntity.badRequest().body(registerResponse);
    }

    try {
        User user = userService.createUser(userDTO);
        registerResponse.setMessage(localizationUtils.getLocalizedMessage(MessageKeys.REGISTER_SUCCESSFULLY));
        registerResponse.setUser(user);
        return ResponseEntity.ok(registerResponse);
    } catch (Exception e) {
        registerResponse.setMessage(e.getMessage());
        return ResponseEntity.badRequest().body(registerResponse);
    }
}

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody UserLoginDTO userLoginDTO,
            HttpServletRequest request) {
        // Kiểm tra thông tin đăng nhập và sinh token
        try {
            String token = userService.login(
                    userLoginDTO.getPhoneNumber(),
                    userLoginDTO.getPassword(),
                    userLoginDTO.getRoleId() == null ? 1 : userLoginDTO.getRoleId());

            String userAgent = request.getHeader("User-Agent");
            User userDetail = userService.getUserDetailsFromToken(token);
            Token jwtToken = tokenService.addToken(userDetail, token, isMobileDevice(userAgent));


            // Trả về token trong response
            return ResponseEntity.ok(LoginResponse.builder()
                    .message(localizationUtils.getLocalizedMessage(MessageKeys.LOGIN_SUCCESSFULLY))
                    .token(jwtToken.getToken())
                    .tokenType(jwtToken.getTokenType())
                    .refreshToken(jwtToken.getRefreshToken())
                    .username(userDetail.getUsername())
                    .roles(userDetail.getAuthorities().stream().map(item -> item.getAuthority()).toList())
                    .id(userDetail.getId())
                    .build());
        } catch (Exception e){
            return ResponseEntity.badRequest().body(
                    LoginResponse.builder()
                            .message(localizationUtils.getLocalizedMessage(MessageKeys.LOGIN_FAILED, e.getMessage()))
                            .build()
            );
        }
    }
    @PostMapping("/refreshToken")
    public ResponseEntity<LoginResponse> refreshToken(
            @Valid @RequestBody RefreshTokenDTO refreshTokenDTO
    ) {
        try {
            User userDetail = userService.getUserDetailsFromRefreshToken(refreshTokenDTO.getRefreshToken());
            Token jwtToken = tokenService.refreshToken(refreshTokenDTO.getRefreshToken(), userDetail);
            return ResponseEntity.ok(LoginResponse.builder()
                    .message("Refresh token successfully")
                    .token(jwtToken.getToken())
                    .tokenType(jwtToken.getTokenType())
                    .refreshToken(jwtToken.getRefreshToken())
                    .username(userDetail.getUsername())
                    .roles(userDetail.getAuthorities().stream().map(item -> item.getAuthority()).toList())
                    .id(userDetail.getId())
                    .build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    LoginResponse.builder()
                            .message(localizationUtils.getLocalizedMessage(MessageKeys.LOGIN_FAILED, e.getMessage()))
                            .build()
            );
        }
    }
    private boolean isMobileDevice(String userAgent) {
        // Kiểm tra User-Agent header để xác định thiết bị di động
        // Ví dụ đơn giản:
        return userAgent.toLowerCase().contains("mobile");
    }
    //lấy thông tin chi tiết của user thông qua token, sd trong trường hợp đã login rồi và vào trang thông tin
    //người dùng và lấy ra thông tin chi tiết thông qua token đó
    @PostMapping("/details")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    public ResponseEntity<UserResponse> getUserDetails(
            //truyền header
            @RequestHeader("Authorization") String token)
    {try{
        // Trích xuất giá trị token từ header Authorization
        String extractedToken = token.substring(7);

        //UserService để lấy thông tin User dựa trên token
        User user = userService.getUserDetailsFromToken(extractedToken);

        // Trả về một ResponseEntity chứa thông tin người dùng dưới dạng UserResponse
        return ResponseEntity.ok(UserResponse.fromUser(user));
        }catch(Exception e){
            return ResponseEntity.badRequest().build();

        }
    }
    @PutMapping("/details/{userId}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    @Operation(security = { @SecurityRequirement(name = "bearer-key") })

    public ResponseEntity<UserResponse> updateUserDetails(
            @PathVariable Long userId,
            @RequestBody UpdateUserDTO updatedUserDTO,
            //truyền header để lấy token trong Authorization
            @RequestHeader("Authorization") String authorizationHeader
    ) {
        try {
            String extractedToken = authorizationHeader.substring(7);
            User user = userService.getUserDetailsFromToken(extractedToken);
            //Toi đang cập nhật chính mình
            if (user.getId() != userId) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            User updatedUser = userService.updateUser(userId, updatedUserDTO);
            return ResponseEntity.ok(UserResponse.fromUser(updatedUser));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
