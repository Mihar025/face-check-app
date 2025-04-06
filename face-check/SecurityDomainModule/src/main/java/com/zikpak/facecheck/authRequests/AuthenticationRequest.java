package com.zikpak.facecheck.authRequests;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class AuthenticationRequest {

//    @Email(regexp = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$", message = "Email is not formatted well!")
//   @NotBlank(message = "Email is required!")
//    @Pattern(regexp = "^[^;'\"]*$", message = "Email contains invalid characters")
    private String email;
 //   @NotBlank(message = "Password is required!")
  //  @NotEmpty(message = "Password is required")
  //  @Size(min = 4,message = "Password should be minimum 6 characters!" )
    private String password;



}
