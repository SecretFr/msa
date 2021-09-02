package com.example.pracitce.coupling;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.InitBinder;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Component
@Lazy
public class SamsungTV implements TV{
    @Autowired
    private AppleSpeaker speaker;
    private int price;

    public SamsungTV() {
        System.out.println(">>>SamsungTV(1) 객체 생성");
    }

    @Autowired
    public SamsungTV(AppleSpeaker speaker, @Value("27000") int price) {
        System.out.println(">>>SamsungTV(2) 객체 생성");
        this.speaker = speaker;
        this.price = price;
    }

    public void powerOn(){
        System.out.println("samsungTV.... power on(price: "+price+")");
    }

    public void powerOff(){
        System.out.println("samsungTV.... power off");
    }
    public void volumeUp(){
        speaker.volumeUp();
    }
    public void volumeDown(){
        speaker.volumeDown();
    }

    @PostConstruct
    private void initMethod() {
        System.out.println("Init SamsungTV object");
    }

    @PreDestroy
    private void destroyMethod() {
        System.out.println("Destroy SumsungTV object");
    }

    public void setSpeaker(AppleSpeaker speaker) {
        System.out.println(">>>CALL setSpeaker()");
        this.speaker = speaker;
    }

    public void setPrice(int price) {
        System.out.println(">>>CALL setPrice()");
        this.price = price;
    }

}
