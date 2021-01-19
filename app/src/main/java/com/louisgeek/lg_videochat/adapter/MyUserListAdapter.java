package com.louisgeek.lg_videochat.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.louisgeek.chat.model.base.UserModel;

import java.util.ArrayList;
import java.util.List;


public class MyUserListAdapter extends BaseAdapter {
    private final List<UserModel> mDataList = new ArrayList<>();

    @Override
    public int getCount() {
        return mDataList.size();
    }

    @Override
    public Object getItem(int position) {
        return mDataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MyViewHolder myViewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_2, parent, false);
            myViewHolder = new MyViewHolder();
            myViewHolder.textView = convertView.findViewById(android.R.id.text1);
            myViewHolder.textView2 = convertView.findViewById(android.R.id.text2);
            convertView.setTag(myViewHolder);
        } else {
            myViewHolder = (MyViewHolder) convertView.getTag();
        }
        //
        UserModel userModel = mDataList.get(position);
        myViewHolder.textView.setText(userModel.userId);
        myViewHolder.textView2.setText(userModel.userName);

        return convertView;
    }


    class MyViewHolder {
        public TextView textView;
        public TextView textView2;
    }

    public void addData(UserModel userModel) {
        mDataList.add(userModel);
        notifyDataSetChanged();
    }

    public void refreshDataList(List<UserModel> userModelList) {
        mDataList.clear();
        mDataList.addAll(userModelList);
        notifyDataSetChanged();
    }
}
