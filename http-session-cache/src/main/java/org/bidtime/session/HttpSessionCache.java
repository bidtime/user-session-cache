package org.bidtime.session;

import org.bidtime.session.bean.ISessionUser;
import org.bidtime.session.cache.IUserCache;
import org.bidtime.session.state.SessionLoginState;
import org.bidtime.session.state.StateEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpSessionCache {
  
  protected static final Logger log = LoggerFactory.getLogger(HttpSessionCache.class);
  
  protected IUserCache cache;

  public HttpSessionCache() {
  }

  // session_destroy
	protected void session_destroy(String sessionId) throws RuntimeException {
    try {
      ISessionUser u = this.cache.del(sessionId);
      if (u != null) {
        this.cache.del(u.getId());
      }
    } catch (Exception e) {
      log.error("session_destroy: {}", e.getMessage());
    }
	}

	// user2DoubleOnLine
	protected boolean user2DoubleOnLine(String sessionId, ISessionUser u) throws RuntimeException {
	  this.cache.set(sessionId, u);
	  String v = this.cache.set(u.getId(), sessionId);
	  if (v != null && !v.equals(sessionId)) {
	    return true;
	  } else {
	    return false;
	  }
	}
	
	// getSessionLoginState
	protected SessionLoginState getSessionLoginState(String sessionId) throws RuntimeException {
	  StateEnum e = StateEnum.NOT_LOGIN;	//未登陆
		ISessionUser u = getUser(sessionId);
		if (u != null) {
		  String userId = u.getId();
		  String sessionIdV = this.cache.get(userId);
		  if (sessionIdV != null && !sessionIdV.equals(sessionId)) {
				e = StateEnum.ANOTHER_LOGIN;	//登陆后被踢
			} else {
				e = StateEnum.LOGGED_IN;	//正常登陆
			}
		}
		return new SessionLoginState(u, e);
	}
	
	// getUser
	protected ISessionUser getUser(String sessionId) throws RuntimeException {
		Object obj = this.cache.get(sessionId);
		if (obj != null) {
			return (ISessionUser)obj;
		} else {
			return null;
		}
	}
	
	// getUserId
	protected String getUserId(String sessionId, String defValue) throws RuntimeException {
		ISessionUser u = getUser(sessionId);
		if (u != null) {
			return u.getId();
		} else {
			return defValue;
		}
	}

	// getUserId
	protected String getUserId(String sessionId) throws RuntimeException {
		return getUserId(sessionId, null);
	}

  // getUserName
  public String getUserName(String sessionId) throws RuntimeException {
    return this.getUserName(sessionId, null);
  }
  
	// getUserName
	public String getUserName(String sessionId, String defValue) throws RuntimeException {
		ISessionUser u = getUser(sessionId);
		if (u != null) {
			return u.getName();
		} else {
			return defValue;
		}
	}
	
  // get
  protected <T> T get(String sessionId, String ext) throws RuntimeException {
    return cache.get(sessionId + ext, false);
  }
  
  // get
  protected <T> T get(String sessionId, String ext, boolean delete) throws RuntimeException {
    return cache.get(sessionId + ext, delete);
  }
  
  // del
  protected <T> T del(String sessionId, String ext) throws RuntimeException {
    return cache.del(sessionId + ext);
  }
  
  // set
  protected <T> T set(String sessionId, String ext, T value) throws RuntimeException {
    return cache.set(sessionId + ext, value);
  }

  public IUserCache getCache() {
    return cache;
  }

  public void setCache(IUserCache cache) {
    this.cache = cache;
  }

}
