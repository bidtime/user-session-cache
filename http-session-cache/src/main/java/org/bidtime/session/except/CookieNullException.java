/*
 * $Id:$
 * Copyright 2017 ecarpo.com All rights reserved.
 */
package org.bidtime.session.except;

/**
 * CookieNullException
 *   此类显示的消息，由后端逻辑产生，不做任何过滤，直接传递到前端
 * 
 * @author riverbo
 * @since 2018.06.01
 */
public class CookieNullException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public CookieNullException() {
    super();
  }

  public CookieNullException(String message) {
    super(message);
  }

  public CookieNullException(String message, Throwable cause) {
    super(message, cause);
  }

  public CookieNullException(Throwable cause) {
    super(cause);
  }

}
