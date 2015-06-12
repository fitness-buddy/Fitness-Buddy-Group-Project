package com.strengthcoach.strengthcoach.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.strengthcoach.strengthcoach.R;
import com.strengthcoach.strengthcoach.adapters.TrainerListAdapter;
import com.strengthcoach.strengthcoach.models.Trainer;
import com.twotoasters.jazzylistview.effects.TiltEffect;
import com.twotoasters.jazzylistview.recyclerview.JazzyRecyclerViewScrollListener;

import java.util.ArrayList;
import java.util.List;

public class TrainersListFragment extends Fragment {
    private RecyclerView recyclerView;
    private TrainerListAdapter adapter;
    private List<Trainer> trainers;

    // Get the list of trainers and update the view
    public void setItems(List<Trainer> trainers) {
        for(Trainer trainer : trainers) {
            this.trainers.add(trainer);
        }
        adapter.notifyDataSetChanged();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_trainers_list, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.rvTrainersList);

        // Setup animation
        JazzyRecyclerViewScrollListener listener = new JazzyRecyclerViewScrollListener();
        listener.setTransitionEffect(new TiltEffect());
        recyclerView.setOnScrollListener(listener);

        // Initialize empty list
        this.trainers = new ArrayList<>();
        adapter = new TrainerListAdapter(getActivity(), trainers);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}
