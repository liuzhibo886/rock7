/**
 * @author lzb
 *
 * 
 *2019年4月16日 下午10:33:11
 */
package com.lzb.rock.login.config;

import java.io.Serializable;

import javax.servlet.ServletRequest;

import org.apache.shiro.session.Session;
import org.apache.shiro.session.UnknownSessionException;
import org.apache.shiro.session.mgt.SessionContext;
import org.apache.shiro.session.mgt.SessionKey;
import org.apache.shiro.web.servlet.ShiroHttpServletRequest;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.apache.shiro.web.session.mgt.WebSessionKey;
import org.apache.shiro.web.util.WebUtils;

import com.lzb.rock.base.util.UtilHttpKit;

import lombok.extern.slf4j.Slf4j;

/**
 * @author lzb
 * 
 *         2019年4月16日 下午10:33:11
 */
@Slf4j
public class MySessionManager extends DefaultWebSessionManager {

	@Override
	protected void onStart(Session session, SessionContext context) {
		super.onStart(session, context);
		ServletRequest request = WebUtils.getRequest(context);
		request.setAttribute(ShiroHttpServletRequest.REFERENCED_SESSION_ID_SOURCE, ShiroHttpServletRequest.COOKIE_SESSION_ID_SOURCE);
	}

	/**
	 * 我们可以通过继承DefaultWebSessionManager来自定义我们的SessionManager，
	 * 如下，我们将session存入到会话中的request。这样就不用每次都去缓存中取，提高性能
	 */
	@Override
	protected Session retrieveSession(SessionKey sessionKey) throws UnknownSessionException {

		String uri = UtilHttpKit.getRequest().getRequestURI();

		if (uri.startsWith("/static/")) {
			// log.debug("retrieveSession;Filter==>uri:{}", uri);
			return null;
		}
		// log.debug("retrieveSession==>uri:{}", uri);
		Serializable sessionId = getSessionId(sessionKey);
		ServletRequest request = null;
		if (sessionId instanceof WebSessionKey) {
			request = ((WebSessionKey) sessionKey).getServletRequest();
		}
		// log.debug("MySessionManager.retrieveSession;sessionId:{};request:{}",
		// sessionId, request);
		if (request != null && sessionId != null) {
			return (Session) request.getAttribute(sessionId.toString());
		}

		Session session = super.retrieveSession(sessionKey);
		if (request != null && sessionId != null) {
			request.setAttribute(sessionId.toString(), session);
		}

		return session;
	}
}
