package dat065.mobil_smarthet;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;
import android.view.ViewGroup;

import dat065.mobil_smarthet.constants.Presets;

/**
 * Created by elias on 2016-02-24.
 */
public class SectionsAdapter extends FragmentPagerAdapter {

    public SectionsAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment (defined as a static inner class below).
        Log.i("adapter", "adaper");
        return SectionFragment.newInstance(position,getPageTitle(position));
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        super.setPrimaryItem(container, position, object);
    }

    @Override
    public int getCount() {
        // Show 3 total pages.
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return Presets.FAVOURITES.getKey();
            case 1:
                return Presets.SLEEP.getKey();
            case 2:
                return Presets.WORK.getKey();
        }
        return null;
    }
}