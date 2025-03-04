package com.ben.vet.dto;

import lombok.Data;

@Data
public class UserRequestDTO {
    private String username;
    private String password;
   // private String role;
    private String firstName;
    private String lastName;
}
