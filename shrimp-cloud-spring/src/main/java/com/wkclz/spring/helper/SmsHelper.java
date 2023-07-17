package com.wkclz.spring.helper;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.aliyun.dysmsapi20170525.Client;
import com.aliyun.dysmsapi20170525.models.SendSmsRequest;
import com.aliyun.dysmsapi20170525.models.SendSmsResponse;
import com.aliyun.dysmsapi20170525.models.SendSmsResponseBody;
import com.aliyun.teaopenapi.models.Config;
import com.aliyun.teautil.models.RuntimeOptions;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class SmsHelper {

    private static final Logger logger = LoggerFactory.getLogger(SmsHelper.class);


    /**
     * @param templateCode
     * @param signName     短信签名
     * @param params
     * @param mobiles      以逗号分隔的多个号码，最多 1000 个
     * @return 成功：OK，失败，错误详情
     */
    public static String sent(String templateCode, String signName, Map<String, String> params, String mobiles) {

        if (StringUtils.isBlank(templateCode)) {
            return "短信模板不能为空";
        }
        if (StringUtils.isBlank(mobiles)) {
            return "目标手机号不能为空";
        }
        if (StringUtils.isBlank(signName)) {
            return "短信签名不能为空";
        }


        /*
        以下代码参照：
        https://help.aliyun.com/document_detail/55284.html?spm=5176.10629532.106.1.49ef1cbeVdXQUp
        请查看上述文档后再做改动
         */

        //设置超时时间-可自行调整
        System.setProperty("sun.net.client.defaultConnectTimeout", "10000");
        System.setProperty("sun.net.client.defaultReadTimeout", "10000");

        String smsAccessKeyId = SystemConfigHelper.getSystemConfig("sms_access_key_id");
        String smsAccessKeySecret = SystemConfigHelper.getSystemConfig("sms_access_key_secret");


        try {
            Client client = createClient(smsAccessKeyId, smsAccessKeySecret);


            //组装请求对象
            SendSmsRequest request = new SendSmsRequest();
            request.setSignName(signName);
            request.setPhoneNumbers(mobiles);
            request.setTemplateCode(templateCode);
            if (params != null) {
                JSONObject jsonObject = JSON.parseObject(JSON.toJSONString(params));
                request.setTemplateParam(jsonObject.toString());
            }

            RuntimeOptions runtime = new RuntimeOptions();
            SendSmsResponse response = client.sendSmsWithOptions(request, runtime);

            SendSmsResponseBody body = response.getBody();
            String bodyStr = sendSmsResponseToString(body);

            if (body.getCode() != null && "OK".equals(body.getCode())) {
                logger.info("短信发送成功：{}", bodyStr);
                return "OK";
            } else {
                logger.error("短信发送失败：{}", bodyStr);
                return "短信发送失败：" + bodyStr;
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return "短信发送失败，未知错误";
    }


    /**
     * 使用AK&SK初始化账号Client
     * @param accessKeyId
     * @param accessKeySecret
     * @return Client
     * @throws Exception
     */
    public static com.aliyun.dysmsapi20170525.Client createClient(String accessKeyId, String accessKeySecret) throws Exception {
        Config config = new Config()
            // 您的AccessKey ID
            .setAccessKeyId(accessKeyId)
            // 您的AccessKey Secret
            .setAccessKeySecret(accessKeySecret);
        // 访问的域名
        config.endpoint = "dysmsapi.aliyuncs.com";
        return new com.aliyun.dysmsapi20170525.Client(config);
    }


    public static String sendSmsResponseToString(SendSmsResponseBody body) {
        return "SendSmsResponseBody {" +
            "requestId='" + body.getRequestId() + '\'' +
            ", bizId='" + body.getBizId() + '\'' +
            ", code='" + body.getCode() + '\'' +
            ", message='" + body.getMessage() + '\'' +
            '}';
    }

}
