package com.beyond.basic.b2_board.post.dto;

import com.beyond.basic.b2_board.author.domain.Author;
import com.beyond.basic.b2_board.post.domain.Post;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class PostDetailDto {
    private Long id;
    private String cateogry;
    private String title;
    private String contents;
    private String authorEmail;
//    관계성 설정을 하지 않을때
//    public static PostDetailDto fromEntity(Post post, Author author){
//        return PostDetailDto.builder()
//                .id(post.getId())
//                .title(post.getTitle())
//                .contents(post.getContents())
//                .authorEmail(author.getEmail())
//                .build();
//    }

//    관계성 설정을 했을때
    public static PostDetailDto fromEntity(Post post){
        return PostDetailDto.builder()
                .id(post.getId())
                .cateogry(post.getCategory())
                .title(post.getTitle())
                .contents(post.getContents())
                .authorEmail(post.getAuthor().getEmail())
                .build();
    }
}
