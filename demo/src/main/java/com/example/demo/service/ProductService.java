package com.example.demo.service;


import com.example.demo.model.Product;
import com.example.demo.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    private final String uploadDir = "src/main/resources/static/productImages";

    // 1. Saare Products nikalna
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    // 2. Search logic
    public List<Product> searchProducts(String keyword) {
        return productRepository.findByNameContainingIgnoreCase(keyword);
    }

    // 3. Product Save/Add karna (With Image Logic)
    public void saveProduct(Product product, MultipartFile file) throws IOException {
        if (!file.isEmpty()) {
            String imageUUID = file.getOriginalFilename();
            Path fileNameAndPath = Paths.get(uploadDir, imageUUID);
            if (!Files.exists(fileNameAndPath.getParent())) {
                Files.createDirectories(fileNameAndPath.getParent());
            }
            Files.write(fileNameAndPath, file.getBytes());
            product.setImageName(imageUUID);
        } else if (product.getImageName() == null) {
            product.setImageName("default.jpg");
        }
        productRepository.save(product);
    }

    // 4. ID se Product nikalna (Edit ke liye)
    public Product getProductById(Long id) {
        return productRepository.findById(id).orElseThrow();
    }

    // 5. Product Delete karna
    public void deleteProductById(Long id) {
        productRepository.deleteById(id);
    }
}
