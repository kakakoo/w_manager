package com.i4uworks.weys.common;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

public class KakaoClient {

	protected static Logger logger = LoggerFactory.getLogger(KakaoClient.class);
	private String TARGET_URL = null;
	private String CLIENT_ID = null;
	private String CLIENT_PASSWD = null;
	private String IB_KAKAO_SENDER_KEY = null;
	private String IB_FROM_TEL = null;

	public KakaoClient(String TARGET_URL, String CLIENT_ID, String CLIENT_PASSWD, String IB_KAKAO_SENDER_KEY,
			String IB_FROM_TEL) {
		this.TARGET_URL = TARGET_URL;
		this.CLIENT_ID = CLIENT_ID;
		this.CLIENT_PASSWD = CLIENT_PASSWD;
		this.IB_KAKAO_SENDER_KEY = IB_KAKAO_SENDER_KEY;
		this.IB_FROM_TEL = IB_FROM_TEL;
	}

	@SuppressWarnings("unchecked")
	public Map<String, Object> sendMsg(String to, String content, String template, String btnNm, String btnUrl) {
		JSONObject body = new JSONObject();
		JSONObject data = new JSONObject();
		JSONObject attr = new JSONObject();
		try {
			data.put("senderid", this.IB_FROM_TEL);
			data.put("to", to);
			data.put("content", content);

			attr.put("sender_key", this.IB_KAKAO_SENDER_KEY);
			attr.put("response_method", "push");
			attr.put("template_code", template);
			if(btnUrl != null){
				List<JSONObject> btns = new ArrayList<>();
				JSONObject btn = new JSONObject();
				btn.put("name", btnNm);
				btn.put("type", "WL");
				btn.put("url_pc", btnUrl);
				btn.put("url_mobile", btnUrl);
				btns.add(btn);
				
				JSONObject button = new JSONObject();
				button.put("button", btns);
				attr.put("attachment", button);
			}

			body.put("msg_type", "AL");
			body.put("mt_failover", "N");
			body.put("msg_data", data);
			body.put("msg_attr", attr);
		} catch (Exception e) {
			logger.info(e.getMessage());
			return null;
		}
		return SendTalk(body);
	}

	private Map<String, Object> SendTalk(JSONObject obj) {

		Map<String, Object> result = new HashMap<String, Object>();
		try {

			URL url = new URL(this.TARGET_URL);

			HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();

			urlConn.setDoInput(true);
			urlConn.setDoOutput(true);

			urlConn.setRequestMethod("POST");

			urlConn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
			urlConn.setRequestProperty("X-IB-Client-Id", this.CLIENT_ID);
			urlConn.setRequestProperty("X-IB-Client-Passwd", this.CLIENT_PASSWD);
			urlConn.setRequestProperty("Accept", "application/json");

			OutputStreamWriter output = new OutputStreamWriter(urlConn.getOutputStream());
			logger.info("RequestData = " + obj.toString());
			output.write(obj.toString());
			output.flush();

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
				result = new ObjectMapper().readValue(sb.toString(), HashMap.class);
			} else {
				BufferedReader br = new BufferedReader(new InputStreamReader(urlConn.getErrorStream(), "utf-8"));
				String line = null;
				while ((line = br.readLine()) != null) {
					sb.append(line + "\n");
				}
				br.close();
				result = new ObjectMapper().readValue(sb.toString(), HashMap.class);
			}
		} catch (Exception e) {
			logger.info("알림톡 에러발생 ::: " + e.getMessage());
			return null;
		}

		return result;
	}
}
