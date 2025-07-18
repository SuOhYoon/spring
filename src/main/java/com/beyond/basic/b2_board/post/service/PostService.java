package com.beyond.basic.b2_board.post.service;

import com.beyond.basic.b2_board.author.domain.Author;
import com.beyond.basic.b2_board.author.repository.AuthorRepository;
import com.beyond.basic.b2_board.post.domain.Post;
import com.beyond.basic.b2_board.post.dto.PostCreateDto;
import com.beyond.basic.b2_board.post.dto.PostDetailDto;
import com.beyond.basic.b2_board.post.dto.PostListDto;
import com.beyond.basic.b2_board.post.repository.PostRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class PostService {
    private final PostRepository postRepository;
    private final AuthorRepository authorRepository;

    @Autowired
    public PostService(PostRepository postRepository, AuthorRepository authorRepository) {
        this.postRepository = postRepository;
        this.authorRepository = authorRepository;
    }

    public void save(PostCreateDto dto){
        Author author = authorRepository.findById(dto.getAuthorId()).orElseThrow(()->new EntityNotFoundException("없는 사용자입니다."));
//        authorId가 실제 있는지 없는지 검증.
        postRepository.save(dto.toEntity(author));
    }

    public PostDetailDto findById(Long id){
        Post post = postRepository.findById(id).orElseThrow(()->new EntityNotFoundException("없는 ID입니다."));
//        엔티티간의 관계성 설정을 하지 않았을때
//        Author author = authorRepository.findById(post.getAuthorId()).orElseThrow(()->new EntityNotFoundException("없는 회원입니다."));
//        return PostDetailDto.fromEntity(post, author);
//        엔티티간의 관계성 설정을 통해 Author객체를 쉽게 조회하는 경우
        return PostDetailDto.fromEntity(post);
    }

    public Page<PostListDto> findByAll(Pageable pageable){
//        List<Post> postList = postRepository.findAll(); // 일반 전체조회
//        List<Post> postList = postRepository.findAllJoin(); // 일반 inner join
//        List<Post> postList = postRepository.findAllFetchJoin(); // inner join fetch
//        postList를 조회시 참조관계에 있는 author까지 조회하므로 N(author 쿼리)+1(post 쿼리)문제 발생
//        jpa는 기본방향성이 fetch lazy이므로, 참조하는 시점에 쿼리를 내보내게 되어 JOIN문을 만들어주지 않고, N+1문제 발생

//        페이지처리 findAll 호출
        Page<Post> postList = postRepository.findAllByDelYn(pageable, "N");
//        return postList.map(a->PostListDto.fromEntity(a)).collect(Collectors.toList());
        return postList.map(a->PostListDto.fromEntity(a));
    }
}
