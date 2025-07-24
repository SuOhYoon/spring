package com.beyond.basic.b2_board.author.service;

import com.beyond.basic.b2_board.author.domain.Author;
import com.beyond.basic.b2_board.author.dto.*;
//import com.beyond.basic.b2_board.repository.AuthorJdbcRepository;
import com.beyond.basic.b2_board.author.repository.AuthorRepository;
import com.beyond.basic.b2_board.post.domain.Post;
import com.beyond.basic.b2_board.post.repository.PostRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

//Component로 대체 가능 (트랜잭션 처리가 없는 경우)
@Service
@Transactional
// 스프링에서 메서드 단위로 트랜잭션 처리를 하고, 만약 예외(unchecked) 발생 시 자동 롤백처리 지원.
@RequiredArgsConstructor
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
    private final S3Client s3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    //객체 조립은 서비스 담당
    public void save(AuthorCreateDto authorCreateDto, MultipartFile profileImage){
        //이메일 중복 검증
        authorRepository.findByEmail(authorCreateDto.getEmail()).ifPresent(a -> { throw new IllegalArgumentException("이미 존재하는 이메일입니다."); });
        //비밀번호 길이 검증
        String encodedPassword = passwordEncoder.encode(authorCreateDto.getPassword());
        Author author =authorCreateDto.authorToEntity(encodedPassword);

        // cascading 테스트 : 회원이 생성될 때, 곧바로 "가입인사" 글을 생성하는 상황
        // 방법 1 : 직접 POST 객체 생성 후 저장
//        Post post = Post.builder()
//                .title("안녕하세요")
//                .contents(authorCreateDto.getName()+"입니다. 반갑습니다.")
//                .author(author)
//                .delYn("N")
//                .build();
//        postRepository.save(post);
        // 방법 2: cascade 옵션 활용
//        author.getPostList().add(post);
        this.authorRepository.save(author);

        // image명 설정
        String fileName = "user-"+author.getId()+"-profileimage-"+profileImage.getOriginalFilename();

        // 저장 객체 구성
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucket)
                .key(fileName)
                .contentType(profileImage.getContentType()) // image//jpg
                .build();

        // 이미지를 업로드 ( byte 형태로 )
        try {
            s3Client.putObject(putObjectRequest, RequestBody.fromBytes(profileImage.getBytes()));
        } catch (IOException e) {
            // checked -> unchecked로 바꿔 전체 rollback 되도록 예외처리
            throw new IllegalArgumentException("이미지 업로드 실패");
        }

        //이미지 url 추출
        String imgUrl = s3Client.utilities().getUrl(a->a.bucket(bucket).key(fileName)).toExternalForm();
        author.updateImgUrl(imgUrl);
    }

    @Transactional(readOnly = true)
    public List<AuthorListDto> findAll(){
//        List<AuthorListDto> authorListDto = new ArrayList<>();
//        for(Author author : this.authorRepository.findAll()){
//            authorListDto.add(author.listFromEntity());
//        }

        return this.authorRepository.findAll().stream().
                map(author -> author.listFromEntity())
                .collect(Collectors.toList());
    }
    @Transactional(readOnly = true)
    public AuthorDetailDto findById(Long id) throws NoSuchElementException{

        Author author = getOrElseThrow(id);
        List<Post> posts = postRepository.findByAuthor(author);
//        return AuthorDetailDto.fromEntity(author, posts.size());
        return AuthorDetailDto.fromEntity(author);
    }

    public Author getOrElseThrow(Long id) {
        return this.authorRepository.findById(id).orElseThrow(() -> new NoSuchElementException("검색된 결과가 없습니다."));
    }

    @Transactional
    public void updatePassword(AuthorUpdatePw authorUpdatePw){
        Author author = this.authorRepository.findByEmail(authorUpdatePw.getEmail()).orElseThrow(()->new NoSuchElementException("비밀번호 변경에 실패했습니다."));
        author.updatePw(authorUpdatePw.getPassword());
        // dirty checking : 객체를 수정한 후 별도의 update 쿼리 발생시키지 않아도, 영속성 컨텍스트에 의해 겍체 변경사항 자동 DB반영
        // repository에 update해주지 않아도 entitymanager가 자동으로 update해준다.
    }

    public void delete(Long id){
        Author author = this.authorRepository.findById(id).orElseThrow(()->new NoSuchElementException("회원 탈퇴에 실패하였습니다."));
        this.authorRepository.delete(author);
    }


    public Author doLogin(AuthorLoginDto authorLoginDto) {
        Optional<Author> author = authorRepository.findByEmail(authorLoginDto.getEmail());
        boolean check = true;

        if(!author.isPresent()){
            check=false;
        }else{
            if(!passwordEncoder.matches(authorLoginDto.getPassword(), author.get().getPassword())){
                check=false;
            }
        }

        if(!check){
            throw new IllegalArgumentException("이메일 또는 비밀번호가 일치하지 않습니다.");
        }
        return author.get();
    }

    public AuthorDetailDto myInfo(){
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Author author = authorRepository.findByEmail(email).orElseThrow(()->new EntityNotFoundException("존재하지 않는 유저입니다."));

        return AuthorDetailDto.fromEntity(author);


    }

}