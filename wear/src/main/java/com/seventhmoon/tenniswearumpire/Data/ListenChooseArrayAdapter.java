package com.seventhmoon.tenniswearumpire.Data;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.seventhmoon.tenniswearumpire.R;

import java.util.ArrayList;



public class ListenChooseArrayAdapter extends ArrayAdapter<ListenChooseItem> {
    private static final String TAG = ListenChooseArrayAdapter.class.getName();

    private LayoutInflater inflater = null;
    //SparseBooleanArray mSparseBooleanArray;
    private int layoutResourceId;
    private ArrayList<ListenChooseItem> items = new ArrayList<>();
    private Context context;

    public ListenChooseArrayAdapter(Context context, int textViewResourceId,
                                    ArrayList<ListenChooseItem> objects) {
        super(context, textViewResourceId, objects);
        this.layoutResourceId = textViewResourceId;
        this.items = objects;
        this.context = context;

        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //mSparseBooleanArray = new SparseBooleanArray();
    }

    @Override
    public int getCount() {
        return items.size();

    }

    public ListenChooseItem getItem(int position)
    {
        return items.get(position);
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        //Log.e(TAG, "getView = "+ position);
        View view;
        ViewHolder holder;
        if (convertView == null || convertView.getTag() == null) {
            //Log.e(TAG, "convertView = null");
            /*view = inflater.inflate(layoutResourceId, null);
            holder = new ViewHolder(view);
            view.setTag(holder);*/

            //LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(layoutResourceId, null);
            holder = new ViewHolder(view);
            //holder.checkbox.setVisibility(View.INVISIBLE);
            view.setTag(holder);
        }
        else {
            view = convertView ;
            holder = (ViewHolder) view.getTag();
        }

        //holder.fileicon = (ImageView) view.findViewById(R.id.fd_Icon1);
        //holder.filename = (TextView) view.findViewById(R.id.fileChooseFileName);
        //holder.checkbox = (CheckBox) view.findViewById(R.id.checkBoxInRow);

        ListenChooseItem listenChooseItem = items.get(position);
        if (listenChooseItem != null) {

            //holder.filename.setText(listenChooseItem.getFileName());
            //holder.checkbox.setTag(position);

            //TextView t1 = (TextView) view.findViewById(R.id.fileChooseFileName);
            //CheckBox ck = (CheckBox) view.findViewById(R.id.checkBoxInRow);
            //TextView t2 = (TextView) v.findViewById(R.id.TextView02);
            //TextView t3 = (TextView) v.findViewById(R.id.TextViewDate);
                       /* Take the ImageView from layout and set the city's image */

            //fileChooseItem.setCheckBox(ck);
            //ck.setVisibility(View.INVISIBLE);

            //if (Data.FileChooseLongClick) {
            //    ck.setVisibility(View.VISIBLE);
            //}

            /*if (mSparseBooleanArray.get(position))
            {
                ck.setChecked(true);
            } else {
                ck.setChecked(false);
            }
            if (holder.checkbox != null) {
                holder.checkbox.setVisibility(View.INVISIBLE);
                holder.checkbox.setChecked(false);
                if (Data.FileChooseLongClick == true) {
                    holder.checkbox.setVisibility(View.VISIBLE);
                }
            } else {
                Log.e(TAG, "checkbox = null");
            }*/
            //if(t1!=null)
            //    t1.setText(listenChooseItem.getFileName());
            //if(t2!=null)
            //    t2.setText(o.getData());
            //if(t3!=null)
            //    t3.setText(o.getDate());


            Bitmap bitmap;
            switch (position) {
                case 0: //uk man
                    bitmap = BitmapFactory.decodeResource(view.getResources(), R.drawable.uk_flag);
                    holder.filename.setText("GBR Man");
                    break;
                case 1: //uk woman
                    bitmap = BitmapFactory.decodeResource(view.getResources(), R.drawable.uk_flag);
                    holder.filename.setText("GBR Woman");
                    break;
                //case 2: //user
                //    bitmap = BitmapFactory.decodeResource(view.getResources(), R.drawable.ic_record_voice_over_white_48dp);
                //    holder.filename.setText(context.getResources().getString(R.string.voice_user_record));
                    //break;
                default:
                    bitmap = BitmapFactory.decodeResource(view.getResources(), R.drawable.uk_flag);
                    holder.filename.setText("GBR Man");
                    break;
            }
            holder.fileicon.setImageBitmap(bitmap);
            //bitmap = BitmapFactory.decodeResource(view.getResources(), R.drawable.file);
            //bm = Bitmap.createScaledBitmap(bitmap, 50, 50, true);
            //ImageView imageCity = (ImageView) view.findViewById(R.id.fd_Icon1);
            //String uri = "drawable/" + o.getImage();
            //int imageResource = c.getResources().getIdentifier(uri, null, c.getPackageName());
            //Drawable image = c.getResources().getDrawable(imageResource);
            //imageCity.setImageBitmap(bm);
            //imageCity.setImageDrawable(image);

            //holder.checkbox.setOnCheckedChangeListener(mCheckedChangeListener);

        }
        return view;
    }

    /*CompoundButton.OnCheckedChangeListener mCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            Log.i(TAG, "switch " + buttonView.getTag() + " checked = " + isChecked);
            //int idx = (Integer) buttonView.getTag();

            //if(isChecked == true) {
            FileChooseItem fileChooseItem = items.get((Integer) buttonView.getTag());

            if (fileChooseItem.getCheckBox() != null) {

                if (!fileChooseItem.getName().equals("..")) {
                    mSparseBooleanArray.put((Integer) buttonView.getTag(), isChecked);
                }
                else {
                    fileChooseItem.getCheckBox().setChecked(false);
                    fileChooseItem.getCheckBox().setVisibility(View.INVISIBLE);
                    mSparseBooleanArray.put((Integer) buttonView.getTag(), false);
                }
            }
            //}
        }
    };*/

    class ViewHolder {
        ImageView fileicon;
        TextView filename;
        //CheckBox checkbox;


        public ViewHolder(View view) {
            this.fileicon = (ImageView) view.findViewById(R.id.listen_Icon1);
            this.filename = (TextView) view.findViewById(R.id.ListenChooseName);
            //this.checkbox = (CheckBox) view.findViewById(R.id.checkBoxInRow);
        }
    }
}
