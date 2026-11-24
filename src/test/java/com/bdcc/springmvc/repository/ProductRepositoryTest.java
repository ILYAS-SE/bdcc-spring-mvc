package com.bdcc.springmvc.repository;

import com.bdcc.springmvc.entities.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class ProductRepositoryTest {

    @Autowired
    private ProductRepository repository;

    @BeforeEach
    void setUp() {
        repository.deleteAll();
        repository.save(Product.builder().name("Computer").price(5000).quantity(10).build());
        repository.save(Product.builder().name("Printer").price(1200).quantity(3).build());
    }

    @Test
    void shouldFindAll() {
        assertEquals(2, repository.findAll().size());
    }

    @Test
    void shouldSearchByKeywordIgnoringCase() {
        var page = repository.findByNameContainsIgnoreCase("comp", PageRequest.of(0, 5));
        assertEquals(1, page.getTotalElements());
        assertEquals("Computer", page.getContent().get(0).getName());
    }

    @Test
    void shouldDelete() {
        Long id = repository.findAll().get(0).getId();
        repository.deleteById(id);
        assertTrue(repository.findById(id).isEmpty());
    }
}
