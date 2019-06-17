package org.bidtime.session;

import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.bidtime.session.bean.ISessionUser;
import org.bidtime.session.cache.HashMapCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserOnLineListener implements HttpSessionListener {

  private static final Logger log = LoggerFactory.getLogger(UserOnLineListener.class);

	private static ConcurrentHashMap<String, HttpSession> user_map = new ConcurrentHashMap<String, HttpSession>();

	public static HttpSession addUser(String sId, HttpSession u) {
		return user_map.put(sId, u);
	}
	
	public void setEventNumSessions(HttpSessionEvent hse, int n) {
		// OnlineCounter.raise(hse.getSession());
		ServletContext ctx = hse.getSession( ).getServletContext( );
    Integer numSessions = (Integer) ctx.getAttribute("numSessions");
    if (numSessions == null) {
      numSessions = new Integer(n);
    } else {
      int count = numSessions.intValue();
      numSessions = new Integer(count + n);
    }
    ctx.setAttribute("numSessions", numSessions);
    log.info("numSessions: {}", numSessions);
	}

	public void sessionCreated(HttpSessionEvent hse) {
		// OnlineCounter.raise(hse.getSession());
		setEventNumSessions(hse, 1);
	}

	public void sessionDestroyed(HttpSessionEvent hse) {
		HttpSession session = hse.getSession();
		sessionRemove(session);
		setEventNumSessions(hse, -1);
	}

//	private static void sessionRemove(HttpSession session) {
//		if (session != null) {
//			String userId = UserSessionCommon.getUserIdString(session);
//			if (userId != null) {
//				try {
//					UserSessionCommon.session_destroy(session);
//				} finally {
//					user_map.remove(userId);
//				}
//			}
//		}
//	}
	
	 protected static void sessionRemove(HttpSession session) {
    if (session != null) {
      try {
        ISessionUser u = HashMapCache.single.del(session.getId());
        if (u != null) {
          HashMapCache.single.del(u.getId());
        }
      } catch (Exception e) {
        log.error("session_destroy: {}", e.getMessage());
      }
    }
  }

	/*
	 * 　再然后，把这个HttpSessionListener实现类注册到网站应用中，也就是在网站应用的web.xml中加入如下内容：
	 * 
	 * <web-app>
	 *   <listener>
	 *     <listener-class>org.bidtime.session.UserOnLineListener</listener-class>
	 *   </listener>
	 * </web-app>
	 */
}
