package com.example.damimall.auth.feign;

import com.example.common.to.auth.OAuth2TokenInfo;
import com.example.common.to.member.LoginUserTo;
import com.example.common.to.member.MemberRegisterTo;
import com.example.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient("damimall-member")
public interface MemberFeign {
    @PostMapping("/member/member/registerMember")
    public R register(@RequestBody MemberRegisterTo memberRegisterTo);

    @PostMapping("/member/member/login")
    public R login(@RequestBody @Validated LoginUserTo loginUserTo);

    @PostMapping("/member/member/oauth2Gitee/login")
    public R gtieeLogin(@RequestBody OAuth2TokenInfo OAuth2TokenInfo);

    @PostMapping("member/member/hello")
    public R hello();
}
