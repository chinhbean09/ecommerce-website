package com.chinhbean.shopapp.services;

import com.chinhbean.shopapp.dtos.UserDTO;
import com.chinhbean.shopapp.exceptions.DataNotFoundException;
import com.chinhbean.shopapp.models.User;

public interface IUserService {
    User createUser(UserDTO userDTO) throws Exception;
    String login(String phoneNumber, String password) throws Exception;


}
