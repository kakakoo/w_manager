package com.i4uworks.weys.common;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SMSUtil {

	protected static Logger logger = LoggerFactory.getLogger(SMSUtil.class);
	public static String sendSms(String targetUrl){

		String result = "";
		try {

			URL url = new URL(targetUrl);

			HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();

			urlConn.setDoInput(true);
			urlConn.setDoOutput(true);

			urlConn.setRequestMethod("GET");
			urlConn.setRequestProperty("Content-Type", "application/json");

//			OutputStreamWriter output = new OutputStreamWriter(urlConn.getOutputStream());
//			output.flush();

			/* Get response data. */

			StringBuilder sb = new StringBuilder();
			int HttpResult = urlConn.getResponseCode();
			
			if (HttpResult == HttpURLConnection.HTTP_OK) {
				BufferedReader br = new BufferedReader(new InputStreamReader(urlConn.getInputStream(), "utf-8"));
				String line = null;
				while ((line = br.readLine()) != null) {
					sb.append(line + "\n");
				}
				br.close();

				result = sb.toString();

			} else {
				BufferedReader br = new BufferedReader(new InputStreamReader(urlConn.getErrorStream(), "utf-8"));
				String line = null;
				while ((line = br.readLine()) != null) {
					sb.append(line + "\n");
				}
				br.close();

				logger.info("ResponseData = " + sb.toString());
				throw new Exception("인포뱅크 에러발생");
			}
		} catch (Exception e) {
			logger.info("INFO BANK exception ::: " + e.getMessage());
			return null;
		}

		return result;
	}
}
