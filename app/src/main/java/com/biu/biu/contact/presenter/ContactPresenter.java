package com.biu.biu.contact.presenter;

import com.biu.biu.contact.entity.ContactInfo;
import com.biu.biu.contact.model.ContactModel;
import com.biu.biu.contact.model.IContactModel;
import com.biu.biu.contact.views.IContactListView;
import com.biu.biu.utils.NetUtils;

import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by fubo on 2016/5/18 0018.
 * email:bofu1993@163.com
 */
public class ContactPresenter implements ContactModel.GetContactListener {

  private IContactListView contactListView;
  private IContactModel contactModel;

  public ContactPresenter(IContactListView contactListView) {
    this.contactListView = contactListView;
    contactModel = new ContactModel(this);
  }

  public void saveContact(ContactInfo contactInfo) {
    contactModel.saveContactInfo(contactInfo);
  }


  public void queryContact(String userId) {
    if (NetUtils.isNetConnected()) {
      contactModel.queryContactListFromNet(userId);
    } else {
      contactListView.updateContactList(contactModel.queryAllContactFromDB());
    }
  }

  public void unbind() {
    contactListView = null;
    contactModel = null;
  }

  @Override
  public void onGetContactList(List<ContactInfo> contactInfoList) {
    if (contactInfoList.size() > 0) {
      contactListView.updateContactList(contactInfoList);
    }
  }

  @Override
  public void onGetRequestNumber(int number) {
    contactListView.hasNewRequest(number);
  }

  public void queryUnDealRequestCount() {
    Observable.just(contactModel.getUnDealRequestCount())
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Subscriber<Integer>() {
          @Override
          public void onCompleted() {

          }

          @Override
          public void onError(Throwable e) {

          }

          @Override
          public void onNext(Integer integer) {
            contactListView.hasNewRequest(integer);
          }
        });
  }
}
