package com.example.orderservice.kafkadto;

import com.example.orderservice.kafkadto.Field;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class Schema {
    private String type;
    private List<Field> fields;
    private boolean optional;
    private String name;
}
