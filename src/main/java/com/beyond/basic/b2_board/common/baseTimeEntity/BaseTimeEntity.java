package com.beyond.basic.b2_board.common.baseTimeEntity;

import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

// 기본적으로 Entity는 상속이 불가능, @MappedSuperclass어노테이션 사용시 상속가능.
@MappedSuperclass
@Getter
public class BaseTimeEntity {
//    컬럼명에 캐멀케이스 사용시, db에는 created_time으로 컬럼 생성
    @CreationTimestamp
    private LocalDateTime createdTime;
    @UpdateTimestamp
    private LocalDateTime updatedTime;
}
