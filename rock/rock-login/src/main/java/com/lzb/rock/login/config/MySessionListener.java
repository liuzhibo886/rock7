/**
 * @author lzb
 *
 * 
 *2019年4月16日 下午10:26:07
 */
package com.lzb.rock.login.config;

import org.apache.shiro.session.Session;
import org.apache.shiro.session.SessionListener;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * @author lzb
 * 
 *         2019年4月16日 下午10:26:07
 */
@Component
@Slf4j
public class MySessionListener implements SessionListener {

	@Override
	public void onStart(Session session) {
		log.debug("=======>登陆+1 ");
	}

	@Override
	public void onStop(Session session) {
		log.debug("=======>登陆-1 ");
	}

	@Override
	public void onExpiration(Session session) {
		log.debug("=======>登陆过期-1 ");
	}
}