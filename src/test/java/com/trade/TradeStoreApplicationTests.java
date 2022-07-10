package com.trade;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.trade.controller.TradeStorageController;
import com.trade.model.TradeModel;
import com.trade.services.TradeStorageService;

@SpringBootTest
class TradeStoreApplicationTests {
	
	@Autowired
	private TradeStorageController tradeStorageController;
	
	@Autowired
	private TradeStorageService service;
	
	@Test
	void validate_trade_store_should_return_success() throws Exception {
		// ******* ARRANGE *******//
		ResponseEntity<Boolean> responseEntity = tradeStorageController.validateTrade(createTradeStore("T1",2,LocalDate.now()));
		// ******* ACT *******//
		List<TradeModel> tradeList =tradeStorageController.findAllTrades();
		// ******* ASSERT *******//
		Assertions.assertEquals(1, tradeList.size());
		Assertions.assertEquals("T1",tradeList.get(0).getTradeId());
		Assertions.assertTrue(true);
	}

	@Test
	void validate_trade_when_Maturity_DatePast_throwException() {
		try {
			// ******* ARRANGE *******//
			LocalDate localDate = getLocalDate(2022, 07, 10);
			// ******* ACT *******//
			ResponseEntity<Boolean> responseEntity = tradeStorageController.validateTrade(createTradeStore("T2", 1, localDate));
		}catch (Exception e) {
			// ******* ASSERT *******//
			Assertions.assertEquals("Trade Id T2 is not found", e.getMessage());
		}
	}

	
	@Test
	void validate_trade_for_Old_Version() throws Exception {
		// ******* ARRANGE *******//
		ResponseEntity<Boolean> newResponse = tradeStorageController.validateTrade(createTradeStore("T1",2,LocalDate.now()));
		// ******* ACT *******//
		List<TradeModel> tradeList =tradeStorageController.findAllTrades();
		// ******* ASSERT *******//
		Assertions.assertEquals(1, tradeList.size());
		Assertions.assertEquals("T1",tradeList.get(0).getTradeId());
		Assertions.assertEquals(2,tradeList.get(0).getVersion());
		Assertions.assertEquals("T1B1",tradeList.get(0).getBookId());
		try {
			// ******* ARRANGE *******//
			ResponseEntity<Boolean> oldResponse = tradeStorageController.validateTrade(createTradeStore("T1", 1, LocalDate.now()));

		}catch (Exception e){
			System.out.println(e.getMessage());
		}
		// ******* ACT *******//
		List<TradeModel> tradeList1 =tradeStorageController.findAllTrades();
		// ******* ASSERT *******//
		Assertions.assertEquals(1, tradeList1.size());
		Assertions.assertEquals("T1",tradeList1.get(0).getTradeId());
		Assertions.assertEquals(2,tradeList1.get(0).getVersion());
		Assertions.assertEquals("T1B1",tradeList.get(0).getBookId());
	}

	@Test
	void validate_trade_for_Same_trade_version() throws Exception{
		ResponseEntity<Boolean> responseEntity = tradeStorageController.validateTrade(createTradeStore("T1",2,LocalDate.now()));
		List<TradeModel> tradeList =tradeStorageController.findAllTrades();
		Assertions.assertEquals(1, tradeList.size());
		Assertions.assertEquals("T1",tradeList.get(0).getTradeId());
		Assertions.assertEquals(2,tradeList.get(0).getVersion());
		Assertions.assertEquals("T1B1",tradeList.get(0).getBookId());

		TradeModel trade2 = createTradeStore("T1",2,LocalDate.now());
		trade2.setBookId("T1B1V2");
		ResponseEntity<Boolean> responseEntity2 = tradeStorageController.validateTrade(trade2);
		List<TradeModel> tradeList2 =tradeStorageController.findAllTrades();
		Assertions.assertEquals(1, tradeList2.size());
		Assertions.assertEquals("T1",tradeList2.get(0).getTradeId());
		Assertions.assertEquals(2,tradeList2.get(0).getVersion());
		Assertions.assertEquals("T1B1V2",tradeList2.get(0).getBookId());

		TradeModel trade3 = createTradeStore("T1",2,LocalDate.now());
		trade3.setBookId("T1B1V3");
		ResponseEntity<Boolean> responseEntity3 = tradeStorageController.validateTrade(trade3);
		List<TradeModel> tradeList3 =tradeStorageController.findAllTrades();
		Assertions.assertEquals(1, tradeList3.size());
		Assertions.assertEquals("T1",tradeList3.get(0).getTradeId());
		Assertions.assertEquals(2,tradeList3.get(0).getVersion());
		Assertions.assertEquals("T1B1V3",tradeList3.get(0).getBookId());

	}
	private TradeModel createTradeStore(String tradeId,int version,LocalDate maturityDate){
		TradeModel trade = new TradeModel();
		trade.setTradeId(tradeId);
		trade.setBookId(tradeId+"B1");
		trade.setVersion(version);
		trade.setCounterPartyId(tradeId+"Cp");
		trade.setMaturityDate(maturityDate);
		trade.setExpiredFlag("Y");
		return trade;
	}

	public static LocalDate getLocalDate(int year,int month, int day){
		LocalDate localDate = LocalDate.of(year,month,day);
		return localDate;
	}

}
