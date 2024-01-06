package com.chinhbean.shopapp.repositories;


import com.chinhbean.shopapp.models.Token;
import com.chinhbean.shopapp.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TokenRepository extends JpaRepository<Token, Long> {
    List<Token> findByUser(User user);
    Token findByToken(String token);

    Token findByRefreshToken(String token);

}

