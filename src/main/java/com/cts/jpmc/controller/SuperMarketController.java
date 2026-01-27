package com.cts.jpmc.controller;

import java.util.List;

import java.util.Optional;
import org.springframework.web.bind.annotation.*;

import com.cts.jpmc.model.Cart;
import com.cts.jpmc.model.SuperMarket;
import com.cts.jpmc.service.SuperMarketService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/supermarket")
@RequiredArgsConstructor
public class SuperMarketController {

    private final SuperMarketService service;

    @GetMapping("/items")
    public List<SuperMarket> getAllItems() {
        return service.getAllItems();
    }

    @PostMapping("/items")
    public SuperMarket addItem(@RequestBody SuperMarket item) {
        return service.addItem(item);
    }

    @PostMapping("/cart/{itemNo}/{quantity}")
    public Cart addToCart(@PathVariable int itemNo, @PathVariable int quantity) {
        return service.addToCart(itemNo, quantity);
    }

    @GetMapping("/cart")
    public List<Cart> getCartItems() {
        return service.getCartItems();
    }
    
    @GetMapping("/cartItem/{itemNo}")
    public Optional<Cart> getCartItem(@PathVariable int itemNo){
    	return service.getCartItem(itemNo);
    }

    @GetMapping("/checkout")
    public List<Float> checkout() {
        return service.checkout();
    }
    
    @DeleteMapping("/cart/remove/{itemNo}")
    public void RemoveItemFromCart(@PathVariable int itemNo) {
    	service.deleteItem(itemNo);
    }
}
