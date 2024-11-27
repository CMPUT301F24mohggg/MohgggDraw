package com.example.mohgggdraw;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.List;

/***
 * Adapter for managing fragments in ViewPager2.
 */
public class TabFragmentAdapter extends FragmentStateAdapter {
    private final List<Fragment> fragmentList;

    public TabFragmentAdapter(@NonNull Fragment fragment, List<Fragment> fragmentList) {
        super(fragment);
        this.fragmentList = fragmentList;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return fragmentList.get(position);
    }

    @Override
    public int getItemCount() {
        return fragmentList.size();
    }
}
