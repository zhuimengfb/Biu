package com.biu.biu.contact.views;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.biu.biu.app.BiuApp;
import com.biu.biu.contact.bean.ChatMessage;
import com.biu.biu.contact.bean.ContactInfo;
import com.biu.biu.contact.utils.ContactAction;
import com.biu.biu.contact.views.adapter.ChatContentListAdapter;
import com.biu.biu.views.base.BaseActivity;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import grf.biu.R;

/**
 * Created by fubo on 2016/5/29 0029.
 * email:bofu1993@163.com
 */
public class ChatActivity extends BaseActivity implements IChatActivity {

  @BindView(R.id.rv_chat_list)
  RecyclerView chatRecyclerView;
  @BindView(R.id.et_chat_content)
  EditText chatContent;
  @BindView(R.id.toolbar)
  Toolbar toolbar;
  @BindView(R.id.toolbar_title)
  TextView toolbarTitle;

  private ChatContentListAdapter chatContentListAdapter;
  private List<ChatMessage> chatMessages = new ArrayList<>();
  private ContactInfo contactInfo;

  @Override

  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_chat_room);
    ButterKnife.bind(this);
    if (getIntent().getSerializableExtra(ContactAction.KEY_CONTACT_INFO)==null) {
      finish();
    }
    contactInfo= (ContactInfo) getIntent().getSerializableExtra(ContactAction.KEY_CONTACT_INFO);
    initView();
    initData();
    initEvent();
  }

  private void initView() {
    setSupportActionBar(toolbar);
    setBackableToolbar(toolbar);
    toolbarTitle.setText("对方");
    chatRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    chatRecyclerView.setItemAnimator(new DefaultItemAnimator());
  }

  private void initData() {
    chatContentListAdapter = new ChatContentListAdapter(chatMessages);
    chatRecyclerView.setAdapter(chatContentListAdapter);
  }

  private void initEvent() {
    chatContent.setOnEditorActionListener(new TextView.OnEditorActionListener() {
      @Override
      public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_SEND) {
          sendMessage(chatContent.getText().toString());
        }
        return false;
      }
    });
  }

  private void sendMessage(String message) {
    if (StringUtils.isEmpty(message)) {
      Toast.makeText(BiuApp.getContext(), getString(R.string.input_nothing), Toast.LENGTH_SHORT)
          .show();
      return;
    }
    chatMessages.add(ChatMessage.generateTextMessage(contactInfo.getId(), message));
    chatContentListAdapter.notifyDataSetChanged();
    chatContent.setText("");
  }

  public static void toChatActivity(Context context, ContactInfo contactInfo) {
    Intent intent = new Intent();
    intent.setClass(context, ChatActivity.class);
    intent.putExtra(ContactAction.KEY_CONTACT_INFO, contactInfo);
    context.startActivity(intent);
  }

}
