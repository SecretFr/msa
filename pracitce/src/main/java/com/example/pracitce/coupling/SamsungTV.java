package com.example.pracitce.coupling;

public class SamsungTV implements TV{
    public void powerOn(){
        System.out.println("samsungTV.... power on");
    }
    public void powerOff(){
        System.out.println("samsungTV.... power off");
    }
    public void volumeUp(){
        System.out.println("samsungTV.... volumeUp");
    }
    public void volumeDown(){
        System.out.println("samsungTV.... volumeDown");
    }

    private void initMethod() {
        System.out.println("Init SamsungTV object");
    }

    private void destroyMethod() {
        System.out.println("Destroy SumsungTV object");
    }
}
