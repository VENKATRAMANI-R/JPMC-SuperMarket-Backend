package com.cts.supermarket;

import com.cts.supermarket.model.Cart;
import com.cts.supermarket.model.SuperMarket;
import com.cts.supermarket.repo.CartRepo;
import com.cts.supermarket.repo.SuperMarketRepo;
import com.cts.supermarket.service.SuperMarketServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

@SpringBootTest
class supermarketApplicationTests {

    @Test
    void contextLoads() {
    }

    @Autowired
    private SuperMarketServiceImpl service;

    @MockitoBean
    private SuperMarketRepo superMarketRepo;

    @MockitoBean
    private CartRepo cartRepo;
    
    //test case initialization
    private SuperMarket sm(int itemNo, String name, float price) {
        SuperMarket s = new SuperMarket();
        s.setItemNo(itemNo);
        s.setItemName(name);
        s.setPrice(price);
        return s;
    }

    private Cart cart(Long id, SuperMarket item, int qty) {
        Cart c = new Cart();
        c.setId(id);
        c.setItem(item);
        c.setQuantity(qty);
        return c;
    }
    // Test cases Item operations
    @Test
    @DisplayName("getAllItems() should return all items from repository")
    void getAllItems_returnsAll() {
        // given
        List<SuperMarket> data = List.of(
                sm(1, "Apple", 30.0f),
                sm(2, "Banana", 10.0f)
        );
        given(superMarketRepo.findAll()).willReturn(data);
        List<SuperMarket> result = service.getAllItems();
        assertThat(result).hasSize(2)
                .extracting(SuperMarket::getItemName)
                .containsExactly("Apple", "Banana");
        then(superMarketRepo).should().findAll();
    }

    @Test
    @DisplayName("getItemById() returns item when found")
    void getItemById_found() {
        // given
        SuperMarket item = sm(100, "Milk", 45.0f);
        given(superMarketRepo.findById(100)).willReturn(Optional.of(item));
        SuperMarket result = service.getItemById(100);// when
        assertThat(result.getItemName()).isEqualTo("Milk");        // then
        then(superMarketRepo).should().findById(100);
    }

    @Test
    @DisplayName("addItem() saves and returns the item")
    void addItem_saves() {
        SuperMarket item = sm(10, "Bread", 35.0f);
        given(superMarketRepo.save(any(SuperMarket.class))).willAnswer(invocation -> invocation.getArgument(0));
        SuperMarket saved = service.addItem(item);// when
        assertThat(saved.getItemName()).isEqualTo("Bread");        // then
        then(superMarketRepo).should().save(item);
    }

    @Test
    @DisplayName("deleteItem() calls cartRepo.deleteById(itemNo) per current implementation")
    void deleteItem_deletesFromCartRepo() {
        service.deleteItem(42);// when
        ArgumentCaptor<Long> captor = ArgumentCaptor.forClass(Long.class);// then
        then(cartRepo).should().deleteById(captor.capture());
        assertThat(captor.getValue()).isEqualTo(42L);
    }
    // Test cases for Cart operations
    @Test
    @DisplayName("getCartItem() fetches from cartRepo by id")
    void getCartItem_fetches() {
        SuperMarket item = sm(1, "Soap", 25.0f);
        Cart c = cart(1L, item, 2);
        given(cartRepo.findById(1L)).willReturn(Optional.of(c));
        Optional<Cart> result = service.getCartItem(1);        // when
        assertThat(result).isPresent();        // then
        assertThat(result.get().getQuantity()).isEqualTo(2);
        then(cartRepo).should().findById(1L);
    }

    @Test
    @DisplayName("addToCart() increments quantity if item already in cart")
    void addToCart_incrementsExisting() {
        SuperMarket item = sm(7, "Sugar", 40.0f);
        given(superMarketRepo.findById(7)).willReturn(Optional.of(item));

        Cart existing = cart(77L, item, 2);
        given(cartRepo.findByItem_ItemNo(7)).willReturn(Optional.of(existing));
        given(cartRepo.save(any(Cart.class))).willAnswer(invocation -> invocation.getArgument(0));
        Cart updated = service.addToCart(7, 5);// when
        assertThat(updated.getId()).isEqualTo(77L);// then
        assertThat(updated.getQuantity()).isEqualTo(7);
        then(cartRepo).should().save(existing);
    }

    @Test
    @DisplayName("getCartItems() returns all items from cart")
    void getCartItems_returnsAll() {
        // given
        List<Cart> items = List.of(
                cart(1L, sm(1, "A", 10f), 1),
                cart(2L, sm(2, "B", 20f), 2)
        );
        given(cartRepo.findAll()).willReturn(items);
        // when
        List<Cart> result = service.getCartItems();
        // then
        assertThat(result).hasSize(2);
        then(cartRepo).should().findAll();
    }
    // Test cases for checkout & clear
    @Test
    @DisplayName("checkout() computes subtotal, GST(18%), and total")
    void checkout_computesTotals() {
        // given
        List<Cart> items = new ArrayList<>();
        items.add(cart(1L, sm(1, "Pen", 10.0f), 3));    // 30.0
        items.add(cart(2L, sm(2, "Book", 50.0f), 2));   // 100.0
        // subtotal = 130.0, GST = 23.4, total = 153.4
        given(cartRepo.findAll()).willReturn(items);
        // when
        List<Float> result = service.checkout();
        // then
        assertThat(result).hasSize(3);
        assertThat(result.get(0)).isEqualTo(130.0f);// subtotal
        assertThat(result.get(1)).isEqualTo(130.0f * 0.18f);// GST
        assertThat(result.get(2)).isEqualTo(130.0f + 130.0f * 0.18f);// total
        then(cartRepo).should().findAll();
    }

    @Test
    @DisplayName("deleteAllItem() clears cart")
    void deleteAllItem_clearsCart() {
        service.deleteAllItem();
        then(cartRepo).should().deleteAll();
    }
}
