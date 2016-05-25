package com.biu.biu.contact.views;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.biu.biu.views.base.BaseFragment;

/**
 * Created by fubo on 2016/5/18 0018.
 * email:bofu1993@163.com
 */
public class ContactListFragment extends BaseFragment implements IContactList {


  public static ContactListFragment getInstance() {
    ContactListFragment contactListFragment = new ContactListFragment();
    return contactListFragment;
  }

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater,
      @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    return super.onCreateView(inflater, container, savedInstanceState);
  }
}
