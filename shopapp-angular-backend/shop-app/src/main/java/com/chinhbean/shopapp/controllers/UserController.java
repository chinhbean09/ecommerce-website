package com.chinhbean.shopapp.controllers;

import com.chinhbean.shopapp.components.LocalizationUtils;
import com.chinhbean.shopapp.dtos.UserDTO;
import com.chinhbean.shopapp.dtos.UserLoginDTO;
import com.chinhbean.shopapp.dtos.*;
import com.chinhbean.shopapp.models.User;
import com.chinhbean.shopapp.responses.LoginResponse;
import com.chinhbean.shopapp.responses.RegisterResponse;
import com.chinhbean.shopapp.services.IUserService;
import com.chinhbean.shopapp.utils.MessageKeys;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/v1/users")
@RequiredArgsConstructor
public class UserController {
    private final LocalizationUtils localizationUtils;
    private final IUserService userService;
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
            @Valid @RequestBody UserLoginDTO userLoginDTO) {
        // Kiểm tra thông tin đăng nhập và sinh token
        try {
            String token = userService.login(userLoginDTO.getPhoneNumber(), userLoginDTO.getPassword(),userLoginDTO.getRoleId());
            // Trả về token trong response
            return ResponseEntity.ok(LoginResponse.builder()
                    .message(localizationUtils.getLocalizedMessage(MessageKeys.LOGIN_SUCCESSFULLY))
                    .token(token)
                    .build());
        } catch (Exception e){
            return ResponseEntity.badRequest().body(
                    LoginResponse.builder()
                            .message(localizationUtils.getLocalizedMessage(MessageKeys.LOGIN_FAILED, e.getMessage()))
                            .build()
            );
        }
    }
}
