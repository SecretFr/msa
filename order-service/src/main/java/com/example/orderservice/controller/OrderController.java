package com.example.orderservice.controller;

import com.example.orderservice.client.CatalogServiceClient;
import com.example.orderservice.dto.OrderDto;
import com.example.orderservice.jpa.OrderEntity;
import com.example.orderservice.kafkadto.KafkaOrderDto;
import com.example.orderservice.mq.KafkaProducer;
import com.example.orderservice.mq.OrderProducer;
import com.example.orderservice.service.OrderService;
import com.example.orderservice.vo.RequestOrder;
import com.example.orderservice.vo.ResponseCatalog;
import com.example.orderservice.vo.ResponseOrder;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/")
@Slf4j
public class OrderController {
    private Environment env;
    OrderService orderService;
    KafkaProducer kafkaProducer;

    CatalogServiceClient catalogServiceClient;
    OrderProducer orderProducer;

    @Autowired
    public OrderController(Environment env, OrderService orderService, KafkaProducer kafkaProducer,
                           CatalogServiceClient catalogServiceClient,
                           OrderProducer orderProducer) {
        this.env = env;
        this.orderService = orderService;
        this.kafkaProducer = kafkaProducer;
        this.catalogServiceClient = catalogServiceClient;
        this.orderProducer = orderProducer;
    }

    @GetMapping("/health_check")
    public String status() {
        return String.format("It's Working in Order Service on PORT %s",
                env.getProperty("local.server.port"));
    }

    @PostMapping("/{userId}/orders")
    public ResponseEntity<ResponseOrder> createOrder(@PathVariable("userId") String userId,
                                                     @RequestBody RequestOrder orderDetails) {
        log.info("Before add orders data");


        //check how much stock is left
        //order-service -> catalog-service
        //resttemplate or openfeign
        boolean isAvailable = true;

        ResponseCatalog responseCatalog = catalogServiceClient.getCatalog(orderDetails.getProductId());

        if(responseCatalog != null &&
            responseCatalog.getStock() - orderDetails.getQty() < 0){
            isAvailable = false;
        }

        if(isAvailable){
            ModelMapper mapper = new ModelMapper();
            mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

            OrderDto orderDto = mapper.map(orderDetails, OrderDto.class);
            orderDto.setUserId(userId);
            /*jpa*/
            OrderDto createdOrder = orderService.createOrder(orderDto);
//            ResponseOrder responseOrder1 = mapper.map(createdOrder, ResponseOrder.class);
            /*kafka*/
            orderDto.setOrderId(UUID.randomUUID().toString());
            orderDto.setTotalPrice(orderDetails.getQty() * orderDetails.getUnitPrice());
            ResponseOrder responseOrder = mapper.map(orderDto, ResponseOrder.class);
            log.info(orderDto.getInstanceId());
            kafkaProducer.send("exam-catalog-topic", orderDto);
            orderProducer.send("orders", orderDto);

            /*store a json file with orderDto*/
//            JSONObject jsonObject = new JSONObject();
//            JSONObject resultObj = new JSONObject();
//            JSONArray jsonArray = new JSONArray();
//
//            jsonObject.put("product_id", orderDto.getProductId());
//            jsonObject.put("qty", orderDto.getQty());
//            jsonObject.put("unit_price", orderDto.getUnitPrice());
//            jsonObject.put("user_id", orderDto.getUserId());
//            jsonObject.put("total_price", orderDto.getTotalPrice());
//            jsonObject.put("order_id", orderDto.getOrderId());
//
//            jsonArray.add(jsonObject);
//
//            resultObj.put("payload", jsonArray.toString());
//
//            String result = resultObj.toString().replaceAll("\"\\[" ,"").replaceAll("\\]\"" ,"").replaceAll("\\\\" ,"");;
//
//            System.out.println(result);
//            try {
//                FileWriter file = new FileWriter("/Users/daramg/Desktop/tstudy/kafka/schema/test.json");
//                file.write(jsonObject.toJSONString());
//                file.flush();
//                file.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }

            log.info("After added orders data");
            return ResponseEntity.status(HttpStatus.CREATED).body(responseOrder);
        }
        else{
            log.info("After added orders data");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }


    }

    @GetMapping("/{userId}/orders")
    public ResponseEntity<List<ResponseOrder>> getOrder(@PathVariable("userId") String userId) throws Exception {
        Iterable<OrderEntity> orderList = orderService.getOrderByUserId(userId);

        List<ResponseOrder> result = new ArrayList<>();
        orderList.forEach(v -> {
            result.add(new ModelMapper().map(v, ResponseOrder.class));
        });

        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @PutMapping("/{orderId}/orders")
    public ResponseEntity<ResponseOrder> updateOrder(@PathVariable String orderId,
                                                     @RequestBody RequestOrder orderDetails){
        boolean isAvailable = true;

        ResponseCatalog responseCatalog = catalogServiceClient.getCatalog(orderDetails.getProductId());

        if(responseCatalog != null &&
                responseCatalog.getStock() - orderDetails.getQty() < 0){
            isAvailable = false;
        }

        if(isAvailable){
            ModelMapper mapper = new ModelMapper();
            mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

            OrderDto orderDto = mapper.map(orderDetails, OrderDto.class);
            orderDto.setOrderId(orderId);

            orderService.updateOrder(orderDto);

            orderDto.setTotalPrice(orderDetails.getQty() * orderDetails.getUnitPrice());

            ResponseOrder responseOrder = mapper.map(orderDto, ResponseOrder.class);

            kafkaProducer.send("exam-catalog-topic", orderDto);
            orderProducer.send("orders", orderDto);

            log.info("After update order data");
            return ResponseEntity.status(HttpStatus.CREATED).body(responseOrder);
        }else {
            log.info("After update order data");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }

    }
}