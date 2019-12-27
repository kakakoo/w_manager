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

import org.apache.commons.collections.MapUtils;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

public class ItripClient {

	protected static Logger logger = LoggerFactory.getLogger(ItripClient.class);
	private String TARGET_URL;
	private int PROD_ID;
	private int OFFICE_ID;
	private static String [] centerArr = new String []{"", "ITRIP", "T1CENTER", "ICNT11B", "T2CENTER", "KIMPO", "SEOULST", "HD"};
	private int COMMAND_ID = 72;	// 현재 담당자 아이디 . LDS

	private static String [] unitArr = new String []{"JPY", "USD", "CNY", "HKD", "TWD", "THB", "EUR", "AUD", "SGD", "PHP"};
	private static int [] prodArr = new int []{450, 451, 452, 453, 454, 455, 456, 457, 458, 459};
	
	public ItripClient(String url, String unit, String store) {
		this.TARGET_URL = url;
		
		for(int i=0 ; i<unitArr.length ; i++){
			if(unit.equals(unitArr[i])){
				this.PROD_ID = prodArr[i];
				break;
			}
		}
		
		for(int i=0 ; i<centerArr.length ; i++){
			if(store.equals(centerArr[i])){
				this.OFFICE_ID = i;
				break;
			}
		}
	}
	
	public Map<String, Object> sendRsvDone(Map<String, Object> infoMap) {
		JSONObject body = new JSONObject();
		JSONObject receive = new JSONObject();
		try {
			String rsvDt =  MapUtils.getString(infoMap, "RSV_DT").replace(".", "");
			String rsvTm = MapUtils.getString(infoMap, "RSV_TM");

			receive.put("customerName", MapUtils.getString(infoMap, "RSV_NM"));
			receive.put("customerPhone", MapUtils.getString(infoMap, "RSV_TEL"));
			receive.put("reserveNo", MapUtils.getString(infoMap, "RSV_QR"));
			receive.put("receiveOffice", this.OFFICE_ID);
			receive.put("receiveDate", rsvDt);
			receive.put("receiveTime", rsvTm + "~" + rsvTm);
			receive.put("memo", MapUtils.getString(infoMap, "UNIT") + " " + Utils.setStringFormatInteger(MapUtils.getString(infoMap, "RSV_AMNT")));
			receive.put("commanderId", this.COMMAND_ID);
			receive.put("officeId", this.OFFICE_ID);
			receive.put("confirmFlag", true);
			
			JSONObject svcType = new JSONObject();
			svcType.put("value", 5);

			JSONObject prodId = new JSONObject();
			prodId.put("id", this.PROD_ID);
			
			List<JSONObject> prodArr = new ArrayList<>();
			JSONObject prod = new JSONObject();
			prod.put("receiveQty", "1");
			prod.put("prod", prodId);
			
			prodArr.add(prod);

			receive.put("prodArr", prodArr);
			receive.put("svcType", svcType);
			body.put("receive", receive);
			
		} catch (Exception e) {
			logger.info(e.getMessage());
			return null;
		}
		return sendRsvInfo(body);
	}

	private Map<String, Object> sendRsvInfo(JSONObject obj) {

		Map<String, Object> result = new HashMap<String, Object>();
		try {

			URL url = new URL(this.TARGET_URL);

			HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();

			urlConn.setDoInput(true);
			urlConn.setDoOutput(true);

			urlConn.setRequestMethod("POST");

			urlConn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
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
