/**
 * 
 */
package org.bidtime.session;

import java.util.UUID;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.bidtime.session.bean.ISessionUser;
import org.bidtime.session.cache.IUserCache;
import org.bidtime.session.except.CookieNullException;
import org.bidtime.session.state.SessionLoginState;

/**
 * @author Administrator
 */
public class HttpUserRequest {

  private HttpSessionCache userSession;

  private String cookieId;  // = "ACCESS_TICKET";

  private boolean cookied;

  public HttpUserRequest() {
    this.userSession = new HttpSessionCache();
  }

  // get cookie
  private String getCookieV(HttpServletRequest req) throws RuntimeException {
    String sessionId = null;
    boolean bKey = false;
    //1 通过Request对象获取上次服务器端发送的Cookie信息
    Cookie[] cookies = req.getCookies();
    //2 判断Request对象中的Cookie是否存在
    if (cookies != null) {
      //3 遍历Request对象中的所有Cookie
      for (Cookie cookie : cookies) {
        //4 获取每一个Cookie的名称
        String name = cookie.getName();
        //5 判断Cookie的名称是否存在是id
        if ( name.equals(cookieId) ) {
          bKey = true;
          sessionId = cookie.getValue();
          break;
        }
      }
    }
    if ( !bKey ) {
      throw new CookieNullException("cookie key " + cookieId + " is null");
    }
    if (sessionId == null || sessionId.isEmpty()) {
      throw new CookieNullException("cookie value is null");
    }
    return sessionId;
  }

  private String getSessionId(HttpServletRequest req) throws RuntimeException {
    return getSessionId(req, false);
  }
  
  private String getSessionId(HttpServletRequest req, boolean newSession) throws RuntimeException {
    String sessionId = null;
    if (cookied) {
      sessionId = getCookieV(req);
      if (sessionId == null || sessionId.isEmpty()) {
        throw new CookieNullException("cookieId is null, cookied is disabled");
      }
    } else {
      HttpSession session = req.getSession(newSession);
      if (session == null) {
        throw new RuntimeException("http session is null");
      }
      sessionId = session.getId();
      if (sessionId == null || sessionId.isEmpty()) {
        throw new RuntimeException("sessionId is null");
      }
    }
    return sessionId;
  }

  // logout
  public void logout(HttpServletRequest req) throws RuntimeException {
    userSession.session_destroy(getSessionId(req));
  }

  // login
  public boolean login(HttpServletRequest req, ISessionUser u) throws RuntimeException {
    return this.login(req, u, true);
  }

  public boolean login(HttpServletRequest req, HttpServletResponse res, ISessionUser u)
      throws RuntimeException {
    return this.login(req, res, u, true);
  }

  // login
  private boolean login(HttpServletRequest req, HttpServletResponse res, ISessionUser u,
      boolean newSession) throws RuntimeException {
    String sessionId = null;
    if (cookied) {
      //1 利用UUID生成一个随机字符串
      sessionId = UUID.randomUUID().toString();//.replace("-", "");
      //2 创建Cookies实例对象
      Cookie cookie = new Cookie(cookieId, sessionId);
      //3 将Cookies实例对象,添加到Response对象中
      res.addCookie(cookie);
    } else {
      HttpSession session = req.getSession(newSession);
      if (session == null) {
        throw new RuntimeException("session is null");
      } else {
        sessionId = session.getId();
      }
      if (sessionId == null || sessionId.isEmpty()) {
        throw new RuntimeException("sessionId is null");
      }
    }
    return userSession.user2DoubleOnLine(sessionId, u);
  }

  // login
  private boolean login(HttpServletRequest req, ISessionUser u, boolean newSession)
      throws RuntimeException {
    HttpSession session = req.getSession(newSession);
    if (session == null) {
      return false;
    } else {
      return userSession.user2DoubleOnLine(session.getId(), u);
    }
  }

  // relogin
  public boolean relogin(HttpServletRequest req) throws RuntimeException {
    ISessionUser u = getUser(req);
    return this.login(req, u, false);
  }

  // relogin
  public boolean relogin(HttpServletRequest req, ISessionUser u) throws RuntimeException {
    return this.login(req, u, false);
  }

  // getSessionLoginState
  public SessionLoginState getSessionLoginState(HttpServletRequest req) {
    return userSession.getSessionLoginState(getSessionId(req));
  }

  // getUser
  public ISessionUser getUser(HttpServletRequest req, boolean newSession) throws RuntimeException {
    return userSession.getUser(getSessionId(req));
  }

  // getUser
  public ISessionUser getUser(HttpServletRequest req) throws RuntimeException {
    return this.getUser(req, false);
  }

  // get
  public Object get(HttpServletRequest req, String ext) throws RuntimeException {
    return this.get(req, ext, false);
  }

  public Object get(HttpServletRequest req, String ext, boolean delete) throws RuntimeException {
    return userSession.get(getSessionId(req), ext, delete);
  }

  // del
  public Object del(HttpServletRequest req, String ext) {
    return userSession.del(getSessionId(req), ext);
  }

  // set
  public void set(HttpServletRequest req, String ext, Object value, boolean newSession) throws RuntimeException {
    userSession.set(getSessionId(req, newSession), ext, value);
  }

  // set
  public void set(HttpServletRequest req, String ext, Object o) throws RuntimeException {
    this.set(req, ext, o, true);
  }

  public IUserCache getCache() {
    return userSession.getCache();
  }

  public void setCache(IUserCache cache) {
    userSession.setCache(cache);
  }

  public String getCookieId() {
    return cookieId;
  }

  public void setCookieId(String cookieId) {
    this.cookieId = cookieId;
    //setCookied(cookieId == null || cookieId.isEmpty());
  }
 
  public void setCookied(boolean cookied) {
    this.cookied = cookied;
  }
  
  public boolean getCookied() {
    return cookied;
  }

}
