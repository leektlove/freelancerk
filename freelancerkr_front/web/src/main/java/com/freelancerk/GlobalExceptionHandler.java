package com.freelancerk;

import com.freelancerk.exception.NotLoggedInException;
import com.freelancerk.exception.UsernameNotFoundException;
import com.freelancerk.io.CommonResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.web.servlet.view.RedirectView;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @Value("${server.url}") String serverUrl;

    @ResponseBody
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(UsernameNotFoundException.class)
    public CommonResponse handleUsernameNotFoundException(UsernameNotFoundException ex) {
        return new CommonResponse.Builder().
                message("존재하지 않는 이메일주소입니다. 다시 한번 확인해주세요. 혹시 SNS계정으로 회원 가입하셨다면, SNS로그인을 시도해주세요.").
                build();
    }

    @ExceptionHandler
    public ModelAndView handleNotLoggedInException(NotLoggedInException e) {
        ModelAndView modelAndView = new ModelAndView(new RedirectView(String.format("%s/auth/login", serverUrl)));
        return modelAndView;
    }
}
