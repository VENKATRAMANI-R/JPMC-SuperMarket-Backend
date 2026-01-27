package com.cts.jpmc.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cts.jpmc.model.Cart;
@Repository
public interface CartRepo extends JpaRepository<Cart, Long> {
	Optional<Cart> findByItem_ItemNo(int itemNo);
	boolean deleteByItem_ItemNo(int itemNo);

}
