package com.biu.biu.user.views.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.biu.biu.app.BiuApp;
import com.biu.biu.user.entity.UserPicInfo;
import com.bumptech.glide.Glide;

import java.io.File;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import grf.biu.R;

/**
 * Created by fubo on 2016/6/2 0002.
 * email:bofu1993@163.com
 */
public class UserUploadPicAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

  private List<UserPicInfo> userPicInfos;
  private static final int TYPE_FOOTER = 1;
  private static final int TYPE_NORMAL = 2;
  private OnAddPicListener addPicListener = null;
  private OnRemovePicListener removePicListener = null;
  private static final int MAX_USER_PIC_NUMBER = 9;

  public UserUploadPicAdapter(List<UserPicInfo> userPicInfos) {
    this.userPicInfos = userPicInfos;
  }

  public void setOnAddPicListener(OnAddPicListener onAddPicListener) {
    this.addPicListener = onAddPicListener;
  }

  public void setOnRemovePicListener(OnRemovePicListener onRemovePicListener) {
    this.removePicListener = onRemovePicListener;
  }

  @Override
  public int getItemViewType(int position) {
    return position < userPicInfos.size() ? TYPE_NORMAL : TYPE_FOOTER;
  }

  @Override
  public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    if (viewType == TYPE_FOOTER) {
      return new ImageViewHolder(LayoutInflater.from(BiuApp.getContext()).inflate(R.layout
          .item_upload_pic_camera, parent, false));
    } else {
      return new ImageViewHolder(LayoutInflater.from(BiuApp.getContext()).inflate(R.layout
          .item_user_upload_pic, parent, false));
    }
  }

  @Override
  public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
    switch (getItemViewType(position)) {
      case TYPE_FOOTER:
        /*((ImageViewHolder) holder).userUploadPicImageView.setImageURI(Uri.parse(GlobalString
            .URI_RES_PREFIX + R.drawable.add_photo));*/
        if (addPicListener != null) {
          ((ImageViewHolder) holder).userUploadPicImageView.setOnClickListener(new View
              .OnClickListener() {
            @Override
            public void onClick(View v) {
              if (userPicInfos.size() < MAX_USER_PIC_NUMBER) {
                addPicListener.onAddClick(v);
              } else {
                Toast.makeText(BiuApp.getContext(), BiuApp.getContext().getString(R.string
                    .upload_pic_max_number), Toast.LENGTH_SHORT).show();
              }
            }
          });
        }
        break;
      case TYPE_NORMAL:
        //从本地文件显示
        /*((ImageViewHolder) holder).userUploadPicImageView.setImageURI(Uri.fromFile(new File
            (userPicInfos.get(position).getLocalPath())));*/
        ((ImageViewHolder) holder).userUploadPicImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        Glide.with(BiuApp.getContext()).load(new File(userPicInfos.get(position).getLocalPath()))
            .into(((ImageViewHolder) holder).userUploadPicImageView);
        ((ImageViewHolder) holder).userUploadPicImageView.setOnLongClickListener(new View
            .OnLongClickListener() {
          @Override
          public boolean onLongClick(View v) {
            if (removePicListener != null) {
              return removePicListener.onRemoveClick(v, position);
            } else {
              return false;
            }
          }
        });
        break;
      default:
        break;
    }
  }

  @Override
  public int getItemCount() {
    return userPicInfos.size() + 1;
  }

  public interface OnAddPicListener {
    void onAddClick(View view);
  }

  public interface OnRemovePicListener {
    boolean onRemoveClick(View view, int position);
  }

  class ImageViewHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.user_upload_pic_image_view)
    ImageView userUploadPicImageView;

    public ImageViewHolder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
    }
  }
}
