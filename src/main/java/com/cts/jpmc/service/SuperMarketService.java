package com.cts.jpmc.service;


import java.util.List;
import java.util.Optional;

import com.cts.jpmc.model.Cart;
import com.cts.jpmc.model.SuperMarket;

public interface SuperMarketService {
	List<SuperMarket> getAllItems();
    SuperMarket getItemById(int itemNo);
    SuperMarket addItem(SuperMarket item);
    SuperMarket updateItem(int itemNo, SuperMarket item);
    void deleteItem(int itemNo);
    Optional<Cart> getCartItem(int itemNo);
    Cart addToCart(int itemNo, int quantity);
    List<Cart> getCartItems();
    List<Float> checkout();
}
