package adnu.capstone.capstoneproject.PetOwner;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import adnu.capstone.capstoneproject.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link petRecords.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link petRecords#newInstance} factory method to
 * create an instance of this fragment.
 */
public class petRecords extends Fragment {

    public petRecords() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootview =  inflater.inflate(R.layout.fragment_pet_records, container, false);



        return rootview;
    }


}
