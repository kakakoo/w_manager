package com.i4uworks.weys.common;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PushService implements Runnable {
	
	private JSONObject json;
	private String FCM_SERVER_KEY;
	private String FCM_API_SEND_URL;
	private String result;

	protected static Logger logger = LoggerFactory.getLogger(PushService.class);
	
	public PushService(JSONObject json, String FCM_SERVER_KEY, String FCM_API_SEND_URL) {
		this.json = json;
		this.FCM_SERVER_KEY = FCM_SERVER_KEY;
		this.FCM_API_SEND_URL = FCM_API_SEND_URL;
		this.result = "";
	}
	
	public String getResult(){
		return this.result;
	}

	@Override
	public void run() {
		
		try{
			String authKey = FCM_SERVER_KEY; // You FCM AUTH key
			String FMCurl = FCM_API_SEND_URL;
			
			URL url = new URL(FMCurl);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();

			conn.setUseCaches(false);
			conn.setDoInput(true);
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Authorization", "key=" + authKey);
			conn.setRequestProperty("Content-Type", "application/json");

			try (OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream())) {
				// 혹시나 한글 깨짐이 발생하면
				// try(OutputStreamWriter wr = new
				// OutputStreamWriter(conn.getOutputStream(), "UTF-8")){ 인코딩을 변경해준다.

				wr.write(json.toString());
				wr.flush();
			} catch (Exception e) {
			}

			if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
				throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
			}

			BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));

			String output;
			while ((output = br.readLine()) != null) {
				this.result = output;
				logger.info("push send ::: " + result);
			}

			conn.disconnect();
		} catch (Exception e) {
			logger.info("push error ::: " + e.getMessage());
		}
	}
}
