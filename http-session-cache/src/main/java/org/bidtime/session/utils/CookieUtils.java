package org.bidtime.session.utils;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CookieUtils {

	/**
	 * 设置cookie
	 * 
	 * @param response
	 * @param name
	 *            cookie名字
	 * @param value
	 *            cookie值
	 * @param maxAge
	 *            cookie生命周期 以秒为单位
	 */
	public static void addCookie(HttpServletResponse response, String name,
			String value, int maxAge) {
		Cookie cookie = new Cookie(name, value);
		cookie.setPath("/");
		if (maxAge > 0) {
			cookie.setMaxAge(maxAge);
		}
		response.addCookie(cookie);
	}

	public static void addCookie(HttpServletResponse response, String name,
			String value) {
		addCookie(response, name, value, 0);
	}
	
	public static void addCookie(HttpServletResponse response, String name,
			String value, int v, EnumTime e) {
		Cookie cookie = new Cookie(name, value);
		cookie.setPath("/");
		if (v > 0) {
			int maxAge = 0;
			if (e == EnumTime.WEEK) {
				maxAge = v * 7 * 24 * 60 * 60;
			} else if (e == EnumTime.DAY) {
				maxAge = v * 24 * 60 * 60;
			} else if (e == EnumTime.HOUR) {
				maxAge = v * 60 * 60;
			} else if (e == EnumTime.MINUTE) {
				maxAge = v * 60;
			} else if (e == EnumTime.SECOND) {
				maxAge = v;
			}
			cookie.setMaxAge(maxAge);
		}
		response.addCookie(cookie);
	}

	/**
	 * 根据名字获取cookie
	 * 
	 * @param request
	 * @param name
	 *            cookie名字
	 * @return
	 */
	public static Cookie getCookie(HttpServletRequest request, String key) {
		Map<String, Cookie> cookieMap = getCookieMap(request);
		//if (cookieMap.containsKey(key)) {
		if (cookieMap != null) {
			Cookie cookie = (Cookie) cookieMap.get(key);
			return cookie;
		} else {
			return null;
		}
	}

	/**
	 * 将cookie封装到Map里面
	 * 
	 * @param request
	 * @return
	 */
	private static Map<String, Cookie> getCookieMap(HttpServletRequest request) {
		Map<String, Cookie> cookieMap = new HashMap<String, Cookie>();
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				cookieMap.put(cookie.getName(), cookie);
			}
		}
		return cookieMap;
	}

}
