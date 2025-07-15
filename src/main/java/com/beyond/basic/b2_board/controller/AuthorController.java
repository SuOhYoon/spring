package com.beyond.basic.b2_board.controller;

import com.beyond.basic.b2_board.domain.Author;
import com.beyond.basic.b2_board.dto.*;
import com.beyond.basic.b2_board.service.AuthorService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
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
    public String save(@RequestBody AuthorCreateDto authorCreateDto){
        try{
            this.authorService.save(authorCreateDto);
            return "ok";
        }catch (IllegalArgumentException e){
            return e.getMessage();
        }
    }

    //회원목록 조회
    @GetMapping("/list")
    public List<AuthorListDto> findAll(){
        return this.authorService.findAll();

    }

    //회원 상세 조회 : id로 조회 author/detail/1
    // 서버에서 별도의 try catch 하지 않으면, 에러 발생ㅅ ㅣ500에러 + 스프링의 포맷으로 에러 리턴
    @GetMapping("/detail/{id}")
    public Object findById(@PathVariable Long id){
        try{
            return this.authorService.findById(id);
        }catch (RuntimeException e){
            e.printStackTrace();
            return new NoSuchElementException("검색 결과가 존재하지 않습니다.");
        }
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