package com.beyond.basic.b2_board.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthorUpdatePw {
    private String email;
    private String password;
}