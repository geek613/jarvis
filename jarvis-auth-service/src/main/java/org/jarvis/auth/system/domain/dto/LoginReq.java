package org.jarvis.auth.system.domain.dto;

import lombok.Data;

@Data
public class LoginReq {
    private String username;
    private String password;
    private String email;
    private String phone;
}
