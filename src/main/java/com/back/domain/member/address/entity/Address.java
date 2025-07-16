package com.back.domain.member.address.entity;

import com.back.domain.member.member.entity.Member;
import com.back.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Address extends BaseEntity {
    @Column(nullable=false)
    private String address;

    private Boolean isDefault = false;

    @ManyToOne(fetch = FetchType.LAZY) //지연 로딩으로 성능 최적화
    @JoinColumn(name= "member_id", referencedColumnName= "id") //외래키 설정
    private Member member;

}
