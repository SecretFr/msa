package com.example.pracitce.coupling;

public class SamsungTV implements TV{
    private AppleSpeaker speaker;
    private int price;

    public SamsungTV() {
        System.out.println(">>>SamsungTV(1) 객체 생성");
    }

    public SamsungTV(AppleSpeaker speaker, int price) {
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

    private void initMethod() {
        System.out.println("Init SamsungTV object");
    }

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
