//package com.beyond.basic.b2_board.controller;
//
//import com.beyond.basic.b2_board.domain.Author;
//import com.beyond.basic.b2_board.dto.CommonDto;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//@RestController
//@RequestMapping("/response/entity")
//public class ResponseEntityController {
////    case1 : @ResponseStatus 어노테이션 사용
//    @ResponseStatus(HttpStatus.CREATED)
//    @PostMapping("/annotation1")
//    public String annotation1(){
//        return "ok";
//    }
//
////    case2. 메서드 체이닝 방식
//    @GetMapping("/channing1")
//    public ResponseEntity<?> channing1(){
//        Author author = new Author("test", "test@naver.com", "1234");
//        return ResponseEntity.status(HttpStatus.CREATED).body(author);
//    }
//
////    case3. ResponseEntity 객체를 직접 생성하는 방식(가장많이사용)
//    @GetMapping("/custom1")
//    public ResponseEntity<?> custom1(){
//        Author author = new Author("test", "test@naver.com", "1234");
//        return new ResponseEntity<>(author, HttpStatus.CREATED);
//    }
//
////    case3. ResponseEntity 객체를 직접 생성하는 방식(가장많이사용)
//    @GetMapping("/custom2")
//    public ResponseEntity<?> custom2(){
//        Author author = new Author("test", "test@naver.com", "1234");
//        return new ResponseEntity<>(new CommonDto(author, HttpStatus.CREATED.value(), "author is created succesfully"), HttpStatus.CREATED);
//    }
//}
