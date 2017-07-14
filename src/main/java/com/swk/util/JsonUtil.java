package com.swk.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.JSONLibDataFormatSerializer;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.sun.org.apache.xml.internal.security.keys.content.KeyValue;
import com.swk.bean.UserInfo;

public class JsonUtil {
	
	/**
	 * 配置并记录每种Java类型对应的序列化类
	 */
	private static final SerializeConfig serializeConfig;
	
	static{
		serializeConfig = new SerializeConfig();
		serializeConfig.put(java.util.Date.class, new JSONLibDataFormatSerializer());
		serializeConfig.put(java.sql.Date.class, new JSONLibDataFormatSerializer());
	}
	
	/**
	 *  QuoteFieldNames	输出key时是否使用双引号,默认为true	
		UseSingleQuotes	使用单引号而不是双引号,默认为false	
		WriteMapNullValue	是否输出值为null的字段,默认为false	
		WriteEnumUsingToString	Enum输出name()或者original,默认为false	
		UseISO8601DateFormat	Date使用ISO8601格式输出，默认为false	
		WriteNullListAsEmpty	List字段如果为null,输出为[],而非null	
		WriteNullStringAsEmpty	字符类型字段如果为null,输出为”“,而非null	
		WriteNullNumberAsZero	数值字段如果为null,输出为0,而非null	
		WriteNullBooleanAsFalse	Boolean字段如果为null,输出为false,而非null	
		SkipTransientField	如果是true，类中的Get方法对应的Field是transient，序列化时将会被忽略。默认为true	
		SortField	按字段名称排序后输出。默认为false	
		WriteTabAsSpecial	把\t做转义输出，默认为false	不推荐
		PrettyFormat	结果是否格式化,默认为false	
		WriteClassName	序列化时写入类型信息，默认为false。反序列化是需用到	
		DisableCircularReferenceDetect	消除对同一对象循环引用的问题，默认为false	
		WriteSlashAsSpecial	对斜杠’/’进行转义	
		BrowserCompatible	将中文都会序列化为"斜杠uXXXX"格式，字节数会多一些，但是能兼容IE 6，默认为false	
		WriteDateUseDateFormat	全局修改日期格式,默认为false。JSON.DEFFAULT_DATE_FORMAT = “yyyy-MM-dd”;JSON.toJSONString(obj, SerializerFeature.WriteDateUseDateFormat);	
		DisableCheckSpecialChar	一个对象的字符串属性中如果有特殊字符如双引号，将会在转成json时带有反斜杠转移符。如果不需要转义，可以使用这个属性。默认为false	
		NotWriteRootClassName	含义	
		BeanToArray	将对象转为array输出	
		WriteNonStringKeyAsString	含义	
		NotWriteDefaultValue	含义	
		BrowserSecure	含义	
		IgnoreNonFieldGetter	含义	
		WriteEnumUsingName	含义
	 */
	
	/**
	 * 设置序列化输出时的字符串格式,以下均设置为true，其余按照其默认值
	 */
	private static final SerializerFeature[] features = {
		SerializerFeature.WriteMapNullValue,// 输出空字段
		SerializerFeature.WriteNullListAsEmpty, // list字段如果为null，输出为[]，而不是null  
        SerializerFeature.WriteNullNumberAsZero, // 数值字段如果为null，输出为0，而不是null  
        SerializerFeature.WriteNullBooleanAsFalse, // Boolean字段如果为null，输出为false，而不是null  
        SerializerFeature.WriteNullStringAsEmpty // 字符类型字段如果为null，输出为""，而不是null  
		
	};
	
	/**
	 * 对象转json
	 * @param object
	 * @return
	 */
	public static String toJsonStr(Object object) {  
        return JSON.toJSONString(object, serializeConfig, features);  
    }
	
	public static String toJsonStr_(Object object){
		return JSON.toJSONString(object, serializeConfig);
	}
	
	/**
	 * json转对象
	 * @param json
	 * @return
	 */
	public static Object toBean(String json){
		return JSON.parse(json);
	}
	
	/**
	 * json转指定类型的对象
	 * @param json
	 * @param clazz
	 * @return
	 */
	public static <T> T toBean(String json,Class<T> clazz){
		return JSON.parseObject(json, clazz);
	}
	
	public static <T> Object[] toArray(String json){
		return toArray(json,null);
	}
	/**
	 * json转换为指定类型的数组  
	 * @param text
	 * @param clazz
	 * @return
	 */
    public static <T> Object[] toArray(String text, Class<T> clazz) {  
        return JSON.parseArray(text, clazz).toArray();  
    }
    
    /**
     * json转指定类型的List
     * @param text
     * @param clazz
     * @return
     */
    public static <T> List<T> toList(String text, Class<T> clazz) {  
        return JSON.parseArray(text, clazz);  
    }
    
    /**
     * json转map
     * @param json
     * @return
     */
	public static Map<?,?> jsonToMap(String json){
    	return JSONObject.parseObject(json);
    }
    
    /**  
     * 将map转化为string  
     * @param m  
     * @return  
     */  
    public static String collectToString(Map<?, ?> m) {  
        String s = JSONObject.toJSONString(m);  
        return s;  
    }  
	
    
	public static void main(String[] args) {
		UserInfo u = new UserInfo();
		u.setMobile("123123");
		u.setId(123123l);
		@SuppressWarnings("unchecked")
		Map<String,String> map = (Map<String, String>) jsonToMap(toJsonStr(u));
		MapUtil.iterator(map);
	}
	
}
