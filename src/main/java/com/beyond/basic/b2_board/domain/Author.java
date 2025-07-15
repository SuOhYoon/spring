package com.beyond.basic.b2_board.domain;

import com.beyond.basic.b2_board.repository.AuthorMemoryRepository;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Author {
    private Long id;
    private String name;
    private String password;
    private String email;

    public Author(String name, String password, String email) {
        this.id = AuthorMemoryRepository.id;
        this.name = name;
        this.password = password;
        this.email = email;
    }

    public void updatePw(String password) {
        this.password = password;
    }
}