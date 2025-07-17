package com.back.domain.member.address.controller;

import com.back.domain.member.address.Service.AddressService;
import com.back.domain.member.address.entity.Address;
import com.back.domain.member.member.dto.MemberDto;
import com.back.domain.member.member.entity.Member;
import com.back.global.rq.Rq;
import com.back.global.rsData.RsData;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/addresses")
@RequiredArgsConstructor
@Tag(name = "AddressController", description = "주소 관리 엔드포인트")
public class AddressController {
    private final AddressService addressService;
    private final Rq rq;

    record AddressSubmitReqBody(
            @NotBlank
            String content
    ) {
    }

    record AddressSubmitResBody(
            Long id,
            String content,
            MemberDto member
    ) {
    }

    @PostMapping
    @Transactional
    @Operation(summary = "주소 등록")
    public RsData<AddressSubmitResBody>  submitAddress(
            @Valid @RequestBody AddressSubmitReqBody reqBody
    ) {
        Member member = rq.getActor();
        Address address = addressService.submitAddress(member, reqBody.content());

        return new RsData<>(
                201,
                "주소가 등록됐습니다.",
                new AddressSubmitResBody(
                        address.getId(),
                        address.getContent(),
                        new MemberDto(address.getMember())
                )
        );
    }



}
