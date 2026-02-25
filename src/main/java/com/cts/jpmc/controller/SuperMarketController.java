package com.cts.jpmc.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import java.util.Optional;

import org.springframework.web.bind.annotation.*;

import com.cts.jpmc.model.Cart;
import com.cts.jpmc.model.SuperMarket;
import com.cts.jpmc.service.SuperMarketService;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;

import jakarta.servlet.http.HttpServletResponse;
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
    public void removeItemFromCart(@PathVariable int itemNo) {
    	service.deleteItem(itemNo);
    }
    @GetMapping("/receipt")
    public void generateReceipt(HttpServletResponse response) throws IOException {
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=\"receipt.pdf\"");

        try (OutputStream os = response.getOutputStream()) {
            PdfWriter writer = new PdfWriter(os);
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document doc = new Document(pdfDoc);
            float[] colomnWidth = {100F, 100F, 100F, 100F, 100F};
            Table table = new Table(colomnWidth);
            table.addHeaderCell("SNO");
            table.addHeaderCell("ITEM NAME");
            table.addHeaderCell("ITEM PRICE");
            table.addHeaderCell("QUANTITY");
            table.addHeaderCell("TOTAL");
            List<Cart> cart = service.getCartItems();
            List<Float> checkout = service.checkout();
            for(Cart c:cart) {
            	table.addCell(String.valueOf(c.getId()));
            	table.addCell(String.valueOf(c.getItem().getItemName()));
            	table.addCell(String.valueOf(c.getItem().getPrice()));
            	table.addCell(String.valueOf(c.getQuantity()));
            	table.addCell(String.valueOf(c.getQuantity()*c.getItem().getPrice()));
            }
            
            doc.add(table);
            float[] colomnWidthTwo = {100F, 100F};
            Table tableTwo = new Table(colomnWidthTwo);
            tableTwo.addCell("Sub Total");
            tableTwo.addCell(String.valueOf(checkout.get(0)));
            tableTwo.addCell("GST");
            tableTwo.addCell(String.valueOf(checkout.get(1)));
            tableTwo.addCell("Total");
            tableTwo.addCell(String.valueOf(checkout.get(2)));
            doc.add(new Paragraph(""));
            doc.add(tableTwo);
            doc.close();
            service.deleteAllItem();
        }
    }
}
