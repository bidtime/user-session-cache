/*
 * $Id:$
 * Copyright 2017 ecarpo.com All rights reserved.
 */
package org.bidtime.session.except;

/**
 * SessionNullException
 *   此类显示的消息，由后端逻辑产生，不做任何过滤，直接传递到前端
 * 
 * @author riverbo
 * @since 2018.06.01
 */
public class SessionNullException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public SessionNullException() {
    super();
  }

  public SessionNullException(String message) {
    super(message);
  }

  public SessionNullException(String message, Throwable cause) {
    super(message, cause);
  }

  public SessionNullException(Throwable cause) {
    super(cause);
  }

}
