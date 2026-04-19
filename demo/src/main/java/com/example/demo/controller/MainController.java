package com.example.demo.controller;

import com.example.demo.model.Product;
import com.example.demo.model.OrderDetails;
import com.example.demo.service.ProductService;
import com.example.demo.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import jakarta.servlet.http.HttpServletResponse;

@Controller
public class MainController {

    @Autowired
    private ProductService productService;

    @Autowired
    private OrderService orderService;

    public static List<Product> cart = new ArrayList<>();

    // 1. Customer Home Page
    @GetMapping("/")
    public String homePage(@RequestParam(name="keyword", required=false) String keyword, Model model) {
        if (keyword != null) {
            model.addAttribute("products", productService.searchProducts(keyword));
        } else {
            model.addAttribute("products", productService.getAllProducts());
        }
        model.addAttribute("cartCount", cart.size()); 
        return "index";
    }

    // 2. Admin Login
    @GetMapping("/admin/login")
    public String showLoginPage() {
        return "login";
    }

    @PostMapping("/admin/login")
    public String processLogin(@RequestParam("password") String password) {
        return "admin123".equals(password) ? "redirect:/admin/dashboard?pass=admin123" : "redirect:/admin/login?error=Invalid";
    }

    // 3. Admin Dashboard
    @GetMapping("/admin/dashboard")
    public String adminDashboard(@RequestParam(name="pass", required=false) String password, Model model) {
        if ("admin123".equals(password)) {
            model.addAttribute("products", productService.getAllProducts());
            return "admin-products";
        }
        return "redirect:/admin/login?error=Unauthorized";
    }

    // 4. Product Management (Add, Delete, Edit)
    @GetMapping("/admin/add")
    public String showAddForm(Model model) {
        model.addAttribute("product", new Product());
        return "add-product";
    }

    @PostMapping("/admin/add")
    public String saveProduct(@ModelAttribute("product") Product product, 
                              @RequestParam("productImage") MultipartFile file) throws IOException {
        productService.saveProduct(product, file);
        return "redirect:/admin/dashboard?pass=admin123";
    }

    @GetMapping("/admin/product/delete/{id}")
    public String deleteProduct(@PathVariable Long id) {
        productService.deleteProductById(id);
        return "redirect:/admin/dashboard?pass=admin123";
    }

    @GetMapping("/admin/product/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        model.addAttribute("product", productService.getProductById(id));
        return "update-product";
    }

    @PostMapping("/admin/product/update/{id}")
    public String updateProduct(@PathVariable("id") Long id, 
                                @ModelAttribute("product") Product product,
                                @RequestParam("productImage") MultipartFile file) throws IOException {
        product.setId(id);
        productService.saveProduct(product, file);
        return "redirect:/admin/dashboard?pass=admin123";
    }

    // 5. Shopping Cart Logic
    @GetMapping("/addToCart/{id}")
    public String addToCart(@PathVariable Long id) {
        cart.add(productService.getProductById(id));
        return "redirect:/"; 
    }

    @GetMapping("/cart")
    public String viewCart(Model model) {
        model.addAttribute("cart", cart);
        double total = cart.stream().mapToDouble(Product::getPrice).sum();
        model.addAttribute("total", total);
        model.addAttribute("cartCount", cart.size());
        return "cart"; 
    } 

    @GetMapping("/cart/removeItem/{index}")
    public String removeItem(@PathVariable int index) {
        if (index >= 0 && index < cart.size()) cart.remove(index);
        return "redirect:/cart";
    }

    // 6. Checkout & Place Order Logic
    @GetMapping("/checkout")
    public String showCheckout(Model model) {
        if(cart.isEmpty()) return "redirect:/"; 
        double total = cart.stream().mapToDouble(Product::getPrice).sum();
        model.addAttribute("total", total);
        model.addAttribute("cartCount", cart.size());
        return "checkout"; 
    }

    @PostMapping("/placeOrder")
    public String placeOrder(@RequestParam("customerName") String name, 
                             @RequestParam("mobileNumber") String mobile, 
                             @RequestParam("address") String address,
                             Model model) {
        OrderDetails order = new OrderDetails();
        order.setCustomerName(name);
        order.setMobileNumber(mobile);
        order.setAddress(address);
        order.setTotalBill(cart.stream().mapToDouble(Product::getPrice).sum());
        order.setStatus("Pending");

        OrderDetails savedOrder = orderService.saveOrder(order);
        model.addAttribute("orderId", savedOrder.getId());
        cart.clear();
        return "order-success";
    }

    @GetMapping("/admin/orders")
    public String viewOrders(@RequestParam(name="pass", required=false) String password, 
                             @RequestParam(name="search", required=false) String search, 
                             Model model) {
        if ("admin123".equals(password)) {
            model.addAttribute("orders", (search != null) ? orderService.searchOrders(search) : orderService.getAllOrders());
            return "view-orders"; 
        }
        return "redirect:/admin/login?error=Unauthorized";
    }

    @GetMapping("/admin/order/delete/{id}")
    public String deleteOrder(@PathVariable Long id) {
        orderService.deleteOrderById(id);
        return "redirect:/admin/orders?pass=admin123";
    }

    @GetMapping("/buyNow/{id}")
    public String buyNow(@PathVariable Long id) {
        cart.clear();
        cart.add(productService.getProductById(id));
        return "redirect:/checkout";
    }

    @GetMapping("/view-my-orders")
    public String viewMyOrders(@RequestParam("mobile") String mobile, Model model) {
        model.addAttribute("orders", orderService.searchOrders(mobile));
        model.addAttribute("userMobile", mobile); 
        return "my-orders-list"; 
    }

    @GetMapping("/my-orders-login")
    public String showOrderLoginPage() {
        return "my-orders-login"; 
    }

    @GetMapping("/downloadReceipt/{id}")
    public void downloadReceipt(@PathVariable Long id, HttpServletResponse response) throws IOException {
        orderService.generatePdf(id, response);
    }
}