package com.beyond.basic.b2_board.author.domain;

import com.beyond.basic.b2_board.author.dto.AuthorDetailDto;
import com.beyond.basic.b2_board.author.dto.AuthorListDto;
import com.beyond.basic.b2_board.common.baseTimeEntity.BaseTimeEntity;
import com.beyond.basic.b2_board.post.domain.Post;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
public class Author extends BaseTimeEntity {

    @Id
    // identity : auto_increment,
    // auto : id 생성 전략을 jpa에게 자동설정하도록 위임 하는 것.
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    // 컬럼에 별다른 설정이 없을 경우 varchar(255)가 default
    private String name;
//        @Column(name = "pw") : 되도록이면 컬럼명과 변수명을 일치시키는 것이 혼선을 줄일 수 있음

    @Column(length = 50, nullable = false)
    private String password;
    private String email;
    @Enumerated(EnumType.STRING)
    @Builder.Default // 빌더 패턴에서 변수 초기화 시 Builder.default 필수
    private Role role = Role.USER;
    //컬럼명에 캐멀케이스 사용 시, db에는 created_time으로 컬럼 생성

    //OneToMany는 선택 사항이다. 또한 default가 lazy다.
    //mappedBy 에는 ManyToOne쪽에 변수명을 문자열로 지정 fk 관리를 반대편 쪽에서 한다는 의미 -> 연관관계 주인 설정
    @OneToMany(mappedBy = "author", fetch = FetchType.LAZY)
    @Builder.Default
    List<Post> postList = new ArrayList<>();

//    public Author(String name, String password, String email) {
////        this.id = AuthorMemoryRepository.id;
    //        this.name = name;
//        this.password = password;
//        this.email = email;
//    }  public Author(String name, String password, String email, Role role) {
////        this.id = AuthorMemoryRepository.id;
//        this.name = name;
//        this.password = password;
//        this.email = email;
//        this.role=role==null?Role.USER:role;
//    }



    public void updatePw(String password) {
        this.password = password;
    }

//    public AuthorDetailDto detailFromEntity(){
//        return new AuthorDetailDto(id,name,email);
//    }

    public AuthorListDto listFromEntity(){
        return new AuthorListDto(id,name);
    }

    @Override
    public String toString() {
        return "Author{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", password='" + password + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}