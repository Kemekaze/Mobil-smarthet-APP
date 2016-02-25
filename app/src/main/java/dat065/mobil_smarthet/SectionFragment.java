package dat065.mobil_smarthet;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by elias on 2016-02-24.
 */
public class SectionFragment extends Fragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";
    private static final String ARG_SECTION_TITLE = "section_title";

    public SectionFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */

    public static SectionFragment newInstance(int sectionNumber, CharSequence title) {
        SectionFragment fragment = new SectionFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        args.putCharSequence(ARG_SECTION_TITLE,title);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_main_fragment, container, false);

        ImageView imageView = (ImageView) rootView.findViewById(R.id.section_icon);
        int imageResource = rootView.getContext().getResources().getIdentifier("section_" + getArguments().getInt(ARG_SECTION_NUMBER), "drawable", rootView.getContext().getPackageName());
        imageView.setImageResource(imageResource);
        TextView textView = (TextView) rootView.findViewById(R.id.preset);
        textView.setText(getArguments().getCharSequence(ARG_SECTION_TITLE).toString());
        return rootView;
    }
}
