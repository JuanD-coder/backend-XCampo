package com.rojas.dev.XCampo.entity;

import com.rojas.dev.XCampo.enumClass.OrderState;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@Entity
@Table(name = "orderProducts")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_order;

    @Temporal(TemporalType.DATE)
    private LocalDate date;

    @Temporal(TemporalType.TIME)
    private LocalTime hour;

    @Enumerated(EnumType.STRING)
    private OrderState state;

    private String message;

    private Long price_delivery;

    private Boolean delivery;

    @OneToMany(mappedBy = "orderProducts")
    private Set<DeliveryProduct> deliveryProductList = new HashSet<>();

    @OneToOne
    @JoinColumn(name = "fk_shoppingCart_id", updatable = false, nullable = false)
    private Shopping_cart shoppingCart;

}
