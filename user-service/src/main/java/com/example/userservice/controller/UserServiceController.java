package com.example.userservice.controller;

import com.example.userservice.dto.UserDto;
import com.example.userservice.entity.UserEntity;
import com.example.userservice.service.UserService;
import com.example.userservice.vo.Greeting;
import com.example.userservice.vo.RequestUser;
import com.example.userservice.vo.ResponseUser;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

@RestController
@RequestMapping("/")
@Slf4j
public class UserServiceController {

    private final Environment env;
    private final UserService userService;
    @Autowired
    private Greeting greeting;

    //Constructor, 생성자에 @Autowired 하는 방법
    @Autowired
    public UserServiceController(Environment env, UserService userService){
        this.env = env;
        this.userService = userService;
    }

    @GetMapping("/welcome")
    public String welcome(){
        //return env.getProperty("greeting.message");
        return greeting.getMessage();
    }

    //RequestMapping은 method 속성을 이용해서 메소에서 처리할 전송 방식을 지정할 수 있다.
    @RequestMapping(value = "/health_check", method = RequestMethod.GET)
    public String status(HttpServletRequest request){
        return String.format("It's Working in User Service"
                +", port(local.server.port)=" + env.getProperty("local.server.port")
                +", port(server.port)=" + env.getProperty("server.port")
                +", with token secret=" + env.getProperty("token.secret")
                +", with token time=" + env.getProperty("token.expiration_time")
                +", gateway_ip=" + env.getProperty("gateway.ip"));
    }

    @GetMapping("/message")
    public String message(@RequestHeader(value = "user-service-request", required = true) String header){
        log.info("Header is={}", header);
        return "message";
    }

    @GetMapping("/check")
    public String check(HttpServletRequest request){
        log.info("server port={}", request.getServerPort());
        return String.format("hi check first on PORT : %s", env.getProperty("local.server.port"));
    }

    @PostMapping("/users")
    public ResponseEntity<ResponseUser> createUser(@RequestBody @Valid RequestUser user){
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        UserDto userDto = mapper.map(user, UserDto.class);
        userService.createUser(userDto);

        //convert UserDto -> ResponseUser
        ResponseUser responseUser = mapper.map(userDto, ResponseUser.class);

        return ResponseEntity.status(HttpStatus.CREATED).body(responseUser);
    }

    //전체 사용자 목록
    @GetMapping("/users")
    public List<ResponseUser> getUser(HttpServletRequest request){
//        Enumeration<String> em = request.getHeaderNames();
//        request.getHeader("token");

        Iterable<UserEntity> userList = userService.getUserByAll();

        List<ResponseUser> result = new ArrayList<>();

        //lamda식, 디버깅하기 쉽지 않다라는 단점이 있습니다.
        userList.forEach(v -> {
            result.add(new ModelMapper().map(v, ResponseUser.class));
        });

        return result;
    }

    //사용자 상세보기 (with 주문 목록)
    @GetMapping(value = "/users/{userId}", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<ResponseUser> getUser(@PathVariable("userId") String userId){
        UserDto userDto = userService.getUserByUserId(userId);
        ResponseUser returnValue = new ModelMapper().map(userDto, ResponseUser.class);

        return ResponseEntity.status(HttpStatus.OK).body(returnValue);
    }
}
