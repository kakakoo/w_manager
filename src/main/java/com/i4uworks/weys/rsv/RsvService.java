package com.i4uworks.weys.rsv;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.commons.collections.MapUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.i4uworks.weys.common.PushService;
import com.i4uworks.weys.common.Utils;
import com.i4uworks.weys.config.TokenHandler;
import com.i4uworks.weys.config.TokenValues;
import com.i4uworks.weys.login.LoginDao;
import com.i4uworks.weys.login.LoginInfoVO;
import com.i4uworks.weys.mail.EmailVO;
import com.i4uworks.weys.mail.Mailer;
import com.i4uworks.weys.common.KakaoClient;
import com.i4uworks.weys.ResValue;
import com.i4uworks.weys.common.AriaUtils;
import com.i4uworks.weys.common.Barcode;
import com.i4uworks.weys.common.Constant;
import com.i4uworks.weys.common.ErrCode;
import com.i4uworks.weys.common.ItripClient;

@Service
public class RsvService {

	protected Logger logger = LoggerFactory.getLogger(getClass());
	@Autowired
	private RsvDao rsvDao;
	@Autowired
	private LoginDao loginDao;

	@Autowired
	private EmailVO emailVO;
	@Autowired
	private Mailer mailer;
	
	@Value("#{props['ENC.KEY']}")
	private String ENC_KEY;
	@Value("${UPLOAD.PATH}")
	private String UPLOAD_PATH;
	@Value("#{props['EMAIL.ID']}")
	private String EMAIL_ID;
	@Value("#{props['EMAIL.PW']}")
	private String EMAIL_PW;
	@Value("${SERVER.PATH}")
	private String SERVER_PATH;
	@Value("${SERVER.TYPE}")
	private String SERVER_TYPE;

	@Value("#{props['IB.SERVICE.ID']}")
	private String IB_SERVICE_ID;
	@Value("#{props['IB.SERVICE.PWD']}")
	private String IB_SERVICE_PWD;
	
	// FCML
	@Value("#{props['FCM.SERVER.KEY']}")
	private String FCM_SERVER_KEY; // FCM 서버 키
	@Value("#{props['FCM.SEND.URL']}")
	private String FCM_SEND_URL; // FCM 발송 URL

	//알림톡
	@Value("#{props['IB.FROM.TEL']}")
	private String IB_FROM_TEL;
	@Value("#{props['IB.KAKAO.ID']}")
	private String IB_KAKAO_ID;
	@Value("#{props['IB.KAKAO.PWD']}")
	private String IB_KAKAO_PWD;
	@Value("#{props['IB.KAKAO.SENDER.KEY']}")
	private String IB_KAKAO_SENDER_KEY;

	
	public List<Map<String, Object>> selectRsvList(RsvReqVO reqVO, TokenValues value) {

		reqVO.setEncKey(ENC_KEY);
		reqVO.setAdminKey(value.getAdminKey());
		if(reqVO.getEndDt() == null)
			reqVO.setEndDt(reqVO.getStartDt());
		
		List<Map<String, Object>> resultMap = new ArrayList<>();
		
		String listTp = reqVO.getListTp();
		
		int index = 0;
		while(true){
			String rsvDt = Utils.getDateFormat(index, reqVO.getStartDt());
			reqVO.setRsvDt(rsvDt);
			Map<String, Object> info = rsvDao.selectRsvDtInfo(reqVO);
			List<RsvInfoVO> rsvList = rsvDao.selectRsvList(reqVO);
			
			Map<String, Object> reqMap = new HashMap<>();
			reqMap.put("adminKey", value.getAdminKey());
			reqMap.put("dt", rsvDt);
			
			int canCnt = rsvDao.selectCancelRsv(reqMap);
			info.put("cancelCnt", canCnt);
			info.put("dt", rsvDt);
			info.put("rsvList", rsvList);
			
			resultMap.add(info);
			
			index = index + 1;
			if(listTp.equals("R")){
				if(rsvDt.equals(reqVO.getEndDt())){
					break;
				}
			} else {
				if(index == 3)
					break;
			}
		}
		
		return resultMap;
	}

	public int updateRsvSt(int adminKey, String rsvId) throws Exception {
		
		/**
		 * 담당자 지정 및 상테 업데이트
		 */
		Map<String, Object> reqMap = new HashMap<>();
		reqMap.put("adminKey", adminKey);
		reqMap.put("rsvId", rsvId);
		reqMap.put("rsvSt", Constant.RSV_READY);
		
		int res = rsvDao.updateRsvSt(reqMap);
		
		/**
		 * 담당자 로그 추가
		 */
		if(res > 0){
			/**
			 * 예약 qr 생성
			 */
			boolean checkBarcode = true;
			String qr = "";
			while (checkBarcode) {
				qr = Barcode.CreateQrCode(2);
				int checkCnt = rsvDao.selectRsvQrCnt(qr);
				if (checkCnt == 0) {
					checkBarcode = false;
				}
			}
			String qrCodeUrl = Barcode.CreateQRCodePng(qr, UPLOAD_PATH, "rsv");
			reqMap.put("rsvId", rsvId);
			reqMap.put("qr", qr);
			reqMap.put("qrCodeUrl", qrCodeUrl);
			rsvDao.updateQrCode(reqMap);
			
			/**
			 * 예약 로그 업데이트
			 */
			reqMap.put("asIs", Constant.RSV_INCOME);
			res = rsvDao.insertRsvLogMap(reqMap);
			
			reqMap.put("act", Constant.ADM_ACT_RSV_READY);
			res = rsvDao.insertAdmActLog(reqMap);
			
			/**
			 * 고객에게 준비가 되었다고 푸시 보내기 
			 */
			Map<String, Object> pushMap = rsvDao.selectPushInfo(reqMap);
			if(pushMap == null)
				return res;
			
			String uuid = MapUtils.getString(pushMap, "UUID", "");
			String os = MapUtils.getString(pushMap, "OS", "");
			String storeNm = MapUtils.getString(pushMap, "STORE_NM", "");
			
			if(os.equals("A")){
				JSONObject dataJson = new JSONObject();
				dataJson.put("type", "reserve");
				dataJson.put("st", "ready");
				dataJson.put("message", Constant.PUSH_MSG_READY + storeNm + " 에서 만나요!");
				
				JSONObject json = new JSONObject();
				json.put("to", uuid);
				json.put("data", dataJson);
				
				PushService push = new PushService(json, FCM_SERVER_KEY, FCM_SEND_URL);
				Thread t = new Thread(push);
				t.start();

			} else if(os.equals("I")){
				JSONObject pushObj = new JSONObject();
				pushObj.put("to", uuid);
				JSONObject dataJson = new JSONObject();
				dataJson.put("title", Constant.PUSH_MSG_READY + storeNm + " 에서 만나요!");
				dataJson.put("contents","reserve");
				dataJson.put("st", "ready");
				dataJson.put("img", "");
				
				JSONObject notiJson = new JSONObject();
				notiJson.put("title", "");
				notiJson.put("body", Constant.PUSH_MSG_READY + storeNm + " 에서 만나요!");
				notiJson.put("icon", "");
				
				pushObj.put("content_available", true);
				pushObj.put("data", dataJson);
				pushObj.put("notification", notiJson);
				pushObj.put("priority", "high");
				
				PushService push = new PushService(pushObj, FCM_SERVER_KEY, FCM_SEND_URL);
				Thread t = new Thread(push);
				t.start();
			}
		} else {
			String tp = rsvDao.selectRsvSt(rsvId); 
			if(tp.equals("C") || tp.equals("CR") || tp.equals("CF")){
				return -1;
			} else {
				return 0;
			}
		}
		return res;
	}

	public RsvInfoVO selectRsvInfo(RsvInfoVO reqVO) {
		reqVO.setEncKey(ENC_KEY);
		
		RsvInfoVO info = rsvDao.selectRsvInfo(reqVO);
		if(info == null){
			return null;
		}

		if(!info.getRsvSt().equals(Constant.RSV_FINISH)){
			info.setModDttm(null);
		}
		
		return info;
	}

	public String updateRsvDone(RsvDoneVO reqVO) throws Exception {
		
		if(reqVO.getSignDttm() == null){
			reqVO.setSignDttm(Utils.getTodayDate("yyyy.MM.dd HH:mm:ss"));
		}
		int res = rsvDao.updateRsvDone(reqVO);
		/**
		 * 로그 등록
		 */
		if(res > 0){
			reqVO.setAsIs(Constant.RSV_READY);
			reqVO.setToBe(Constant.RSV_FINISH);
			res = rsvDao.insertRsvLog(reqVO);

			Map<String, Object> reqMap = new HashMap<>();
			reqMap.put("adminKey", reqVO.getAdminKey());
			reqMap.put("act", Constant.ADM_ACT_RSV_FINISH);
			res = rsvDao.insertAdmActLog(reqMap);
			
			reqVO.setEncKey(ENC_KEY);
			Map<String, Object> infoMap = rsvDao.selectCompleteRsvInfo(reqVO);

			int usrId = MapUtils.getIntValue(infoMap, "USR_ID");
			
//			String tp = MapUtils.getString(infoMap, "RSV_TP");
//			String rate = "";
//			if(tp.equals("M")){
//				rate = MapUtils.getString(infoMap, "BASIC_RATE_WEYS");
//			} else {
//				double weys = MapUtils.getDoubleValue(infoMap, "BASIC_RATE_WEYS");
//				double bank = MapUtils.getDoubleValue(infoMap, "BASIC_RATE_BANK");
//				double dRate = (weys + bank) / 2.0;
//				rate = String.format("%.2f", dRate);
//			}
			
			/**
			 * 알림톡 발송
			 * 
			 	[웨이즈]

				조은용 고객님께 외화 전달을 완료하였습니다.
				즐겁고 안전한 여행되세요!
				
				■전달내역
				- 외화금액: USD 1,200
				- 전달완료시간: 2018/03/17 10:41
			 */
			String msg = "[웨이즈]\n\n";
			msg += MapUtils.getString(infoMap, "RSV_NM") + " 고객님께 외화 전달을 완료하였습니다.\n";
			msg +="즐겁고 안전한 여행되세요!\n\n";

			msg +="■전달내역\n";
			msg +="- 외화금액: " + MapUtils.getString(infoMap, "UNIT") + " " + Utils.setStringFormatInteger(MapUtils.getString(infoMap, "RSV_AMNT")) + "\n";
			msg +="- 전달완료시간: " + reqVO.getSignDttm();
			
			String nation = "82"; 
			if(usrId != 0){
				nation = rsvDao.selectUsrNation(reqVO.getRsvId());
			}
			String usrTel = MapUtils.getString(infoMap, "RSV_TEL");
			if(nation.equals("82") && usrTel.startsWith("010"))
				usrTel = usrTel.substring(1);
			String tel = nation + usrTel;
			
			KakaoClient kakao = new KakaoClient(Constant.KAKAO_TALK_MSG, IB_KAKAO_ID, IB_KAKAO_PWD, IB_KAKAO_SENDER_KEY, IB_FROM_TEL);
			kakao.sendMsg(tel, msg, "delivery_complete_v6", null, null);

			if(usrId != 0){
				Map<String, Object> talk =  new HashMap<>();
				talk.put("usrId", usrId);
				talk.put("msg", msg);
				talk.put("templete", "delivery_complete_v6");
				rsvDao.insertKakaoLog(talk);
			}
			
			String rsvForm = MapUtils.getString(infoMap, "RSV_FORM");
			String addr = "";
			String rsvDt = "";
			if(rsvForm.equals("D")){
				addr = MapUtils.getString(infoMap, "RSV_ADDR") + " " + MapUtils.getString(infoMap, "RSV_ADDR_DETAIL");
				
				String tm = MapUtils.getString(infoMap, "RSV_TM");
				int deliverTm = MapUtils.getIntValue(infoMap, "DELIVER_TIME");
				StringTokenizer st = new StringTokenizer(tm, ":");
				String hour = st.nextToken();
				String min = st.nextToken();
				
				int iHour = Integer.parseInt(hour) + deliverTm;
				hour = iHour + ":" + min;
				
				rsvDt = MapUtils.getString(infoMap, "RSV_DT") + " " + MapUtils.getString(infoMap, "RSV_TM") + " ~ " + hour + " 경 ";
			} else {
				addr = MapUtils.getString(infoMap, "STORE_NM");
				rsvDt = MapUtils.getString(infoMap, "RSV_DT") + " " + MapUtils.getString(infoMap, "RSV_TM");
			}

			if(SERVER_TYPE.equals("USER")){
				/**
				 * 이메일 발송
				 */
				Map<String, Object> mailMap = new HashMap<>();
				mailMap.put("usrNm", MapUtils.getString(infoMap, "RSV_NM"));
				mailMap.put("rsvNo", MapUtils.getString(infoMap, "RSV_NO"));
				mailMap.put("regDttm", Utils.getTodayDate("yyyy년 MM월 dd일 a hh시 mm분"));
				
				mailMap.put("unit", MapUtils.getString(infoMap, "UNIT_NM") + "(" + MapUtils.getString(infoMap, "UNIT") + ")");
				mailMap.put("rsvAmnt", Utils.setStringFormatInteger(MapUtils.getString(infoMap, "RSV_AMNT")));
				
				mailMap.put("addr", addr);
				mailMap.put("rsvDt", rsvDt);

				mailMap.put("sign", SERVER_PATH + "/imgView/" + MapUtils.getString(infoMap, "RSV_SIGN"));
				
				emailVO.setEmailMap(mailMap);
				emailVO.setSubject(MapUtils.getString(infoMap, "RSV_NM") + " 고객님, 외화를 전달하였습니다.");
				emailVO.setVeloTmp("done.vm");
				emailVO.setFrom(EMAIL_ID);
				emailVO.setReceiver(MapUtils.getString(infoMap, "RSV_EMAIL"));
				
				try{
					mailer.sendEmail(emailVO);
				} catch (Exception e) {
					logger.info("mail error : " + e.getMessage());
				}
			}
			
			/**
			 * 아이트립에 완료건 내용 전송
			 */
			String storeCenter = MapUtils.getString(infoMap, "TAG_NM");
			if(storeCenter.equals("ITR")){
				String url = SERVER_TYPE.equals("USER") ? Constant.ITRIP_REAL_URL : Constant.ITRIP_DEV_URL;
				String unit = MapUtils.getString(infoMap, "UNIT");
				String storeTag = MapUtils.getString(infoMap, "STORE_TAG");
				ItripClient itrip = new ItripClient(url + Constant.ITRIP_GRP_LIST_URL, unit, storeTag);
				itrip.sendRsvDone(infoMap);
			}
			
			/**
			 * 예약내역 시제 반영
			 * 2019.06.20 일부터 시작
			 */
			String dt = Utils.getDiffDate(0);
			
			if(!dt.equals("2019.06.19")){
				String unit = MapUtils.getString(infoMap, "UNIT");
				List<Map<String, Object>> moneyList = rsvDao.selectMoneyList(unit);
				Map<String, Object> moneyInfo = rsvDao.selectRsvMoneyInfo(reqVO.getRsvId());

				int rsvAmnt = MapUtils.getIntValue(moneyInfo, "RSV_AMNT");
				double basicRate = MapUtils.getDoubleValue(moneyInfo, "BASIC_RATE_WEYS");
				
				for(Map<String, Object> mMng : moneyList){
					
					int buyAmnt = MapUtils.getIntValue(mMng, "BUY_AMNT");
					if(rsvAmnt < buyAmnt){
						int sellKor = (int) (rsvAmnt * basicRate);
						if(unit.equals("JPY"))
							sellKor = sellKor / 100;
						
						mMng.put("sellAmnt", rsvAmnt);
						mMng.put("sellKor", sellKor);
						
						rsvDao.updateMoneyMng(mMng);
						break;
					} else {
						rsvAmnt = rsvAmnt - buyAmnt;
						
						int sellAmnt = buyAmnt;
						int sellKor = (int) (sellAmnt * basicRate);
						if(unit.equals("JPY"))
							sellKor = sellKor / 100;
						
						mMng.put("sellAmnt", sellAmnt);
						mMng.put("sellKor", sellKor);
						
						rsvDao.updateMoneyMng(mMng);
					}
				}
			}
			
			if(usrId != 0){
				/**
				 * 거래 완료 알림 등록
				 */
				String armTitle = "고객님께 " + MapUtils.getString(infoMap, "UNIT") + " " + Utils.setStringFormatInteger(MapUtils.getString(infoMap, "RSV_AMNT"));
				armTitle += " 전달을 완료하였습니다.";
				
				Map<String, Object> alarm =  new HashMap<>();
				alarm.put("armTp", "I");
				alarm.put("armTitle", armTitle);
				alarm.put("armTarget", "rsv");
				alarm.put("armVal", "/api/user/gVerSion/rsv/" + reqVO.getRsvId());
				rsvDao.insertAlarm(alarm);

				/**
				 * 푸시 보내기
				 */
				String uuid = MapUtils.getString(infoMap, "UUID", "");
				String os = MapUtils.getString(infoMap, "OS", "");
				String pushSt = MapUtils.getString(infoMap, "PUSH_ST", "");
				
				if(pushSt.equals("Y") && !uuid.equals("")){
					if(os.equals("A")){
						JSONObject dataJson = new JSONObject();
						dataJson.put("type", "reserve");
						dataJson.put("st", "done");
						dataJson.put("message", armTitle);
						dataJson.put("val", reqVO.getRsvId());
						
						JSONObject json = new JSONObject();
						json.put("to", uuid);
						json.put("data", dataJson);
						
						PushService push = new PushService(json, FCM_SERVER_KEY, FCM_SEND_URL);
						Thread t = new Thread(push);
						t.start();

					} else if(os.equals("I")){
						JSONObject pushObj = new JSONObject();
						pushObj.put("to", uuid);
						JSONObject dataJson = new JSONObject();
						dataJson.put("title", armTitle);
						dataJson.put("contents","reserve");
						dataJson.put("st", "done");
						dataJson.put("val", reqVO.getRsvId());
						dataJson.put("img", "");
						
						JSONObject notiJson = new JSONObject();
						notiJson.put("title", "즐겁고 안전한 여행되세요!");
						notiJson.put("body", armTitle);
						notiJson.put("icon", "");
						
						pushObj.put("content_available", true);
						pushObj.put("data", dataJson);
						pushObj.put("priority", "high");
						pushObj.put("notification", notiJson);
						
						PushService push = new PushService(pushObj, FCM_SERVER_KEY, FCM_SEND_URL);
						Thread t = new Thread(push);
						t.start();
					}
				}
			}
		}
		return reqVO.getSignDttm();
	}

	public void notiUserRsv() throws Exception {
		
		/**
		 * 한시간 이후의 예약자들에게 푸시
		 */
		String tm = Utils.getTodayDate("HH");
		int iTm = Integer.parseInt(tm) + 1;
		
		Map<String, Object> reqMap = new HashMap<>();
		reqMap.put("rsvDt", Utils.getTodayDate("yyyy.MM.dd"));
		reqMap.put("startTm", iTm + ":00");
		reqMap.put("endTm", iTm + ":59");
		
		List<Map<String, Object>> pushMap = rsvDao.selectNotiUserInfo(reqMap);
		for(Map<String, Object> temp : pushMap){
			String uuid = MapUtils.getString(temp, "UUID");
			String os = MapUtils.getString(temp, "OS");
			
			if(os.equals("A")){
				JSONObject dataJson = new JSONObject();
				dataJson.put("type", "reserveSoon");
				dataJson.put("message", Constant.PUSH_MSG_SOON);
				
				JSONObject json = new JSONObject();
				json.put("to", uuid);
				json.put("data", dataJson);
				
				PushService push = new PushService(json, FCM_SERVER_KEY, FCM_SEND_URL);
				Thread t = new Thread(push);
				t.start();

			} else if(os.equals("I")){
				JSONObject pushObj = new JSONObject();
				pushObj.put("to", uuid);
				JSONObject dataJson = new JSONObject();
				dataJson.put("title",  Constant.PUSH_MSG_SOON);
				dataJson.put("contents","reserveSoon");
				dataJson.put("img", "");
				
				JSONObject notiJson = new JSONObject();
				notiJson.put("title",  "");
				notiJson.put("body", Constant.PUSH_MSG_SOON);
				notiJson.put("icon", "");
				
				pushObj.put("content_available", true);
				pushObj.put("data", dataJson);
				pushObj.put("notification", notiJson);
				pushObj.put("priority", "high");
				
				PushService push = new PushService(pushObj, FCM_SERVER_KEY, FCM_SEND_URL);
				Thread t = new Thread(push);
				t.start();
			}
		}
	}

	public RsvInfoVO selectRsvDetail(String rsvId, int adminKey) {
		Map<String, Object> reqMap = new HashMap<>();
		reqMap.put("encKey", ENC_KEY);
		reqMap.put("rsvId", rsvId);
		reqMap.put("adminKey", adminKey);
		
		RsvInfoVO info = rsvDao.selectRsvDetail(reqMap);
		
		int rsvCnt = rsvDao.selectRsvSameCnt(info.getRsvId());
		
		info.setuCnt(rsvCnt);
		if(!info.getRsvSt().equals(Constant.RSV_FINISH)){
			info.setModDttm(null);
		}
		
		List<Map<String, Object>> memoList = rsvDao.selectUsrMemoList(rsvId);
		
		info.setMemoList(memoList);
		info.setEncKey(null);
		return info;
	}

	public void delete3MonthRsv() {
		String dt = Utils.getDiffDate(-90);
		rsvDao.deleteRsvQr(dt);
	}
	
	public void updateRsvCheckIncome() throws Exception {
		
		/**
		 * 30분 지난 예약 시간초과 처리 및 쿠폰 취소
		 */
//		if(SERVER_TYPE.equals("USER")){

			Map<String, Object> reqMap = new HashMap<>();
			reqMap.put("st", Utils.getAfter30Min(-10));
			reqMap.put("et", Utils.getAfter30Min(0));

			
			int cnt = rsvDao.updateRsvPass(reqMap);
			if(cnt > 0){
				/**
				 * 사용 쿠폰 취소
				 */
				rsvDao.updateCouponReturn(reqMap);
				rsvDao.updateBonusReturn(reqMap);
				
				reqMap.put("encKey", ENC_KEY);
				
				/**
				 * 사용자에게 시간초과 처리 푸시 보내기
				 */
				List<Map<String, Object>> pushInfo = rsvDao.selectMissUuid(reqMap);
				
				for(Map<String, Object> uuidMap : pushInfo){
					/**
					 * 알림톡 발송
					 * 
					 	[웨이즈]

						#{name} 고객님의 #{r_currency} #{r_amount} 예약이 시간 초과로 자동 취소되었습니다.
					 */
					String msg = "[웨이즈]\n\n";
					msg += MapUtils.getString(uuidMap, "RSV_NM") + " 고객님의 " + MapUtils.getString(uuidMap, "UNIT") + " " + Utils.setStringFormatInteger(MapUtils.getString(uuidMap, "RSV_AMNT")) + " 예약이 시간 초과로 자동 취소되었습니다.\n\n";

					String nation = MapUtils.getString(uuidMap, "NATION");
					String usrTel = MapUtils.getString(uuidMap, "RSV_TEL");
					if(nation.equals("82") && usrTel.startsWith("010"))
						usrTel = usrTel.substring(1);
					String tel = nation + usrTel;
					
					KakaoClient kakao = new KakaoClient(Constant.KAKAO_TALK_MSG, IB_KAKAO_ID, IB_KAKAO_PWD, IB_KAKAO_SENDER_KEY, IB_FROM_TEL);
					kakao.sendMsg(tel, msg, "reservation_timeout", null, null);
					
					
					String pushMsg = "입금 제한 시간이 초과 되었습니다. 다시 예약 후 진행해 주세요.";
					String uuid = MapUtils.getString(uuidMap, "UUID", "");
					String os = MapUtils.getString(uuidMap, "OS", "");
					
					if(os.equals("A")){
						JSONObject dataJson = new JSONObject();
						dataJson.put("type", "reserve");
						dataJson.put("st", "income");
						dataJson.put("val", "");
						dataJson.put("message", pushMsg);
						
						JSONObject json = new JSONObject();
						json.put("to", uuid);
						json.put("data", dataJson);
						
						PushService push = new PushService(json, FCM_SERVER_KEY, FCM_SEND_URL);
						Thread t = new Thread(push);
						t.start();

					} else if(os.equals("I")){
						JSONObject pushObj = new JSONObject();
						pushObj.put("to", uuid);
						JSONObject dataJson = new JSONObject();
						dataJson.put("title", pushMsg);
						dataJson.put("contents","reserve");
						dataJson.put("st", "income");
						dataJson.put("val", "");
						dataJson.put("img", "");
						
						JSONObject notiJson = new JSONObject();
						notiJson.put("title", "예약이 취소되었습니다.");
						notiJson.put("body", pushMsg);
						notiJson.put("icon", "");
						
						pushObj.put("content_available", true);
						pushObj.put("data", dataJson);
						pushObj.put("priority", "high");
						pushObj.put("notification", notiJson);
						
						PushService push = new PushService(pushObj, FCM_SERVER_KEY, FCM_SEND_URL);
						Thread t = new Thread(push);
						t.start();
					}

					Map<String, Object> alarm =  new HashMap<>();
					alarm.put("usrId", MapUtils.getInteger(uuidMap, "USR_ID"));
					alarm.put("armTp", "I");
					alarm.put("armTitle", pushMsg);
					alarm.put("armTarget", "rsv");
					alarm.put("armVal", "/api/user/gVerSion/rsv/" + MapUtils.getIntValue(uuidMap, "rsvId"));
					rsvDao.insertAlarm(alarm);
					
				}
				
			}
			
			
			/**
			 * 수동일때 관리자알림
			 */
//			int cnt = rsvDao.selectRsvCheck(reqMap);
//			if(cnt > 0){
//				String mngMsg = "[웨이즈 예약] 30분 전에 들어온 예약이 " + cnt + " 건이 있습니다. 확인해주세요.";
//				
//				List<String> mngTels = rsvDao.selectMngList(ENC_KEY);
//				
//				for(String mngTel : mngTels){
//					String mTel = mngTel.replaceAll("-", "");
//					mTel = mTel.substring(1);
//					
//					String url = Constant.INFO_BANK_URL_REAL
//							+ "?id=" + IB_SERVICE_ID
//							+ "&pwd=" + IB_SERVICE_PWD
//							+ "&message=" + URLEncoder.encode(mngMsg, "UTF-8")
//							+ "&from=" + IB_FROM_TEL
//							+ "&to_country=82"
//							+ "&to=" + mTel
//							+ "&report_req=1";
//					
//					String resSms = SMSUtil.sendSms(url);
//				}
//			}
			
			
//		}
		
//		
//		
//		List<Integer> rsvIdList = rsvDao.selectRsvMissIncome(dt);
//		
//		for(int rsvId : rsvIdList){
//			/**
//			 * 멤버십 사용된 내역이면 사용된 멤버십 복구
//			 * MEMBER COST 복구
//			 * MEMBER_ACTIVE USE_COST 복구
//			 * MEMBER_ACTIVE 예약내역 삭제
//			 */
//			rsvDao.updateReturnMemCost(rsvId);
//			rsvDao.updateReturnUseCost(rsvId);
//			rsvDao.insertReturnRA(rsvId);
//
//			/**
//			 * 지점 돈 복구
//			 * 
//			 * 잘못됨
//			 */
//			Map<String, Object> moneyMap = new HashMap<>();
//			moneyMap.put("rsvId", rsvId);
//			moneyMap.put("storeId", 1);
//			rsvDao.insertReturnMoneyLog(moneyMap);
//			rsvDao.updateReturnMoney(moneyMap);
//		}
//
//		
//		/**
//		 * 사용한 쿠폰이 있으면 반납
//		 */
//		List<Integer> couponList = rsvDao.selectRsvMissIncomeCp(dt);
//		if(couponList.size() > 0)
//			rsvDao.updateReturnCoupon(couponList);
//		
//		rsvDao.updateRsvCheckIncome(dt);
	}

	public void updateCancelRate(String date) {
		/**
		 * 취소정책 변경으로 취소 수수료 없음. 입금금액을 전액 환불. 
		 * 
		 * 어제 날짜에 취소 들어온 리스트들을 어제 마지막 환율 고시로 계산하여 업데이트
		 * 멤버십 예약은 전액 환불
		 * 비멤버십 예약은 은행 수수료율로 계산한 금액 환불 
		 */
		int res = rsvDao.updateCancelAmnt(date);
		if(res > 0){
			Map<String, Object> logMap = new HashMap<>();
			logMap.put("date", date);
			logMap.put("asIs", Constant.RSV_CANCEL);
			logMap.put("toBe", Constant.RSV_CANCEL_READY);
			rsvDao.insertCancelLog(logMap);
		}
	}

	public void notiTomorrowRsv(String date) throws Exception {

		List<Map<String, Object>> rsvCntList = rsvDao.selectRsvCntList(date);
		for(Map<String, Object> temp : rsvCntList){
			List<Map<String, Object>> admTokenInfo = rsvDao.selectAdmTokenInfo(MapUtils.getInteger(temp, "STORE_ID"));
			int cnt = MapUtils.getIntValue(temp, "CNT");
			
			for(Map<String, Object> token : admTokenInfo){
				String uuid = MapUtils.getString(token, "UUID");
				String os = MapUtils.getString(token, "OS");
				
				if(os.equals("A")){
					JSONObject dataJson = new JSONObject();
					dataJson.put("type", "reserve");
					dataJson.put("st", "tomorrow");
					dataJson.put("message", "내일 총 " + cnt + Constant.PUSH_MSG_ADM_TOMORROW);
					
					JSONObject json = new JSONObject();
					json.put("to", uuid);
					json.put("data", dataJson);
					
					PushService push = new PushService(json, FCM_SERVER_KEY, FCM_SEND_URL);
					Thread t = new Thread(push);
					t.start();

				} else if(os.equals("I")){
					JSONObject pushObj = new JSONObject();
					pushObj.put("to", uuid);
					JSONObject dataJson = new JSONObject();
					dataJson.put("title", "내일 총 " + cnt + Constant.PUSH_MSG_ADM_TOMORROW);
					dataJson.put("contents","reserve");
					dataJson.put("st", "tomorrow");
					dataJson.put("img", "");
					
					JSONObject notiJson = new JSONObject();
					notiJson.put("title", "");
					notiJson.put("body", "내일 총 " + cnt + Constant.PUSH_MSG_ADM_TOMORROW);
					notiJson.put("icon", "");
					
					pushObj.put("content_available", true);
					pushObj.put("data", dataJson);
					pushObj.put("notification", notiJson);
					pushObj.put("priority", "high");
					
					PushService push = new PushService(pushObj, FCM_SERVER_KEY, FCM_SEND_URL);
					Thread t = new Thread(push);
					t.start();
				}
			}
		}
	}

	public void insertNotiUserBonus() throws Exception {
		
		List<Integer> nums = new ArrayList<>();
		nums.add(7);
		nums.add(14);
		nums.add(30);
		
		for(int num : nums){
			String dt = Utils.getDiffDate(num);
			Map<String, Object> reqMap = new HashMap<>();
			reqMap.put("dt", dt);
			reqMap.put("amnt", 50000);
			reqMap.put("os", "A");
			List<String> aosList = rsvDao.selectUsrBonusAlert(reqMap);
			reqMap.put("os", "I");
			List<String> iosList = rsvDao.selectUsrBonusAlert(reqMap);
			
			/**
			 * AOS PUSH SEND 
			 */
			String msg = "보너스가 " + num + "일 후 소멸예정입니다.(" + dt + " 만료예정)";
			JSONObject dataJson = new JSONObject();
			dataJson.put("type", "bonus");
			dataJson.put("st", "");
			dataJson.put("message", msg);
			
			JSONObject json = new JSONObject();
			json.put("registration_ids", aosList);
			json.put("data", dataJson);
			
			PushService push = new PushService(json, FCM_SERVER_KEY, FCM_SEND_URL);
			Thread t = new Thread(push);
			t.start();
			
			/**
			 * IOS PUSH SEND 
			 */
			JSONObject pushObj = new JSONObject();
			pushObj.put("registration_ids", iosList);
			dataJson = new JSONObject();
			dataJson.put("title", msg);
			dataJson.put("contents","bonus");
			dataJson.put("st", "");
			dataJson.put("img", "");
			
			JSONObject notiJson = new JSONObject();
			notiJson.put("title", "보너스 소멸예정");
			notiJson.put("body", msg);
			notiJson.put("icon", "");
			
			pushObj.put("content_available", true);
			pushObj.put("data", dataJson);
			pushObj.put("notification", notiJson);
			pushObj.put("priority", "high");
			
			push = new PushService(pushObj, FCM_SERVER_KEY, FCM_SEND_URL);
			t = new Thread(push);
			t.start();
			
			Map<String, Object> alarm =  new HashMap<>();
			alarm.put("armTp", "B");
			alarm.put("armTitle", msg);
			alarm.put("armTarget", "bonus");
			alarm.put("armVal", "");
			alarm.put("dt", dt);
			alarm.put("amnt", 50000);
			rsvDao.insertAlarmBonus(alarm);
			
		}
		
	}

	public void updateNotiTodayDeliver(String date) throws Exception {
		
		List<Map<String, Object>> listMap = rsvDao.selectTodayDeliver(date);

		for(Map<String, Object> temp : listMap){
			String os = MapUtils.getString(temp, "OS");
			String uuid = MapUtils.getString(temp, "UUID", "");
			String unit = MapUtils.getString(temp, "UNIT");
			String rsvAmnt = MapUtils.getString(temp, "RSV_AMNT");
			String pushSt = MapUtils.getString(temp, "PUSH_ST");
			String rsvTm = MapUtils.getString(temp, "RSV_TM");
			int delivTm = MapUtils.getIntValue(temp, "DELIVER_TIME");
			
			StringTokenizer st = new StringTokenizer(rsvTm, ":");
			String hour = st.nextToken();
			String min = st.nextToken();
			
			int iHour = Integer.parseInt(hour) + delivTm;
			
			String msg = "고객님의 " + unit + " " + Utils.setStringFormatInteger(rsvAmnt) + " 이 금일 ";
			msg += rsvTm + "~" + iHour + ":" + min + " 경 배송 예정입니다.(본인 외 수령불가)";
			
			if(pushSt.equals("Y") && !uuid.equals("")){
				if(os.equals("A")){
					JSONObject dataJson = new JSONObject();
					dataJson.put("type", "reserve");
					dataJson.put("st", "deliver");
					dataJson.put("message", msg);
					
					JSONObject json = new JSONObject();
					json.put("to", uuid);
					json.put("data", dataJson);
					
					PushService push = new PushService(json, FCM_SERVER_KEY, FCM_SEND_URL);
					Thread t = new Thread(push);
					t.start();
				} else {
					JSONObject pushObj = new JSONObject();
					pushObj.put("to", uuid);
					JSONObject dataJson = new JSONObject();
					dataJson.put("title", msg);
					dataJson.put("contents","reserve");
					dataJson.put("st", "deliver");
					dataJson.put("img", "");
					
					JSONObject notiJson = new JSONObject();
					notiJson.put("title", "오늘 외화가 배송될 예정입니다.");
					notiJson.put("body", msg);
					notiJson.put("icon", "");
					
					pushObj.put("content_available", true);
					pushObj.put("data", dataJson);
					pushObj.put("notification", notiJson);
					pushObj.put("priority", "high");
					
					PushService push = new PushService(pushObj, FCM_SERVER_KEY, FCM_SEND_URL);
					Thread t = new Thread(push);
					t.start();
				}
			}
			int rsvId = MapUtils.getIntValue(temp, "RSV_ID");
			
			Map<String, Object> alarm =  new HashMap<>();
			alarm.put("armTp", "I");
			alarm.put("armTitle", msg);
			alarm.put("armTarget", "rsv");
			alarm.put("armVal", "/api/user/gVerSion/rsv/" + rsvId);
			alarm.put("rsvId", rsvId);
			rsvDao.insertAlarm(alarm);
		}
	}

	public void updateNotiTodayRsv() throws Exception {
		String dt = Utils.getDiffDate(0);
		String tm = Utils.getDiffMinHM(120);
		
		Map<String, Object> reqMap = new HashMap<>();
		reqMap.put("dt", dt);
		reqMap.put("tm", tm);
		
		List<Map<String, Object>> listMap = rsvDao.selectTodayRsv(reqMap);

		for(Map<String, Object> temp : listMap){
			String os = MapUtils.getString(temp, "OS");
			String uuid = MapUtils.getString(temp, "UUID", "");
			String pushSt = MapUtils.getString(temp, "PUSH_ST");
			
			String unit = MapUtils.getString(temp, "UNIT");
			String rsvAmnt = MapUtils.getString(temp, "RSV_AMNT");

			String storeNm = MapUtils.getString(temp, "STORE_NM");
			String storeAddr = MapUtils.getString(temp, "STORE_ADDR");

			int rsvId = MapUtils.getIntValue(temp, "RSV_ID");
			
			String msg = "고객님의 " + unit + " " + Utils.setStringFormatInteger(rsvAmnt) + " 수령 예정 시간이 2시간 남았습니다. 수령위치 : ";
			msg += storeNm + " " + storeAddr + "(본인 외 수령불가)";
			
			if(pushSt.equals("Y") && !uuid.equals("")){
				if(os.equals("A")){
					JSONObject dataJson = new JSONObject();
					dataJson.put("type", "reserve");
					dataJson.put("st", "deliver");
					dataJson.put("message", msg);
					dataJson.put("val", rsvId);
					
					JSONObject json = new JSONObject();
					json.put("to", uuid);
					json.put("data", dataJson);
					
					PushService push = new PushService(json, FCM_SERVER_KEY, FCM_SEND_URL);
					Thread t = new Thread(push);
					t.start();
				} else {
					JSONObject pushObj = new JSONObject();
					pushObj.put("to", uuid);
					JSONObject dataJson = new JSONObject();
					dataJson.put("title", msg);
					dataJson.put("contents","reserve");
					dataJson.put("st", "deliver");
					dataJson.put("img", "");
					dataJson.put("val", rsvId);
					
					JSONObject notiJson = new JSONObject();
					notiJson.put("title", "외화를 수령하세요.");
					notiJson.put("body", msg);
					notiJson.put("icon", "");
					
					pushObj.put("content_available", true);
					pushObj.put("data", dataJson);
					pushObj.put("notification", notiJson);
					pushObj.put("priority", "high");
					
					PushService push = new PushService(pushObj, FCM_SERVER_KEY, FCM_SEND_URL);
					Thread t = new Thread(push);
					t.start();
				}
			}
			
			Map<String, Object> alarm =  new HashMap<>();
			alarm.put("armTp", "I");
			alarm.put("armTitle", msg);
			alarm.put("armTarget", "rsv");
			alarm.put("armVal", "/api/user/gVerSion/rsv/" + rsvId);
			alarm.put("rsvId", rsvId);
			rsvDao.insertAlarm(alarm);
		}

	}

	public Map<String, Object> selectAcceptCnt(int adminKey) {
		Map<String, Object> reqMap = new HashMap<>();
		reqMap.put("adminKey", adminKey);
		reqMap.put("dt", Utils.getDiffDate(0));
		return rsvDao.selectAcceptCnt(reqMap);
	}

	public String updateAcceptDone(Map<String, Object> reqMap) {
		
		String readyDttm = Utils.getTodayDate("yyyy.MM.dd HH:mm:ss");
		reqMap.put("readyDttm", readyDttm);
		int res = rsvDao.updateAcceptDoneRsv(reqMap);
		
		if(res > 0){
			List<Integer> rsvIds = (List<Integer>) MapUtils.getObject(reqMap, "rsvList");
			String rsv = rsvIds.toString();
			reqMap.put("rsv", rsv);
			rsvDao.insertAcceptLog(reqMap);
		} else {
			return null;
		}
		
		return readyDttm;
	}

	public int updateRsvDt(RsvReqVO reqVO) {
		
		Map<String, Object> oriMap = rsvDao.selectOriginRsv(reqVO.getRsvId());
		
		int res = rsvDao.updateRsvDt(reqVO);
		if(res > 0){
			int usrId = MapUtils.getIntValue(oriMap, "USR_ID", 0);
			if(usrId > 0){
				String adminNm = rsvDao.selectAdminNm(reqVO.getAdminKey());
				String msg = MapUtils.getString(oriMap, "RSV_DT") + " " + MapUtils.getString(oriMap, "RSV_TM") + " -> "
							+ reqVO.getRsvDt() + " " + reqVO.getRsvTm();
				oriMap.put("adminNm", adminNm);
				oriMap.put("msg", msg);
				
				rsvDao.insertUsrMemoCh(oriMap);
				
				/**
				 * 센터 예약일 경우 인수완료된 예약이라면 인수를 풀고 
				 * 그룹 예약 상태를 변경으로 수정한다
				 */
				String adminSt = MapUtils.getString(oriMap, "ADMIN_ST");
				if(adminSt.equals("Y") && !(reqVO.getRsvDt().equals(MapUtils.getString(oriMap, "RSV_DT")))){
					int ch = rsvDao.updateGrpLog(reqVO.getRsvId());
					if(ch > 0){
						rsvDao.updateRollBackRsv(reqVO.getRsvId());
					}
				}
			}
		}
		return res;
	}

	public int insertUsrMemo(RsvReqVO reqVO) {
		return rsvDao.insertUsrMemo(reqVO);
	}

	public Map<String, Object> selectSmsText(String rsvId) {
		
		Map<String, Object> reqMap = new HashMap<>();
		reqMap.put("rsvId", rsvId);
		reqMap.put("encKey", ENC_KEY);
		Map<String, Object> infoMap = rsvDao.selectRsvNotiInfo(reqMap);
		
		Map<String, Object> resultMap = new HashMap<>();
		if(infoMap == null){
			resultMap.put("dataList", new ArrayList<>());
		} else {
			List<Map<String, Object>> smsList = rsvDao.selectSmsList(infoMap);
			resultMap.put("dataList", smsList);
		}
		return resultMap;
	}

	public Map<String, Object> selectTransferInfo(int adminKey) {
		
		Map<String, Object> reqMap = new HashMap<>();
		reqMap.put("adminKey", adminKey);
		reqMap.put("dt", Utils.getDiffDate(0));
		
		Map<String, Object> resultMap = rsvDao.selectTransferInfo(reqMap);

		int canCnt = rsvDao.selectCancelRsv(reqMap);
		int chgCnt = rsvDao.selectChangeRsv(reqMap);

		resultMap.put("canCnt", canCnt);
		resultMap.put("chgCnt", chgCnt);
		
		return resultMap;
	}


	public ResValue updateTransfer(LoginInfoVO reqVO, TokenValues tk) throws Exception {
		
		ResValue res = new ResValue();
		/**
		 * 아이디 비밀번호 확인
		 */
		LoginInfoVO info = loginDao.selectAdmInfo(reqVO.getAdminId());
		
		if(info == null){
			/**
			 * 존재하지 않는 아이디
			 */
			res.setResCode(ErrCode.LOGIN_FAILED);
			res.setResMsg(ErrCode.getMessage(ErrCode.LOGIN_FAILED));
		} else {
			String inputPw = reqVO.getAdminPw();
			String originPw = info.getAdminPw();
			inputPw = AriaUtils.encryptPassword(inputPw, info.getAdminId());

			/**
			 * 비밀번호 불일치
			 */
			if(!inputPw.equals(originPw)){
				res.setResCode(ErrCode.LOGIN_FAILED);
				res.setResMsg(ErrCode.getMessage(ErrCode.LOGIN_FAILED));
				return res;
			}
			
			/**
			 * 토큰 생성 및 접속로그 등록
			 */
			String token = TokenHandler.getToken(info); 
			Calendar cal = Calendar.getInstance();
			Date dt = new Date();
			cal.setTime(dt);
			cal.add(Calendar.DATE, 365);
			Date d = cal.getTime();
			
			info.setTokenExpireDttm(d);
			info.setTokenAdm(token);
			info.setOs(reqVO.getOs());
			
			if(reqVO.getUuid() != null)
				info.setUuid(reqVO.getUuid());
			
			int result = loginDao.insertTokeninfo(info);
			
			if(result > 0){
				loginDao.insertLog(info.getAdminKey());
				Map<String, Object> resMap = new HashMap<>();
				resMap.put("tokenAdm", token);
				resMap.put("adminNm", info.getAdminName());
				resMap.put("storeNm", info.getStoreNm());
				resMap.put("adminTp", info.getAdminTp());

				res.setResData(resMap);
				
				/**
				 * 기존 담당자 로그아웃, 
				 * 인수인계자 로그인
				 */
				
				Map<String, Object> reqMap = new HashMap<>();
				reqMap.put("adminKey", tk.getAdminKey());
				reqMap.put("dt", Utils.getDiffDate(0));
				
				Map<String, Object> txtMap = rsvDao.selectTransferInfo(reqMap);

				int canCnt = rsvDao.selectCancelRsv(reqMap);
				int chgCnt = rsvDao.selectChangeRsv(reqMap);
				
				String memo = "총 예약 : " + MapUtils.getIntValue(txtMap, "totCnt") + "건\n"
						+ "완료된 예약 : " + MapUtils.getIntValue(txtMap, "cmpCnt") + "건\n"
						+ "남은 예약 : " + MapUtils.getIntValue(txtMap, "resCnt") + "건\n"
						+ "변경된 예약 : " + chgCnt + "건\n"
						+ "취소된 예약 : " + canCnt + "건\n";
				
				reqMap.put("act", "TLO");

				rsvDao.insertTransLogout(reqMap);
				rsvDao.updateTransLogoutToken(reqMap);

				reqMap.put("act", "TLI");
				reqMap.put("adminKey", info.getAdminKey());
				reqMap.put("memo", memo);
				rsvDao.insertTransLogIn(reqMap);
				
			} else {
				logger.info("error ::: 토큰 업데이트 실패");
				res.setResCode(ErrCode.UNKNOWN_ERROR);
				res.setResMsg(ErrCode.getMessage(ErrCode.UNKNOWN_ERROR));
			}
		}
		return res;
	}

	public void updateRsvFinishSurvey() {

		Map<String, Object> reqMap = new HashMap<>();
		reqMap.put("st", Utils.getAfter30Min(-40));
		reqMap.put("et", Utils.getAfter30Min(-30));
		reqMap.put("encKey", ENC_KEY);
		
		List<Map<String, Object>> listInfo = rsvDao.selectRsvDoneSurvey(reqMap);
		
		/**
		 * 알림톡 발송
		 * 
		 	#{name} 고객님, 서비스 이용에 감사드립니다.

			웨이즈는 고객님의 의견에 귀 기울이며 보다 나은 서비스 제공을 위해 노력합니다.
			준비된 설문 항목에 대하여 고객님의 소중한 의견 부탁드립니다.
			
			(본 고객 만족도 조사는 전송된 링크를 통해서만 응답이 가능합니다.)
		 */
		for(Map<String, Object> infoMap : listInfo){
			String msg = "";
			msg += MapUtils.getString(infoMap, "RSV_NM") + " 고객님, 서비스 이용에 감사드립니다.\n\n";
			
			msg += "웨이즈는 고객님의 의견에 귀 기울이며 보다 나은 서비스 제공을 위해 노력합니다.\n";
			msg += "준비된 설문 항목에 대하여 고객님의 소중한 의견 부탁드립니다.\n\n";

			msg += "(본 고객 만족도 조사는 전송된 링크를 통해서만 응답이 가능합니다.)";
			
			String nation = MapUtils.getString(infoMap, "NATION");
			String usrTel = MapUtils.getString(infoMap, "RSV_TEL");
			if(nation.equals("82") && usrTel.startsWith("010"))
				usrTel = usrTel.substring(1);
			String tel = nation + usrTel;
			String rsvNo = MapUtils.getString(infoMap, "RSV_NO");
			KakaoClient kakao = new KakaoClient(Constant.KAKAO_TALK_MSG, IB_KAKAO_ID, IB_KAKAO_PWD, IB_KAKAO_SENDER_KEY, IB_FROM_TEL);
			String url = SERVER_TYPE.equals("USER") ? Constant.SURVEY_URL_REAL : Constant.SURVEY_URL_DEV;
			url = url + rsvNo;
			
			kakao.sendMsg(tel, msg, "survey_after_rsv", "고객 만족도 조사 참여", url);
		}

	}

	public Map<String, Object> selectGrpList(String barcode) {
		Map<String, Object> reqMap = new HashMap<>();
		reqMap.put("encKey", ENC_KEY);
		reqMap.put("barcode", barcode);
		List<Map<String, Object>> rsvList = rsvDao.selectGrpList(reqMap);
		
		Map<String, Object> resultMap = rsvDao.selectGrpTp(barcode);
		resultMap.put("rsvList", rsvList);
		return resultMap;
	}

	public String updateGrpAcceptDone(Map<String, Object> reqMap) {

		Map<String, Object> tpMap = rsvDao.selectGrpTp(MapUtils.getString(reqMap, "barcode"));
		
		String grpSt = MapUtils.getString(reqMap, "grpSt");
		String checkSt = "";
		
		if(grpSt.equals("D")){
			checkSt = "P";
		} else if(grpSt.equals("S")){
			checkSt = "D";
		}

		String doneDttm = Utils.getTodayDate("yyyy.MM.dd HH:mm:ss");
		reqMap.put("doneDttm", doneDttm);
		reqMap.put("checkSt", checkSt);
		reqMap.put("groupTp", tpMap.get("groupTp"));
		int res = rsvDao.updateGrpAcceptDone(reqMap);
		
		if(res < 1){
			return null;
		} else {
			/**
			 * 로그 등록
			 */
			res = rsvDao.insertGrpAccptLog(reqMap);
		}
		
		return doneDttm;
	}

	public Map<String, Object> selectCenterRsv(int adminKey) {
		
		String today = Utils.getTodayDate("yyyy.MM.dd");
		
		Map<String, Object> reqMap = new HashMap<>();
		reqMap.put("today", today);
		reqMap.put("adminKey", adminKey);
		reqMap.put("encKey", ENC_KEY);
		
		List<Map<String, Object>> rsvList = rsvDao.selectCenterRsv(reqMap);

		int totCnt = rsvList.size();
		int returnCnt = rsvDao.selectReturnCnt(reqMap);
		int finCnt = rsvDao.selectFinCnt(reqMap);
		
		Map<String, Object> resultMap = new HashMap<>();
		resultMap.put("rsvList", rsvList);
		resultMap.put("dt", today);
		resultMap.put("totalCnt", totCnt);
		resultMap.put("readyCnt", totCnt - returnCnt - finCnt);
		resultMap.put("returnCnt", returnCnt);
		
		return resultMap;
	}

	public List<Map<String, Object>> selectStoreList(RsvReqVO reqVO) {
		
		return rsvDao.selectStoreList(reqVO);
	}

	public Map<String, Object> selectStoreRsvList(RsvReqVO reqVO) {
		
		Map<String, Object> groupInfo = rsvDao.selectStoreInfo(reqVO);
		
		reqVO.setEncKey(ENC_KEY);
		List<Map<String, Object>> rsvList = rsvDao.selectStoreGrpList(reqVO);
		groupInfo.put("rsvList", rsvList);
		return groupInfo;
	}
	
	public Map<String, Object> selectCenterAllDayRsv(int adminKey) {
		
		String today = Utils.getTodayDate("yyyy.MM.dd");
		
		Map<String, Object> reqMap = new HashMap<>();
		reqMap.put("encKey", ENC_KEY);
		reqMap.put("today", today);
		reqMap.put("adminKey", adminKey);
		
		List<Map<String, Object>> resultList = new ArrayList<>();
		
		for(int i = -2 ; i <= -1 ; i++){
			reqMap.put("dt", Utils.getDateFormat(i, today));
			reqMap.put("tp", "O");
			List<Map<String, Object>> rsvList = rsvDao.selectCenterADRsv(reqMap);
			
			if(resultList.size() == 0){
				resultList = rsvList;
			} else {
				int size = resultList.size();
				resultList.addAll(size, rsvList);
			}
		}

		reqMap.put("dt", today);
		reqMap.put("tp", "T");
		List<Map<String, Object>> rsvList = rsvDao.selectCenterADRsv(reqMap);
		
		if(resultList.size() == 0){
			resultList = rsvList;
		} else {
			int size = resultList.size();
			resultList.addAll(size, rsvList);
		}
		List<Map<String, Object>> returnRsv = rsvDao.selectReturnAD(reqMap);
		if(returnRsv.size() > 0){
			resultList.addAll(resultList.size(), returnRsv);
		}
		
		int totCnt = resultList.size();
		int returnCnt = rsvDao.selectReturnCntAD(reqMap) + returnRsv.size();
		int finCnt = rsvDao.selectFinCnt(reqMap);
		
		Map<String, Object> resultMap = new HashMap<>();
		resultMap.put("rsvList", resultList);
		resultMap.put("dt", today);
		resultMap.put("totalCnt", totCnt);
		resultMap.put("readyCnt", totCnt - returnCnt - finCnt);
		resultMap.put("returnCnt", returnCnt);
		
		return resultMap;
	}

}
