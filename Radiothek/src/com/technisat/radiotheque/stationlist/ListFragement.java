package com.technisat.radiotheque.stationlist;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.technisat.radiothek.R;
import com.technisat.radiotheque.player.PlayerFragement;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass. Activities that
 * contain this fragment must implement the
 * {@link PlayerFragement.OnStationDetailListener} interface to handle
 * interaction events. Use the {@link PlayerFragement#newInstance} factory
 * method to create an instance of this fragment.
 * 
 */
public class ListFragement extends Fragment {

	public ListFragement(){}
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
  
        View rootView = inflater.inflate(R.layout.fragment_stationlist_list_fragement, container, false);
          
        return rootView;
    }
}