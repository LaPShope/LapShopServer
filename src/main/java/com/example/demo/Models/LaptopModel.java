package com.example.demo.Models;

import com.example.demo.Common.Enums;
import com.example.demo.Repository.LaptopModelRepository;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "laptop_model")
public class LaptopModel {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String branch;

    @Column(nullable = false)
    private String cpu;

    @Column(nullable = false)
    private String ram;

    @Column(nullable = false)
    private String storage;

    @Column(nullable = false)
    private String display;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Enums.Color color;

    @Column(nullable = false)
    private BigDecimal price;

    private String description;

    @JsonIgnore
    @OneToMany(mappedBy = "laptopModel",cascade = {
            CascadeType.DETACH,
            CascadeType.MERGE,
            CascadeType.REMOVE,
            CascadeType.PERSIST,
            CascadeType.REFRESH
    })
    private List<Comment> commentList;

    @OneToMany(mappedBy = "laptopModel", cascade = {
            CascadeType.DETACH,
            CascadeType.MERGE,
            CascadeType.REMOVE,
            CascadeType.PERSIST,
            CascadeType.REFRESH
    })
    private List<Laptop> laptopList ;

    @ManyToMany(mappedBy = "laptopModelList",cascade = {CascadeType.PERSIST,
            CascadeType.DETACH,
            CascadeType.MERGE,
            CascadeType.REMOVE,
            CascadeType.REFRESH})
    private List<Image> imageList;

    @ManyToMany(mappedBy = "laptopModelList",cascade = {CascadeType.PERSIST,
            CascadeType.DETACH,
            CascadeType.MERGE,
            CascadeType.REFRESH})
    private List<Sale> saleList;

    @JsonIgnore
    @OneToMany(mappedBy = "laptopModel",cascade = {CascadeType.PERSIST,
            CascadeType.DETACH,
            CascadeType.MERGE,
            CascadeType.REMOVE,
            CascadeType.REFRESH},orphanRemoval = true)
    private List<OrderDetail> orderDetailList;

    @JsonIgnore
    @OneToMany(mappedBy = "laptopModel",cascade = {CascadeType.PERSIST,
            CascadeType.DETACH,
            CascadeType.REMOVE,
            CascadeType.MERGE,
            CascadeType.REFRESH},orphanRemoval = true)
    private List<LaptopOnCart> laptopOnCartList;

    // Helper method để thêm laptop
    public void addLaptop(Laptop laptop) {
        this.laptopList.add(laptop);
        laptop.setLaptopModel(this);
    }

    // Helper method để xóa laptop
    public void removeLaptop(Laptop laptop) {
        this.laptopList.remove(laptop);
        laptop.setLaptopModel(null);
    }


}
