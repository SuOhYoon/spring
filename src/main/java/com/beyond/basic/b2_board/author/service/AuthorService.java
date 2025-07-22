package com.beyond.basic.b2_board.author.service;
import com.beyond.basic.b2_board.author.domain.Author;
import com.beyond.basic.b2_board.author.dto.*;
import com.beyond.basic.b2_board.author.repository.AuthorRepository;
import com.beyond.basic.b2_board.post.domain.Post;
import com.beyond.basic.b2_board.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

//Component로 대체 가능 (트랜잭션 처리가 없는 경우)
@Service
@RequiredArgsConstructor
// 스프링에서 메서드단위로 트랜잭션처리를 하고, 만약 예외(unchecked exception) 발생시 자동 롤백처리 지원.
@Transactional
public class AuthorService {

    //의존성 주입 ( DI ) 방법 1. AutoWired 어노테이션 사용 - > 필드 주입
//    @Autowired
//    AuthorRepository authorRepository;
    // 의존성 주입 ( DI ) 방법 2. 생성자 주입 방식 ( 가장 많이 쓰는 방식 )
    //장점 1) final을 통해 상수로 사용 가능 ( 안정성 향상 ) // 장점 2) 다형성 구현 가능 // 장점 3) 순환참조방지(컴파일 타임에 check)

//    private final AuthorRepository authorRepository;
// 객체로 만들어지는 시점에 스프링에서 authorRepository 객체를 매개변수로 주입

//  생성자가 하나밖에 없을 때에는 AutoWired 생략 가능
//    @Autowired
//    public AuthorService(AuthorRepository authorRepository){
//        this.authorRepository = authorRepository;
//    }

    // 의존성 주입방법 3. RequiredArgs 어노테이션 사용 -> 반드시 초기화 되어야 하는 필드(final 등)을 대상으로 생성자를 자동생성
// 다형성 설계는 불가
      private final AuthorRepository authorRepository;
      private final PostRepository postRepository;
      private final PasswordEncoder passwordEncoder;
    //객체 조립은 서비스 담당
    public void save(AuthorCreateDto authorCreateDto){
        //이메일 중복 검증
        authorRepository.findByEmail(authorCreateDto.getEmail()).ifPresent(a -> { throw new IllegalArgumentException("이미 존재하는 이메일입니다."); });
        //비밀번호 길이 검증
//        Author author = new Author(authorCreateDto.getName(), authorCreateDto.getPassword(), authorCreateDto.getEmail());
//        toEntity 패턴을 통해 Author객체 조립을 공통화
        String encodedPassword = passwordEncoder.encode(authorCreateDto.getPassword());
        Author author = authorCreateDto.authorToEntity(encodedPassword);
        Author dbAuthor = this.authorRepository.save(author);

//    cascading 테스트 : 회원이 생성될때, 곧바로 가입인사 글을 생성하는 상황
//    방법 2가지
//    방법 1. 직접 POST 객체 생성 후 저장
        Post post = Post.builder()
                .title("안녕하세요")
                .contents(authorCreateDto.getName() + "입니다. 반갑습니다.")
//                author객체가 db에 save되는 순간 엔티티매니저에 의해 author객체에도 id값 생성
                .author(dbAuthor)
                .build();
//        postRepository.save(post);
//    방법 2. cascade 옵션 활용
        author.getPostList().add(post);
        this.authorRepository.save(dbAuthor);
    }

    public Author doLogin(AuthorLoginDto dto){
        Optional<Author> optionalAuthor = authorRepository.findByEmail(dto.getEmail());
        boolean check = true;
        if(!optionalAuthor.isPresent()){
            check = false;
        } else {
//            비밀번호 검증 : matches함수를 통해서 암호화되지않은값을 다시 암호화해 db의 password를 검증
            if(!passwordEncoder.matches(dto.getPassword(), optionalAuthor.get().getPassword())){
                check = false;
            }
        }
        if(!check){
            throw new IllegalArgumentException("email 또는 비밀번호가 잘못되었습니다.");
        }
        return optionalAuthor.get();
    }
//    트랜잭션이 필요없는 경우, 아래와 같이 명시적으로 제외
    @Transactional(readOnly = true)
    public List<AuthorListDto> findAll(){
        return authorRepository.findAll().stream()
                .map(a->a.listFromEntity()).collect(Collectors.toList());
    }
    @Transactional(readOnly = true)
    public AuthorDetailDto findById(Long id) throws NoSuchElementException{
        Author author = authorRepository.findById(id).orElseThrow(()->new NoSuchElementException("검색된 결과가 없습니다."));
//        AuthorDetailDto dto1 = AuthorDetailDto.detailFromEntity();

//        연관관계설정 없이 직접 조회해서 count값 설정하는 경우
//        List<Post> postList = postRepository.findByAuthorr(author);
//        AuthorDetailDto dto2 = AuthorDetailDto.fromEntity(author);

//        OneToMany연관관계 설정을 통해 count값 찾는 경우
        AuthorDetailDto dto2 = AuthorDetailDto.fromEntity(author);
        return dto2;
    }

    public void updatePassword(AuthorUpdatePw authorUpdatePw){
        Author author = authorRepository.findByEmail(authorUpdatePw.getEmail()).orElseThrow(()->new NoSuchElementException("비밀번호 변경에 실패했습니다."));
//        dirty checking : 객체를 수정 후 별도의 update쿼리 발생시키지 않아도, 영속성 컨텍스트에 의해 객체 변경사항 자동 DB 반영
        author.updatePw(authorUpdatePw.getPassword());
    }

    public void delete(Long id) {
        Author author = authorRepository.findById(id).orElseThrow(() -> new NoSuchElementException("회원 탈퇴에 실패하였습니다."));
        authorRepository.delete(author);
    }

}
