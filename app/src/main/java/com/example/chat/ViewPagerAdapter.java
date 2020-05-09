package com.example.chat;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

public class ViewPagerAdapter extends FragmentStatePagerAdapter {
    FragmentManager fm;
    int behaviour;

    public ViewPagerAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
      this.behaviour=behavior;
      this.fm=fm;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
       switch (position)
       {
           case 0:ChatFragment tab1=new ChatFragment();
                        return tab1;
           case 1: GroupChatFragment tab2=new GroupChatFragment();
                            return  tab2;
           case 2: ContactFragment tab3=new ContactFragment();
                          return tab3;

       }
          return  null;
    }

    @Override
    public int getCount() {
        return behaviour;
    }
}
