package com.chinhbean.shopapp.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserLoginDTO {
    @JsonProperty
    @NotBlank(message = "Phone Number cannot be blank")
    private String phoneNumber;

@NotBlank(message = "Password cannot be blank")
private String password;

}
