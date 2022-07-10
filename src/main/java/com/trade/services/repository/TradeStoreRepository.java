package com.trade.services.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.trade.model.TradeModel;

@Repository
public interface TradeStoreRepository extends JpaRepository<TradeModel,String> { 
    
}
