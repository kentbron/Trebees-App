// src/main/java/com/trebees/cafe/controller/AdminProductController.java
package com.trebees.cafe.controller;

import com.trebees.cafe.model.Product;
import com.trebees.cafe.repository.OrderRepository;
import com.trebees.cafe.service.ProductService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

@Controller
@RequestMapping("/admin/products")
public class AdminProductController {

    private static final Logger logger = LoggerFactory.getLogger(AdminProductController.class);

    private final ProductService productService;
    private final OrderRepository orderRepository;

    public AdminProductController(ProductService productService,
                                  OrderRepository orderRepository) {
        this.productService = productService;
        this.orderRepository = orderRepository;
    }

    @GetMapping
    public String listProducts(Model model) {
        model.addAttribute("products", productService.findAll());
        return "products/list";
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("product", new Product());
        return "products/form";
    }

    @PostMapping("/save")
    public String saveProduct(
            @Valid @ModelAttribute("product") Product product,
            BindingResult result,
            @RequestParam("imageFile") MultipartFile imageFile,
            RedirectAttributes redirectAttributes
    ) {
        if (result.hasErrors()) {
            return "products/form";
        }

        product.setProductName(product.getProductName().trim());

        // --- image upload logic (unchanged) ---
        String uploadDir = "uploads/";
        File uploadPath = new File(uploadDir);
        if (!uploadPath.exists()) uploadPath.mkdirs();

        if (!imageFile.isEmpty()) {
            String contentType = imageFile.getContentType();
            if (contentType == null ||
                (!contentType.equals("image/png") && !contentType.equals("image/jpeg"))) {
                redirectAttributes.addFlashAttribute("error", "Only JPG and PNG images are allowed.");
                return "redirect:/admin/products";
            }
            if (imageFile.getSize() > 5 * 1024 * 1024) {
                redirectAttributes.addFlashAttribute("error", "Image size must be under 5MB.");
                return "redirect:/admin/products";
            }

            String sanitized = StringUtils.cleanPath(imageFile.getOriginalFilename()).replace("..", "");
            String ext = sanitized.substring(sanitized.lastIndexOf('.') + 1);
            String fileName = UUID.randomUUID() + "-" + sanitized;

            try {
                BufferedImage original = ImageIO.read(imageFile.getInputStream());
                if (original == null) throw new IOException("Unsupported image format.");

                int w = 400;
                int h = (int)(original.getHeight() * (400.0 / original.getWidth()));
                Image scaled = original.getScaledInstance(w, h, Image.SCALE_SMOOTH);
                BufferedImage resized = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
                Graphics2D g = resized.createGraphics();
                g.drawImage(scaled, 0, 0, null);
                g.dispose();

                File out = new File(uploadDir + fileName);
                ImageIO.write(resized, ext, out);
                product.setImagePath("/uploads/" + fileName);

            } catch (IOException e) {
                logger.error("Image upload failed", e);
                redirectAttributes.addFlashAttribute("error", "Image upload failed.");
                return "redirect:/admin/products";
            }
        }

        Optional<Product> exists = productService.findById(product.getProductName());
        if (exists.isPresent()) {
            productService.update(product.getProductName(), product);
        } else {
            productService.save(product);
        }

        redirectAttributes.addFlashAttribute("success", "Product saved successfully.");
        return "redirect:/admin/products";
    }

    @GetMapping("/edit/name/{productName}")
    public String showEditForm(@PathVariable String productName,
                               Model model,
                               RedirectAttributes redirectAttributes) {

        Optional<Product> productOpt = productService.findById(productName.trim());
        if (productOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Product not found.");
            return "redirect:/admin/products";
        }
        model.addAttribute("product", productOpt.get());
        return "products/form";
    }

    @GetMapping("/markUnavailable/{productName}")
    public String markUnavailable(@PathVariable String productName,
                                  RedirectAttributes redirectAttributes) {
        String clean = productName.trim();
        long pending = orderRepository.countPendingOrdersForProduct(clean);
        if (pending > 0) {
            redirectAttributes.addFlashAttribute("error",
                "Cannot mark unavailable: there are " + pending + " pending orders.");
            return "redirect:/admin/products";
        }
        productService.findById(clean).ifPresent(p -> {
            p.setIsAvailable(0);
            productService.update(clean, p);
            redirectAttributes.addFlashAttribute("success", "Product marked as unavailable.");
        });
        return "redirect:/admin/products";
    }

    @GetMapping("/markAvailable/{productName}")
    public String markAvailable(@PathVariable String productName,
                                RedirectAttributes redirectAttributes) {
        String clean = productName.trim();
        productService.findById(clean).ifPresent(p -> {
            p.setIsAvailable(1);
            productService.update(clean, p);
            redirectAttributes.addFlashAttribute("success", "Product marked as available.");
        });
        return "redirect:/admin/products";
    }

    @GetMapping("/delete/name/{productName}")
    public String deleteProduct(@PathVariable String productName,
                                RedirectAttributes redirectAttributes) {
        String clean = productName.trim();

        // **NEW**: count all orders (of any status)
        long total = orderRepository.countOrdersForProduct(clean);
        if (total > 0) {
            redirectAttributes.addFlashAttribute("error",
                "Cannot delete product: it has " + total + " order(s) associated.");
            return "redirect:/admin/products";
        }

        Optional<Product> productOpt = productService.findById(clean);
        if (productOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Product not found.");
            return "redirect:/admin/products";
        }

        // remove image file
        String path = productOpt.get().getImagePath();
        if (path != null) {
            File img = new File("uploads/" + new File(path).getName());
            if (img.exists()) img.delete();
        }

        productService.delete(clean);
        redirectAttributes.addFlashAttribute("success", "Product deleted successfully.");
        return "redirect:/admin/products";
    }
}
