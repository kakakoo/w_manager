package com.i4uworks.weys.config;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.filter.OncePerRequestFilter;

public class AddRequestURLFilter extends OncePerRequestFilter {

	/**
	 * 키
	 */
	public static final String WEYS_REQUEST_URL = "weysRequestURL";
	
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		
		String servletPath = request.getServletPath();

		if (!servletPath.contains("/resources/")||!servletPath.contains("/js/")||!servletPath.contains("/css/")) {
			request.setAttribute(WEYS_REQUEST_URL, request.getRequestURL());
		}

		filterChain.doFilter(request, response);
	}

}
