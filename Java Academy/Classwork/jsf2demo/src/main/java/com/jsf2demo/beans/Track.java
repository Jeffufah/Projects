package com.jsf2demo.beans;

import java.util.HashMap;
import java.util.Map;
import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class Track 
{

    private Map<String, Integer> map = new HashMap<>();

    public void add(String ipAddress) 
    {
        map.put(ipAddress, map.containsKey(ipAddress)
                ? map.get(ipAddress) + 1 : 1);
    }

    public int getCount(String ipAddress) 
    {
        return map.containsKey(ipAddress) ? map.get(ipAddress) : 0;
    }

    public String getAllCount() 
    {
        return "Count summary is " + map;
    }
}