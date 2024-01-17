package com.chinhbean.shopapp.services.user;

import com.chinhbean.shopapp.dtos.UpdateUserDTO;
import com.chinhbean.shopapp.dtos.UserDTO;
import com.chinhbean.shopapp.exceptions.DataNotFoundException;
import com.chinhbean.shopapp.exceptions.InvalidPasswordException;
import com.chinhbean.shopapp.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IUserService {
    User createUser(UserDTO userDTO) throws Exception;
    String login(String phoneNumber, String password, Long roleId) throws Exception;

    User getUserDetailsFromToken(String token) throws Exception;

    User getUserDetailsFromRefreshToken(String token) throws Exception;

    User updateUser(Long userId, UpdateUserDTO updatedUserDTO) throws Exception;

    Page<User> findAll(String keyword, Pageable pageable) throws Exception;
    void resetPassword(Long userId, String newPassword)
            throws InvalidPasswordException, DataNotFoundException ;
    public void blockOrEnable(Long userId, Boolean active) throws DataNotFoundException;
}
