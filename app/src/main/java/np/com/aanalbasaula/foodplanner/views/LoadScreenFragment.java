package np.com.aanalbasaula.foodplanner.views;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import np.com.aanalbasaula.foodplanner.R;

/**
 * A simple {@link Fragment} subclass which shows a loading screen.
 * Use the {@link LoadScreenFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LoadScreenFragment extends Fragment {

    public LoadScreenFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment LoadScreenFragment.
     */
    public static LoadScreenFragment newInstance() {
        return new LoadScreenFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_load_screen, container, false);
    }
}
