package com.bdcc.springmvc.web;

import com.bdcc.springmvc.entities.Product;
import com.bdcc.springmvc.repository.ProductRepository;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@AllArgsConstructor
public class ProductController {

    private static final int PAGE_SIZE = 5;

    // Injection par constructeur (generee par Lombok @AllArgsConstructor)
    private final ProductRepository productRepository;

    @GetMapping("/")
    public String home() {
        return "redirect:/user/index";
    }

    // ---------- Liste + recherche + pagination ----------
    @GetMapping("/user/index")
    @PreAuthorize("hasRole('USER')")
    public String index(Model model,
                        @RequestParam(name = "page", defaultValue = "0") int page,
                        @RequestParam(name = "keyword", defaultValue = "") String keyword) {
        Page<Product> pageProducts = productRepository.findByNameContainsIgnoreCase(
                keyword, PageRequest.of(Math.max(page, 0), PAGE_SIZE, Sort.by("id")));
        model.addAttribute("productList", pageProducts.getContent());
        model.addAttribute("totalPages", pageProducts.getTotalPages());
        model.addAttribute("totalElements", pageProducts.getTotalElements());
        model.addAttribute("currentPage", pageProducts.getNumber());
        model.addAttribute("keyword", keyword);
        return "products";
    }

    // ---------- Suppression (POST + jeton CSRF) ----------
    @PostMapping("/admin/delete")
    @PreAuthorize("hasRole('ADMIN')")
    public String delete(@RequestParam Long id,
                         @RequestParam(defaultValue = "0") int page,
                         @RequestParam(defaultValue = "") String keyword,
                         RedirectAttributes ra) {
        productRepository.deleteById(id);
        ra.addFlashAttribute("message", "Produit supprime avec succes");
        ra.addAttribute("page", page);
        ra.addAttribute("keyword", keyword);
        return "redirect:/user/index";
    }

    // ---------- Ajout ----------
    @GetMapping("/admin/newProduct")
    @PreAuthorize("hasRole('ADMIN')")
    public String newProduct(Model model) {
        model.addAttribute("product", new Product());
        return "productForm";
    }

    // ---------- Edition ----------
    @GetMapping("/admin/editProduct")
    @PreAuthorize("hasRole('ADMIN')")
    public String editProduct(@RequestParam Long id, Model model, RedirectAttributes ra) {
        Optional<Product> product = productRepository.findById(id);
        if (product.isEmpty()) {
            ra.addFlashAttribute("error", "Produit introuvable");
            return "redirect:/user/index";
        }
        model.addAttribute("product", product.get());
        return "productForm";
    }

    // ---------- Enregistrement (ajout ET mise a jour) avec validation ----------
    @PostMapping("/admin/saveProduct")
    @PreAuthorize("hasRole('ADMIN')")
    public String saveProduct(@Valid Product product, BindingResult bindingResult, RedirectAttributes ra) {
        if (bindingResult.hasErrors()) {
            return "productForm";
        }
        boolean isNew = product.getId() == null;
        productRepository.save(product);
        if (isNew) {
            ra.addFlashAttribute("message", "Produit ajoute avec succes");
            return "redirect:/admin/newProduct";
        }
        ra.addFlashAttribute("message", "Produit mis a jour avec succes");
        return "redirect:/user/index";
    }

    // ---------- Pages techniques ----------
    @GetMapping("/notAuthorized")
    public String notAuthorized() {
        return "notAuthorized";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }
}
