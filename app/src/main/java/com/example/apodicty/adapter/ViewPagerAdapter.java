package com.example.apodicty.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.apodicty.page.fragment.FavoriteFragment;
import com.example.apodicty.page.fragment.HomeFragment;
import com.example.apodicty.page.fragment.ProfileFragment;
import com.example.apodicty.page.fragment.SearchFragment;

public class ViewPagerAdapter extends FragmentStateAdapter {

    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new HomeFragment();
            case 1:
                return new SearchFragment();
            case 2:
                return new FavoriteFragment();
            case 3:
                return new ProfileFragment();
            default:
                return new HomeFragment(); // Default, should not happen
        }
    }

    @Override
    public int getItemCount() {
        return 4; // Ada 4 fragment
    }
}