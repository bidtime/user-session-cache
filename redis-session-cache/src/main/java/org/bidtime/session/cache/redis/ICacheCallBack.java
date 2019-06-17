package org.bidtime.session.cache.redis;

public interface ICacheCallBack<R> {

  /**
   * 组装数据
   *
   * @param r
   */
  R callback() throws Exception;

}
