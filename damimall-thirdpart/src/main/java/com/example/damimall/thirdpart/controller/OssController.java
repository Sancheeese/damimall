package com.example.damimall.thirdpart.controller;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.MatchMode;
import com.aliyun.oss.model.PolicyConditions;
import com.example.common.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
//import com.aliyun.sts20150401.models.AssumeRoleResponse;
//import com.aliyun.sts20150401.models.AssumeRoleResponseBody;
//import com.aliyun.tea.TeaException;
import com.aliyun.oss.common.utils.BinaryUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.codec.binary.Base64;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RestController()
@RequestMapping("/third-part/oss")
public class OssController {
//    @Autowired
//    OSS ossClient;

    @Value("${alibaba.cloud.oss.endpoint}")
    private String endpoint;

    @Value("${alibaba.cloud.oss.bucket}")
    private String bucket;

    @Value("${alibaba.cloud.access-key}")
    private String accessId;

    @Value("${alibaba.cloud.secret-key}")
    private String accessKey;

    private String region = "cn-shenzhen";

    private Long expire_time = 3600L;

//    @RequestMapping("/policy")
//    public R policy(){
//        String host = "https://" + bucket + "." + endpoint;
//        String callBack = "http://88.88.88.88:8888";
//        String prefix = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
//        String dir = prefix + "/";
//
//        Map<String, String> respMap = new LinkedHashMap<>();
//        try{
//            long expireTime = 30L;
//            long expireEndTime = System.currentTimeMillis() + expireTime * 1000;
//            Date expire = new Date(expireEndTime);
//            PolicyConditions policyConditions = new PolicyConditions();
//            policyConditions.addConditionItem(PolicyConditions.COND_CONTENT_LENGTH_RANGE, 0, 10 * 1024 * 1024);
//            policyConditions.addConditionItem(MatchMode.StartWith, PolicyConditions.COND_KEY, dir);
//
//            String postPolicy = ossClient.generatePostPolicy(expire, policyConditions);
//            byte[] binaryData = postPolicy.getBytes(StandardCharsets.UTF_8);
//            String encodedPolicy = BinaryUtil.toBase64String(binaryData);
//            String postSignature = ossClient.calculatePostSignature(encodedPolicy);
//
//            respMap.put("accessid", accessId);
//            respMap.put("policy", postPolicy);
//            respMap.put("signature", postSignature);
//            respMap.put("dir", dir);
//            respMap.put("host", host);
//            respMap.put("expire", String.valueOf(expireEndTime / 1000));
//        } catch (Exception e){
//            System.out.println(e);
//        }
//
//        return R.ok().put("data", respMap);
//    }


    public static String generateExpiration(long seconds) {
        // 获取当前时间戳（以秒为单位）
        long now = Instant.now().getEpochSecond();
        // 计算过期时间的时间戳
        long expirationTime = now + seconds;
        // 将时间戳转换为Instant对象，并格式化为ISO8601格式
        Instant instant = Instant.ofEpochSecond(expirationTime);
        // 定义时区
        ZoneId zone = ZoneId.systemDefault();  // 使用系统默认时区
        // 将 Instant 转换为 ZonedDateTime
        ZonedDateTime zonedDateTime = instant.atZone(zone);
        // 定义日期时间格式，例如2023-12-03T13:00:00.000Z
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        // 格式化日期时间
        String formattedDate = zonedDateTime.format(formatter);
        // 输出结果
        return formattedDate;
    }

    @RequestMapping("/policy")
    public R getPostSignatureForOssUpload() throws Exception {
        String accesskeyid = accessId;
        String accesskeysecret = accessKey;

        String host = "http://" + bucket + "." + endpoint;
        String upload_dir = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

        //获取x-oss-credential里的date，当前日期，格式为yyyyMMdd
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        String date = today.format(formatter);

        //获取x-oss-date
        ZonedDateTime now = ZonedDateTime.now().withZoneSameInstant(java.time.ZoneOffset.UTC);
        DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss'Z'");
        String x_oss_date = now.format(formatter2);

        // 步骤1：创建policy。
        String x_oss_credential = accesskeyid + "/" + date + "/" + region + "/oss/aliyun_v4_request";

        ObjectMapper mapper = new ObjectMapper();

        Map<String, Object> policy = new HashMap<>();
        policy.put("expiration", generateExpiration(expire_time));

        List<Object> conditions = new ArrayList<>();

        Map<String, String> bucketCondition = new HashMap<>();
        bucketCondition.put("bucket", bucket);
        conditions.add(bucketCondition);

        Map<String, String> signatureVersionCondition = new HashMap<>();
        signatureVersionCondition.put("x-oss-signature-version", "OSS4-HMAC-SHA256");
        conditions.add(signatureVersionCondition);

        Map<String, String> credentialCondition = new HashMap<>();
        credentialCondition.put("x-oss-credential", x_oss_credential); // 替换为实际的 access key id
        conditions.add(credentialCondition);

        Map<String, String> dateCondition = new HashMap<>();
        dateCondition.put("x-oss-date", x_oss_date);
        conditions.add(dateCondition);

        conditions.add(Arrays.asList("content-length-range", 1, 10240000));
        conditions.add(Arrays.asList("eq", "$success_action_status", "200"));
        conditions.add(Arrays.asList("starts-with", "$key", upload_dir));

        policy.put("conditions", conditions);

        String jsonPolicy = mapper.writeValueAsString(policy);

        // 步骤2：构造待签名字符串（StringToSign）。
        String stringToSign = new String(Base64.encodeBase64(jsonPolicy.getBytes()));

        // 步骤3：计算SigningKey。
        byte[] dateKey = hmacsha256(("aliyun_v4" + accesskeysecret).getBytes(), date);
        byte[] dateRegionKey = hmacsha256(dateKey, region);
        byte[] dateRegionServiceKey = hmacsha256(dateRegionKey, "oss");
        byte[] signingKey = hmacsha256(dateRegionServiceKey, "aliyun_v4_request");

        // 步骤4：计算Signature。
        byte[] result = hmacsha256(signingKey, stringToSign);
        String signature = BinaryUtil.toHex(result);

        Map<String, String> response = new HashMap<>();
        // 将数据添加到 map 中
        response.put("version", "OSS4-HMAC-SHA256");
        // 这里是易错点，不能直接传policy，需要做一下Base64编码
        response.put("policy", stringToSign);
//        response.put("policy", postPolicy);
        response.put("accessKeyId", accessId);
        response.put("credential", x_oss_credential);
        response.put("ossdate", x_oss_date);
        response.put("signature", signature);
//        response.put("signature", postSignature);
        response.put("dir", upload_dir);
        response.put("host", host);
        response.put("success_action_status", String.valueOf(200));

        return R.ok().put("data", response);
    }
    public static byte[] hmacsha256(byte[] key, String data) {
        try {
            // 初始化HMAC密钥规格，指定算法为HMAC-SHA256并使用提供的密钥。
            SecretKeySpec secretKeySpec = new SecretKeySpec(key, "HmacSHA256");

            // 获取Mac实例，并通过getInstance方法指定使用HMAC-SHA256算法。
            Mac mac = Mac.getInstance("HmacSHA256");
            // 使用密钥初始化Mac对象。
            mac.init(secretKeySpec);

            // 执行HMAC计算，通过doFinal方法接收需要计算的数据并返回计算结果的数组。
            byte[] hmacBytes = mac.doFinal(data.getBytes());

            return hmacBytes;
        } catch (Exception e) {
            throw new RuntimeException("Failed to calculate HMAC-SHA256", e);
        }
    }
}
