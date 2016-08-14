package com.bignerdranch.android.newprojectdemo;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;

/**
 * Created by simon on 6/29/16.
 */
public class ViewPagerAdapter extends FragmentStatePagerAdapter {

    private ImageGalleryFragment imageGalleryFragment;
    private AudioGalleryFragment audioGalleryFragment;
    private VideoGalleryFragment videoGalleryFragment;

    public ImageGalleryFragment getImageGalleryFragment(){
        return imageGalleryFragment;
    }

    public AudioGalleryFragment getAudioGalleryFragment(){
        return audioGalleryFragment;
    }

    public VideoGalleryFragment getVideoGalleryFragment(){
        return videoGalleryFragment;
    }

    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        Log.e("Position", "Position: "+position);
        if(position == 0) {
            imageGalleryFragment = new ImageGalleryFragment();
            return imageGalleryFragment;
        }
        else if(position == 1) {
            audioGalleryFragment = new AudioGalleryFragment();
            return audioGalleryFragment;
        }
        else if(position == 2) {
            videoGalleryFragment = new VideoGalleryFragment();
            return  videoGalleryFragment;
        }
        else return new TabFragment(position);    // Which Fragment should be dislpayed by the viewpager for the given position
        // In my case we are showing up only one fragment in all the three tabs so we are
        // not worrying about the position and just returning the TabFragment
    }

    @Override
    public int getCount() {
        return 3;           // As there are only 3 Tabs
    }


//    @Override
//    public CharSequence getPageTitle(int position) {
//        return super.getPageTitle(position);
//    }
}
