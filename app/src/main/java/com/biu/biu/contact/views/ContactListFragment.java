package com.biu.biu.contact.views;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.biu.biu.contact.entity.ContactInfo;
import com.biu.biu.contact.presenter.ContactPresenter;
import com.biu.biu.contact.views.adapter.ContactRecyclerAdapter;
import com.biu.biu.user.utils.UserPreferenceUtil;
import com.biu.biu.views.base.BaseFragment;
import com.camnter.easyrecyclerviewsidebar.EasyRecyclerViewSidebar;
import com.camnter.easyrecyclerviewsidebar.sections.EasyImageSection;
import com.camnter.easyrecyclerviewsidebar.sections.EasySection;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import grf.biu.R;

/**
 * Created by fubo on 2016/5/18 0018.
 * email:bofu1993@163.com
 */
public class ContactListFragment extends BaseFragment implements IContactListView {


  @BindView(R.id.rv_contact_list)
  RecyclerView contactRecyclerView;
  @BindView(R.id.contact_sidebar)
  EasyRecyclerViewSidebar easyRecyclerViewSidebar;
  @BindView(R.id.section_layout)
  RelativeLayout sectionLayout;
  @BindView(R.id.tv_section_letter)
  TextView sectionLetter;
  @BindView(R.id.rl_nothing_layout)
  RelativeLayout nothingLayout;

  private ContactPresenter contactPresenter;

  private ContactRecyclerAdapter contactRecyclerAdapter;
  private List<ContactInfo> contactInfoList = new ArrayList<>();

  public static ContactListFragment getInstance() {
    ContactListFragment contactListFragment = new ContactListFragment();
    return contactListFragment;
  }

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater,
      @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_contact, container, false);
    ButterKnife.bind(this, view);
    contactPresenter = new ContactPresenter(this);
    initView();
    initData();
    return view;
  }

  private void initView() {
    contactRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    contactRecyclerView.setItemAnimator(new DefaultItemAnimator());
    contactRecyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(getActivity
        ()).margin(40, 60).size(1).build());
  }

  private void initData() {
    /*ContactInfo contactInfo = new ContactInfo();
    contactInfo.setName("酷派");
    contactInfo.setEnglishName("kupai");
    contactInfo.setUserId("ffffffff-a1f3-bbb5-6508-e21700000000");
    contactPresenter.saveContact(contactInfo);
    contactInfoList.add(contactInfo);
    ContactInfo contactInfo1 = new ContactInfo();
    contactInfo1.setName("FBI");
    contactInfo1.setEnglishName("FBI");
    contactInfo1.setUserId("ffffffff-9b6a-5295-ffff-ffffcb1967dc");
    contactPresenter.saveContact(contactInfo1);
    contactInfoList.add(contactInfo1);*/
    contactPresenter.queryContact(UserPreferenceUtil.getUserPreferenceId());
    contactRecyclerAdapter = new ContactRecyclerAdapter(getActivity(), contactInfoList);
    contactRecyclerView.setAdapter(contactRecyclerAdapter);
    easyRecyclerViewSidebar.setSections(contactRecyclerAdapter.getSections());
    easyRecyclerViewSidebar.setFloatView(sectionLayout);
    easyRecyclerViewSidebar.setOnTouchSectionListener(new EasyRecyclerViewSidebar
        .OnTouchSectionListener() {

      @Override
      public void onTouchImageSection(int sectionIndex, EasyImageSection imageSection) {

      }

      @Override
      public void onTouchLetterSection(int sectionIndex, EasySection letterSection) {
        sectionLetter.setText(letterSection.letter);
        ((LinearLayoutManager) contactRecyclerView.getLayoutManager()).scrollToPositionWithOffset
            (contactRecyclerAdapter.getHeaderPosition(sectionIndex), 0);
      }
    });
    contactRecyclerAdapter.setOnItemClickListener(new ContactRecyclerAdapter.OnItemClickListener() {
      @Override
      public void onItemClick(ContactInfo contactInfo) {
        ChatActivity.toChatActivity(getActivity(), contactInfo);
      }
    });
  }

  @Override
  public void updateContactList(List<ContactInfo> contactInfoList) {
    if (contactInfoList.size() > 0) {
      nothingLayout.setVisibility(View.GONE);
    } else {
      nothingLayout.setVisibility(View.VISIBLE);
    }
    this.contactInfoList.clear();
    contactRecyclerAdapter.notifyDataSetChanged();
    this.contactInfoList.addAll(contactInfoList);
    contactRecyclerAdapter.updateData(this.contactInfoList);
    easyRecyclerViewSidebar.setSections(contactRecyclerAdapter.getSections());
  }

  @Override
  public void hasNewRequest(int number) {
    contactRecyclerAdapter.setHasNewRequest(number);
  }

  class AddContactListener implements View.OnClickListener {

    @Override
    public void onClick(View v) {
      Intent intent = new Intent();
      intent.setClass(getActivity(), AddContactActivity.class);
      getActivity().startActivity(intent);
    }
  }

  @Override
  public void onResume() {
    if (contactPresenter != null) {
      contactPresenter.queryUnDealRequestCount();
    }
    super.onResume();
  }
}
