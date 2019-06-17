/**
 * 
 */
package org.bidtime.session;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.bidtime.session.bean.ISessionUser;
import org.bidtime.session.cache.IUserCache;
import org.bidtime.session.state.SessionLoginState;

/**
 * @author Administrator
 * 
 */
public class HttpUserRequest {
  
  private HttpSessionCache userSession;
  
  public HttpUserRequest() {
    this.userSession = new HttpSessionCache();
  }
  
  // logout
	public void logout(HttpServletRequest request) {
		HttpSession session = request.getSession();
		userSession.session_destroy(session);
	}
	
	// login
	public boolean login(HttpServletRequest request, ISessionUser u) {
		return this.login(request, u, true);
	}
	
	// login
	private boolean login(HttpServletRequest request, ISessionUser u, boolean newSession) {
		HttpSession session = request.getSession(newSession);
		return userSession.user2DoubleOnLine(session, u);
	}

	// relogin
	public boolean relogin(HttpServletRequest request) {
	  ISessionUser u = getUser(request);
		return this.login(request, u, false);
	}

	// relogin
	public boolean relogin(HttpServletRequest request, ISessionUser u) {
		return this.login(request, u, false);
	}

	// getSessionLoginState
	public SessionLoginState getSessionLoginState(HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		return userSession.getSessionLoginState(session);
	}

	// getUser
	public ISessionUser getUser(HttpServletRequest request, boolean newSession) {
		return userSession.getUser(request.getSession(newSession));
	}

	// getUser
	public ISessionUser getUser(HttpServletRequest request) {
		return this.getUser(request, false);
	}
  
  // get
  public Object get(HttpServletRequest req, String ext) {
    return this.get(req, ext, false);
  }
  
  public Object get(HttpServletRequest req, String ext, boolean delete) {
    HttpSession session = req.getSession(false);
    return userSession.get(session, ext, delete);
  }
  
  // del
  public Object del(HttpServletRequest req, String ext) {
    HttpSession session = req.getSession(false);
    return userSession.del(session, ext);
  }
  
	// set
	public void set(HttpServletRequest req, String ext, Object value, boolean newSession) {
		HttpSession session = req.getSession(newSession);
		userSession.set(session, ext, value);
	}
	
	// set
	public void set(HttpServletRequest req, String ext, Object o) {
		this.set(req, ext, o, true);
	}

  public IUserCache getCache() {
    return userSession.getCache();
  }

  public void setCache(IUserCache cache) {
    userSession.setCache(cache);
  }

}