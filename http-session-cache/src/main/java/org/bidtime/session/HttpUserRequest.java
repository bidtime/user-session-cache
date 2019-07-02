/**
 * 
 */
package org.bidtime.session;

import java.util.UUID;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.bidtime.session.bean.ISessionUser;
import org.bidtime.session.cache.IUserCache;
import org.bidtime.session.except.CookieNullException;
import org.bidtime.session.state.SessionLoginState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Administrator
 */
public class HttpUserRequest {
  
  protected static final Logger log = LoggerFactory.getLogger(HttpUserRequest.class);

  private HttpSessionCache userSession;

  private String cookieId = "JSESSIONID";

  public HttpUserRequest() {
    this.userSession = new HttpSessionCache();
  }

  // get cookie
  private String getCookieV(HttpServletRequest req, String key) throws RuntimeException {
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
        if ( name.equals(key) ) {
          bKey = true;
          sessionId = cookie.getValue();
          break;
        }
      }
    }
    if ( !bKey ) {
      throw new CookieNullException("cookie key " + key + " is null");
    }
    if (sessionId == null || sessionId.isEmpty()) {
      throw new CookieNullException("cookie key " + key + "'s value is null");
    }
    return sessionId;
  }
  
  // getSessionId
  private String getSessionId(HttpServletRequest req) throws RuntimeException {
    return getCookieV(req, this.cookieId);
  }

  // logout
  public void logout(HttpServletRequest req) throws RuntimeException {
    userSession.session_destroy(getSessionId(req));
  }

  // login
  public boolean login(HttpServletRequest req, HttpServletResponse res, ISessionUser u)
      throws RuntimeException {
    return this.loginCookie(req, res, u, this.cookieId);
  }

  // loginC
  private boolean loginCookie(HttpServletRequest req, HttpServletResponse res, ISessionUser u,
      String cookieK) throws RuntimeException {
    //1 利用UUID生成一个随机字符串
    String sessionId = UUID.randomUUID().toString().replace("-", "");
    //2 创建Cookies实例对象
    Cookie cookie = new Cookie(cookieK, sessionId);
    cookie.setPath("/");
    //3 将Cookies实例对象,添加到Response对象中
    res.addCookie(cookie);
    // debug
    log.debug("loginC: {}, {}, {}, {}", cookieK, sessionId, u.getId(), u.getName());
    //4 user2DoubleOnLine
    return userSession.user2DoubleOnLine(sessionId, u);
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

  // getUserId
  protected String getUserId(HttpServletRequest req, String defValue) throws RuntimeException {
    return this.userSession.getUserId(getSessionId(req), defValue);
  }

  // getUserId
  protected String getUserId(HttpServletRequest req) throws RuntimeException {
    return this.userSession.getUserId(getSessionId(req));
  }

  // getUserName
  public String getUserName(HttpServletRequest req) throws RuntimeException {
    return this.userSession.getUserName(getSessionId(req));
  }
  
  // getUserName
  public String getUserName(HttpServletRequest req, String defValue) throws RuntimeException {
    return this.userSession.getUserName(getSessionId(req), defValue);
  }

  // get
  public Object get(HttpServletRequest req, String ext) throws RuntimeException {
    return this.get(req, ext, false);
  }

  // get
  public Object get(HttpServletRequest req, String ext, boolean delete) throws RuntimeException {
    return this.userSession.get(getSessionId(req), ext, delete);
  }

  // del
  public Object del(HttpServletRequest req, String ext) {
    return this.userSession.del(getSessionId(req), ext);
  }

  // set
  public void set(HttpServletRequest req, String ext, Object value) throws RuntimeException {
    this.userSession.set(getSessionId(req), ext, value);
  }

  public IUserCache getCache() {
    return this.userSession.getCache();
  }

  public void setCache(IUserCache cache) {
    this.userSession.setCache(cache);
  }

  public String getCookieId() {
    return this.cookieId;
  }

  public void setCookieId(String cookieId) {
    this.cookieId = cookieId;
  }

}
