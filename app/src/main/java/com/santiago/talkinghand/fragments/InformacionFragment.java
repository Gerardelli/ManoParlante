package com.santiago.talkinghand.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.Query;
import com.santiago.talkinghand.R;
import com.santiago.talkinghand.activities.VideoActivity;
import com.santiago.talkinghand.adapters.VideosAdapter;
import com.santiago.talkinghand.models.Video;
import com.santiago.talkinghand.providers.VideoProvider;

/**
 * A simple {@link Fragment} subclass.
 */
public class InformacionFragment extends Fragment {
    View mView;
    Toolbar mToolbar;
    FloatingActionButton mFabVideo;
    RecyclerView mRecyclerView;
    VideoProvider mVideoProvider;
    VideosAdapter mVideosAdapter;

    public InformacionFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_informacion, container, false);

        mToolbar = mView.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(mToolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Noticias");

        setHasOptionsMenu(true);

        mFabVideo = mView.findViewById(R.id.fabVideo);
        mFabVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                irAVideos();
            }
        });

        mRecyclerView = mView.findViewById(R.id.recyclerViewVideos);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(linearLayoutManager);

        mVideoProvider = new VideoProvider();

        return mView;
    }

    @Override
    public void onStart() {
        super.onStart();
        Query query = mVideoProvider.getTodos();
        FirestoreRecyclerOptions<Video> options = new FirestoreRecyclerOptions.Builder<Video>().
                setQuery(query, Video.class).build();
        mVideosAdapter = new VideosAdapter(options, getContext(), this.getLifecycle());
        mRecyclerView.setAdapter(mVideosAdapter);
        mVideosAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        mVideosAdapter.stopListening();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mVideosAdapter.getListener() != null){
            mVideosAdapter.getListener().remove();
        }
    }

    private void irAVideos() {
        Intent intent = new Intent(getContext(), VideoActivity.class);
        startActivity(intent);
    }
}