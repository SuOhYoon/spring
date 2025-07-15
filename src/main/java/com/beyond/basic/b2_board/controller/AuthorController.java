package com.beyond.basic.b2_board.controller;

import com.beyond.basic.b2_board.domain.Author;
import com.beyond.basic.b2_board.dto.*;
import com.beyond.basic.b2_board.service.AuthorService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@RestController // Controller + ResponseBody
@RequiredArgsConstructor
@RequestMapping("/author")
public class AuthorController {
    //서비스 주입받기
    private final AuthorService authorService;

    //회원가입

    @PostMapping("/create")
    public ResponseEntity<?> save(@RequestBody AuthorCreateDto authorCreateDto){
//        try{
//            this.authorService.save(authorCreateDto);
//            return new ResponseEntity<>("OK", HttpStatus.OK);
//        }catch (IllegalArgumentException e){
//            e.printStackTrace();
////            생성자 매개변수 body부분의 객체와 header부에 상태코드
//            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
//        }
//        controllerAdvice가 없었으면 위와 같이 개별적인 예외처리가 필요하나, 이제는 전역적인 예외 처리가 가능하다.
        this.authorService.save(authorCreateDto);
        return new ResponseEntity<>("OK", HttpStatus.CREATED);
    }

    //회원목록 조회
    @GetMapping("/list")
    public List<AuthorListDto> findAll(){
        return this.authorService.findAll();
    }

    //회원 상세 조회 : id로 조회 author/detail/1
    // 서버에서 별도의 try catch 하지 않으면, 에러 발생ㅅ ㅣ500에러 + 스프링의 포맷으로 에러 리턴
    @GetMapping("/detail/{id}")
    public ResponseEntity<?> findById(@PathVariable("id") Long id){
//        try{
//            return new ResponseEntity<>(new CommonDto(authorService.findById(id), HttpStatus.OK.value(), "OK"), HttpStatus.OK);
//        }catch (RuntimeException e){
//            e.printStackTrace();
//            return new ResponseEntity<>(new CommonErrorDto(HttpStatus.NOT_FOUND.value(), e.getMessage()), HttpStatus.NOT_FOUND);
//        }
        AuthorDetailDto result = authorService.findById(id); // 반환 타입에 맞게 수정
        return new ResponseEntity<>(new CommonDto(result, HttpStatus.OK.value(), "성공"), HttpStatus.OK);
    }

    //비밀번호 수정 : email,password -> json
    // get 조회 post 등록 patch 부분수정 put 대체 delete 삭제
    @PatchMapping("/updatePw")
    public String updatePw(@RequestBody AuthorUpdatePw authorUpdatePw){
        try{
            this.authorService.updatePassword(authorUpdatePw);
            return "비밀번호 변경 완료";
        }catch (RuntimeException e){
            return "비밀번호 변경 실패";
        }
    }

    //회원퇄퇴(삭제) : /author/1
    @DeleteMapping("/delete/{id}")
    public String delete(@PathVariable Long id){
        try{
            this.authorService.delete(id);
            return "회원 탈퇴 완료";
        }catch (RuntimeException e){
            return e.getMessage();
        }
    }


}