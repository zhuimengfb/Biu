package com.biu.biu.contact.views.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.biu.biu.app.BiuApp;
import com.biu.biu.contact.bean.ChatMessage;
import com.biu.biu.userconfig.UserConfigParams;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import grf.biu.R;

/**
 * Created by fubo on 2016/5/30 0030.
 * email:bofu1993@163.com
 */
public class ChatContentListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

  private List<ChatMessage> chatMessages;
  private static final int TYPE_MESSAGE_DEFAULT = 0;
  private static final int TYPE_MESSAGE_TEXT_LEFT = 1;
  private static final int TYPE_MESSAGE_TEXT_RIGHT = 2;

  public ChatContentListAdapter(List<ChatMessage> chatMessages) {
    this.chatMessages = chatMessages;
  }

  @Override
  public int getItemViewType(int position) {
    if (chatMessages.get(position).getType() == ChatMessage.TYPE_TEXT) {
      if (chatMessages.get(position).getSenderId().equals(UserConfigParams.device_id)) {
        return TYPE_MESSAGE_TEXT_RIGHT;
      } else {
        return TYPE_MESSAGE_TEXT_LEFT;
      }
    }
    return TYPE_MESSAGE_DEFAULT;
  }

  @Override
  public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    switch (viewType) {
      case TYPE_MESSAGE_TEXT_LEFT:
        return new ContentViewHolder(LayoutInflater.from(BiuApp.getContext()).inflate(R.layout
            .item_chat_content_left, parent, false));
      case TYPE_MESSAGE_TEXT_RIGHT:
        return new ContentViewHolder(LayoutInflater.from(BiuApp.getContext()).inflate(R.layout
            .item_chat_content_right, parent, false));
      default:
        return null;
    }
  }

  @Override
  public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
    switch (getItemViewType(position)) {
      case TYPE_MESSAGE_TEXT_LEFT:
        showText(holder, chatMessages.get(position));
        break;
      case TYPE_MESSAGE_TEXT_RIGHT:
        showText(holder,chatMessages.get(position));
        break;
      default:
        break;
    }
  }

  private void showText(RecyclerView.ViewHolder holder, ChatMessage chatMessage) {
    ((ContentViewHolder)holder).chatContent.setText(chatMessage.getContent());
  }

  @Override
  public int getItemCount() {
    return chatMessages.size();
  }

  class ContentViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.iv_chat_user_icon)
    SimpleDraweeView userIcon;
    @BindView(R.id.tv_chat_content)
    TextView chatContent;

    public ContentViewHolder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
    }
  }
}
