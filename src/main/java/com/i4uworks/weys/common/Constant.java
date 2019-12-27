package com.i4uworks.weys.common;

public class Constant {

	public static final int I_SERVER_VERSION = 4;
	
	public static final String PAY_SUCCESS = "결제 성공";
	public static final String PAY_CANCEL = "결제 취소";
	public static final String PAY_ERROR = "결제 에러";
	public static final String PAY_ERROR_MONEY = "결제 금액이 일치하지 않음";

	/**
	 * KEB 환율 정보 URL
	 */
	public static final String EXCHANGE_REQ_URL = "http://fx.kebhana.com/FER1101M.web";		// 환율 정보 요청 URL 
	
	/**
	 * 샌드버드 API 
	 */
	public static final String SEND_CREATE_USER_URL = "https://api.sendbird.com/v3/users";	// 샌드버드 유저 생성, 업데이트 URL 
	public static final String SEND_CREATE_CHAT_URL = "https://api.sendbird.com/v3/group_channels";	// 샌드버드 채팅방 생성 URL 

	/**
	 * 아임포트 API
	 */
	public static final String IAMPORT_GET_TOKEN_URL = "https://api.iamport.kr/users/getToken";	// 아임포트 토큰 URL 
	public static final String IAMPORT_PAYMENTS_URL = "https://api.iamport.kr/payments/";			// 아임포트 거래내역 조회 URL 
	public static final String IAMPORT_CANCEL_URL = "https://api.iamport.kr/payments/cancel";		// 아임포트 거래 취소 URL 
	public static final String IAMPORT_VBANK_URL = "https://api.iamport.kr/vbanks";		// 아임포트 가상계좌 개설 URL  
	
	/**
	 * KT 기프티쇼 관련 정보
	 */
	public static final String GIFTSHOW_MDCODE = "M000101425";
	public static final String GIFTSHOW_TITLE = "WEYS 기프티쇼";
	public static final String GIFTSHOW_MSG = "WEYS COIN 포인트 교환";
	public static final String GIFTSHOW_CALLBACK = "07087677893";
	
	public static final String GIFTSHOW_AUTH_URL = "https://giftishowgw.giftishow.co.kr/media/Auth.asp";			// 기프티쇼 사용자 핸드폰 인증 URL
	public static final String GIFTSHOW_SEND_MMS_URL = "https://giftishowgw.giftishow.co.kr/media/request.asp";	// 기프티쇼 사용자 MMS 발송 URL
	public static final String GIFTSHOW_RESEND_MMS_URL = "https://giftishowgw.giftishow.co.kr/media/Resend.asp";	// 기프티쇼 사용자 MMS 재전송 URL
	public static final String GIFTSHOW_CHECK_GOODS_URL = "https://giftishowgw.giftishow.co.kr/media/check_good.asp";	// 기프티쇼 상품 확인 URL
	public static final String GIFTSHOW_CANCEL_GOODS_URL = "https://giftishowgw.giftishow.co.kr/media/coupon_cancel.asp";	// 기프티쇼 상품 취소 URL

	/**
	 * 예약 상태 
	 */
	public static final String RSV_START = "S";				// 1. 예약 완료
	public static final String RSV_INCOME = "I";			// 2. 입금 완료
	public static final String RSV_READY = "R";				// 3. 준비 완료
	public static final String RSV_FINISH = "F";			// 4. 거래 완료
	
	
	public static final String RSV_MISS = "M";				// 2-1. 입금 시간 초과 
	public static final String RSV_CANCEL_BEFORE = "CB";	// 2-2. 입금 전 취소 
	public static final String RSV_CANCEL = "C";			// 4-1. 예약 취소
	public static final String RSV_CANCEL_READY = "CR";		// 4-2. 환불 대기
	public static final String RSV_CANCEL_FINISH = "CF";	// 4-3. 환불 완료

	/**
	 * 인포뱅크 메시지 URL TEST & REAL 
	 */
	public static final String INFO_BANK_URL_TEST = "http://rest.supersms.co:6200/sms/xml";
	public static final String INFO_BANK_URL_REAL = "https://rest.supersms.co/sms/xml";
	/**
	 * 담당자 활동 로그
	 */
	public static final String ADM_ACT_LOGIN = "L";		// 로그인
	
	public static final String ADM_ACT_RSV_READY = "RR";		// 예약 준비 완료
	public static final String ADM_ACT_RSV_FINISH = "RF";		// 예약 거래 완료
	public static final String ADM_ACT_RSV_CANCEL = "RC";		// 예약 거래 강제 취소

	/**
	 * 카카오 알림톡 URL 
	 */
	public static final String KAKAO_TALK_MSG = "https://msggw.supersms.co:9443/v1/send/kko";
	
	/**
	 * 예약 푸쉬 메세지
	 */
	public static final String PUSH_MSG_INCOME = "입금이 정상적으로 확인되었습니다.";
	public static final String PUSH_MSG_READY = "담당자가 배정되었습니다. ";
	public static final String PUSH_MSG_SOON = "예약 장소에서 만나요!";

	public static final String PUSH_MSG_ADM_NEW = "새로운 예약이 있습니다. 확인해 주세요.";
	public static final String PUSH_MSG_ADM_TOMORROW = " 건의 예약이 있습니다.";
	
	/**
	 * 설문조사 URL
	 */
	public static final String SURVEY_URL_DEV = "https://dev.weys.exchange/web/survey/";
	public static final String SURVEY_URL_REAL = "https://weys.exchange/survey/";
	
	/**
	 * 아이트립 서비스 URL
	 */
	public static final String ITRIP_DEV_URL = "http://dev.e-itrip.com:50001";
	public static final String ITRIP_REAL_URL = "http://admin.e-itrip.com:50001";

	public static final String ITRIP_GRP_LIST_URL = "/api/v1/openAPI/addReceive";
	
	
}
