package com.freelancerk.service.impl;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClientBuilder;
import com.amazonaws.services.sns.model.MessageAttributeValue;
import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.services.sns.model.PublishResult;
import com.freelancerk.service.SmsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class SmsServiceImpl implements SmsService{

    @Value("${cloud.aws.sns.accessKey}")
    private String snsUserAccessKey ;
    @Value("${cloud.aws.sns.secretKey}")
    private String snsUserSecretKey;

    @Override
    public String sendMessageAndReturnId(String message, String to) {
        BasicAWSCredentials awsCreds = new BasicAWSCredentials( snsUserAccessKey, snsUserSecretKey );
        AmazonSNS snsClient = AmazonSNSClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(awsCreds)).withRegion(Regions.US_WEST_2).build();

        Map<String, MessageAttributeValue> smsAttributes = new HashMap<>();
        smsAttributes.put("AWS.SNS.SMS.SMSType", new MessageAttributeValue().withStringValue("Transactional").withDataType("String"));

        PublishResult publishResult = snsClient.publish(new PublishRequest().withMessage(message).withPhoneNumber(to).withMessageAttributes(smsAttributes));
        return publishResult.getMessageId();
    }
}
