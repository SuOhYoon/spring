package com.beyond.basic.b2_board.author.dto;

import com.beyond.basic.b2_board.author.domain.Author;
import com.beyond.basic.b2_board.author.domain.Role;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data // dto 계층은 데이터의 안정성이 엔티티만큼 중요하지 않으므로, setter도 일반적으로 추가
@AllArgsConstructor
@NoArgsConstructor
public class AuthorCreateDto {
    @NotEmpty(message = "이름은 필수 입력 항목입니다.")
    private String name;
    @NotEmpty(message = "email은 필수 입력 항목입니다.")
    private String email;
    @NotEmpty(message = "password는 필수 입력 항목입니다.")
    @Size(min = 8,message = "password의 길이가 너무 짧습니다.")
    private String password;
//    문자열로 값이 넘어오면 Role에 값으로 매핑
    private Role role = Role.USER;
    public Author authorToEntity(String encodedPassword){
        return Author.builder()
                .email(this.email)
                .name(this.name)
                .password(encodedPassword)
                .build();
    }
}