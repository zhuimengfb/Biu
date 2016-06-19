package com.biu.biu.contact.model;

import com.biu.biu.app.BiuApp;
import com.biu.biu.contact.entity.AddContactBean;
import com.biu.biu.contact.entity.AddContactRequest;
import com.biu.biu.contact.entity.AddRequestUserInfo;
import com.biu.biu.contact.entity.ContactInfo;
import com.biu.biu.contact.entity.ContactListNetBean;
import com.biu.biu.user.entity.SimpleUserInfo;
import com.biu.biu.user.utils.UserPreferenceUtil;
import com.biu.biu.utils.GlobalString;
import com.biu.biu.utils.PinyinUtil;
import com.biu.biu.utils.UUIDGenerator;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.jackson.JacksonConverterFactory;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by fubo on 2016/5/18 0018.
 * email:bofu1993@163.com
 */
public class ContactModel implements IContactModel {

  private Retrofit retrofit = new Retrofit.Builder()
      .baseUrl(GlobalString.BASE_URL)
      .addConverterFactory(JacksonConverterFactory.create())
      .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
      .build();
  private ContactApiService contactApiService = retrofit.create(ContactApiService.class);
  private GetContactListener getContactListener;


  public ContactModel() {
  }

  public ContactModel(GetContactListener getContactListener) {
    this.getContactListener = getContactListener;
  }


  @Override
  public ContactInfo queryContactInfo(String contactId) {
    Realm realm = Realm.getInstance(BiuApp.getRealmConfiguration());
    RealmResults<ContactInfo> contactInfos = realm.where(ContactInfo.class).equalTo
        (ContactInfo.KEY_USER_ID, contactId).equalTo(ContactInfo.KEY_FLAG, ContactInfo
        .FLAG_NORMAL).findAll();
    if (contactInfos != null && contactInfos.size() > 0) {
      return contactInfos.get(0);
    }
    return null;
  }

  @Override
  public List<ContactInfo> queryAllContactFromDB() {
    Realm realm = Realm.getInstance(BiuApp.getRealmConfiguration());
    RealmResults<ContactInfo> contactInfos = realm.where(ContactInfo.class).equalTo(ContactInfo
        .KEY_FLAG, ContactInfo.FLAG_NORMAL).findAll();
    return new ArrayList<>(contactInfos);
  }

  @Override
  public void saveContactInfos(List<ContactInfo> contactInfoList) {
    Observable.from(contactInfoList).doOnNext(new Action1<ContactInfo>() {
      @Override
      public void call(ContactInfo contactInfo) {
        Realm realm = Realm.getInstance(BiuApp.getRealmConfiguration());
        realm.beginTransaction();
        realm.copyToRealmOrUpdate(contactInfo);
        realm.commitTransaction();
        realm.close();
      }
    }).subscribeOn(Schedulers.io())
        .observeOn(Schedulers.io())
        .subscribe(new Subscriber<ContactInfo>() {
          @Override
          public void onCompleted() {

          }

          @Override
          public void onError(Throwable e) {

          }

          @Override
          public void onNext(ContactInfo contactInfo) {

          }
        });
  }

  private void deleteContactInfoInDB(ContactInfo contactInfo) {
    Realm realm = Realm.getInstance(BiuApp.getRealmConfiguration());
    realm.beginTransaction();
    contactInfo.setFlag(ContactInfo.FLAG_DELETED);
    realm.copyToRealmOrUpdate(contactInfo);
    realm.commitTransaction();
    realm.close();
  }
  @Override
  public List<AddContactBean> queryAllAddContactBean() {
    List<AddContactBean> addContactBeanList = new ArrayList<>();
    Realm realm = Realm.getInstance(BiuApp.getRealmConfiguration());
    RealmResults<AddContactRequest> addContactRequests = realm.where(AddContactRequest.class)
        .findAll();
    for (AddContactRequest addContactRequest : addContactRequests) {
      AddContactBean addContactBean = new AddContactBean();
      addContactBean.setAddContactRequest(AddContactRequest.getFromRealm(addContactRequest));
      RealmResults<SimpleUserInfo> simpleUserInfos = realm.where(SimpleUserInfo.class).equalTo
          (SimpleUserInfo.KEY_JM_ID, addContactRequest.getSenderJmId()).findAll();
      if (simpleUserInfos.size() > 0) {
        addContactBean.setSenderInfo(SimpleUserInfo.getSimpleUserInfoFromRealm(simpleUserInfos
            .get(0)));
        addContactBeanList.add(addContactBean);
      }
    }
    return addContactBeanList;
  }

  @Override
  public void acceptFriendRequest(final AddContactBean addContactBean) {
    Call<ResponseBody> call = contactApiService.acceptFriendRequest(addContactBean
        .getAddContactRequest().getSenderJmId(), addContactBean.getAddContactRequest()
        .getReceiverJmId());
    call.enqueue(new Callback<ResponseBody>() {
      @Override
      public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
        ContactInfo contactInfo = new ContactInfo();
        contactInfo.setStartDate(new Date());
        contactInfo.setFlag(ContactInfo.FLAG_NORMAL);
        contactInfo.setName(addContactBean.getSenderInfo().getNickname());
        contactInfo.setEnglishName(PinyinUtil.getPinYin(contactInfo.getName()));
        contactInfo.setUserId(addContactBean.getSenderInfo().getJm_id());
        contactInfo.setIconNetAddress(addContactBean.getSenderInfo().getIcon_small());
        saveContactInfo(contactInfo);
      }

      @Override
      public void onFailure(Call<ResponseBody> call, Throwable t) {
        t.printStackTrace();
      }
    });
  }

  @Override
  public void updateFriendRequest(AddContactBean addContactBean) {
    Realm realm = Realm.getInstance(BiuApp.getRealmConfiguration());
    realm.beginTransaction();
    realm.copyToRealmOrUpdate(addContactBean.getAddContactRequest());
    realm.commitTransaction();
  }

  @Override
  public int getUnDealRequestCount() {
    Realm realm = Realm.getInstance(BiuApp.getRealmConfiguration());
    RealmResults<AddContactRequest> addContactRequests = realm.where(AddContactRequest.class)
        .equalTo(AddContactRequest.KEY_STATUS, AddContactBean.STATUS_ADD_NORMAL).findAll();
    return addContactRequests.size();
  }

  @Override
  public void queryAllAddContactBeanFromNet(Subscriber<List<AddContactBean>> subscriber) {
    contactApiService.getAddContactList(UserPreferenceUtil.getUserPreferenceId())
        .map(new Func1<List<AddRequestUserInfo>, List<AddContactBean>>() {
          @Override
          public List<AddContactBean> call(List<AddRequestUserInfo> addRequestUserInfos) {
            for (AddRequestUserInfo addRequestUserInfo : addRequestUserInfos) {
              if (! addAlready(addRequestUserInfo.getJm_id())) {
                //如果没有添加，则保存联系人
                SimpleUserInfo simpleUserInfo = addRequestUserInfo.getSimpleUserInfo();
                saveSimpleUserInfo(simpleUserInfo);
                AddContactBean addContactBean = new AddContactBean();
                AddContactRequest addContactRequest = new AddContactRequest();
                addContactBean.setSenderInfo(simpleUserInfo);
                addContactRequest.setId(UUIDGenerator.getUUID());
                addContactRequest.setReceiverId(UserPreferenceUtil.getUserPreferenceId());
                addContactRequest.setReceiverJmId(UserPreferenceUtil.getUserPreferenceId());
                addContactRequest.setSenderId(addRequestUserInfo.getDevice_id());
                addContactRequest.setSenderJmId(addRequestUserInfo.getJm_id());
                addContactRequest.setStatus(addContactBean.STATUS_ADD_NORMAL);
                addContactRequest.setMessage(addRequestUserInfo.getMessage());
                addContactBean.setAddContactRequest(addContactRequest);
                saveAddContactRequest(addContactRequest);
              }
            }
            return queryAllAddContactBean();
          }
        })
        .subscribeOn(Schedulers.newThread())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(subscriber);
  }

  @Override
  public void deleteAddContactRequest(AddContactRequest addContactRequest) {
    contactApiService.refuseRequest(addContactRequest.getSenderJmId(), addContactRequest
        .getReceiverJmId());
    deleteAddContactRequestInDB(addContactRequest);
  }

  private void deleteAddContactRequestInDB(AddContactRequest addContactRequest) {
    Realm realm = Realm.getInstance(BiuApp.getRealmConfiguration());
    realm.beginTransaction();
    addContactRequest.removeFromRealm();
    realm.commitTransaction();
  }

  @Override
  public void saveContactInfo(ContactInfo contactInfo) {
    Realm realm = Realm.getInstance(BiuApp.getRealmConfiguration());
    realm.beginTransaction();
    realm.copyToRealmOrUpdate(contactInfo);
    realm.commitTransaction();
  }

  private void saveSimpleUserInfo(SimpleUserInfo simpleUserInfo) {
    Realm realm = Realm.getInstance(BiuApp.getRealmConfiguration());
    realm.beginTransaction();
    realm.copyToRealmOrUpdate(simpleUserInfo);
    realm.commitTransaction();
  }

  @Override
  public void queryContactListFromNet(String userId) {
    contactApiService.queryContactList(userId)
        .subscribeOn(Schedulers.newThread())
        .doOnNext(new Action1<ContactListNetBean>() {
          @Override
          public void call(ContactListNetBean contactListNetBean) {
            for (int i = 0; i < contactListNetBean.getBiu_friends().size(); i++) {
              saveContactInfo(contactListNetBean.getBiu_friends().get(i).toContactInfo());
            }
          }
        })
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Subscriber<ContactListNetBean>() {
          @Override
          public void onCompleted() {

          }

          @Override
          public void onError(Throwable e) {

          }

          @Override
          public void onNext(ContactListNetBean contactListNetBean) {
            getContactListener.onGetRequestNumber(contactListNetBean.getRequests());
            List<ContactInfo> contactInfos = new ArrayList<ContactInfo>();
            for (int i = 0; i < contactListNetBean.getBiu_friends().size(); i++) {
              contactInfos.add(contactListNetBean.getBiu_friends().get(i).toContactInfo());
            }
            getContactListener.onGetContactList(contactInfos);
          }
        });
  }

  private boolean addAlready(String jmId) {
    Realm realm = Realm.getInstance(BiuApp.getRealmConfiguration());
    RealmResults<AddContactRequest> addContactRequests = realm.where(AddContactRequest.class)
        .equalTo(AddContactRequest.KEY_JM_ID, jmId).findAll();
    if (addContactRequests == null || addContactRequests.size() == 0) {
      return false;
    } else {
      return true;
    }
  }

  public void saveAddContactRequest(AddContactRequest addContactRequest) {
    Realm realm = Realm.getInstance(BiuApp.getRealmConfiguration());
    realm.beginTransaction();
    realm.copyToRealmOrUpdate(addContactRequest);
    realm.commitTransaction();
  }

  public interface GetContactListener {
    void onGetContactList(List<ContactInfo> contactInfoList);

    void onGetRequestNumber(int number);
  }

}
