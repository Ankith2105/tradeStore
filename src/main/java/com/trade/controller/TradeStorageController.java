package com.trade.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.trade.model.TradeModel;
import com.trade.services.TradeStorageService;

@RestController

public class TradeStorageController {
	@Autowired
    TradeStorageService tradeStorageService;
	
	@PostMapping("/tradeStore")
    public ResponseEntity<Boolean> validateTrade(@RequestBody TradeModel tradeModel) throws Exception{

        return ResponseEntity.ok(tradeStorageService.validateTrade(tradeModel));
    }
	
	@GetMapping("/trade")
    public List<TradeModel> findAllTrades(){
        return tradeStorageService.findAll();
    }
}
