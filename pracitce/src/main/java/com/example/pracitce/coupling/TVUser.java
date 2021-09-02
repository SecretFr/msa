package com.example.pracitce.coupling;

import com.example.pracitce.factory.BeanFactory;
import org.springframework.context.support.AbstractApplicationContext;

import org.springframework.context.support.GenericXmlApplicationContext;

public class TVUser {
    public static void main(String[] args){
        //1.spring container를 구동한다
        AbstractApplicationContext factory =
                new GenericXmlApplicationContext("META-INF/spring/app-context.xml");

        TV tv = (TV) factory.getBean("tv");
        TV tv2 = (TV) factory.getBean("tv");
        TV tv3 = (TV) factory.getBean("tv");
        TV tv4 = (TV) factory.getBean("tv");

        tv.volumeDown();
        tv.volumeUp();
        tv.powerOff();
        tv.powerOn();

        factory.close();
//        TV tv = BeanFactory.getBean("lg");
//        tv.powerOn();
//        tv.powerOff();
//        tv.volumeUp();
//        tv.volumeDown();

        //인터페이스 다형성을 이용하여 결합도 낮추기
//        TV tv = new SamsungTV();
//        tv.powerOn();
//        tv.powerOff();
//        tv.volumeUp();
//        tv.volumeDown();
//
//        TV tv2 = new LgTV();
//        tv2.powerOn();
//        tv2.powerOff();
//        tv2.volumeUp();
//        tv2.volumeDown();
    }
}
