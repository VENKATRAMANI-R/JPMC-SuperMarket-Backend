package com.cts.jpmc.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cts.jpmc.model.SuperMarket;
@Repository
public interface SuperMarketRepo extends JpaRepository<SuperMarket, Integer>{
	SuperMarket findByItemName(String itemName);
}
