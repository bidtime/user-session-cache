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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Administrator
 */
public class HttpUserRequest2 {
  
  protected static final Logger log = LoggerFactory.getLogger(HttpUserRequest2.class);

  private HttpSessionCache userSession;

  private String cookieId;  // = "ACCESS_TICKET";

  private boolean cookied;

  public HttpUserRequest2() {
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
      throw new CookieNullException("cookie key " + key + " value is null");
    }
    return sessionId;
  }

  private String getSessionId(HttpServletRequest req) throws RuntimeException {
    return this.getSessionId(req, false);
  }
  
  private String getSessionId(HttpServletRequest req, boolean newSession) throws RuntimeException {
    String sessionId = null;
    //if (cookied) {
      sessionId = getCookieV(req, cookied?this.cookieId:"JSESSIONID");
      if (sessionId == null || sessionId.isEmpty()) {
        throw new CookieNullException("cookieId is null, cookied is disabled");
      }
//    } else {
//      HttpSession session = req.getSession(newSession);
//      if (session == null) {
//        throw new RuntimeException("http session is null");
//      }
//      sessionId = session.getId();
//      if (sessionId == null || sessionId.isEmpty()) {
//        throw new RuntimeException("sessionId is null");
//      }
//    }
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
    if (this.cookied) {
      return this.loginC(req, res, u, this.cookieId);
    } else {
      return this.loginC(req, res, u, "JSESSIONID");
    }
  }

  // loginCookie
//  private boolean loginC(HttpServletRequest req, HttpServletResponse res, ISessionUser u)
//      throws RuntimeException {
//    return loginC(req, res, u, this.cookieId);
//  }
  
  private boolean loginC(HttpServletRequest req, HttpServletResponse res, ISessionUser u,
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
  
//  private Cookie[] removeCookies(HttpServletRequest req) throws RuntimeException {
//    Cookie[] cookies = req.getCookies();
//    //2 判断Request对象中的Cookie是否存在 res.getCookies()
//    if (cookies != null) {
//      List<Cookie> ls = new ArrayList<>();
//      //3 遍历Request对象中的所有Cookie
//      for (Cookie c : cookies) {
//        if (!c.getName().equals("JSESSIONID")) {
//          ls.add(c);
//        }
//      }
//      Cookie[] cc = new Cookie[ls.size()];
//      ls.toArray(cc);
//      return cc;
//    }    
//  }

//  private Collection<Cookie> getCookies(HttpServletRequest req) throws RuntimeException {
//    //1 get cookies
//    Cookie[] cookies = req.getCookies();
//    //2 判断Request对象中的Cookie是否存在 res.getCookies()
//    if (cookies != null) {
//      Map<String, Cookie> map = new HashMap<>();
//      //3 遍历Request对象中的所有Cookie
//      for (Cookie c : cookies) {
//        log.info("get cookie: {}, {}", c.getName(), c.getValue());
//        map.put(c.getName(), c);
//      }
//      //map.values();
//      return map.values();
//    } else {
//      return null;
//    }
//  }
//  
//  private void cloneCookies(HttpServletRequest req, HttpServletResponse res) throws RuntimeException {
//    Collection<Cookie> cc = getCookies(req);
//    setCookies(res, cc);
//  }
//  
//  private void setCookies(HttpServletResponse res, Collection<Cookie> cookies) throws RuntimeException {
//    if (cookies != null) {
//      for (Cookie c : cookies) {
//        log.info("set cookie: {}, {}", c.getName(), c.getValue());
//        Cookie cc = new Cookie(c.getName(), null);  //(Cookie)c.clone();
//        cc.setPath("/");
//        res.addCookie(cc);
//      }
//    }    
//  }

//  private void cloneCookies(HttpServletRequest req, HttpServletResponse res) throws RuntimeException {
//    Cookie[] cookies = req.getCookies();
//    if (cookies != null) {
//      for (Cookie c : cookies) {
//        log.info("get cookie: {}, {}", c.getName(), c.getValue());
//        Cookie cc = new Cookie(c.getName(), null);
//        cc.setPath("/");
//        res.addCookie(cc);
//      }
//    }
//  }
//  
//  private boolean loginS(HttpServletRequest req, HttpServletResponse res, ISessionUser u,
//      String cookieK) throws RuntimeException {
//    String sessionId = null;
//    HttpSession session = req.getSession();
//    //
//    cloneCookies(req, res);
//    //
//    if (session != null) {
//      sessionId = session.getId();
//    }
//    return loginC(req, res, u, cookieK);
////    if (sessionId == null || sessionId.isEmpty()) {
////      log.debug("loginS-C: {}, {}, {}, {}", cookieK, sessionId, u.getId(), u.getName());
////      return loginC(req, res, u, cookieK);
////    } else {
////      log.debug("loginS: {}, {}, {}, {}", cookieK, sessionId, u.getId(), u.getName());
////      return userSession.user2DoubleOnLine(sessionId, u);
////    }
//  }

  // login
  private boolean login(HttpServletRequest req, ISessionUser u, boolean newSession)
      throws RuntimeException {
    HttpSession session = req.getSession(newSession);
    if (session == null) {
      throw new RuntimeException("session is null");
    } else {
      String sessionId = session.getId();
      if (sessionId == null || sessionId.isEmpty()) {
        throw new RuntimeException("sessionId is null");
      }
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

  public Object get(HttpServletRequest req, String ext, boolean delete) throws RuntimeException {
    return this.userSession.get(getSessionId(req), ext, delete);
  }

  // del
  public Object del(HttpServletRequest req, String ext) {
    return this.userSession.del(getSessionId(req), ext);
  }

  // set
  public void set(HttpServletRequest req, String ext, Object value, boolean newSession) throws RuntimeException {
    this.userSession.set(getSessionId(req, newSession), ext, value);
  }

  // set
  public void set(HttpServletRequest req, String ext, Object o) throws RuntimeException {
    this.set(req, ext, o, true);
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
 
  public void setCookied(boolean cookied) {
    this.cookied = cookied;
  }
  
  public boolean getCookied() {
    return this.cookied;
  }

}
