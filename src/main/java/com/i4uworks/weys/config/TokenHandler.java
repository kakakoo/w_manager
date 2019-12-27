package com.i4uworks.weys.config;

import java.util.Calendar;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.i4uworks.weys.login.LoginInfoVO;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

public class TokenHandler {
	
	public static final String SECREKEY = "TWoeKYesNmNg";
	public static final String TOKEN_VALUES_KEY = "roles";
	private static final int EXPIRES_DAYS = 365; // 365일 
	
	protected static Logger logger = LoggerFactory.getLogger(TokenHandler.class);

	// 토큰 생성
	public static String getToken(LoginInfoVO loginInfo) {
		
		Calendar cal = Calendar.getInstance();
		Date dt = new Date();
		cal.setTime(dt);
		cal.add(Calendar.DATE, EXPIRES_DAYS);
		
		Date d = cal.getTime();

		TokenValues values = new TokenValues();

		values.setAdminKey(loginInfo.getAdminKey());			// 담당자 키
		values.setAdminId(loginInfo.getAdminId());				// 담당자 아이디
		values.setStores(loginInfo.getStores());			// 담당자 이름
		
		return Jwts.builder()
				.setId(String.valueOf(values.getAdminKey()))
				.setSubject(values.getAdminId())
				.setExpiration(d)
				.claim("name", values.getStores())
				.setIssuedAt(new Date())
				.signWith(SignatureAlgorithm.HS256, SECREKEY).compact();
	}
	
	// 회원정보 리턴
	public static TokenValues getTokenValues(HttpServletRequest req, HttpServletResponse res) throws Exception  {
		TokenValues values = new TokenValues();
		
		try{
			final Claims claims = (Claims) req.getAttribute("claims");
			
			if (claims == null) {
				logger.info("claims null");
				return null;
			}
			
			String adminKey = claims.getId();						
			String adminId = claims.getSubject();					
			String stores = claims.get("name", String.class);		
			
			if (adminKey == null || adminId == null || stores == null) {
				return null;
			}

			values.setAdminKey(Integer.parseInt(adminKey));		
			values.setAdminId(adminId);				
			values.setStores(stores);			
			
		} catch (Exception e){
			logger.info("error ::: " + e.getMessage());
			return null;
		}
		
		return values;
	}
}
