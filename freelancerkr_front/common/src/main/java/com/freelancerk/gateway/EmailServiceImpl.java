package com.freelancerk.gateway;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClientBuilder;
import com.amazonaws.services.simpleemail.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import javax.mail.internet.MimeUtility;


@Service
public class EmailServiceImpl implements EmailService {

    private static final String FROM = "voc@freelancerk.com";
    private Environment env;

    @Autowired
    public EmailServiceImpl(Environment environment) {
        this.env = environment;
    }

    @Override
    public void sendEmail(String to, String subject, String content) {
        if (env.getActiveProfiles().length == 0 || !"real".equalsIgnoreCase(env.getActiveProfiles()[0])) {
            return;
        }

        AmazonSimpleEmailService client =
                AmazonSimpleEmailServiceClientBuilder.standard()
                        .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials("AKIAQIJ4QFM352FQDOH4", "S/JeYNKLDFzEI8D/Qo8YpBrr0N9l3yOon7ze74T9")))
                        .withRegion(Regions.US_WEST_2).build();
        SendEmailRequest request = new SendEmailRequest()

                .withDestination(
                        new Destination().withToAddresses(to))
                .withMessage(new Message()
                        .withBody(new Body()
                                .withHtml(new Content()
                                        .withCharset("UTF-8").withData(content))
                                .withText(new Content()
                                        .withCharset("UTF-8").withData(content)))
                        .withSubject(new Content()
                                .withCharset("UTF-8").withData(subject)))
                .withSource(encodeNameWithAddress("프리랜서코리아", FROM));
        client.sendEmail(request);
    }

    private static String encodeNameWithAddress(String name, String address){

        String returnStr = address;
        try {
            String encoded = MimeUtility.encodeText(name, "utf-8", "Q");
            returnStr = encoded + "<"+address+">";

        } catch (Exception e) {
            e.printStackTrace();
        }

        return returnStr;
    }

}
