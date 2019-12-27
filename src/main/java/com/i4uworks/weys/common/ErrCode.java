package com.i4uworks.weys.common;

public class ErrCode {

	public final static int SUCCESS = 200;					// 정상
	
	public static final int	LOGIN_FAILED = 1;				// 로그인 실패(아이디 또는 비밀번호 실패)
	public static final int	LOGIN_CLOSED = 2;				// 인증 전
	public static final int	EMAIL_NOT_EXIST = 3;			// 비밀번호 찾기 시도, 존재하지 않는 메일 
	public static final int	PWD_INCORRECT = 4;				// 비밀번호 변경 시도, 일치하지 않는 비밀번호
	
	public static final int	SIGNUP_EMAIL_OVERLAP = 11;		// 이메일 중복 에러
	public static final int	SIGNUP_FAILED = 12;				// 회원 가입 실패
	public static final int	SIGNUP_NICK_OVERLAP = 13;		// 닉네임 중복 에러
	public static final int DISABLE_EMOTICON = 14;			// 이모티콘입력 불가

	public static final int	IMG_UPLOAD_FAILED = 21;			// 이미지 업로드 실패

	public static final int	ALREADY_REPORTED = 31;			// 이미 신고한 게시물 
	public static final int	TRADE_DONE_DELETE = 32;			// 삭제 또는 완료된 게시물 

	public static final int	ALREADY_BANNED = 41;			// 이미 차단한 사용자 

	public static final int	PAYMENT_CALCEL = 51;			// 결제 진행중 오류 

	public static final int RSV_ENOUGH_MEMBERSHIP = 550;	// 멤버십 포인트 부족 
	public static final int RSV_ALREADY_EXIST = 555;		// 이미 존재하는 예약
	public static final int RSV_ALREADY_CANCEL = 556;		// 이미 취소된 예약
	public static final int RSV_NOT_EXIST = 560;			// 존재하지 않는 예약
	public static final int RSV_NOT_READY = 570;			// 준비되지 않은 예약

	public static final int	POINT_LACK = 61;				// 사용할 포인트 부족

	public static final int GIFTYSHOW_ERROR = 666;			// 기프티쇼 에러 

	public static final int ADM_GRP_NOT_CORRECT = 610;		// 그룹인수 잘못된 바코드 
	
	public static final int ACCESS_CRASH = 770;				// 다른 디바이스 접속 
	public static final int INVALID_TOKEN = 777;			// 토큰 에러
	public static final int INVALID_PARAMETER = 888;		// 파라미터 에러
	
	// 오늘 수령가능한 예약이 아닙니다. 

	public static final int	VERSION_ERROR = 990;			// 버전 에러
	public static final int	UNKNOWN_ERROR = 999;			// 알수없는 에러
	
	public static String getMessage (int code) {
		String result = null;
		
		switch(code) {
		case SUCCESS :
			result = "정상";
			break;
		case INVALID_PARAMETER :
			result = "파라미터 에러";
			break;
		case LOGIN_FAILED :
			result = "아이디 또는 비밀번호가 일치하지 않습니다.";
			break;
		case LOGIN_CLOSED :
			result = "이메일 인증이 완료되지 않았습니다.";
			break;
		case SIGNUP_EMAIL_OVERLAP :
			result = "이미 등록된 이메일 입니다.\n"
					+ "확인 후 다시 입력해 주세요.";
			break;
		case UNKNOWN_ERROR :
			result = "알수없는 에러가 발생하였습니다.";
			break;
		case INVALID_TOKEN :
			result = "인증기간이 만료되었습니다.\n"
					+ "다시 로그인 해주세요.";
			break;
		case SIGNUP_FAILED :
			result = "회원가입 실패! \n"
					+ "다시 시도해 주세요.";
			break;
		case IMG_UPLOAD_FAILED :
			result = "이미지 업로드 실패! \n"
					+ "다시 시도해 주세요.";
			break;
		case ALREADY_REPORTED :
			result = "이미 신고한 게시물입니다.";
			break;
		case TRADE_DONE_DELETE :
			result = "삭제 또는 완료된 게시물입니다.";
			break;
		case ALREADY_BANNED :
			result = "이미 차단한 사용자입니다.";
			break;
		case ACCESS_CRASH :
			result = "다른 디바이스에서 접속했습니다.\n"
					+ "확인 후 다시 접속해주세요.";
			break;
		case EMAIL_NOT_EXIST :
			result = "가입된 이메일이 아닙니다.";
			break;
		case SIGNUP_NICK_OVERLAP :
			result = "이미 등록된 닉네임 입니다.\n"
					+ "확인 후 다시 입력해 주세요.";
			break;
		case PAYMENT_CALCEL :
			result = "결제가 취소되었습니다.\n"
					+ "다시 시도해 주세요.";
			break;
		case PWD_INCORRECT :
			result = "비밀번호가 일치하지 않습니다.";
		break;
		case POINT_LACK :
			result = "포인트가 부족합니다.";
		break;
		case DISABLE_EMOTICON :
			result = "이모티콘은 입력할 수 없습니다.";
		break;
		case VERSION_ERROR :
			result = "서버에 일치하는 버전이 없습니다.";
		break; 
		case RSV_ALREADY_EXIST :
			result = "예약이 이미 존재합니다.";
		break;
		case RSV_ENOUGH_MEMBERSHIP :
			result = "예약가능한 멤버십 한도가 부족합니다.";
		break;
		case RSV_NOT_EXIST :
			result = "존재하지 않는 예약입니다.";
		break;
		case RSV_ALREADY_CANCEL :
			result = "이미 취소된 예약입니다.";
		break;
		case ADM_GRP_NOT_CORRECT :
			result = "해당 건의 인수건을 다시 확인해주세요.";
		break;
		}
		
		return result;
	}
}
