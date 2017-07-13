package com.swk.util;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * class目录下所有properties文件加载到内存当中
 * @author fuyuwei
 */
public class PropertiesUtil {

	private static String FILETYPE = ".properties";
	private static Map<String,String> properties = new ConcurrentHashMap<String, String>();
	
	static{
		initProperties();
	}
	
	public static String getString(String name,String def){
		String value = properties.get(name);
		return (value == null || "".equals(value)) ? def : value;
	}
	
	public static long getLong(String name,long def){
		String value = properties.get(name);
		return (value == null || "".equals(value)) ? def : Long.parseLong(value);
	}
	
	public static int getInt(String name,int def){
		String value = properties.get(name);
		return (value == null || "".equals(value)) ? def : Integer.parseInt(value);
	}
	
	public static boolean getBoolean(String name,boolean def){
		String value = properties.get(name);
		return (value == null || "".equals(value)) ? def : Boolean.parseBoolean(value);
	}
	
	public static String[] getArray(String name,String seperator,String[] def){
		String value = getString(name,null);
		return (value == null || "".equals(value)) ? def : value.split(seperator);
	}
	
	private static void initProperties() {
		String path = Property_.class.getResource("/").getPath();
		File file = new File(path);
		File[] propFiles = file.listFiles(new FileFilter(){

			@Override
			public boolean accept(File pathname) {
				if(pathname.getName().endsWith(FILETYPE)){
					return true;
				}
				return false;
			}
		});
		for(File propFile:propFiles){
			properties.putAll(new Property_(propFile, "utf-8").propertiesValues());
		}
	}
	
	private static class Property_{
		private Properties prop;
		private File file;
		
		public Property_(File file,String charset){
			FileInputStream fileInputStream = null;
			InputStreamReader inputStreamReader = null;
			this.file = file;
			prop = new Properties();
			try {
				fileInputStream = new FileInputStream(file);
				inputStreamReader = new InputStreamReader(fileInputStream,charset);
				prop.load(inputStreamReader);
			} catch (Exception e) {
				throw new RuntimeException("fail to load properties file:"+file != null? file.getName():"",e);
			}finally{
				if(inputStreamReader != null){
					try {
						inputStreamReader.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				if (fileInputStream != null) {
					try {
						fileInputStream.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
		
		public Map<String,String> propertiesValues(){
			Map<String,String> map = new HashMap<String,String>();
			String fileName = file.getName().split("\\.")[0];
			for(Object key:prop.keySet()){
				map.put(fileName+"."+String.valueOf(key), String.valueOf(prop.getProperty(String.valueOf(key))));
			}
			return map;
		}
	}
	
	public static void main(String[] args) {
		String ip = properties.get("redis.redis1.ip");
		System.out.println(ip);
	}
}
