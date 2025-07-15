package com.beyond.basic.b1_hello.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;
import java.util.Map;


//어노테이션 내에 컴포넌트 어노테이션이 붙어있을 경우 별도의 객체를 생성할 필요가 없는 싱글톤 객체 생성한다.
//Controller 어노테이션을 통해 쉽게 사용자의 http rep를 분석하고, http res를 생서해준다.

@Controller
@RequestMapping("/hello")
public class HelloController {

    //get 요청 case들
    // case1. 서버가 사용자에게 단순히 String 데이터 return - @ResponseBody 있을 때

    @GetMapping("") //아래 메서드에 대한 서버의 엔드포인트 설정
// ResponseBody가 없고 return 타입이 String인 경우 서버는 templates폴더 밑에 파일 명을 찾는다.
//    @ResponseBody
    public String helloWorld(){
        return "helloworld";
    }

    // case 2. 서버가 사용자에게 String(json 형식)의 데이터 return

    @GetMapping("/json")
    @ResponseBody
    public Hello helloJson() throws JsonProcessingException {
        //직접 josn으로 직렬할 필요업이 return 타입에 객체가 있으면 자동으로 직렬화
        Hello h1 = new Hello("jinwoo", "jin@naver.com");
//        ObjectMapper objectMapper = new ObjectMapper();
//        String result = objectMapper.writeValueAsString(h1);
        return h1;
    }


    // case 3. parameter 방식을 통해 사용자로부터 값을 수신
    // parameter 형식 : /member?name=hongildong

    @GetMapping("/param")
    @ResponseBody
    public Hello param(@RequestParam(value = "name")String inputName){
        return new Hello(inputName, inputName+"@naver.com");
    }

    // case 4. pathVariable 방식
    // /member/1
    // pathVariable 방식은 url을 통해 자원의 구조를 명확하게 표현할 때 사용 좀 더 restful함
    @GetMapping("/path/{inputId}")
    @ResponseBody
    public String path(@PathVariable long inputId){
        //별도의 형변환 없이도, 매개변수에 타입지정 시 자동형변환 시켜줌.
        //        long id = Long.parseLong(inputId);
        System.out.println(inputId);
        return "OK";
    }

    // case 5. parameter 2개 이상 형식
    @GetMapping("/param2")
    @ResponseBody
    public Hello param2(@RequestParam(value = "name")String inputName, @RequestParam(value = "email")String inputEmail){

        return new Hello(inputName,inputEmail);
    }

    //case 6. parameter가 많아질 경우, 데이터 바인딩을 통해 input값 처리
    // 데이터 바인딩 : param을 이용하여 객체로 생성

    @GetMapping("/param3")
    @ResponseBody
//    public String param3(Hello hello)
    public String param3(@ModelAttribute Hello hello){
        //param 형식의 데이터를 받겠다라는 것을 명시적으로 표현
        System.out.println(hello);
        return "ok";
    }

    // case 7. 서버에서 화면을 return, 사용자로부터 넘어오는 input 값을 활용하여 동적인 화면 생성
    // 서버에서 화면(+데이터)을 렌더링 해주는 ssr 방식 ( csr은 서버는 데이터만)
    // mvc(model, view , controller ) 패턴이라고도 함
    @GetMapping("/model-param")
    public String modelParam(@RequestParam(value="id")Long inputId, Model model){
        // model 객체는 데이터를 화면에 전달해주는 역할을 한다.
        // name 이라는 키에 hong을 key value로 전달
        if(inputId==1){
            model.addAttribute("name" , "hong1");
            model.addAttribute("email" , "hong1@naver.com");
        }else if (inputId==2){
            model.addAttribute("name" , "hong2");
            model.addAttribute("email" , "hong2@naver.com");
        }

        return "helloworld2";
    }

    //post 요청의 case들 : url 인코딩 방식 또는 multipart-formdata, json
    // case1. text만 있는 form-data 형식
    @GetMapping("/form-view")
    public String formView(){
        return "form-view";
    }

    @PostMapping("/form-view")
    @ResponseBody
    // get 요청에 url에 파라미터방식과 동일한 데이터 형식이므로, RequestParam 데이터 바인딩 방식 가능
    public String formViewPost(@ModelAttribute Hello hello, HttpServletRequest httpServletRequest){
        httpServletRequest.getParameterMap().forEach((key, value) ->
                System.out.println(key + " = " + Arrays.toString(value))
        );
        System.out.println(hello);
        return"ok";
    }


    // case 2-1. text와 file이 있는 form-data 형식(순수 html로 제출)
    @GetMapping("/form-file-view")
    public String formFileView(){
        return "form-file-view";
    }

    @PostMapping("/form-file-view")
    @ResponseBody
    public String formFileViewPost(@ModelAttribute Hello hello,
                                   @RequestParam(value = "photo")MultipartFile photo){
        System.out.println(hello);
        System.out.println(photo.getOriginalFilename());
        return"ok";
    }
    // case 2-2. text와 file이 있는 form-data 형식(js 로 제출)
    @GetMapping("/axios-file-view")
    public String axiosFileView(){
        return "axios-file-view";
    }

    // case 3. text와 멀티 file이 있는 form-data 형식
    @GetMapping("/axios-multi-file-view")
    public String axiosMultiFileView(){
        return "axios-multi-file-view";
    }

    @PostMapping("/axios-multi-file-view")
    @ResponseBody
    public String axiosMultiFileViewPost(@ModelAttribute Hello hello,
                                         @RequestParam(value = "photos") List<MultipartFile> photos){
        System.out.println(hello);
        for(int i=0; i<photos.size(); i++){
            System.out.println(photos.get(i).getOriginalFilename());

        }
        return"ok";
    }
    // case 4. json 데이터 처리

    @GetMapping("/axios-json-view")
    public String axiosJsonView(){
        return "axios-json-view";
    }

    @PostMapping("/axios-json-view")
    @ResponseBody
    //RequestBody : json 형식으로 데이터가 들어올 때 객체로 자동 파싱
    public String axiosJsonViewPost(@RequestBody Hello hello){
        System.out.println(hello);
        return "ok";
    }


    // case 5. 중첩된 json 데이터 처리

    @GetMapping("/axios-nested-json-view")
    public String axiosNestedJsonView(){
        return "axios-nested-json-view";
    }

    @PostMapping("/axios-nested-json-view")
    @ResponseBody
    //RequestBody : json 형식으로 데이터가 들어올 때 객체로 자동 파싱
    public String axiosNestedJsonViewPost(@RequestBody Student student){
        System.out.println(student);

        return "ok";
    }

    // case 6. json+file 같이 처리할 떄  (text는 json으로)
    // 데이터 형식 : hello = {name:"xx", email : "xx"}&photo=이미지.jpg
    // 결론은 단순 json 구조가 아닌, multipart-formdata구조안에 json을 넣는 구조
    @GetMapping("/axios-json-file-view")
    public String axiosJsonFileView(){
        return "axios-json-file-view";
    }

    @PostMapping("/axios-json-file-view")
    @ResponseBody
    public String axiosJsonFileViewPost(@RequestPart("hello")Hello hello,
                                        @RequestPart("photo")MultipartFile photo){
        System.out.println(hello);
        System.out.println(photo.getOriginalFilename());
        return "ok";
    }

}