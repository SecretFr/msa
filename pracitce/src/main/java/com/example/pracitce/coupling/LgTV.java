package com.example.pracitce.coupling;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class LgTV implements TV{
    @Autowired
    private AppleSpeaker speaker;
    private int price;

    public LgTV() {
        System.out.println(">>>LgTV(1) 객체 생성");
    }

    public LgTV(AppleSpeaker speaker, int price) {
        System.out.println(">>>LgTV(2) 객체 생성");
        this.speaker = speaker;
        this.price = price;
    }

    public void powerOn(){
        System.out.println("LgTV.... power on"+price);
    }
    public void powerOff(){
        System.out.println("LgTV.... power off");
    }
    public void volumeUp(){
        speaker.volumeUp();
    }
    public void volumeDown(){
        speaker.volumeDown();
    }
}
