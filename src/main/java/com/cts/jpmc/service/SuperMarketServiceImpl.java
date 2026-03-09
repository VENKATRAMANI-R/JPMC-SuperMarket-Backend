	package com.cts.jpmc.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.cts.jpmc.exception.ItemNotFoundException;
import com.cts.jpmc.model.Cart;
import com.cts.jpmc.model.SuperMarket;
import com.cts.jpmc.repo.CartRepo;
import com.cts.jpmc.repo.SuperMarketRepo;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SuperMarketServiceImpl implements SuperMarketService {
	private final Logger logger = LoggerFactory.getLogger(SuperMarketServiceImpl.class);
    private final SuperMarketRepo superMarketRepo;
    private final CartRepo cartRepo;

    @Override
    public List<SuperMarket> getAllItems() {
    	
        return superMarketRepo.findAll();
    }

    @Override
    public SuperMarket getItemById(int itemNo) {
    	logger.info("getting Item By Id");
        return superMarketRepo.findById(itemNo)
                .orElseThrow(() -> new ItemNotFoundException(itemNo+": Not found"));

    }

    @Override
    public SuperMarket addItem(SuperMarket item) {
    	logger.info("Item Added");
        return superMarketRepo.save(item);
    }

    @Override
    public SuperMarket updateItem(int itemNo, SuperMarket item) {
        SuperMarket existing = getItemById(itemNo);
        existing.setItemName(item.getItemName());
        existing.setPrice(item.getPrice());
        logger.info("Item Updated");
        return superMarketRepo.save(existing);
    }

    @Override
    public void deleteItem(int itemNo) {
    	logger.info("Item Deleted");
        cartRepo.deleteById((long) itemNo);
    }
    
    @Override
    public Optional<Cart> getCartItem(int itemNo) {
    	return cartRepo.findById((long) itemNo);
    }
    @Override
    public Cart addToCart(int itemNo, int quantity) {
        SuperMarket item = getItemById(itemNo);
        
        Optional<Cart> existingCartEntry = cartRepo.findByItem_ItemNo(itemNo);

        Cart cartEntry;
        if (existingCartEntry.isPresent()) {
            cartEntry = existingCartEntry.get();
            cartEntry.setQuantity(cartEntry.getQuantity() + quantity); 
            logger.info("Item quantity updated in cart");
        } else {
            cartEntry = new Cart();
            cartEntry.setItem(item);
            cartEntry.setQuantity(quantity);
            logger.info("New item added to cart");
        }

        return cartRepo.save(cartEntry);
    }

    @Override
    public List<Cart> getCartItems() {
    	logger.info("Get Cart Items");
        return cartRepo.findAll();
    }

    @Override
    public List<Float> checkout() {
        List<Cart> cartItems = cartRepo.findAll();
        float total = 0.0f;
        List<Float> Result = new ArrayList<Float>();
        total = cartItems.stream().map(i->i.getQuantity()*i.getItem().getPrice()).reduce(0.0f, Float::sum);
        GST calcGst = (float x)->x*0.18f;
        Result.add(total);
        float gst = calcGst.calculateGST(total);
        Result.add(gst);
        Result.add(total+gst);
        logger.info("Get Checkout details");
        return Result;
    }
    
    @Override
    public void deleteAllItem() {
    	logger.info("All Item Deleted");
        cartRepo.deleteAll();
    }
}
