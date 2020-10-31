package com.santiago.talkinghand.adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleRegistry;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;
import com.santiago.talkinghand.R;
import com.santiago.talkinghand.models.Like;
import com.santiago.talkinghand.models.Video;
import com.santiago.talkinghand.providers.AuthProvider;
import com.santiago.talkinghand.providers.LikesProvider;
import com.santiago.talkinghand.providers.UsuarioProvider;

import java.util.Date;

public class VideosAdapter extends FirestoreRecyclerAdapter<Video, VideosAdapter.ViewHolder> {
    Context context;
    UsuarioProvider mUsuarioProvider;
    LikesProvider mLikesProvider;
    AuthProvider mAuthProvider;
    ListenerRegistration mListener;
    Lifecycle lifecycle;

    public VideosAdapter(FirestoreRecyclerOptions<Video> options, Context context, Lifecycle lifecycle){
        super(options);
        this.context = context;
        mUsuarioProvider = new UsuarioProvider();
        mLikesProvider = new LikesProvider();
        mAuthProvider = new AuthProvider();
        this.lifecycle = lifecycle;
    }

    @Override
    protected void onBindViewHolder(@NonNull final ViewHolder holder, int position, @NonNull final Video video) {
        DocumentSnapshot document = getSnapshots().getSnapshot(position);
        final String videoId = document.getId();
        holder.txtDescripcionVideo.setText(video.getDescripcion());

        if(video.getVideo() != null){
            if(!video.getVideo().isEmpty()){
                /**
                holder.videoViewVideo.setVideoURI(Uri.parse(video.getVideo()));
                holder.videoViewVideo.seekTo(1);
                holder.videoViewVideo.resume();*/
                lifecycle.addObserver(holder.youTubePlayerView);
                holder.youTubePlayerView.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
                    @Override
                    public void onReady(YouTubePlayer youTubePlayer) {
                        super.onReady(youTubePlayer);
                        youTubePlayer.loadVideo(video.getVideo(), 0);
                        youTubePlayer.mute();
                    }
                });
            }
        }

        holder.imageViewLikes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Like like = new Like();
                like.setIdUsuario(mAuthProvider.getUid());
                like.setIdPublicacion(videoId);
                like.setTimeStamp(new Date().getTime());
                like(like, holder);
            }
        });

        getUsuarioInfo(video.getIdUsuario(), holder);
        getNumberLikesByVideo(videoId, holder);
        verificarLike(videoId, mAuthProvider.getUid(), holder);
    }

    private void verificarLike(String videoId, String uid, final ViewHolder holder) {
        mLikesProvider.getLikeByPublicacionAndUsuario(videoId, uid).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                int numberDocuments = queryDocumentSnapshots.size();
                if(numberDocuments > 0){
                    holder.imageViewLikes.setImageResource(R.drawable.icon_like_blue);
                }else{
                    holder.imageViewLikes.setImageResource(R.drawable.icon_like_gray);
                }
            }
        });
    }

    private void getNumberLikesByVideo(String videoId, final ViewHolder holder) {
        mListener = mLikesProvider.getLikesByPublicacion(videoId).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if(queryDocumentSnapshots != null){
                    int numberLikes = queryDocumentSnapshots.size();
                    holder.txtLikes.setText(String.valueOf(numberLikes) + " Me gusta");
                }
            }
        });
    }

    private void like(final Like like, final ViewHolder holder) {
        mLikesProvider.getLikeByPublicacionAndUsuario(like.getIdPublicacion(), mAuthProvider.getUid()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                int numberDocuments = queryDocumentSnapshots.size();
                if(numberDocuments > 0){
                    String idLike = queryDocumentSnapshots.getDocuments().get(0).getId();
                    holder.imageViewLikes.setImageResource(R.drawable.icon_like_gray);
                    mLikesProvider.delete(idLike);
                }else{
                    holder.imageViewLikes.setImageResource(R.drawable.icon_like_blue);
                    mLikesProvider.create(like);
                }
            }
        });
    }

    private void getUsuarioInfo(String idUsuario, final ViewHolder holder) {
        mUsuarioProvider.getUsuario(idUsuario).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()){
                    if(documentSnapshot.contains("usuario")){
                        String username = documentSnapshot.getString("usuario");
                        holder.txtUsuario.setText(username);
                    }
                }
            }
        });
    }

    public ListenerRegistration getListener(){
        return mListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_videos, parent, false);

        return new ViewHolder(view);
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView txtDescripcionVideo;
        TextView txtUsuario;
        TextView txtLikes;
        YouTubePlayerView youTubePlayerView;
        ImageView imageViewLikes;
        View viewHolder;

        public ViewHolder(View view){
            super(view);
            txtDescripcionVideo = view.findViewById(R.id.txtVideoCard);
            txtUsuario = view.findViewById(R.id.txtUsuarioVideoCard);
            txtLikes = view.findViewById(R.id.txtLike);
            youTubePlayerView = view.findViewById(R.id.youtube_player_view);
            imageViewLikes = view.findViewById(R.id.imageViewLike);

            viewHolder = view;
        }
    }
}
