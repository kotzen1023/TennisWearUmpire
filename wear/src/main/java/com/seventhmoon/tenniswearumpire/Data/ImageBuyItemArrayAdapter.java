package com.seventhmoon.tenniswearumpire.Data;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.seventhmoon.tenniswearumpire.R;

import java.util.ArrayList;


public class ImageBuyItemArrayAdapter extends ArrayAdapter<ImageBuyItem> {
    private static final String TAG = ImageBuyItemArrayAdapter.class.getName();

    private LayoutInflater inflater = null;

    private int layoutResourceId;
    private Context context;

    private ArrayList<ImageBuyItem> items = new ArrayList<>();
    private boolean[] selection;

    public ImageBuyItemArrayAdapter(Context context, int layoutResourceId, ArrayList<ImageBuyItem> objects) {
        super(context, layoutResourceId, objects);
        this.context = context;
        this.layoutResourceId = layoutResourceId;
        this.items = objects;
        this.context = context;

        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        selection = new boolean[items.size()];
    }

    @Override
    public int getCount() {
        return items.size();

    }

    @Override
    public ImageBuyItem getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //View row = convertView;
        View view;
        ViewHolder holder;

        if (convertView == null || convertView.getTag() == null) {
            //LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            //convertView = inflater.inflate(layoutResourceId, parent, false);
            view = inflater.inflate(layoutResourceId, null);
            holder = new ViewHolder(view);
            //holder.imageTitle = (TextView) convertView.findViewById(R.id.voiceText);
            //holder.checkbox = (CheckBox) convertView.findViewById(R.id.itemCheckBox);
            //holder.image = (ImageView) convertView.findViewById(R.id.voiceImage);
            view.setTag(holder);


        } else {
            view = convertView ;
            holder = (ViewHolder) view.getTag();
        }

        ImageBuyItem item = items.get(position);
        //holder.imageTitle.setText(item.getTitle());
        //holder.image.setImageBitmap(item.getImage());
        if (item != null) {

            if (item.getImage() != null) {
                holder.image.setImageBitmap(item.getImage());
            }

            holder.imageTitle.setText(item.getTitle());

            holder.id = position;
            if (item.getSelected()) {
                holder.setState(true);


                switch (position) {
                    case 0:
                        //actionBar.setHomeAsUpIndicator(R.drawable.uk_flag);
                        //actionBar.setTitle(context.getResources().getString(R.string.voice_gbr_man));
                        break;
                    case 1:
                        //actionBar.setHomeAsUpIndicator(R.drawable.uk_flag);
                        //actionBar.setTitle(context.getResources().getString(R.string.voice_gbr_woman));
                        break;
                    case 2:
                        //actionBar.setHomeAsUpIndicator(R.drawable.ic_record_voice_over_white_48dp);
                        //actionBar.setTitle(context.getResources().getString(R.string.voice_user_record));
                        break;
                    default:
                        //actionBar.setHomeAsUpIndicator(R.drawable.uk_flag);
                        //actionBar.setTitle(context.getResources().getString(R.string.voice_gbr_man));
                        break;
                }

                //holder.image.setBackgroundColor(Color.argb(255, 0x46,0x6e,0x9b));
                //holder.imageTitle.setBackgroundColor(Color.argb(255, 0x46,0x6e,0x9b));
                selection[holder.id] = true;
                view.setBackgroundColor(Color.rgb(0x4d, 0x90, 0xfe));
            } else {
                holder.setState(false);

                selection[holder.id] = false;
                view.setBackgroundColor(Color.TRANSPARENT);
            }


        }

        return view;
    }

    private class ViewHolder {
        TextView imageTitle;
        ImageView image;
        //CheckBox checkbox;
        int id;
        boolean state;

        //public boolean getState()
        //{
        //    return state;
        //}
        private ViewHolder(View view) {
            this.image = (ImageView) view.findViewById(R.id.voiceImage);
            //this.videoView = (VideoView) view.findViewById(R.id.videoView);
            this.imageTitle = (TextView) view.findViewById(R.id.voiceText);
            //this.videotime = (TextView) view.findViewById(R.id.songTime);
        }

        private void setState(boolean state)
        {
            this.state = state;
        }
    }
}
