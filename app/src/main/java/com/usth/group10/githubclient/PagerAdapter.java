
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class PagerAdapter extends FragmentStatePagerAdapter {

    int mNoOfTabs;

    public PagerAdapter(FragmentManager fm, int NumberOfTabs){
        super(fm);
        this.mNoOfTabs = NumberOfTabs;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                Readme readme = new Readme();
                return readme;
            case 1:
                Files files = new Files();
                return files;
            case 2:
                Commits commits = new Commits();
                return commits;
            case 3:
                Release release = new Release();
                return release;
            case 4:
                Contributions contributions = new Contributions();
                return contributions;
            default: return null;
        }
    }

    @Override
    public int getCount() {
        return mNoOfTabs;
    }
}
