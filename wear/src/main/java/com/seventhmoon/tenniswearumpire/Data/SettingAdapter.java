package com.seventhmoon.tenniswearumpire.Data;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.seventhmoon.tenniswearumpire.R;

import java.util.ArrayList;
import java.util.List;


public class SettingAdapter extends RecyclerView.Adapter<SettingAdapter.ViewHolder> {
    public static final String TAG = SettingAdapter.class.getName();
    private ArrayList<SettingItem> mData;

    public SettingAdapter(ArrayList<SettingItem> data) {
        mData = data;
    }

    // 建立ViewHolder
    class ViewHolder extends RecyclerView.ViewHolder{
        // 宣告元件
        private TextView txtItem;

        ViewHolder(View itemView) {
            super(itemView);
            txtItem = (TextView) itemView.findViewById(R.id.setting_title);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // 連結項目布局檔list_item
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.setting_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // 設置txtItem要顯示的內容

        Log.e(TAG, "position = "+position+" title = "+mData.get(position).getTitle());

        holder.txtItem.setText(mData.get(position).getTitle());
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }
}
