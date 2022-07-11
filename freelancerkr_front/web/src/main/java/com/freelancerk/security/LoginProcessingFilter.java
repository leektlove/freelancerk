package com.freelancerk.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.freelancerk.domain.User;
import com.freelancerk.exception.ExceedPasswordFailCountException;
import com.freelancerk.exception.UsernameNotFoundException;
import com.freelancerk.io.CommonResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
public class LoginProcessingFilter extends AbstractAuthenticationProcessingFilter {

	private final String serverUrl;
	private final CustomUserDetailsService userDetailsService;

	protected LoginProcessingFilter(Environment environment,
									String defaultFilterProcessesUrl, CustomUserDetailsService userDetailsService,
									AuthenticationManager authManager) {
		super(defaultFilterProcessesUrl);
		this.serverUrl = environment.getProperty("server.url");
		this.userDetailsService = userDetailsService;
		setAuthenticationManager(authManager);
	}

	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse httpServletResponse)
			throws AuthenticationException, IOException, ServletException {

		String username = request.getParameter("username");
		String password = request.getParameter("password");
		String role = request.getParameter("role");
		String authType = request.getParameter("authType");
		String fpUser = "N";
		if(request.getParameter("fpUser")!=null && !"".equals(request.getParameter("fpUser"))) {
			fpUser = "Y";
		}
		User user = null;
		try {
			
			user = userDetailsService.loadUserByUsernameAndFpUser(username,fpUser);
			if (user != null && user.isLegacyUser() && User.AuthType.EMAIL.name().equals(authType)) {
				password = MysqlPasswordUtil.passwordFunc(password);
			}

			UsernamePasswordAuthTypeAuthenticationToken loginToken = new UsernamePasswordAuthTypeAuthenticationToken(
					username, password, authType, role);
			AuthenticationManager manager = getAuthenticationManager();
			return manager.authenticate(loginToken);
		} catch (UsernameNotFoundException | org.springframework.security.core.userdetails.UsernameNotFoundException e) {
			log.error("<<< UsernameNotFoundException.", e);
			httpServletResponse.sendError(HttpServletResponse.SC_NOT_FOUND);
		} catch (BadCredentialsException e) {
			user = userDetailsService.loadUserByUsername(username);
			CommonResponse commonResponse = new CommonResponse();
			commonResponse.setMessage(String.format("%d", user.getPasswordFailCnt()));
			ObjectMapper objectMapper = new ObjectMapper();
			objectMapper.registerModule(new JavaTimeModule());
			objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
			httpServletResponse.getWriter().write(objectMapper.writeValueAsString(commonResponse));
			httpServletResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		} catch (ExceedPasswordFailCountException e) {
			CommonResponse commonResponse = new CommonResponse();
			commonResponse.setMessage("오류횟수를 초과하였습니다. 5회 이상 오류 시, 24시간 동안 이용이 불가하며, 비밀번호 찾기(등록된 이메일로 전송)를 통해 해결할 수 있습니다.");
			ObjectMapper objectMapper = new ObjectMapper();
			objectMapper.registerModule(new JavaTimeModule());
			objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
			httpServletResponse.getWriter().write(objectMapper.writeValueAsString(commonResponse));
			httpServletResponse.setStatus(429);
		}

		return null;
	}

	@Override
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
			Authentication authentication) throws IOException, ServletException {

		String role = request.getParameter("role");
		final User authenticatedUser = userDetailsService.loadUserByUsername(authentication.getName());
		authenticatedUser.setLoginRole(User.Role.valueOf(role));
		SecurityContextHolder.getContext().setAuthentication(authenticatedUser);

		if (User.Role.ROLE_CLIENT.equals(User.Role.valueOf(role))) {
			response.sendRedirect(String.format("%s/client/profile/index", serverUrl));
		} else {
			response.sendRedirect(String.format("%s/freelancer/profile/index", serverUrl));
		}
	}

	@Override
	protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
		log.error("<<< auth error", failed);
		response.sendError(HttpServletResponse.SC_UNAUTHORIZED,
				"Authentication Failed");
	}
}
