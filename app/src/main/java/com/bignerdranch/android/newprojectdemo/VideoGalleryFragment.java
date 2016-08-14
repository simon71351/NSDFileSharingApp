package com.bignerdranch.android.newprojectdemo;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by nyanye on 7/2/16.
 */
public class VideoGalleryFragment extends Fragment {

    ListView musiclist;
    Cursor videocursor;
    Cursor videoThumnailCursor;
    int music_column_index;
    int count;
    private MusicAdapter musicAdapter;
    private ArrayList<SongModel> songList;
    private ArrayList<Integer> selectedPos;

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

            String[] proj = {MediaStore.Video.Media._ID,
                    MediaStore.Video.Media.DATA,
                    MediaStore.Video.Media.DISPLAY_NAME,
                    MediaStore.Video.Media.SIZE,
                    MediaStore.Video.Media.MINI_THUMB_MAGIC
            };

            videocursor = getActivity().getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                    proj, MediaStore.Video.Media.DATA + " not like ?", new String[]{"%m4a"}, MediaStore.Video.Media._ID);

            String[] proj1 = {MediaStore.Video.Thumbnails._ID,
                    MediaStore.Video.Thumbnails.DATA};

            videoThumnailCursor = getActivity().getContentResolver().query(MediaStore.Video.Thumbnails.EXTERNAL_CONTENT_URI,
                    proj1, null, null, MediaStore.Video.Thumbnails._ID);

            //"LOWER("+MediaStore.Audio.Media.TITLE+") ASC"
            count = videocursor.getCount();

            Log.e("Video Cursor", "Video Cursor Loaded: "+videocursor.getCount());
            Log.e("Video Thumbnail Cursor", "Video Cursor Loaded: "+videoThumnailCursor.getCount());

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
            musiclist.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {


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
            });
        }catch(Exception e){
            e.printStackTrace();
        }

        return view;
    }





    private AdapterView.OnItemClickListener musicgridlistener = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView parent, View v, int position,
                                long id) {
            System.gc();
            music_column_index = videocursor
                    .getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
            videocursor.moveToPosition(position);
            String filename = videocursor.getString(music_column_index);

            Intent intent = new Intent();
            intent.setAction(android.content.Intent.ACTION_VIEW);
            File file = new File(filename);
            intent.setDataAndType(Uri.fromFile(file), "video/*");
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

                    view = getLayoutInflater(null).inflate(R.layout.video_gallery_item, parent, false);


                } else {
                    view = convertView;
                }

                music_column_index = videocursor
                        .getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME);

                videocursor.moveToPosition(position);
                String id = videocursor.getString(music_column_index);

                ((TextView) view.findViewById(R.id.text1)).setText(id);

                String songPath = videocursor.getString(videocursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA));
                songList.get(position).setSongPath(songPath);

                music_column_index = videocursor
                        .getColumnIndexOrThrow(MediaStore.Video.Media.SIZE);
                String size = " Size:" + String.format("%.2f", (Double.parseDouble(videocursor.getString(music_column_index)) / (1024 * 1024))) + " MB";

                ((TextView) view.findViewById(R.id.text2)).setText(size);


                videoThumnailCursor.moveToPosition(position);
                String thumbnailPath = videoThumnailCursor.getString(videocursor.getColumnIndex(MediaStore.Video.Media.DATA));

                Bitmap thumb = ThumbnailUtils.createVideoThumbnail(thumbnailPath,
                        MediaStore.Images.Thumbnails.MINI_KIND);
                Log.e("Thumpnial path", "Path: "+thumbnailPath);
                Log.e("Bitmap thumbnail", "Thumbnail: "+thumb);

                BitmapFactory.Options options = new BitmapFactory.Options();
                Bitmap thumbnailImage = BitmapFactory.decodeFile(thumbnailPath, options);
//
//                Bitmap thumbnail = MediaStore.Video.Thumbnails.getThumbnail(getActivity().getContentResolver(),
//                        Long.parseLong(thumbnailID), MediaStore.Video.Thumbnails.MINI_KIND, options);

//                Uri uri = Uri.withAppendedPath(
//                        MediaStore.Video.Thumbnails.EXTERNAL_CONTENT_URI, "" + thumbnailID);

                ((ImageView) view.findViewById(R.id.video_thumbnail)).setImageBitmap(thumbnailImage);

                view.findViewById(R.id.check_icon).
                        setVisibility((songList.get(position).isChecked()) ? View.VISIBLE : View.INVISIBLE);
                return view;
            }catch (Exception e){
                e.printStackTrace();

            }

            return null;
        }
    }

    public String getPath(Uri uri) {
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = getActivity().managedQuery(uri, projection, null, null, null);
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }
}
