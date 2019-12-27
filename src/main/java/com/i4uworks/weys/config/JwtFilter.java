package com.i4uworks.weys.config;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.filter.GenericFilterBean;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

public class JwtFilter extends GenericFilterBean {

	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
			throws IOException, ServletException {

		// CORS
		HttpServletResponse response = (HttpServletResponse) res;
		response.addHeader("Access-Control-Allow-Origin", "*");
		response.addHeader("Access-Control-Allow-Methods", "POST, GET, PUT, DELETE");
		response.addHeader("Access-Control-Max-Age", "3600");
		response.addHeader("Access-Control-Allow-Headers", "Content-Type, X-Requested-With,  Authorization, Accept");

		final HttpServletRequest request = (HttpServletRequest) req;

		final String authHeader = request.getHeader("Authorization");

		if (authHeader == null || !authHeader.startsWith("Bearer ")) {
			request.setAttribute("claims", null);
		} else {

			final String token = authHeader.substring(7); // "Bearer " 이후의 토큰만 

			try {
				final Claims claims = Jwts.parser().setSigningKey(TokenHandler.SECREKEY).parseClaimsJws(token).getBody();

				request.setAttribute("claims", claims);
				request.setAttribute("HeadToken", token);
			} catch (final Exception e) {
				request.setAttribute("claims", null);
			}
		}

		chain.doFilter(req, res);
	}
}
