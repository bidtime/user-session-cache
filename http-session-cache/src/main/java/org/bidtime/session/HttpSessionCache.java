package org.bidtime.session;

import javax.servlet.http.HttpSession;

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

  public HttpSessionCache(IUserCache cache) {
    this.cache = cache;
  }

  // session_destroy
	protected void session_destroy(HttpSession session) {
    if (session != null) {
      try {
        ISessionUser u = this.cache.del(session.getId());
        if (u != null) {
          this.cache.del(u.getId());
        }
      } catch (Exception e) {
        log.error("session_destroy: {}", e.getMessage());
      }
    }
	}

	// user2DoubleOnLine
	protected boolean user2DoubleOnLine(HttpSession session, ISessionUser u) {
		if (session != null) {
		  this.cache.set(session.getId(), u);
		  String v = this.cache.set(u.getId(), session.getId());
		  if (v != null && !v.equals(session.getId())) {
		    return true;
		  } else {
		    return false;
		  }
		} else {
			return false;
		}
	}
	
	// getSessionLoginState
	protected SessionLoginState getSessionLoginState(HttpSession session) {
		if (session != null) {
		  StateEnum e = StateEnum.NOT_LOGIN;	//未登陆
			ISessionUser u = getUser(session);
			if (u != null) {
			  String userId = u.getId();
			  String sessionId = this.cache.get(userId);
			  if (sessionId != null && !session.getId().equals(sessionId)) {
					e = StateEnum.ANOTHER_LOGIN;	//登陆后被踢
				} else {
					e = StateEnum.LOGGED_IN;	//正常登陆
				}
			}
			return new SessionLoginState(u, e);
		} else {
			return null;
		}
	}
	
	// getUser
	protected ISessionUser getUser(HttpSession session) {
		Object obj = this.cache.get(session.getId());
		if (obj != null) {
			return (ISessionUser)obj;
		} else {
			return null;
		}
	}
	
	// getUserId
	protected String getUserIdString(HttpSession session, String defValue) {
		ISessionUser u = getUser(session);
		if (u != null) {
			return u.getId();
		} else {
			return defValue;
		}
	}

	// getUserId
	protected String getUserIdString(HttpSession session) {
		return getUserIdString(session, null);
	}

	// getUserName
	public String getUserName(HttpSession session, String defValue) {
		ISessionUser u = getUser(session);
		if (u != null) {
			return u.getName();
		} else {
			return defValue;
		}
	}
	
  // get
  protected <T> T get(HttpSession session, String ext) {
    return get(session, ext, false);
  }
  
  // get
  @SuppressWarnings("unchecked")
  protected <T> T get(HttpSession session, String ext, boolean delete) {
    if (session != null) {
      Object o = cache.get(session.getId() + ext);
      if (delete) {
        cache.del(session.getId() + ext);
      }
      return (T)o;
    } else {
      return null;
    }
  }
  
  // del
  protected <T> T del(HttpSession session, String ext) {
    if (session != null) {
      return cache.del(session.getId() + ext);
    } else {
      return null;
    }
  }
  
  // set
  protected <T> T set(HttpSession session, String ext, T value) {
    if (session != null) {
      return cache.set(session.getId() + ext, value);
    } else {
      return null;
    }
  }

  public IUserCache getCache() {
    return cache;
  }

  public void setCache(IUserCache cache) {
    this.cache = cache;
  }

}
