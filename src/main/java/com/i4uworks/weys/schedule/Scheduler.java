package com.i4uworks.weys.schedule;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.i4uworks.weys.common.Utils;
import com.i4uworks.weys.rsv.RsvService;

@Component
public class Scheduler {
	
	protected Logger logger = LoggerFactory.getLogger(getClass());
	@Autowired
	private RsvService rsvService;

	@Value("${SCH}")
	private String SCH;
	
	/*
	//crontab 설정 
	*	*	*	*	*	*  수행할 명령어
	┬	┬	┬	┬	┬	┬
	│	│	│	│	│	│
	│	│	│	│	│	└───────── 요일 (0 - 6) (0 =일요일)
	│	│	│	│	└───────── 월 (1 - 12)
	│	│	│	└────────── 일 (1 - 31)
	│	│	└─────────── 시 (0 - 23)
	│	└──────────── 분 (0 - 59)
	└───────────── 초 (0 - 59)
	*/

	@Scheduled(cron = "0 0 9 * * *")
	public void notiRsv(){
		try {
//			logger.info("7, 14, 30일 잔여금액 알려주기 : " + Utils.getTodayDate("yyyy.MM.dd HH:mm:ss"));
//			rsvService.insertNotiUserBonus();

			if(SCH.equals("N")){
				return ;
			}
			logger.info("9시. 오늘 배달 예약 푸시 보내기 : " + Utils.getTodayDate("yyyy.MM.dd HH:mm:ss"));
			String date = Utils.getDiffDate(0);
			rsvService.updateNotiTodayDeliver(date);
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
	}

	@Scheduled(cron = "30 0/10 5-23 * * *")
	public void sendRsvFinishSurvey(){
		try {
			if(SCH.equals("N")){
				return ;
			}
			logger.info("매 10마다 거래완료 고객 설문지 보내기 : " + Utils.getTodayDate("yyyy.MM.dd HH:mm:ss"));
			rsvService.updateRsvFinishSurvey();
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
	}

	@Scheduled(cron = "10 0/10 * * * *")
	public void checkRsvIncome(){
		try {
			if(SCH.equals("N")){
				return ;
			}
			logger.info("매 10마다 사용자 입금 지난예약 상태 변경 : " + Utils.getTodayDate("yyyy.MM.dd HH:mm:ss"));
			rsvService.updateRsvCheckIncome();
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
	}

	@Scheduled(cron = "0 0/15 * * * *")
	public void notiTodayRsv(){
		try {
			if(SCH.equals("N")){
				return ;
			}
			logger.info("15분 마다 2시간전 예약 푸시 발송 : " + Utils.getTodayDate("yyyy.MM.dd HH:mm:ss"));
			
			rsvService.updateNotiTodayRsv();
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
	}
}
