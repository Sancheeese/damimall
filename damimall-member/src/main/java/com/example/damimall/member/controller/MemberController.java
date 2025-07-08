package com.example.damimall.member.controller;

import java.util.Arrays;
import java.util.Map;

// import org.apache.shiro.authz.annotation.RequiresPermissions;
import com.example.common.to.auth.OAuth2TokenInfo;
import com.example.common.to.member.LoginUserTo;
import com.example.common.to.member.MemberRegisterTo;
import com.example.damimall.member.feign.CouponFeignService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.example.damimall.member.entity.MemberEntity;
import com.example.damimall.member.service.MemberService;
import com.example.common.utils.PageUtils;
import com.example.common.utils.R;



/**
 * 会员
 *
 * @author zhangshurui
 * @email 2689142369@qq.com
 * @date 2025-02-08 00:16:33
 */
@RestController
@RequestMapping("member/member")
public class MemberController {
    @Autowired
    private MemberService memberService;

    @Autowired
    CouponFeignService couponFeignService;

    @RequestMapping("/test")
    public R memberTest(){
        return couponFeignService.memberCoupon();
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
    // @RequiresPermissions("member:member:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = memberService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    // @RequiresPermissions("member:member:info")
    public R info(@PathVariable("id") Long id){
		MemberEntity member = memberService.getById(id);

        return R.ok().put("member", member);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    // @RequiresPermissions("member:member:save")
    public R save(@RequestBody MemberEntity member){
		memberService.save(member);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    // @RequiresPermissions("member:member:update")
    public R update(@RequestBody MemberEntity member){
		memberService.updateById(member);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    // @RequiresPermissions("member:member:delete")
    public R delete(@RequestBody Long[] ids){
		memberService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

    @PostMapping("/registerMember")
    public R register(@RequestBody MemberRegisterTo memberRegisterTo){
        return memberService.register(memberRegisterTo);
    }

    @PostMapping("/login")
    public R login(@RequestBody @Validated LoginUserTo loginUserTo){
        return memberService.login(loginUserTo);
    }

    @PostMapping("/oauth2Gitee/login")
    public R gtieeLogin(@RequestBody OAuth2TokenInfo OAuth2TokenInfo){
        return memberService.giteeLogin(OAuth2TokenInfo);
    }

    @PostMapping("/hello")
    public R hello(){
        System.out.println("hello");
        return R.ok();
    }
}
