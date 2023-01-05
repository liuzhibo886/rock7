/**
 * @author lzb
 *
 * 
 *2019年4月19日 上午12:41:03
 */
package com.lzb.rock.login.util;

import java.io.Serializable;
import java.util.Base64;

import org.apache.commons.lang3.SerializationUtils;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.SimpleSession;

import com.lzb.rock.base.util.UtilString;

/**
 * @author lzb
 * 
 *         2019年4月19日 上午12:41:03
 */
public class UtilShiroSession {

	private final static String SHIRO_SESSIOM_PREFIX = "shiro-session-";
	private static InheritableThreadLocal<Session> sessionLocal = new InheritableThreadLocal<>();

	/**
	 * 获取线程空间内 shiro session
	 * 
	 * @return
	 */
	public static Session get() {

		return sessionLocal.get();
	}

	/**
	 * 设置线程空间内 shiro session
	 * 
	 * @param session
	 */
	public static void set(Session session) {
		sessionLocal.set(session);
	}

	/**
	 * 删除线程空间内 shiro session
	 */
	public static void remove() {
		sessionLocal.remove();
	}

	/**
	 * 生成shiro session id 字符串
	 * 
	 * @param key
	 * @return
	 */
	public static String getKey(Serializable key) {
		return SHIRO_SESSIOM_PREFIX + key.toString();
	}

	/**
	 * session 序列化字符串
	 * 
	 * @param session
	 * @return
	 */
	public static String serialize(Session session) {
		if (session == null) {
			return null;
		}
		byte[] value = null;
		if (session instanceof SimpleSession) {
			value = SerializationUtils.serialize((SimpleSession) session);
		}

		return Base64.getEncoder().encodeToString(value);
	}

	/**
	 * session 反序列化字符串
	 * 
	 * @param session
	 * @return
	 */
	public static Session deserialize(String text) {
		if (UtilString.isBlank(text)) {
			return null;
		}
		byte[] value = Base64.getDecoder().decode(text);

		return (Session) SerializationUtils.deserialize(value);
	}

}
