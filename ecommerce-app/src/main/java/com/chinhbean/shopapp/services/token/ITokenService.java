package com.chinhbean.shopapp.services.token;

import com.chinhbean.shopapp.models.Token;
import com.chinhbean.shopapp.models.User;
import org.springframework.stereotype.Service;

@Service

public interface ITokenService {
    Token addToken(User user, String token, boolean isMobileDevice);
    Token refreshToken(String refreshToken, User user) throws Exception;

}
