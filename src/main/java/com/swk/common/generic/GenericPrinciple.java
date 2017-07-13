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
	
	/**
	 * 自定义泛型,<T> 必须在方法返回值前面，表示定义了一种新的类型，并且必须为对象类型，不能是基本数据类型，例如int,long之类的
	 * @param t
	 * @return
	 */
	public static <T> T customerGeneric(T t){
		return null;
	}
	
	public static void main(String[] args) {
		erasureGeneric();
	}
}
