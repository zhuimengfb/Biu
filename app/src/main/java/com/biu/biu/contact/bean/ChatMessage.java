package com.biu.biu.contact.bean;

import com.biu.biu.userconfig.UserConfigParams;
import com.biu.biu.utils.UUIDGenerator;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by fubo on 2016/5/30 0030.
 * email:bofu1993@163.com
 */
public class ChatMessage implements Serializable {


  public static final int TYPE_TEXT = 1;
  private String id;
  private String senderId;
  private String receiverId;
  private String content;
  private int type;
  private Date generateTime;
  private int readFlag;
  private int flag;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getSenderId() {
    return senderId;
  }

  public void setSenderId(String senderId) {
    this.senderId = senderId;
  }

  public String getReceiverId() {
    return receiverId;
  }

  public void setReceiverId(String receiverId) {
    this.receiverId = receiverId;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public int getType() {
    return type;
  }

  public void setType(int type) {
    this.type = type;
  }

  public Date getGenerateTime() {
    return generateTime;
  }

  public void setGenerateTime(Date generateTime) {
    this.generateTime = generateTime;
  }

  public int getReadFlag() {
    return readFlag;
  }

  public void setReadFlag(int readFlag) {
    this.readFlag = readFlag;
  }

  public int getFlag() {
    return flag;
  }

  public void setFlag(int flag) {
    this.flag = flag;
  }

  public static ChatMessage generateTextMessage(String receiverId, String content) {
    ChatMessage chatMessage = new ChatMessage();
    chatMessage.setReceiverId(receiverId);
    chatMessage.setContent(content);
    chatMessage.setType(TYPE_TEXT);
    chatMessage.setId(UUIDGenerator.getUUID());
    chatMessage.setSenderId(UserConfigParams.device_id);
    return chatMessage;
  }
}
