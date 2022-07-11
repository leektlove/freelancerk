package com.freelancerk.gateway;

import com.freelancerk.domain.AligoKakaoMessageResponse;
import com.freelancerk.domain.AligoKakaoMessageTemplate;
import com.freelancerk.domain.AligoToken;
import com.freelancerk.domain.repository.AligoKakaoMessageResponseRepository;
import com.freelancerk.domain.repository.AligoTokenRepository;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.LocalDateTime;

@PropertySource("aligo.properties")
@Service
public class KakaoMessageServiceImpl implements KakaoMessageService {

    private AligoToken aligoToken;
    @Value("${aligo.server.url}")
    private String aligoServerUrl;
    @Value("${aligo.apikey}")
    private String aligoApiKey;
    @Value("${aligo.plusid}")
    private String aligoPlusId;
    @Value("${aligo.userid}")
    private String aligoUserid;
    @Value("${aligo.sender}")
    private String aligoSender;
    @Value("${aligo.senderkey}")
    private String aligoSenderKey;
    private Environment env;
    private AligoTokenRepository aligoTokenRepository;
    private AligoKakaoMessageResponseRepository aligoKakaoMessageResponseRepository;

    @Autowired
    public KakaoMessageServiceImpl(Environment environment,
                                   AligoTokenRepository aligoTokenRepository,
                                   AligoKakaoMessageResponseRepository aligoKakaoMessageResponseRepository) {
        this.env = environment;
        this.aligoTokenRepository = aligoTokenRepository;
        this.aligoKakaoMessageResponseRepository = aligoKakaoMessageResponseRepository;
    }

    private String getOrRefreshToken() {


        if (aligoToken == null || aligoToken.getExpiredAt().minusDays(1).isBefore(LocalDateTime.now())) {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
            map.add("apikey", aligoApiKey);
            map.add("userid", aligoUserid);

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<KakaoMessageTokenResponse> response = restTemplate.postForEntity(String.format("%s/akv10/token/create/1/m/", aligoServerUrl), request, KakaoMessageTokenResponse.class);
            KakaoMessageTokenResponse tokenResponse = response.getBody();
            if (0 == tokenResponse.getCode()) {
                AligoToken aligoToken = new AligoToken();
                try {
                    aligoToken.setIpAddress(InetAddress.getLocalHost().getHostAddress());
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }
                aligoToken.setExpiredAt(LocalDateTime.now().plusMonths(1));
                aligoToken.setToken(tokenResponse.getToken());
                this.aligoToken = aligoTokenRepository.save(aligoToken);
            }
        }

        return this.aligoToken.getToken();
    }

    @Override
    public KakaoMessageSendResponse sendMessage(String templateCode, String receiver, String title, String content) {
        if (env.getActiveProfiles().length == 0 || !"real".equalsIgnoreCase(env.getActiveProfiles()[0])) {
            return new KakaoMessageSendResponse();
        }

        if (StringUtils.isEmpty(receiver)) return new KakaoMessageSendResponse();

        String token = getOrRefreshToken();
//        String token = "283e2ea3218387929c724a26a1af0d9763d55b4294492502eeaa505ff320bdd336eca531c0dccb7ca4d0ddc9e2781a2c3c703d65dcc37f1f459b49251cc30b04OCPAF6MubvGxSKUp8DTeEWu3ZB719e3xWHeeGUH3GHugx6t8AZI9z/WtnEsqBPutFdPZuHridILyEksoUMMCKQ==";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("apikey", aligoApiKey);
        map.add("userid", aligoUserid);
        map.add("token", token);
        map.add("senderkey", aligoSenderKey);
        map.add("tpl_code", templateCode);
        map.add("sender", aligoSender);
        map.add("receiver_1", receiver);
        map.add("subject_1", title);
        map.add("message_1", content);
        map.add("failover", "Y");
        map.add("fsubject_1", title);
        map.add("fmessage_1", content);
        if (AligoKakaoMessageTemplate.Code.TA_3172.name().equalsIgnoreCase(templateCode)) {
            JsonArray jsonArray = new JsonArray();
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("name", "이용안내");
            jsonObject.addProperty("linkType", "WL");
            jsonObject.addProperty("linkTypeName", "웹링크");
            jsonObject.addProperty("linkMo", "https://www.freelancerk.com/common/howItWorks?role=ROLE_CLIENT");
            jsonObject.addProperty("linkPc", "https://www.freelancerk.com/common/howItWorks?role=ROLE_CLIENT");
            jsonArray.add(jsonObject);
            JsonObject button1Object = new JsonObject();
            button1Object.add("button", jsonArray);
            map.add("button_1", button1Object.toString());
        } else if (AligoKakaoMessageTemplate.Code.TA_3173.name().equalsIgnoreCase(templateCode)) {
            JsonArray jsonArray = new JsonArray();
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("name", "이용안내");
            jsonObject.addProperty("linkType", "WL");
            jsonObject.addProperty("linkTypeName", "웹링크");
            jsonObject.addProperty("linkMo", "https://www.freelancerk.com/common/howItWorks?role=ROLE_FREELANCER");
            jsonObject.addProperty("linkPc", "https://www.freelancerk.com/common/howItWorks?role=ROLE_FREELANCER");
            jsonArray.add(jsonObject);
            JsonObject button1Object = new JsonObject();
            button1Object.add("button", jsonArray);
            map.add("button_1", button1Object.toString());
        }

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<KakaoMessageSendResponse> response = restTemplate.postForEntity(String.format("%s/akv10/alimtalk/send/", aligoServerUrl), request, KakaoMessageSendResponse.class);
        KakaoMessageSendResponse messageSendResponse = response.getBody();
        AligoKakaoMessageResponse messageResponse = new AligoKakaoMessageResponse();
        messageResponse.setResponse(messageSendResponse.getMessage());
        messageResponse.setReceiver(receiver);
        aligoKakaoMessageResponseRepository.save(messageResponse);

        return messageSendResponse;
    }
}
