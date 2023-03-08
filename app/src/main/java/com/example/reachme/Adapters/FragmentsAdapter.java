package com.example.reachme.Adapters;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.reachme.Fragments.CallsFragment;
import com.example.reachme.Fragments.ChatsFragment;
import com.example.reachme.Fragments.PostFragment;
import com.example.reachme.Fragments.SearchUserFragment;
import com.example.reachme.Fragments.NotificationFragment;

public class FragmentsAdapter extends FragmentPagerAdapter {
    public FragmentsAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    public FragmentsAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0: return new ChatsFragment();
            case 1: return new PostFragment();
            case 2: return new SearchUserFragment();
            case 3: return new CallsFragment();
            case 4: return new NotificationFragment();
            default:return new ChatsFragment();
        }
    }

    @Override
    public int getCount() {
        return 5;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        String title = null;
        if (position == 0){
            title = "CHATS";
        }else if (position == 1){
            title = "POSTS";
        }
        else if (position == 2){
            title = "FOLLOW";
        }
        else if(position==3){
            title = "CALLS";
        }
        else if(position==4){
            title = "ALERTS";
        }
        return title;
    }
}
