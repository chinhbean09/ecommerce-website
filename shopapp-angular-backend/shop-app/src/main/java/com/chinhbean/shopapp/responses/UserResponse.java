package com.chinhbean.shopapp.responses;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.JoinColumn;
import lombok.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserResponse{
    private Long id;
    private String fullName;
    private String phoneNumber;
    private String address;
    private boolean active;
    private Date dateOfBirth;
    private int facebookAccountId;
    private int googleAccountId;
    @JsonProperty("role")
    private com.chinhbean.shopapp.models.Role role;

    public static UserResponse fromUser(com.chinhbean.shopapp.models.User user) {
        return UserResponse.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .phoneNumber(user.getPhoneNumber())
                .active(user.isActive())
                .dateOfBirth(user.getDateOfBirth())
                .facebookAccountId(user.getFacebookAccountId())
                .googleAccountId(user.getGoogleAccountId())
                .role(user.getRole())
                .build();

    }
}
