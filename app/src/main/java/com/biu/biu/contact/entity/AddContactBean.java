package com.biu.biu.contact.entity;

import com.biu.biu.user.entity.SimpleUserInfo;

import java.io.Serializable;

/**
 * Created by fubo on 2016/6/8 0008.
 * email:bofu1993@163.com
 */
public class AddContactBean implements Serializable{
  public static final int STATUS_ADD_ALREADY = 1;
  public static final int STATUS_ADD_NORMAL = 0;
  private AddContactRequest addContactRequest;
  private SimpleUserInfo senderInfo;

  public AddContactRequest getAddContactRequest() {
    return addContactRequest;
  }

  public void setAddContactRequest(AddContactRequest addContactRequest) {
    this.addContactRequest = addContactRequest;
  }

  public SimpleUserInfo getSenderInfo() {
    return senderInfo;
  }

  public void setSenderInfo(SimpleUserInfo senderInfo) {
    this.senderInfo = senderInfo;
  }
}
