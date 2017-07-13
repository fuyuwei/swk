package com.swk.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class MapUtil {
	
	/**
	 * 遍历Map的四种方式
	 * @param map
	 */
	public static void iterator(Map<String,String> map){
		for(String key:map.keySet()){
			System.out.println(String.format("iterator1 %s:%s", key,map.get(key)));
		}
		
		Iterator<Map.Entry<String, String>> it = map.entrySet().iterator();
		while(it.hasNext()){
			Map.Entry<String, String> entry = it.next();
			System.out.println(String.format("iterator2 %s:%s",entry.getKey(),entry.getValue()));
		}
		
		// 推荐
		for(Map.Entry<String, String> entry:map.entrySet()){
			System.out.println(String.format("iterator3 %s:%s",entry.getKey(),entry.getValue()));
		}
		
		// 无法遍历key
		for(String value:map.values()){
			System.out.println("iterator4 "+value);
		}
	}
	
	public static void main(String[] args) {
		Map<String,String> map = new HashMap<String,String>();
		map.put("swk", "孙悟空");
		map.put("zydx", "镇元大仙");
		iterator(map);																																			
	}
}
