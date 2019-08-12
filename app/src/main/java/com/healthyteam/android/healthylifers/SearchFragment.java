package com.healthyteam.android.healthylifers;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.healthyteam.android.healthylifers.R;

public class SearchFragment extends Fragment {

    private View view;
    private Spinner distances;
    private ArrayAdapter<CharSequence> adapter;
    private Fragment parrent;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_search,container,false);
        distances = view.findViewById(R.id.distances_spinner);
        adapter = ArrayAdapter.createFromResource(getContext(),R.array.distances_array,android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        distances.setAdapter(adapter);
        return view;
    }
}
