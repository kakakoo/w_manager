package com.i4uworks.weys.vbank;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

@Repository
public interface VbankDao {

	List<Map<String, Object>> selectFindRsv(VbankVO reqVO);

	int insertVbankSt(VbankVO reqVO);

	int updateRsvIncome(Map<String, Object> reqMap);

	void insertRsvLogVO(Map<String, Object> reqMap);

	int selectRsvQrCnt(String qr);

	int updateQrCode(Map<String, Object> reqMap);

	Map<String, Object> selectRsvForm(int rsvId);

	Map<String, Object> selectResPayInfo(Map<String, Object> reqMap);

	void insertKakaoLog(Map<String, Object> talk);

	Map<String, Object> selectVbFinUuid(Map<String, Object> reqMap);

	void insertAlarm(Map<String, Object> alarm);

	List<String> selectAdminUuid();

}
