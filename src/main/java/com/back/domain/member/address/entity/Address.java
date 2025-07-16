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
    // ------------ [필드] ------------
    @Column(nullable=false)
    private String address;

    @Column(nullable=false)
    private Boolean isDefault;

    @ManyToOne(fetch = FetchType.LAZY) //지연 로딩으로 성능 최적화
    @JoinColumn(name= "member_id", referencedColumnName= "id") //외래키 설정
    private Member member;

    // ------------ [생성자] ------------
    public Address(String address, Boolean isDefault, Member member) {
        if (address == null || address.isBlank())
            throw new IllegalArgumentException("주소는 비어있을 수 없습니다.");
        if (member == null)
            throw new IllegalArgumentException("회원 정보는 비어있을 수 없습니다.");

        this.address = address;
        this.isDefault = isDefault;
        this.member = member;
    }

    public Address(String address, Member member){
        this(address, false, member);
    }

    // ------------ [메서드] ------------
    public void setDefault(Boolean isDefault) {
        this.isDefault = isDefault;
    }

}
