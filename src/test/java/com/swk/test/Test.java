package com.swk.test;

import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by fuyuwei on 2017/9/8.
 */
public class Test {
    public static void main(String[] args) {
        TreeMap<String, Object> map = new TreeMap<String, Object>();
        map.put("110000", "北京市");
        map.put("120000", "天津市");
        map.put("﻿130000", "河北省");
        map.put("140000", "山西省");
        map.put("110000", "Level 1");
        map.put("120000", "Level 2");
        map.put("130000", "Level 3");
        map.put("140000", "Level 4");

        Iterator<Map.Entry<String, Object>> it = map.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, Object> entry = it.next();
            System.out.println(entry.getKey() + " : " + entry.getValue());
        }
    }
}