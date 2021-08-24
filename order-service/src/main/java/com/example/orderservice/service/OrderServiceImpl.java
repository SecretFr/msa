package com.example.orderservice.service;


import com.example.orderservice.dto.OrderDto;
import com.example.orderservice.jpa.OrderEntity;
import com.example.orderservice.jpa.OrderRepository;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

@Service
public class OrderServiceImpl implements OrderService{
    OrderRepository repository;
    Environment env;

    @Autowired
    public OrderServiceImpl(OrderRepository repository,
                            Environment env) {
        this.repository = repository;
        this.env = env;
    }

    @Override
    public OrderDto createOrder(OrderDto orderDetail) {
        orderDetail.setOrderId(UUID.randomUUID().toString());
        orderDetail.setTotalPrice(orderDetail.getQty() * orderDetail.getUnitPrice());
        System.out.println(env.getProperty("eureka.instance.instance-id"));
        System.out.println(env.getProperty("server.port"));
        orderDetail.setInstanceId(env.getProperty("eureka.instance.instance-id"));

        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        OrderEntity orderEntity = modelMapper.map(orderDetail, OrderEntity.class);

        repository.save(orderEntity);

        OrderDto returnValue = modelMapper.map(orderEntity, OrderDto.class);

        return returnValue;
    }

    @Override
    public OrderDto getOrderByOrderId(String orderId) {
        OrderEntity orderEntity = repository.findByOrderId(orderId);
        OrderDto orderDto = new ModelMapper().map(orderEntity, OrderDto.class);
        return orderDto;
    }

    @Override
    public Iterable<OrderEntity> getOrderByUserId(String userId) {
        return repository.findByUserId(userId);
    }

    @Override
    public OrderDto updateOrder(OrderDto orderDetail, Long id) {
        Date now = new Date();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        OrderEntity orderEntity = repository.findByOrderId(orderDetail.getOrderId());

        OrderDto orderDto = modelMapper.map(orderEntity, OrderDto.class);
        orderDto.setId(id);
        orderDto.setQty(orderDetail.getQty());
        orderDto.setUnitPrice(orderDetail.getUnitPrice());
        orderDto.setTotalPrice(orderDetail.getQty() * orderDetail.getUnitPrice());
        try {
            orderDto.setModifiedAt(dateFormat.parse(dateFormat.format(now)));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        orderEntity = modelMapper.map(orderDto, OrderEntity.class);

        repository.save(orderEntity);

        return orderDto;
    }
}
