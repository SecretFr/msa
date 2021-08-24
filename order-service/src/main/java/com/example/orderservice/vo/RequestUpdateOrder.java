package com.example.orderservice.vo;

import lombok.Data;

@Data
public class RequestUpdateOrder {
    private Integer qty;
    private Integer unitPrice;
}
