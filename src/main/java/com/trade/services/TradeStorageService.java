package com.trade.services;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.trade.model.TradeModel;
import com.trade.services.repository.TradeStoreRepository;

@Service
public class TradeStorageService {

	private static final Logger log = LoggerFactory.getLogger(TradeStorageService.class);
	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

	@Autowired
	TradeStoreRepository tradeStoreRepository;

	public boolean validateTrade(TradeModel tradeModel) throws Exception {
		if (isTradeValid(tradeModel)) {
			updateTrade(tradeModel);
			return true;
		} else {
			throw new Exception("Trade id is not valid: " + tradeModel.getTradeId());
		}
	}

	public boolean isTradeValid(TradeModel tradeModel) {
		if (validateMaturityDate(tradeModel)) {
			// Check if trade already exists
			Optional<TradeModel> exsitingTrade = tradeStoreRepository.findById(tradeModel.getTradeId());
			if (exsitingTrade.isPresent()) {
				// Check if the version is not less than existing trade
				return validateVersion(tradeModel, exsitingTrade.get());
			} else {
				return true;
			}
		}
		return false;
	}

	private boolean validateVersion(TradeModel newtrade, TradeModel existingTrade) {
		if (newtrade.getVersion() >= existingTrade.getVersion()) {
			return true;
		}
		return false;
	}
	
	//Save trade if validation is true
	public void updateTrade(TradeModel trade) {
		trade.setCreatedDate(LocalDate.now());
		tradeStoreRepository.save(trade);
	}

	public void updateExpiryFlag() {
		tradeStoreRepository.findAll().stream().forEach(t -> {
			if (!validateMaturityDate(t)) {
				t.setExpiredFlag("Y");
				log.info("Trade which needs to updated {}", t);
				tradeStoreRepository.save(t);
			}
		});
	}

	public List<TradeModel> findAll() {
		return tradeStoreRepository.findAll();
	}

	private boolean validateMaturityDate(TradeModel trade) {
		return trade.getMaturityDate().isBefore(LocalDate.now()) ? false : true;
	}
	
	 @Scheduled(cron = "${trade.schedule}")
	  public void currentTimeSchedule() throws InterruptedException {
		 log.info("Current Time {}", dateFormat.format(new Date()));
		 updateExpiryFlag();
	  
	  }

}
