package com.bignerdranch.android.newprojectdemo;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by simon on 8/20/16.
 */
public class LogFragment extends Fragment {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public class MusicAdapter extends BaseAdapter {
        private Context mContext;
        private String[] fileNames;
        private String[] fileTypes;
        private boolean[] upDownStatus;

        public MusicAdapter(Context c, String[] fileNames, String fileTypes[], boolean[] upDownStatus) {
            mContext = c;
            this.fileNames = fileNames;
            this.fileTypes = fileTypes;
            this.upDownStatus = upDownStatus;
        }

        public int getCount() {
            return fileNames.length;
        }

        public Object getItem(int position) {
            return position;
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            //System.gc();
            View view;

            try {

                if (convertView == null) {
                    view = getLayoutInflater(null).inflate(R.layout.log_list_item, parent, false);
                } else {
                    view = convertView;
                }

                //0 - Download, 1 - upload
                ((ImageView)view.findViewById(R.id.upDownview)).setImageResource((upDownStatus[position])? R.drawable.ic_download: R.drawable.ic_upload );

                ((TextView)view.findViewById(R.id.imagePath)).setText(fileNames[position]);
                ((TextView)view.findViewById(R.id.fileType)).setText(fileTypes[position]);

                return view;
            }catch (Exception e){
                e.printStackTrace();

            }

            return null;
        }
    }
}
