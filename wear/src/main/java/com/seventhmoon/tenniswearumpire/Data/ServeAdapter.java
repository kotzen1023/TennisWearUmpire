package com.seventhmoon.tenniswearumpire.Data;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.seventhmoon.tenniswearumpire.R;

import java.util.ArrayList;


public class ServeAdapter extends ArrayAdapter<ServeItem> {
    public static final String TAG = ServeAdapter.class.getName();
    //private Context context;
    private LayoutInflater inflater = null;

    private int layoutResourceId;
    private ArrayList<ServeItem> items = new ArrayList<>();

    public ServeAdapter(Context context, int ResourceId,
                        ArrayList<ServeItem> objects) {
        super(context, ResourceId, objects);
        //this.context = context;
        this.layoutResourceId = ResourceId;
        this.items = objects;

        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public int getCount() {
        return items.size();

    }

    public ServeItem getItem(int position)
    {
        return items.get(position);
    }

    @NonNull
    public View getView(int position, View convertView, ViewGroup parent) {

        //Log.e(TAG, "getView = " + position);
        View view;
        ViewHolder holder;


        if (convertView == null || convertView.getTag() == null) {
            /*LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            convertView = inflater.inflate(layoutResourceId, parent, false);
            holder = new ViewHolder();

            holder.jid = (TextView) convertView.findViewById(R.id.contact_jid);
            holder.avatar = (ImageView) convertView.findViewById(R.id.contact_icon);
            convertView.setTag(holder);*/
            view = inflater.inflate(layoutResourceId, null);
            holder = new ViewHolder(view);
            view.setTag(holder);


        } else {
            view = convertView;
            //Log.e(TAG, "here!");
            holder = (ViewHolder) view.getTag();
        }


        ServeItem item = items.get(position);


        if (item != null) {
            //holder.color.setBackgroundColor(item.getColor());
            //holder.color.setBackgroundColor(Color.parseColor("#FFFFFF"));
            Drawable d = new ColorDrawable(Color.rgb(item.getColorR(), item.getColorG(), item.getColorB()));
            holder.color.setImageDrawable(d);

            holder.text.setText(item.getText());
            holder.text.setTextColor(Color.rgb(item.getColorR(), item.getColorG(), item.getColorB()));

        }


        return view;
    }

    public View getDropDownView(int position, View convertView, ViewGroup
            parent){
        return getView(position,convertView,parent);

    }

    class ViewHolder {

        ImageView color;
        TextView text;

        public ViewHolder(View view) {

            this.color = (ImageView) view.findViewById(R.id.color_serve);
            this.text = (TextView) view.findViewById(R.id.text_serve);
        }


    }
}
