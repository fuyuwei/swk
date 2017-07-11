package com.swk.common.generic;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

/**
 * 泛型擦除
 * @author fuyuwei
 */
public class GenericPrinciple {
	
	/**
	 * 泛型擦除
	 */
	public static void erasureGeneric(){
		ArrayList<String> list = new ArrayList<String>();
		ArrayList<Integer> list_ = new ArrayList<Integer>();
		// true,在JVM中是相同的字节码
		System.out.println(list.getClass() == list_.getClass());
		try {
			// 得到字节码之后擦除Integer泛型，可以添加String类型的数据
			list_.getClass().getMethod("add", Object.class).invoke(list_, "swk");
			System.out.println(list_.get(0));
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		erasureGeneric();
	}
}
