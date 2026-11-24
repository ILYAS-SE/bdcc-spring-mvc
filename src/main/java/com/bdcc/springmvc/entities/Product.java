package com.bdcc.springmvc.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty(message = "Le nom ne doit pas etre vide")
    @Size(min = 3, max = 50, message = "Le nom doit contenir entre 3 et 50 caracteres")
    private String name;

    @Min(value = 0, message = "Le prix ne peut pas etre negatif")
    private double price;

    @Min(value = 1, message = "La quantite doit etre superieure ou egale a 1")
    private int quantity;
}
