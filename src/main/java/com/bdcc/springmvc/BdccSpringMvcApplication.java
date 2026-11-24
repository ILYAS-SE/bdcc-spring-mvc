package com.bdcc.springmvc;

import com.bdcc.springmvc.entities.Product;
import com.bdcc.springmvc.repository.ProductRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

// Etape 5 (demo du prof) : pour DESACTIVER la protection par defaut de Spring Security,
// remplacer l'annotation par :
// @SpringBootApplication(exclude = org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class)
// ATTENTION : avec la SecurityConfig fournie, cette exclusion n'est plus necessaire (voir README).
@SpringBootApplication
public class BdccSpringMvcApplication {

    public static void main(String[] args) {
        SpringApplication.run(BdccSpringMvcApplication.class, args);
    }

    /** Etape 4 : test de la couche DAO au demarrage. */
    @Bean
    CommandLineRunner start(ProductRepository productRepository) {
        return args -> {
            if (productRepository.count() == 0) {
                productRepository.save(Product.builder().name("Computer").price(5400).quantity(12).build());
                productRepository.save(Product.builder().name("Printer").price(1200).quantity(3).build());
                productRepository.save(Product.builder().name("Smartphone").price(3200).quantity(25).build());
                productRepository.save(Product.builder().name("Clavier mecanique").price(450).quantity(40).build());
                productRepository.save(Product.builder().name("Souris sans fil").price(180).quantity(2).build());
                productRepository.save(Product.builder().name("Ecran 27 pouces").price(2100).quantity(8).build());
                productRepository.save(Product.builder().name("Casque audio").price(650).quantity(15).build());
            }
            productRepository.findAll().forEach(p -> System.out.println(p.toString()));
        };
    }
}
