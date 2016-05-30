package com.biu.biu.contact.views;

import com.biu.biu.contact.bean.ContactInfo;

import java.util.List;

/**
 * Created by fubo on 2016/5/18 0018.
 * email:bofu1993@163.com
 */
public interface IContactList {

  void updateContactNumber(int number);

  void updateContactList(List<ContactInfo> contactInfoList);
}
