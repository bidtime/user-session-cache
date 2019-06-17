package org.mybatis.caches.bean;

import java.util.ArrayList;
import java.util.List;

public class Student {
  
  private Integer id;
  
  private String name;

  public Student() {
  }
  
  public Student(Integer id, String name) {
    this.id = id;
    this.name = name;
  }
  
  public static Student newStudent() {
    return new Student(1, "jss");
  }
  
  public static List<Student> newList() {
    return newList(10);
  }
  
  public static List<Student> newList(int n) {
    List<Student> ls = new ArrayList<>();
    for (int i=0; i<n; i++) {
      ls.add( new Student(i, "jss" + i) );
    }
    return ls;
  }
  
  public Integer getId() {
    return id;
  }
  
  public void setId(Integer id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
  
  @Override
  public String toString() {
    return "Student [id=" + id + ", name=" + name + "]";
  }

}
