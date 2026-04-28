package com.kaysiodl.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * DTO class for representing user authentication request.
 */
@Getter
@Setter
public class AuthRequest {
    private String login;
    private String password;
}
