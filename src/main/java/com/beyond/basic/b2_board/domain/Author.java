package com.beyond.basic.b2_board.domain;

import com.beyond.basic.b2_board.dto.AuthorDetailDto;
import com.beyond.basic.b2_board.dto.AuthorListDto;
import com.beyond.basic.b2_board.repository.AuthorMemoryRepository;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
// JPA를 사용할경우 Entity에 반드시 붙여야하는 어노테이션
// JPA의 EntityManager에게 객체를 위임하기 위한 어노테이션
// 엔티티매니저는 영속성컨텍스트(엔티티의현재상황)를 통해 db 데이터 관리
@Entity
public class Author {
    @Id
//    identity : auto_increment, auto : id생성전략을 jpa에게 자동설정하도록 위임하는것.
    @GeneratedValue(strategy = GenerationType.IDENTITY)// pk 설정
    private Long id;
//    컬럼에 별다른 설정이 없을 경우 default varchar(255)
    private String name;
    @Column(length = 50, unique = true, nullable = false)
    private String email;
//    @Column(name = "pw") : 되도록이면 컬럼명과 변수명을 일치시키는것이 개발의 혼선을 줄일 수 있음.
    private String password;
    @CreationTimestamp
    private LocalDateTime createdTime;
    @UpdateTimestamp
    private LocalDateTime updatedTime;
//    컬럼명에 캐멀케이스 사용시, db에는 created_time으로 컬럼 생성
    public Author(String name, String email, String password) {
//        this.id = AuthorMemoryRepository.id;
        this.name = name;
        this.email = email;
        this.password = password;
    }

    public void updatePw(String password) {
        this.password = password;
    }

    public AuthorDetailDto detailFromEntity(){
        return new AuthorDetailDto(this.id, this.name, this.email);
    }

    public AuthorListDto listFromEntity() {
        return new AuthorListDto(this.id, this.name);
    }
}