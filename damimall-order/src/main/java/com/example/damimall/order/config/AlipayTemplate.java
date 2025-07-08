package com.example.damimall.order.config;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.AlipayConfig;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.*;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.response.AlipayTradePagePayResponse;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.example.damimall.order.vo.PayVo;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.ArrayList;

@Component
public class AlipayTemplate {
    public String pay(PayVo payVo) throws AlipayApiException {
        // 初始化SDK
        AlipayClient alipayClient = new DefaultAlipayClient(getAlipayConfig());

        // 构造请求参数以调用接口
        AlipayTradePagePayRequest request = new AlipayTradePagePayRequest();
        AlipayTradePagePayModel model = new AlipayTradePagePayModel();

        // 设置商户订单号
        model.setOutTradeNo(payVo.getOrderSn());

        // 设置订单总金额
        model.setTotalAmount(payVo.getTotalAmount().setScale(2, RoundingMode.UP).toString());

        // 设置订单标题
        model.setSubject("很多东西");

        // 设置产品码
        model.setProductCode("FAST_INSTANT_TRADE_PAY");

        LocalDateTime expire = LocalDateTime.now().plusMinutes(15);
        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        model.setTimeExpire(format.format(expire));

        // 设置PC扫码支付的方式
//        model.setQrPayMode("1");

        // 设置商户自定义二维码宽度
        model.setQrcodeWidth(100L);

        // 设置订单包含的商品列表信息
//        List<GoodsDetail> goodsDetail = new ArrayList<>();
//        GoodsDetail goodsDetail0 = new GoodsDetail();
//        goodsDetail0.setGoodsName("ipad");
//        goodsDetail0.setAlipayGoodsId("20010001");
//        goodsDetail0.setQuantity(1L);
//        goodsDetail0.setPrice("2000");
//        goodsDetail0.setGoodsId("apple-01");
//        goodsDetail0.setGoodsCategory("34543238");
//        goodsDetail0.setCategoriesTree("124868003|126232002|126252004");
//        goodsDetail0.setShowUrl("http://www.alipay.com/xxx.jpg");
//        goodsDetail.add(goodsDetail0);
//        model.setGoodsDetail(goodsDetail);

        // 设置订单绝对超时时间
//        model.setTimeExpire("2016-12-31 10:05:01");

        // 设置二级商户信息
//        SubMerchant subMerchant = new SubMerchant();
//        subMerchant.setMerchantId("2088000603999128");
//        subMerchant.setMerchantType("alipay");
//        model.setSubMerchant(subMerchant);
//
//        // 设置业务扩展参数
//        ExtendParams extendParams = new ExtendParams();
//        extendParams.setSysServiceProviderId("2088511833207846");
//        extendParams.setHbFqSellerPercent("100");
//        extendParams.setHbFqNum("3");
//        extendParams.setIndustryRefluxInfo("{\"scene_code\":\"metro_tradeorder\",\"channel\":\"xxxx\",\"scene_data\":{\"asset_name\":\"ALIPAY\"}}");
//        extendParams.setRoyaltyFreeze("true");
//        extendParams.setCardType("S0JP0000");
//        model.setExtendParams(extendParams);
//
//        // 设置商户传入业务信息
//        model.setBusinessParams("{\"mc_create_trade_ip\":\"127.0.0.1\"}");
//
//        // 设置优惠参数
//        model.setPromoParams("{\"storeIdType\":\"1\"}");
//
//        // 设置请求后页面的集成方式
//        model.setIntegrationType("PCWEB");
//
//        // 设置请求来源地址
//        model.setRequestFromUrl("https://");
//
//        // 设置商户门店编号
//        model.setStoreId("NJ_001");
//
//        // 设置商户的原始订单号
//        model.setMerchantOrderNo("20161008001");
//
//        // 设置外部指定买家
//        ExtUserInfo extUserInfo = new ExtUserInfo();
//        extUserInfo.setCertType("IDENTITY_CARD");
//        extUserInfo.setCertNo("362334768769238881");
//        extUserInfo.setName("李明");
//        extUserInfo.setMobile("16587658765");
//        extUserInfo.setMinAge("18");
//        extUserInfo.setNeedCheckInfo("F");
//        extUserInfo.setIdentityHash("27bfcd1dee4f22c8fe8a2374af9b660419d1361b1c207e9b41a754a113f38fcc");
//        model.setExtUserInfo(extUserInfo);
//
//        // 设置开票信息
//        InvoiceInfo invoiceInfo = new InvoiceInfo();
//        InvoiceKeyInfo keyInfo = new InvoiceKeyInfo();
//        keyInfo.setTaxNum("1464888883494");
//        keyInfo.setIsSupportInvoice(true);
//        keyInfo.setInvoiceMerchantName("ABC|003");
//        invoiceInfo.setKeyInfo(keyInfo);
//        invoiceInfo.setDetails("[{\"code\":\"100294400\",\"name\":\"服饰\",\"num\":\"2\",\"sumPrice\":\"200.00\",\"taxRate\":\"6%\"}]");
//        model.setInvoiceInfo(invoiceInfo);

        request.setBizModel(model);
        // 第三方代调用模式下请设置app_auth_token
        // request.putOtherTextParam("app_auth_token", "<-- 请填写应用授权令牌 -->");

        request.setReturnUrl("http://member.damimall.com/memberOrder.html");
        request.setNotifyUrl("http://1064iw6ll7512.vicp.fun/payed/notify");
        AlipayTradePagePayResponse response = alipayClient.pageExecute(request, "POST");
        // 如果需要返回GET请求，请使用
        // AlipayTradePagePayResponse response = alipayClient.pageExecute(request, "GET");
        String pageRedirectionData = response.getBody();
        System.out.println(pageRedirectionData);

        if (response.isSuccess()) {
            System.out.println("调用成功");
        } else {
            System.out.println("调用失败");
            // sdk版本是"4.38.0.ALL"及以上,可以参考下面的示例获取诊断链接
            // String diagnosisUrl = DiagnosisUtils.getDiagnosisUrl(response);
            // System.out.println(diagnosisUrl);
        }

        return pageRedirectionData;
    }

    private static AlipayConfig getAlipayConfig() {
        String privateKey = "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQCO3A5QKAIAVEmbIkBay0pH/K8nfalaHK1h/Eww2tydc7RtNqAw+MUCUeFSJx6+nzEvYLp4f+5c1Wo8F8kMJCtEGMnsicyCA6xtGZp3yu3O9qe+PpbsCuG02oYA3AbS8b9l6VF7PooDW+eT95Dt01uCbVSLLVB2qkxbF8o9jjbbj0bdd5scrbl/+mam+dHgANg4HcgB+aIyYSHPEmjT50SEbrcf3NJMw4taI6KzNyp4NT/e169Tkzb3aFAG3G+BMlbJr9UMPW1wLzroI3Mwkyh8DChMRgEHQZeYWBHkxT5oV9c/k0bxjPnqTgo6pw98tRa+XquCJJwYaMAj2q90mx7nAgMBAAECggEAV+mffUBBiwZTjX4UcEst2QLsnra74tJG+EeUc3Qb02ECpTq6daiBRB4zNKUSTGgWkntKBSFlBAfpKjXQ6QHRBkTi7gcWrOOp1jGtVw6wLW6oZ3CI25UOtIbNc/Xy1RkjmYltNRSVRd/msK3mqBQq+iZhoowuOfguqNgJArxjkdS0NBEa7L5f80lxnZy5x6y1Vk+b8dwjKi0t1a7qP12A4WGBhJqOr4xHKffQIUgKIsfPrKc2mmjcmJP+LjzFQS2tz2vNColnFgnD5NGwxn5zT678IK+Ve8TxORPsCUD2BbSrgTqixGsVViuaOPUiPv3JBcemi2pYnxnugrtFox9vgQKBgQD5LPeEvd4IG26iNkkkyWvR2aFGgbjy8qEGV0yrYfJq6ab+kHa5/uUfhYlXKBZhpcv6LoGmPIv3F3l5CSU2/xXNcUJZQxHpkxiFFfOwmqy4K54oIC1isPnmN8b8V84LUViYy9E5vGL7VS+OMnIgOgs8B4NdDzvTiojgkZ/nf4+8BwKBgQCSxa4wtsf+xcUoKuBwh32e/25PmWoGyyepmTt9MVBRXbnnHOzVk8NJczyNeckGmsSpnDvc0u8vg5v5Fs7mj4ZBorXK9ss/0hBWcBSIBWhWgPy41vNrmSFvjVkuC4f0n/l8PZygQA66AeYaMuRwYhWF1iVumWVMX2TFEm2CTviOIQKBgQCAVqvJ/+0LKe3quKElxGS3Oit54Di9UUCkvnQr1ZABhwTPGuM721Bx0tqxPGGCu916p1d5orfIdZvIPnCfifTV0t8EkKn6Lxwy9+KC9Le/SB/2tNQLf/HGpLXlpOfoJmbGlDvYS/KDVxXeXJcxiBPfQFNlrAH5a+IiIH9GwgnrIwKBgC7ZSJOmFEp/aEI9BW0F4pQ45kYkKxLGD//zuGhqsD94kllyAyrdQo2UafSitgcUqceV5gnDjrN05/lnTV2M3Ibg3/RMOG5DqBFfLz6Hs1lBTOnXOtXSiDiqPpgS+C+eTK1aoT8Jk2nOJ1ufeQliVYpl44bdEdeB75F0I15fxLjhAoGBAND0QOFP4Nzg/pKhGe+QjYwbvmIsHSkEZkAZIFTiSG584cL49mJ7QZOQyyQOfvdQiuNigwTe9ZBsCgSJUjzlw0DmtDAOKQtcxMKHL1Q4ZBDcFvDUSDxFSjzLcGqprXgmWiuachwNJRAl8iMQu+dHUsGuK+VKsMk75hseiGEGINbN";
        String alipayPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAjr4SFsXZe+52ZcT6h+Y3UTI93gObuIQfAZl2dL2cVSdyvZ/N5Zw/d8+NWWrmVkBu9BVFRA8XIxMD1bXqKwQbznCpG3dGYY85aJ+zujNkEyMx/2PqLaqT8QUtblMAJsvM5OaaSPNpVP0A1gi2H9nqxTmVEPOI6CORvgoKmFXkZgNQXtNwgf77XlwnK609ZPpL64uiLGUI6qZ2a5JwHA9skldIzwehANu9KqNVClAIbSKmMeJr4JlgYcbC/9m0P2ZYxmC6Q7MU03/tQen0Cupu6XMcnuhEVutmD1qFTggRTYmTvYbWZr5h/4J9qaLcBZKX7/RxvWW1iRmPGrdR3TEU3QIDAQAB";
        AlipayConfig alipayConfig = new AlipayConfig();
        alipayConfig.setServerUrl("https://openapi-sandbox.dl.alipaydev.com/gateway.do");
        alipayConfig.setAppId("2021000147668185");
        alipayConfig.setPrivateKey(privateKey);
        alipayConfig.setFormat("json");
        alipayConfig.setAlipayPublicKey(alipayPublicKey);
        alipayConfig.setCharset("UTF-8");
        alipayConfig.setSignType("RSA2");
        return alipayConfig;
    }

    public static boolean checkSign(HttpServletRequest req) throws AlipayApiException {
        // 验证签名
        //获取支付宝POST过来反馈信息，将异步通知中收到的待验证所有参数都存放到map中
        Map< String , String > params = new HashMap< String , String >();
        Map requestParams = req.getParameterMap();
        for(Iterator iter = requestParams.keySet().iterator(); iter.hasNext();){
            String name = (String)iter.next();
            String[] values = (String [])requestParams.get(name);
            String valueStr = "";
            for(int i = 0;i < values.length;i ++ ){
                valueStr =  (i==values.length-1)?valueStr + values [i]:valueStr + values[i] + ",";
            }
            //乱码解决，这段代码在出现乱码时使用。
            //valueStr = new String(valueStr.getBytes("ISO-8859-1"), "utf-8");
            params.put (name,valueStr);
        }
        //调用SDK验证签名
        //公钥验签示例代码
        AlipayConfig config = getAlipayConfig();
        boolean signVerified = AlipaySignature.rsaCheckV1(params, config.getAlipayPublicKey(), config.getCharset(), config.getSignType()) ;
        //公钥证书验签示例代码
        //   boolean flag = AlipaySignature.rsaCertCheckV1(params,alipayPublicCertPath,"UTF-8","RSA2");

        if (signVerified){
            // TODO 验签成功后
            //按照支付结果异步通知中的描述，对支付结果中的业务内容进行1\2\3\4二次校验，校验成功后在response中返回success
            return true;
        } else {
            // TODO 验签失败则记录异常日志，并在response中返回fail.
            return false;
        }
    }
}
