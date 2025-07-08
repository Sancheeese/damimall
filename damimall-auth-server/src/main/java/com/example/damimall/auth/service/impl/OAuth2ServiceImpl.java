package com.example.damimall.auth.service.impl;

import com.example.common.to.auth.OAuth2TokenInfo;
import com.example.common.to.auth.SimpleUserToken;
import com.example.common.utils.JWTUserUtils;
import com.example.common.utils.ObjectMapperUtils;
import com.example.common.utils.R;
import com.example.damimall.auth.feign.MemberFeign;
import com.example.damimall.auth.service.OAuth2Service;
import com.fasterxml.jackson.core.type.TypeReference;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
public class OAuth2ServiceImpl implements OAuth2Service {
    private static final String CLIENT_ID = "45de77e0cc3e3991e568ae6d48f5e44245128c931c65cd76be7c0123221e4303";
    private static final String REDIRECT_URL = "http://auth.damimall.com/oauth2/gitee/success";
    private static final String CLIENT_SECRET = "1231600c7084b0db8dd58112d9ce82bf23758f977c533ef4cf167db017c30fae";

    @Autowired
    MemberFeign memberFeign;

    @Override
    public String socialGiteeLogin(String code, HttpServletResponse resp) {
        String getTokenURL = "https://gitee.com/oauth/token?grant_type=authorization_code";

        Map<String, String> params = new HashMap<>();
        params.put("code", code);
        params.put("client_id", CLIENT_ID);
        params.put("redirect_uri", REDIRECT_URL);
        params.put("client_secret", CLIENT_SECRET);

        HttpPost postRequest = new HttpPost(getTokenURL);
        StringEntity body = new StringEntity(ObjectMapperUtils.writeValueAsString(params), ContentType.APPLICATION_JSON);
        postRequest.setEntity(body);

        // 发送请求
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            try (CloseableHttpResponse response = client.execute(postRequest)) {
                if (response.getCode() != 200) return "redirect:http://auth.damimall.com/login.html";
                String json = EntityUtils.toString(response.getEntity());
                OAuth2TokenInfo OAuth2TokenInfo = ObjectMapperUtils.readValue(json, new TypeReference<OAuth2TokenInfo>() {});

                R r = memberFeign.gtieeLogin(OAuth2TokenInfo);
                SimpleUserToken userToken = r.getData(new TypeReference<SimpleUserToken>() {});
                JWTUserUtils.setToken(resp, userToken);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "redirect:http://damimall.com";
    }
}
