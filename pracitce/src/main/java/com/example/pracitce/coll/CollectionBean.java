package com.example.pracitce.coll;

import java.util.List;
import java.util.Map;
import java.util.Properties;

public class CollectionBean {
    private List<String> list;
    public void setList(List<String> list){
        this.list = list;
    }
    public List<String> getList(){
        return list;
    }

    private Map<String, String> map;
    public void setMap(Map<String, String> map){
        this.map = map;
    }
    public Map<String, String> getMap(){
        return map;
    }

    private Properties proper;

    public void setProper(Properties proper) {
        this.proper = proper;
    }

    public Properties getProper() {
        return proper;
    }
}
