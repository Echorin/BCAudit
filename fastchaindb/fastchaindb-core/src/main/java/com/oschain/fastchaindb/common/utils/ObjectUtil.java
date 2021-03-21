package com.oschain.fastchaindb.common.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

public class ObjectUtil {
	
	private static final String CHARSET_NAME = "UTF-8";

	public static boolean isNullOrEmptyString(Object o) {
		if (o == null)
			return true;

		if (!(o instanceof String))
			return false;

		String str = (String) o;
		return (str.length() == 0);
	}

	public static boolean isEmpty(Object o) {
		if (o == null) {
			return true;
		}

		if (o instanceof String) {
			if (((String) o).length() != 0)
				return false;

			return true;
		}
		if (o instanceof Collection) {
			if (!(((Collection) o).isEmpty()))
				return false;

			return true;
		}
		if (o.getClass().isArray()) {
			if (Array.getLength(o) != 0)
				return false;

			return true;
		}
		if (o instanceof Map) {
			if (!(((Map) o).isEmpty()))
				return false;

			return true;
		}

		return false;
	}

	public static boolean isNotEmpty(Object c) throws IllegalArgumentException {
		return (!(isEmpty(c)));
	}

	/**
	 * 注解到对象复制，只复制能匹配上的方法。
	 * 
	 * @param annotation
	 * @param object
	 */
	public static void annotationToObject(Object annotation, Object object) {
		if (annotation != null) {
			Class<?> annotationClass = annotation.getClass();
			if (object != null) {
				Class<?> objectClass = object.getClass();
				for (Method m : objectClass.getMethods()) {
					if (StringUtils.startsWith(m.getName(), "set")) {
						try {
							String s = StringUtils.uncapitalize(StringUtils.substring(m.getName(), 3));
							Object obj = annotationClass.getMethod(s).invoke(annotation);
							if (obj != null && !"".equals(obj.toString())) {
								// if (object == null){
								object = objectClass.newInstance();
								// }
								m.invoke(object, obj);
							}
						} catch (Exception e) {
							// 忽略所有设置失败方法
							e.printStackTrace();
						}
					}
				}
			}
		}
	}

	/**
	 * 序列化对象
	 * 
	 * @param object
	 * @return
	 */
	public static byte[] serialize(Object object) {
		ObjectOutputStream oos = null;
		ByteArrayOutputStream baos = null;
		try {
			if (object != null) {
				baos = new ByteArrayOutputStream();
				oos = new ObjectOutputStream(baos);
				oos.writeObject(object);
				return baos.toByteArray();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 反序列化对象
	 * 
	 * @param bytes
	 * @return
	 */
	public static Object unserialize(byte[] bytes) {
		ByteArrayInputStream bais = null;
		try {
			if (bytes != null && bytes.length > 0) {
				bais = new ByteArrayInputStream(bytes);
				ObjectInputStream ois = new ObjectInputStream(bais);
				return ois.readObject();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 转换为字节数组
	 * 
	 * @param str
	 * @return
	 */
	public static byte[] getBytes(String str) {
		if (str != null) {
			try {
				return str.getBytes(CHARSET_NAME);
			} catch (UnsupportedEncodingException e) {
				return null;
			}
		} else {
			return null;
		}
	}

	/**
	 * 转换为字节数组
	 * 
	 * @param bytes
	 * @return
	 */
	public static String toString(byte[] bytes) {
		try {
			return new String(bytes, CHARSET_NAME);
		} catch (UnsupportedEncodingException e) {
			return "";
		}
	}

}