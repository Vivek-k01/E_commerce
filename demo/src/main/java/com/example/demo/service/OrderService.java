package com.example.demo.service;


import com.example.demo.model.OrderDetails;
import com.example.demo.repository.OrderRepository;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.property.TextAlignment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    // 1. Order Save karne ka logic
    public OrderDetails saveOrder(OrderDetails order) {
        return orderRepository.save(order);
    }

    // 2. PDF Banane ka logic (Controller se uthakar yahan laya)
    public void generatePdf(Long id, HttpServletResponse response) throws IOException {
        OrderDetails order = orderRepository.findById(id).orElseThrow();
        
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=Order_Slip_" + id + ".pdf");

        PdfWriter writer = new PdfWriter(response.getOutputStream());
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        document.add(new Paragraph("APNI SHOP - RECEIPT").setBold().setFontSize(20).setTextAlignment(TextAlignment.CENTER));
        document.add(new Paragraph("Order ID: #" + order.getId()));
        document.add(new Paragraph("Customer: " + order.getCustomerName()));
        document.add(new Paragraph("Total: Rs. " + order.getTotalBill()));
        document.add(new Paragraph("Status: " + order.getStatus()));
        
        document.close();
    }
 
    // 1. Saare Orders nikalne ke liye (Admin Dashboard ke liye)
    public List<OrderDetails> getAllOrders() {
        return orderRepository.findAll();
    }

    // 2. Mobile Number ya Naam se Search karne ke liye
    public List<OrderDetails> searchOrders(String keyword) {
        return orderRepository.findByCustomerNameContainingIgnoreCaseOrMobileNumberContaining(keyword, keyword);
    }

    // 3. Order Delete karne ke liye
    public void deleteOrderById(Long id) {
        orderRepository.deleteById(id);
    }

}
