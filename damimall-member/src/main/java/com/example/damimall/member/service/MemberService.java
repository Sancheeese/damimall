package com.example.damimall.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.common.to.auth.OAuth2TokenInfo;
import com.example.common.to.member.LoginUserTo;
import com.example.common.to.member.MemberRegisterTo;
import com.example.common.utils.PageUtils;
import com.example.common.utils.R;
import com.example.damimall.member.entity.MemberEntity;

import java.util.Map;

/**
 * 会员
 *
 * @author zhangshurui
 * @email 2689142369@qq.com
 * @date 2025-02-08 00:16:33
 */
public interface MemberService extends IService<MemberEntity> {

    PageUtils queryPage(Map<String, Object> params);

    R register(MemberRegisterTo memberRegisterTo);

    R login(LoginUserTo loginUserTo);

    R giteeLogin(OAuth2TokenInfo socialUser);

    R memberOrderPage(Integer pageNum);
}

