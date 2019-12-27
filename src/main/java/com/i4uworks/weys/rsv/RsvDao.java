package com.i4uworks.weys.rsv;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

@Repository
public interface RsvDao {

	Map<String, Object> selectRsvListCnt(RsvInfoVO reqVO);

	List<RsvInfoVO> selectRsvList(RsvReqVO reqVO);

	int updateRsvSt(Map<String, Object> reqMap);

	int insertAdmActLog(Map<String, Object> reqMap);

	RsvInfoVO selectRsvInfo(RsvInfoVO reqVO);

	int insertRsvLogMap(Map<String, Object> reqMap);

	int updateRsvDone(RsvDoneVO reqVO);

	int insertRsvLog(RsvDoneVO reqVO);

	Map<String, Object> selectPushInfo(Map<String, Object> reqMap);

	List<Map<String, Object>> selectNotiUserInfo(Map<String, Object> reqMap);

	RsvInfoVO selectRsvDetail(Map<String, Object> reqMap);

	int deleteRsvQr(String dt);

	int selectRsvQrCnt(String qr);

	void updateQrCode(Map<String, Object> reqMap);

	int updateRsvCheckIncome(String dt);

	Map<String, Object> selectCompleteRsvInfo(RsvDoneVO reqVO);

	String selectRsvSt(String rsvId);

	int updateCancelAmnt(String date);

	void insertCancelLog(Map<String, Object> logMap);

	List<Map<String, Object>> selectRsvCntList(String date);

	List<Map<String, Object>> selectAdmTokenInfo(Integer integer);

	String selectUsrNation(int rsvId);

	void insertKakaoLog(Map<String, Object> talk);

	List<Integer> selectRsvMissIncome(String dt);

	void updateReturnMemCost(int rsvId);

	void updateReturnUseCost(int rsvId);

	void insertReturnRA(int rsvId);

	void insertAlarm(Map<String, Object> alarm);

	List<Integer> selectRsvMissIncomeCp(String dt);

	void updateReturnCoupon(List<Integer> couponList);

	List<String> selectUsrBonusAlert(Map<String, Object> reqMap);

	void insertAlarmBonus(Map<String, Object> alarm);

	List<Map<String, Object>> selectTodayDeliver(String date);

	List<Map<String, Object>> selectTodayRsv(Map<String, Object> reqMap);

	void updateReturnMoney(Map<String, Object> moneyMap);

	void insertReturnMoneyLog(Map<String, Object> moneyMap);

	int selectRsvCheck(Map<String, Object> reqMap);

	List<String> selectMngList(String eNC_KEY);

	Map<String, Object> selectRsvDtInfo(RsvReqVO reqVO);

	Map<String, Object> selectAcceptCnt(Map<String, Object> reqMap);

	int updateAcceptDoneRsv(Map<String, Object> reqMap);

	void insertAcceptLog(Map<String, Object> reqMap);

	List<Map<String, Object>> selectUsrMemoList(String rsvId);

	int updateRsvDt(RsvReqVO reqVO);

	int insertUsrMemo(RsvReqVO reqVO);

	Map<String, Object> selectRsvNotiInfo(Map<String, Object> reqMap);

	List<Map<String, Object>> selectSmsList(Map<String, Object> infoMap);

	Map<String, Object> selectTransferInfo(Map<String, Object> reqMap);

	int selectCancelRsv(Map<String, Object> reqMap);

	int selectChangeRsv(Map<String, Object> reqMap);

	void insertTransLogout(Map<String, Object> reqMap);

	void insertTransLogIn(Map<String, Object> reqMap);

	void updateTransLogoutToken(Map<String, Object> reqMap);

	int selectRsvSameCnt(int rsvId);

	List<Map<String, Object>> selectRsvDoneSurvey(Map<String, Object> reqMap);

	List<Map<String, Object>> selectRsvDoneSurveyTest(Map<String, Object> reqMap);

	Map<String, Object> selectOriginRsv(int rsvId);

	String selectAdminNm(int adminKey);

	void insertUsrMemoCh(Map<String, Object> oriMap);

	List<Map<String, Object>> selectGrpList(Map<String, Object> reqMap);

	int updateGrpAcceptDone(Map<String, Object> reqMap);

	int insertGrpAccptLog(Map<String, Object> reqMap);

	int updateGrpLog(int rsvId);

	void updateRollBackRsv(int rsvId);

	List<Map<String, Object>> selectCenterRsv(Map<String, Object> reqMap);

	int selectReturnCnt(Map<String, Object> reqMap);

	int selectFinCnt(Map<String, Object> reqMap);

	List<Map<String, Object>> selectStoreList(RsvReqVO reqVO);

	Map<String, Object> selectStoreInfo(RsvReqVO reqVO);

	List<Map<String, Object>> selectStoreGrpList(RsvReqVO reqVO);

	Map<String, Object> selectGrpTp(String barcode);

	int updateRsvPass(Map<String, Object> reqMap);

	void updateCouponReturn(Map<String, Object> reqMap);

	void updateBonusReturn(Map<String, Object> reqMap);

	List<Map<String, Object>> selectMissUuid(Map<String, Object> reqMap);

	List<Map<String, Object>> selectMoneyList(String unit);

	Map<String, Object> selectRsvMoneyInfo(int rsvId);

	void updateMoneyMng(Map<String, Object> mMng);

	List<Map<String, Object>> selectCenterADRsv(Map<String, Object> reqMap);

	List<Map<String, Object>> selectReturnAD(Map<String, Object> reqMap);

	int selectReturnCntAD(Map<String, Object> reqMap);
}
