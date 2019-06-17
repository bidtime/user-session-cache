package org.mybatis.caches.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 定时任务，运行统一类
 * 
 * @author riverbo
 * @since 2018.07.06
 */
/**
 * @author root
 */
public class FileU {

  private static final Logger log = LoggerFactory.getLogger(FileU.class);
 
//  private static String getExtName(String s) {
//    return getExtName(s, '.');
//  }
//  
//  private static String getExtName(String s, char split) {
//    int i = s.indexOf(split);
//    int leg = s.length();
//    return (i > 0 ? (i + 1) == leg ? " " : s.substring(i, s.length()) : " ");
//  }

  /**
   * the traditional io way
   * 
   * @param filename
   * @return
   * @throws IOException
   */
  public static byte[] file2bytes(String filename) throws Exception {
    File file = new File(filename);
    if (!file.exists()) {
      throw new FileNotFoundException(filename);
    }
    return file2bytes(file);
  }

  /**
   * the traditional io way
   * 
   * @param filename
   * @return
   * @throws IOException
   */
  public static byte[] file2bytes(File file) throws Exception {
    ByteArrayOutputStream bos = new ByteArrayOutputStream((int) file.length());
    BufferedInputStream in = null;
    try {
      in = new BufferedInputStream(new FileInputStream(file));
      int buf_size = 1024;
      byte[] buffer = new byte[buf_size];
      int len = 0;
      while (-1 != (len = in.read(buffer, 0, buf_size))) {
        bos.write(buffer, 0, len);
      }
      return bos.toByteArray();
    } catch (IOException e) {
      log.error("toByteArray: file[{}], {}", file.getName(), e.getMessage());
      throw e;
    } finally {
      try {
        if (in != null) {
          in.close();
        }
      } catch (IOException e) {
        log.error("toByteArray: file[{}], {}", file.getName(), e.getMessage());
      }
      bos.close();
    }
  }
  
  /**
   * 将Byte数组转换成文件
   * 
   * @param bytes
   * @param filename
   */
  public static boolean bytes2file(byte[] bytes, String filename) throws Exception {
    File file = new File(filename);
//    if (!file.exists()) {
//      throw new FileNotFoundException(filename);
//    }
    return bytes2file(bytes, file);
  }

  /**
   * 将Byte数组转换成文件
   * 
   * @param bytes
   * @param filePath
   * @param fileName
   */
  public static boolean bytes2file(byte[] bytes, File file) throws Exception {
    boolean b = false;
    BufferedOutputStream bos = null;
    FileOutputStream fos = null;
    try {
      fos = new FileOutputStream(file);
      bos = new BufferedOutputStream(fos);
      bos.write(bytes);
      b = true;
    } catch (Exception e) {
      log.error("bytes2file: file[{}], {}", file.getName(), e.getMessage());
      throw e;
    } finally {
      if (bos != null) {
        try {
          bos.close();
        } catch (IOException e) {
          log.error("bytes2file: file[{}], {}", file.getName(), e.getMessage());
        }
      }
      if (fos != null) {
        try {
          fos.close();
        } catch (IOException e) {
          log.error("bytes2file: file[{}], {}", file.getName(), e.getMessage());
        }
      }
    }
    return b;
  }

  /**
   * 两个时间相差距离多少天多少小时多少分多少秒
   * 
   * @param str1 时间参数 1 格式：1990-01-01 12:00:00
   * @param str2 时间参数 2 格式：2009-01-01 12:00:00
   * @return long[] 返回值为：{天, 时, 分, 秒}
   */
  private static long[] getBewteenTimes(Date one, Date two) {
    long day = 0;
    long hour = 0;
    long min = 0;
    long sec = 0;
    long ms = 0;
    //
    long time1 = one.getTime();
    long time2 = two.getTime();
    long diff;
    if (time1 < time2) {
      diff = time2 - time1;
    } else {
      diff = time1 - time2;
    }
    day = diff / (24 * 60 * 60 * 1000);
    hour = (diff / (60 * 60 * 1000) - day * 24);
    min = ((diff / (60 * 1000)) - day * 24 * 60 - hour * 60);
    sec = (diff / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - min * 60);
    ms = (diff - day * 24 * 60 * 60 * 1000 - hour * 60 * 60 * 1000 - min * 60 * 1000 - sec * 1000);
    long[] times = { day, hour, min, sec, ms };
    return times;
  }

  public static String getTimes(Date one, Date two) {
    return getTimes(one, two, "");
  }

  /**
   * 两个时间相差距离多少天多少小时多少分多少秒
   * 
   * @param str1 时间参数 1 格式：1990-01-01 12:00:00
   * @param str2 时间参数 2 格式：2009-01-01 12:00:00
   * @return String 返回值为：{天, 时, 分, 秒}
   */
  private static String getTimes(Date one, Date two, String split) {
    long[] times = getBewteenTimes(one, two);
    String[] units = { "d", "h", "m", "s", "ms" };
    StringBuilder sb = new StringBuilder();
    try {
      for (int i = 0; i < times.length; i++) {
        long l = times[i];
        if (l != 0 || i == (times.length - 1)) {
          if (sb.length() > 0) {
            sb.append(", ");
          }
          sb.append(l);
          sb.append(split);
          sb.append(units[i]);
        }
      }
      return sb.toString();
    } finally {
      sb.setLength(0);
      sb = null;
    }
  }

  /**
   * toDateTime
   * 
   * @param date
   * @return
   * @throws Exception
   * @author riverbo
   * @since 2018.07.05
   */
  //  private static String toDateTime(Date date) throws Exception {
  //    return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(date);
  //  }

  /**
   * @param date
   * @return
   * @throws Exception
   * @author riverbo
   * @since 2018.07.05
   */
  public static String toDateTime(Date date) throws Exception {
    return new SimpleDateFormat("HH:mm:ss.SSS").format(date);
  }

}
