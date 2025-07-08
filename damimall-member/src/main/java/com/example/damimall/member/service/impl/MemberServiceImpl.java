package com.example.damimall.member.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.example.common.exception.BizCodeEnum;
import com.example.common.to.auth.OAuth2TokenInfo;
import com.example.common.to.auth.SimpleUserToken;
import com.example.common.to.member.LoginUserTo;
import com.example.common.to.member.MemberRegisterTo;
import com.example.common.utils.ObjectMapperUtils;
import com.example.common.utils.R;
import com.example.damimall.member.exception.PhoneExistException;
import com.example.damimall.member.exception.UsernameExistException;
import com.example.damimall.member.feign.OrderFeign;
import com.example.damimall.member.service.MemberLevelService;
import com.fasterxml.jackson.core.type.TypeReference;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.common.utils.PageUtils;
import com.example.common.utils.Query;

import com.example.damimall.member.dao.MemberDao;
import com.example.damimall.member.entity.MemberEntity;
import com.example.damimall.member.service.MemberService;


@Service("memberService")
public class MemberServiceImpl extends ServiceImpl<MemberDao, MemberEntity> implements MemberService {
    @Autowired
    MemberLevelService memberLevelService;

    @Autowired
    MemberDao memberDao;

    @Autowired
    OrderFeign orderFeign;

    private static final String INFO_URL = "https://gitee.com/api/v5/user";

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<MemberEntity> page = this.page(
                new Query<MemberEntity>().getPage(params),
                new QueryWrapper<MemberEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public R register(MemberRegisterTo memberRegisterTo) {
        MemberEntity memberEntity = new MemberEntity();
        Long defaultId = memberLevelService.getDefaultLevelId();
        if (defaultId != null) memberEntity.setLevelId(defaultId);

        String username = memberRegisterTo.getUsername();
        String password = memberRegisterTo.getPassword();
        String phone = memberRegisterTo.getPhone();
        
        // 检查用户名和手机号是否唯一
        try {
            checkUsernameUnique(username);
            checkPhoneUnique(phone);
        }catch (UsernameExistException e){
            return R.error(e.getMessage());
        }catch (PhoneExistException e){
            return R.error(e.getMessage());
        }

        memberEntity.setUsername(username);
        memberEntity.setMobile(phone);
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        memberEntity.setPassword(passwordEncoder.encode(password));
        memberEntity.setNickname("user:" + UUID.randomUUID());
        
        save(memberEntity);
        return R.ok();
    }

    @Override
    public R login(LoginUserTo loginUserTo) {
        String account = loginUserTo.getLoginacct();
        String password = loginUserTo.getPassword();

//        String truePassword = memberDao.getPasswordByAccount(account);
        MemberEntity memberEntity = query().eq("username", account).or().eq("mobile", account).one();

        if (memberEntity == null)
            return R.error(BizCodeEnum.LOGIN_ACCOUNT_NOT_EXIST_EXCEPTION.getCode(), BizCodeEnum.LOGIN_ACCOUNT_NOT_EXIST_EXCEPTION.getMsg());

        String truePassword = memberEntity.getPassword();

        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        if (!passwordEncoder.matches(password, truePassword)){
            return R.error(BizCodeEnum.LOGIN_PASSWORD_EXCEPTION.getCode(), BizCodeEnum.LOGIN_PASSWORD_EXCEPTION.getMsg());
        }

        SimpleUserToken userToken = new SimpleUserToken();
        userToken.setUserId(memberEntity.getId());
        userToken.setName(memberEntity.getNickname());

        return R.ok().setData(userToken);
    }

    public void checkUsernameUnique(String username) throws UsernameExistException{
        Long count = query().eq("username", username).count();
        if (count > 0) throw new UsernameExistException();
    }

    public void checkPhoneUnique(String phone) throws PhoneExistException{
        Long count = query().eq("mobile", phone).count();
        if (count > 0) throw new PhoneExistException();
    }

    @Override
    public R giteeLogin(OAuth2TokenInfo tokenInfo) {
        if (tokenInfo.getAccessToken() == null)
            return R.error(BizCodeEnum.SOCIAL_LOGIN_MISSING_TOKEN_EXCEPTION.getCode(), BizCodeEnum.SOCIAL_LOGIN_MISSING_TOKEN_EXCEPTION.getMsg());

        String accessToken = tokenInfo.getAccessToken();

        HttpGet getRequest = new HttpGet(INFO_URL + "?access_token=" + accessToken);
        MemberEntity member = new MemberEntity();
        try (CloseableHttpClient client = HttpClients.createDefault();
             CloseableHttpResponse response = client.execute(getRequest)) {

            if (response.getCode() != 200) return R.error();

            String json = EntityUtils.toString(response.getEntity());
            Map<String, String> simpleMap = ObjectMapperUtils.readValue(json, new TypeReference<Map<String, String>>() {});
            String id = simpleMap.get("id");
            String nickName = simpleMap.get("name");

            // 如果已经存在就更新时间
            MemberEntity socialMember = query().eq("social_uid", id).one();
            if (socialMember != null){
                member.setAccessToken(accessToken);
                member.setExpiresIn(tokenInfo.getExpiresIn());
                update(member, new UpdateWrapper<MemberEntity>().eq("social_uid", socialMember.getSocialUid()));
                return R.ok();
            }

            member.setNickname(nickName);
            member.setAccessToken(accessToken);
            member.setExpiresIn(tokenInfo.getExpiresIn());
            member.setSocialUid(id);
            Long defaultId = memberLevelService.getDefaultLevelId();
            if (defaultId != null) member.setLevelId(defaultId);

            save(member);


        } catch (ParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        SimpleUserToken userToken = new SimpleUserToken();
        userToken.setUserId(member.getId());
        userToken.setName(member.getNickname());
        return R.ok().setData(userToken);
    }

    @Override
    public R memberOrderPage(Integer pageNum) {
        Map<String, Object> param = new HashMap<>();
        param.put("page", pageNum);

        R r = orderFeign.listItems(param);
        return r;
    }

}