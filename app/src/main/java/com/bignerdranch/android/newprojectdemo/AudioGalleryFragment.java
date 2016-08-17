package com.bignerdranch.android.newprojectdemo;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by nyanye on 7/2/16.
 */
public class AudioGalleryFragment extends Fragment {

    ListView musiclist;
    Cursor musiccursor;
    int music_column_index;
    int count;
    private MusicAdapter musicAdapter;
    private ArrayList<SongModel> songList;
    private ArrayList<Integer> selectedPos = new ArrayList<Integer>();

    class SongModel{
        private boolean checked;
        private String songPath;

        public boolean isChecked() {
            return checked;
        }

        public void setChecked(boolean checked) {
            this.checked = checked;
        }

        public void setSongPath(String path){
            songPath = path;
        }

        public String getSongPath(){
            return songPath;
        }
    }

    public String[] getSelectedSongs(){
        String[] selectedSongPaths = new String[selectedPos.size()];
        for(int i = 0; i < selectedPos.size(); i++){
            selectedSongPaths[i] = songList.get(selectedPos.get(i)).getSongPath();
        }
        return selectedSongPaths;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

            View view = inflater.inflate(R.layout.list_gallery, container, false);

        try{

            String[] proj = {MediaStore.Audio.Media._ID,
                    MediaStore.Audio.Media.DATA,
                    MediaStore.Audio.Media.DISPLAY_NAME,
                    MediaStore.Video.Media.SIZE};
            musiccursor = getActivity().getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    proj, null, null, null);
            //"LOWER("+MediaStore.Audio.Media.TITLE+") ASC"
            count = musiccursor.getCount();
            songList = new ArrayList<>();
            for(int i = 0; i < count; i++){
                songList.add(new SongModel());
            }
            Log.e("CursorNum", "CursorNum: " + count);

            musiclist = (ListView) view.findViewById(R.id.PhoneMusicList);
            musicAdapter = new MusicAdapter(getContext());
            musiclist.setAdapter(musicAdapter);

            musiclist.setOnItemClickListener(musicgridlistener);
//        mMediaPlayer = new MediaPlayer();


            musiclist.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE_MODAL);

            musiclist.setMultiChoiceModeListener(listViewListener);
        }catch(Exception e){
            e.printStackTrace();
        }

        return view;
    }

    AbsListView.MultiChoiceModeListener listViewListener = new AbsListView.MultiChoiceModeListener() {


        @Override
        public void onItemCheckedStateChanged(android.view.ActionMode actionMode, int i, long l, boolean b) {
            int selectCount = musiclist.getCheckedItemCount();
            switch (selectCount) {
                case 1:
                    actionMode.setSubtitle("One item selected");
                    break;
                default:
                    actionMode.setSubtitle("" + selectCount + " items selected");
                    break;
            }


//                if(musiclist.getChildAt(i).findViewById(R.id.check_icon).getVisibility() == View.INVISIBLE){
//                    musiclist.getChildAt(i).findViewById(R.id.check_icon).setVisibility(View.VISIBLE);
//                }else{
//                    musiclist.getChildAt(i).findViewById(R.id.check_icon).setVisibility(View.INVISIBLE);
//                }

//                try {

            int pos = i - musiclist.getFirstVisiblePosition();
            if (songList.get(i).isChecked()) {
                musiclist.getChildAt(pos).findViewById(R.id.check_icon).setVisibility(View.INVISIBLE);
                songList.get(i).setChecked(false);
                selectedPos.remove(selectedPos.lastIndexOf(i));
            } else {
//                        Log.e("ChildCount", "ChildCount: "+musiclist.getChildCount());
                            /*Log.e("Position", "Position: "+i);
                        Log.e("FirstVisible", "FirstVisiblePos: "+musiclist.getFirstVisiblePosition()
                                +", LastPos: "+musiclist.getLastVisiblePosition()
                                +", Selected: "+musiclist.getSelectedItemPosition());*/


                musiclist.getChildAt(pos).findViewById(R.id.check_icon).setVisibility(View.VISIBLE);
                songList.get(i).setChecked(true);
                selectedPos.add(i);
            }
//                }catch (Exception e){
//                    e.printStackTrace();
//                }



                /*ImageAdapter.MarkableImageView imageView = (ImageAdapter.MarkableImageView) gridView.getChildAt(position);
                imageView.setChecked(checked);*/
        }

        @Override
        public boolean onCreateActionMode(android.view.ActionMode actionMode, Menu menu) {
            actionMode.setTitle("Select Items");
            actionMode.setSubtitle("One item selected");
            selectedPos = new ArrayList<Integer>();
            return true;
        }

        @Override
        public boolean onPrepareActionMode(android.view.ActionMode actionMode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(android.view.ActionMode actionMode, MenuItem menuItem) {
            return false;
        }

        @Override
        public void onDestroyActionMode(android.view.ActionMode actionMode) {
            musicAdapter.removeSelection(selectedPos);
        }
    };


    public void removeAudioSelection(){
        if(selectedPos.size() != 0){
            musicAdapter.removeSelection(selectedPos);
            //listViewListener.onDestroyActionMode(null);
            musiclist.setChoiceMode(AbsListView.CHOICE_MODE_NONE);
            musiclist.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE_MODAL);
        }

    }


    private AdapterView.OnItemClickListener musicgridlistener = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView parent, View v, int position,
                                long id) {
            System.gc();
            music_column_index = musiccursor
                    .getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
            musiccursor.moveToPosition(position);
            String filename = musiccursor.getString(music_column_index);

            Intent intent = new Intent();
            intent.setAction(android.content.Intent.ACTION_VIEW);
            File file = new File(filename);
            intent.setDataAndType(Uri.fromFile(file), "audio/*");
            startActivity(intent);
        }
    };

    public class MusicAdapter extends BaseAdapter {
        private Context mContext;


        public MusicAdapter(Context c) {
            mContext = c;

        }

        public int getCount() {
            return count;
        }

        public Object getItem(int position) {
            return position;
        }

        public long getItemId(int position) {
            return position;
        }

        public void setNewSelection(int position){
            if(songList.get(position).isChecked())  songList.get(position).setChecked(false);
            else songList.get(position).setChecked(true);

        }

        public void removeSelection(ArrayList<Integer> selectionList){
            for(int i = 0; i < selectionList.size(); i++){
                musiclist.getChildAt(selectionList.get(i).intValue()).findViewById(R.id.check_icon).setVisibility(View.INVISIBLE);
                songList.get(selectionList.get(i).intValue()).setChecked(false);
                Log.e("SelectionList", "SelectionList: "+selectionList.get(i).intValue());
            }
            selectionList.clear();
            notifyDataSetChanged();
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            //System.gc();
            View view;

            try {


                if (convertView == null) {

                    view = getLayoutInflater(null).inflate(R.layout.audio_gallery_item, parent, false);


                } else {
                    view = convertView;
                }

                music_column_index = musiccursor
                        .getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME);

                musiccursor.moveToPosition(position);
                String id = musiccursor.getString(music_column_index);

                ((TextView) view.findViewById(R.id.text1)).setText(id);

                String songPath = musiccursor.getString(musiccursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));
                songList.get(position).setSongPath(songPath);


                music_column_index = musiccursor
                        .getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE);
                String size = " Size:" + String.format("%.2f", (Double.parseDouble(musiccursor.getString(music_column_index)) / (1024 * 1024))) + " MB";

                ((TextView) view.findViewById(R.id.text2)).setText(size);

                view.findViewById(R.id.check_icon).
                        setVisibility((songList.get(position).isChecked()) ? View.VISIBLE : View.INVISIBLE);
                return view;
            }catch (Exception e){
                e.printStackTrace();

            }

            return null;
        }
    }
}
