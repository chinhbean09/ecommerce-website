package com.chinhbean.shopapp.services.token;

import com.chinhbean.shopapp.models.User;
import org.springframework.stereotype.Service;

@Service

public interface ITokenService {
    void addToken(User user,String token, boolean isMobileDevice);
}
