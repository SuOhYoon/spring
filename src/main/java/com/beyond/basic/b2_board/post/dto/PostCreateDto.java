package com.beyond.basic.b2_board.post.dto;

import com.beyond.basic.b2_board.author.domain.Author;
import com.beyond.basic.b2_board.post.domain.Post;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class PostCreateDto {
    private String category;
    @NotEmpty
    private String title;
    private String contents;
    @Builder.Default
    private String appointment = "N";
//    시간정보는 직접 LocalDateTime으로 형변환하는 경우가 많음
    private String appointmentTime;
    public Post toEntity(Author author, LocalDateTime appointmentTime){
        return Post.builder()
                .category(this.category)
                .title(this.title)
                .contents(this.contents)
//                .author(this.authorId) // case1. 관계성 설정 전        .author(author)
                .appointment(this.appointment)
                .appointmentTime(appointmentTime)
                .delYn("N")
                .build();
    }
}
