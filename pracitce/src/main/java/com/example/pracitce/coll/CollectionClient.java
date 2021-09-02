package com.example.pracitce.coll;

import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;

import java.util.List;
import java.util.Map;
import java.util.Properties;

public class CollectionClient {
    public static void main(String[] args){
        //1. spring 컨테이너를 구동한다.
        AbstractApplicationContext factory =
                new GenericXmlApplicationContext("META-INF/spring/app-context2.xml");


        //2. spring 컨테이너로부터 필요한 객체를 요청(LookUp)한다.
        CollectionBean coll = (CollectionBean) factory.getBean("coll");
        List<String> list = coll.getList();

        for(String li : list){
            System.out.println(li);
        }

        Map<String, String> map = coll.getMap();
        System.out.println(map.entrySet());

        Properties properties = coll.getProper();
        System.out.println(properties);

        factory.close();
    }

}
