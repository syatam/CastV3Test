package com.example.castv3test;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.cast.MediaInfo;
import com.google.android.gms.cast.MediaLoadOptions;
import com.google.android.gms.cast.MediaMetadata;
import com.google.android.gms.cast.framework.CastButtonFactory;
import com.google.android.gms.cast.framework.CastContext;
import com.google.android.gms.cast.framework.CastSession;
import com.google.android.gms.cast.framework.Session;
import com.google.android.gms.cast.framework.SessionManager;
import com.google.android.gms.cast.framework.SessionManagerListener;
import com.google.android.gms.cast.framework.media.RemoteMediaClient;
import com.google.android.gms.common.images.WebImage;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private CastContext castContext;

    private CastSession mCastSession;
    private SessionManager mSessionManager;
    private final SessionManagerListener mSessionManagerListener =
            new SessionManagerListenerImpl();
    RemoteMediaClient remoteMediaClient;

    private class SessionManagerListenerImpl implements SessionManagerListener {
        @Override
        public void onSessionStarting(Session session) {

        }

        @Override
        public void onSessionStarted(Session session, String sessionId) {
            invalidateOptionsMenu();
        }

        @Override
        public void onSessionStartFailed(Session session, int i) {

        }

        @Override
        public void onSessionEnding(Session session) {

        }

        @Override
        public void onSessionResumed(Session session, boolean wasSuspended) {
            invalidateOptionsMenu();
        }

        @Override
        public void onSessionResumeFailed(Session session, int i) {

        }

        @Override
        public void onSessionSuspended(Session session, int i) {

        }

        @Override
        public void onSessionEnded(Session session, int error) {

        }

        @Override
        public void onSessionResuming(Session session, String s) {

        }
    }

    private Button loadMedia;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mSessionManager = CastContext.getSharedInstance(this).getSessionManager();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        castContext = CastContext.getSharedInstance(this);

        loadMedia = findViewById(R.id.load_media);

        loadMedia.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                loadMedia();
            }
        });
    }

    @Override
    protected void onResume() {
        mCastSession = mSessionManager.getCurrentCastSession();
        mSessionManager.addSessionManagerListener(mSessionManagerListener);
        super.onResume();
    }
    @Override
    protected void onPause() {
        super.onPause();
        mSessionManager.removeSessionManagerListener(mSessionManagerListener);
        mCastSession = null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu, menu);
        CastButtonFactory.setUpMediaRouteButton(getApplicationContext(),
                menu,
                R.id.media_route_menu_item);
        return true;
    }

    public void loadMedia(){

        mCastSession = CastContext.getSharedInstance(this).getSessionManager()
                .getCurrentCastSession();



       MediaMetadata movieMetadata = new MediaMetadata(MediaMetadata.MEDIA_TYPE_MOVIE);

        String imgURL = "https://commondatastorage.googleapis.com/gtv-videos-bucket/CastVideos/images/780x1200/DesigningForGoogleCast-887x1200.jpg";
        String mediaURL = "https://commondatastorage.googleapis.com/gtv-videos-bucket/CastVideos/mp4/DesigningForGoogleCast.mp4";

        movieMetadata.putString(MediaMetadata.KEY_TITLE, "Test");
        movieMetadata.putString(MediaMetadata.KEY_SUBTITLE, "Subtitle");
        movieMetadata.addImage(new WebImage(Uri.parse(imgURL)));
        movieMetadata.addImage(new WebImage(Uri.parse(imgURL)));

        MediaInfo mediaInfo = new MediaInfo.Builder(mediaURL)
                .setStreamType(MediaInfo.STREAM_TYPE_BUFFERED)
                .setContentType("videos/mp4")
                .setMetadata(movieMetadata)
                .build();

        MediaLoadOptions mediaLoadOptions = new MediaLoadOptions.Builder().setAutoplay(true).build();


        if(mCastSession !=null){
            remoteMediaClient = mCastSession.getRemoteMediaClient();

            remoteMediaClient.addListener(new RemoteMediaClient.Listener() {
                @Override
                public void onStatusUpdated() {
                    Intent intent = new Intent(MainActivity.this, MyExpandedControlsActivity.class);
                    startActivity(intent);
                    remoteMediaClient.removeListener(this);
                }

                @Override
                public void onMetadataUpdated() {
                }

                @Override
                public void onQueueStatusUpdated() {
                }

                @Override
                public void onPreloadStatusUpdated() {
                }

                @Override
                public void onSendingRemoteMediaRequest() {
                }

                @Override
                public void onAdBreakStatusUpdated() {

                }
            });

            if(remoteMediaClient !=null)
                remoteMediaClient.load(mediaInfo,mediaLoadOptions);
            else
                Log.e(TAG, "remotemediaclient: " + remoteMediaClient);
        }

        else
            Log.e(TAG, "mCastSession: " + mCastSession);

    }




}
