package com.biu.biu.contact.bean;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Date;

/**
 * Created by fubo on 2016/5/25 0025.
 * email:bofu1993@163.com
 */
public class ContactInfo implements Serializable {

  private String id;
  private String name;
  private String englishName;
  private String iconNetAddress;
  private String iconFileAddress;

  private Date startDate;
  private int flag;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getEnglishName() {
    return englishName;
  }

  public void setEnglishName(String englishName) {
    this.englishName = englishName;
  }

  public String getIconNetAddress() {
    return iconNetAddress;
  }

  public void setIconNetAddress(String iconNetAddress) {
    this.iconNetAddress = iconNetAddress;
  }

  public String getIconFileAddress() {
    return iconFileAddress;
  }

  public void setIconFileAddress(String iconFileAddress) {
    this.iconFileAddress = iconFileAddress;
  }

  public Date getStartDate() {
    return startDate;
  }

  public void setStartDate(Date startDate) {
    this.startDate = startDate;
  }

  public int getFlag() {
    return flag;
  }

  public void setFlag(int flag) {
    this.flag = flag;
  }


  public static class ContactComparator implements Comparator<ContactInfo> {

    @Override
    public int compare(ContactInfo lhs, ContactInfo rhs) {
      if (lhs.englishName != null && rhs.englishName != null) {
        return lhs.englishName.compareToIgnoreCase(rhs.englishName);
      } else if (lhs.englishName == null && rhs.englishName != null) {
        return - 1;
      } else if (lhs.englishName == null && rhs.englishName == null) {
        return 0;
      } else {
        return 1;
      }
    }

    @Override
    public boolean equals(Object object) {
      return false;
    }
  }
}
