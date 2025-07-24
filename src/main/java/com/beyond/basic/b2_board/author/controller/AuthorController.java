package com.beyond.basic.b2_board.author.controller;

import com.beyond.basic.b2_board.author.domain.Author;
import com.beyond.basic.b2_board.author.dto.*;
//import com.beyond.basic.b2_board.dto.*;
import com.beyond.basic.b2_board.author.service.AuthorService;
import com.beyond.basic.b2_board.common.JwtTokenProvider;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.NoSuchElementException;

@RestController // Controller + ResponseBody
@RequiredArgsConstructor
@RequestMapping("/author")
public class AuthorController {
    //서비스 주입받기
    private final AuthorService authorService;
    private final JwtTokenProvider jwtTokenProvider;

    //회원가입

    @PostMapping("/create")
    //dto에 있는 validation어노테이션과 controller의 @Valid 한쌍
    public ResponseEntity<String> save(@RequestPart(name = "authorCreateDto") @Valid AuthorCreateDto authorCreateDto,
                                       @RequestPart(name = "profileImage")MultipartFile profileImage) {
        System.out.println(profileImage.getOriginalFilename());
//        try{
//        this.authorService.save(authorCreateDto);
//        return new ResponseEntity<>("회원가입 완료", HttpStatus.CREATED);
//        }catch (IllegalArgumentException e){
//            e.printStackTrace();
//            // 생성자 매개변수로 body부분의 객체와 header 부분의 상태코드
//            // 제네릭 타입은 body부의 타입을 결정한 것이다
//            // 여러 타입을 return 하려면 object를 타입으로 지정하면 됨
//            ResponseEntity<String> response = new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
//            return response;
//        }
//    }
        //controllerAdvice로 예외를 전역적으로 처리하는 게 가능함.
//        this.authorService.save(authorCreateDto);

        this.authorService.save(authorCreateDto);
        return new ResponseEntity<>("회원가입 완료", HttpStatus.CREATED);
    }

    //회원목록 조회
    @GetMapping("/list")
    @PreAuthorize("hasRole('ADMIN')")
    public List<AuthorListDto> findAll(){
        return this.authorService.findAll();

    }

    //회원 상세 조회 : id로 조회 author/detail/1
    // 서버에서 별도의 try catch 하지 않으면, 에러 발생ㅅ ㅣ500에러 + 스프링의 포맷으로 에러 리턴
    @GetMapping("/detail/{id}")
    // ADMIN 권한이 있는지를 authentication 객체에서 쉽게 확인
    // 권한이 없을 경우 필터 체인에서 에러가 터진다.
//    @PreAuthorize("hasRole('ADMIN') or hasRole('SELLER')")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> findById(@PathVariable Long id){
        try{
            AuthorDetailDto author = this.authorService.findById(id);
            return new ResponseEntity<>(new CommonDto(author,HttpStatus.ACCEPTED.value(), "유저 조회 성공"), HttpStatus.ACCEPTED);
        }catch (NoSuchElementException e){
            e.printStackTrace();
            return new ResponseEntity<>(new CommonErrorDto(HttpStatus.NOT_FOUND.value(), "회원 조회 실패"), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/myinfo")
    public ResponseEntity<?> myInfo(){
        AuthorDetailDto authorDetailDto = authorService.myInfo();
        return new ResponseEntity<>(new CommonDto(authorDetailDto, HttpStatus.OK.value(), "내 정보 보기"), HttpStatus.OK);
    }

    //비밀번호 수정 : email,password -> json
    // get 조회 post 등록 patch 부분수정 put 대체 delete 삭제
    @PatchMapping("/updatePw")
    public ResponseEntity<String> updatePw(@RequestBody AuthorUpdatePw authorUpdatePw){
        try{
            this.authorService.updatePassword(authorUpdatePw);
            return new ResponseEntity<>("비밀번호 변경 완료", HttpStatus.ACCEPTED);
        }catch (RuntimeException e){
            return new ResponseEntity<>("비밀번호 변경 실패", HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/doLogin")
    public ResponseEntity<?> doLogin(@RequestBody AuthorLoginDto authorLoginDto){
        Author author = authorService.doLogin(authorLoginDto);
        // 토큰 생성 및 return
        String token = jwtTokenProvider.createAtToken(author);

        return new ResponseEntity<>(new CommonDto(token,HttpStatus.OK.value(),"토큰이 생성되었습니다."),HttpStatus.OK);
    }

    //회원퇄퇴(삭제) : /author/1
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id){
        try{
            this.authorService.delete(id);
            return new ResponseEntity<>("회원 탈퇴 완료", HttpStatus.ACCEPTED);
        }catch (RuntimeException e){
            return new ResponseEntity<>("회원 탈퇴 실패", HttpStatus.BAD_REQUEST);
        }



    }}