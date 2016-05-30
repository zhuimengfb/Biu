package com.biu.biu.contact.views.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.biu.biu.app.BiuApp;
import com.biu.biu.contact.bean.AddContactRequest;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import grf.biu.R;

/**
 * Created by fubo on 2016/5/27 0027.
 * email:bofu1993@163.com
 */
public class AddContactListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

  List<AddContactRequest> requestList;

  public AddContactListAdapter(List<AddContactRequest> requestList) {
    this.requestList = requestList;
  }

  @Override
  public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    return new AddContactViewHolder(LayoutInflater.from(BiuApp.getContext()).inflate(R.layout
        .item_add_contact, parent, false));
  }

  @Override
  public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

  }

  @Override
  public int getItemCount() {
    return requestList.size();
  }

  class AddContactViewHolder extends RecyclerView.ViewHolder{

    @BindView(R.id.iv_add_head_icon)
    SimpleDraweeView addContactIcon;
    @BindView(R.id.tv_add_contact_name)
    TextView addContactName;
    @BindView(R.id.bt_add_contact)
    Button addContactButton;
    @BindView(R.id.tv_already_add)
    TextView addAlready;

    public AddContactViewHolder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
    }
  }
}
