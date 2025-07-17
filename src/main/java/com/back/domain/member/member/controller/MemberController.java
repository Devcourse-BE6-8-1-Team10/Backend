package com.back.domain.member.member.controller;

import com.back.domain.member.member.dto.MemberDto;
import com.back.domain.member.member.dto.MemberWithAuthDto;
import com.back.domain.member.member.entity.Member;
import com.back.domain.member.member.service.MemberService;
import com.back.global.exception.ServiceException;
import com.back.global.rq.Rq;
import com.back.global.rsData.RsData;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
@Tag(name = "MemberController", description = "회원 관련 API 컨트롤러")
public class MemberController {
    private final MemberService memberService;
    private final Rq rq;

    record MemberJoinReqBody(
            @NotBlank
            @Email
            String email,

            @NotBlank
            @Size(min = 8, max = 20)
            String password,

            @NotBlank
            @Size(min = 2, max = 30)
            String name
    ) { }


    @PostMapping("/join")
    @Transactional
    @Operation(summary = "회원 가입")
    public RsData<MemberDto> join(
            @Valid @RequestBody MemberJoinReqBody reqBody
    ) {
        Member member = memberService.join(
                reqBody.email(),
                reqBody.password(),
                reqBody.name()
        );

        return new RsData<>(
                201,
                "%s님 환영합니다. 회원가입이 완료되었습니다.".formatted(member.getName()),
                new MemberDto(member)
        );
    }

    record MemberLoginReqBody(
            @NotBlank
            @Email
            String email,

            @NotBlank
            @Size(min = 8, max = 50)
            String password
    ) { }

    record MemberLoginResBody(
            MemberWithAuthDto member,
            String apiKey,
            String accessToken
    ) { }


    @PostMapping("/login")
    @Transactional
    @Operation(summary = "회원 로그인")
    public RsData<MemberLoginResBody> login(
            @Valid @RequestBody MemberJoinReqBody reqBody
    ) {
        Member member = memberService.findByEmail(reqBody.email())
                .orElseThrow(() -> new ServiceException(401, "존재하지 않는 이메일입니다."));

        memberService.checkPassword(
                member,
                reqBody.password()
        );

        String accessToken = memberService.genAccessToken(member);

        rq.setCookie("apkKey", member.getApiKey());
        rq.setCookie("accessToken", accessToken);

        return new RsData<>(
                200,
                "%s님 환영합니다.".formatted(member.getName()),
                new MemberLoginResBody(
                        new MemberWithAuthDto(member),
                        member.getApiKey(),
                        accessToken
                )
        );
    }




}
