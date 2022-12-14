package com.freelancerk.controller;

import java.util.Locale;

import javax.mail.internet.MimeUtility;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClientBuilder;
import com.amazonaws.services.simpleemail.model.Body;
import com.amazonaws.services.simpleemail.model.Content;
import com.amazonaws.services.simpleemail.model.Destination;
import com.amazonaws.services.simpleemail.model.Message;
import com.amazonaws.services.simpleemail.model.SendEmailRequest;
import com.freelancerk.domain.MailDto;
import com.freelancerk.domain.User;
import com.freelancerk.gateway.EmailService;
import com.freelancerk.service.MailService;

@Controller
public class MailController  {
	
	private static final String FROM = "voc@freelancerk.com";
	
	private MailService mailService;
    private TemplateEngine templateEngine;
    private EmailService emailService;
	@Autowired
	public MailController (MailService mailService,EmailService emailService,TemplateEngine templateEngine) {
		this.mailService = mailService;
        this.templateEngine = templateEngine;
        this.emailService = emailService;
	}
	
	@PostMapping("/mail")
	public void execMail(MailDto mailDto) {
//		mailService.mailSend(mailDto);
	}
	
	@ResponseBody
	@RequestMapping(value = "/sendApplyMail", method = RequestMethod.GET)
	public String sendApplyMail(Model model, HttpServletRequest request, HttpServletResponse response) {
		MailDto mailDto = new MailDto();
		mailDto.setAddress("junhyuk1122@naver.com");
		mailDto.setTitle("?????????");
		mailDto.setMessage("test1");
		String email ="junhyuk1122@naver.com";
		System.out.println(email+"??? ????????????	");
		try {
            if (StringUtils.isNotEmpty(email)) {
                String content = makeSignUpEmailContent("?????????", email, User.Role.ROLE_FREELANCER);
                AmazonSimpleEmailService client =
                        AmazonSimpleEmailServiceClientBuilder.standard()
                                .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials("AKIAQIJ4QFM352FQDOH4", "S/JeYNKLDFzEI8D/Qo8YpBrr0N9l3yOon7ze74T9")))
                                .withRegion(Regions.US_WEST_2).build();
                SendEmailRequest request2 = new SendEmailRequest()
                        .withDestination(
                                new Destination().withToAddresses("?????????"))
                        .withMessage(new Message()
                                .withBody(new Body()
                                        .withHtml(new Content()
                                                .withCharset("UTF-8").withData(content))
                                        .withText(new Content()
                                                .withCharset("UTF-8").withData(content)))
                                .withSubject(new Content()
                                        .withCharset("UTF-8").withData(email)))
                        .withSource(encodeNameWithAddress("?????????????????????", FROM));
                client.sendEmail(request2);
                //emailService.sendEmail(email, "???????????? ????????? ??????????????? ?????????????????????.", content);
            }
        } catch (Exception e) {
        }
//		mailService.mailSend(mailDto);
		
		return null;
	}
	
   private String makeSignUpEmailContent(String userName, String userEmail, User.Role role) {
        final Context ctx = new Context(Locale.KOREA);
        ctx.setVariable("username", userName);
        ctx.setVariable("userEmail", userEmail);

        return this.templateEngine.process(User.Role.ROLE_CLIENT.equals(role)?"email/signup-client":"email/signup-freelancer", ctx);
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
