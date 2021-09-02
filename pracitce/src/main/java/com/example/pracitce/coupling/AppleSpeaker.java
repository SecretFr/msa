package com.example.pracitce.coupling;

import org.springframework.stereotype.Component;

@Component("apple")
public class AppleSpeaker {
    public AppleSpeaker(){
        System.out.println(">>> AppleSpeaker 객체 생성");
    }

    public void volumeUp(){
        System.out.println("AppleSpeaker----소리 울린다.");
    }

    public void volumeDown(){
        System.out.println("AppleSpeaker----소리 내린다.");
    }
}
