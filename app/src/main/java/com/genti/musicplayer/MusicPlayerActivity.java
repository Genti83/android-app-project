package com.genti.musicplayer;

import android.Manifest;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.media.audiofx.Visualizer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.media.MediaMetadataRetriever;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MusicPlayerActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 100;

    private ImageView albumArt;
    private TextView tvFilename, tvTitle, tvArtist, tvAlbum, tvYear, tvGenre, tvNext;
    private TextView tvKbps, tvType, tvRepeat, tvTrack, tvKhz;
    private TextView tvCurrentTime, tvTotalTime;
    private SeekBar seekBar;
    private AudioVisualizerView visualizerView;
    private Button btnMenu, btnPrev, btnPause, btnNext, btnClose;

    private MediaPlayer mediaPlayer;
    private Visualizer visualizer;
    private Handler handler;
    private List<SongInfo> songList;
    private int currentSongIndex = 0;
    private boolean isPlaying = false;
    private int repeatMode = 2; // 0=OFF, 1=ONE, 2=ALL

    private final Runnable updateSeekBar = new Runnable() {
        @Override
        public void run() {
            if (mediaPlayer != null && isPlaying) {
                int currentPosition = mediaPlayer.getCurrentPosition();
                int duration = mediaPlayer.getDuration();
                seekBar.setMax(duration);
                seekBar.setProgress(currentPosition);
                tvCurrentTime.setText(formatTime(currentPosition));
                tvTotalTime.setText(formatTime(duration));
                handler.postDelayed(this, 500);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_player);

        initViews();
        setupListeners();

        handler = new Handler(Looper.getMainLooper());
        songList = new ArrayList<>();

        if (checkPermissions()) {
            loadSongs();
        } else {
            requestPermissions();
        }
    }

    private void initViews() {
        albumArt = findViewById(R.id.albumArt);
        tvFilename = findViewById(R.id.tvFilename);
        tvTitle = findViewById(R.id.tvTitle);
        tvArtist = findViewById(R.id.tvArtist);
        tvAlbum = findViewById(R.id.tvAlbum);
        tvYear = findViewById(R.id.tvYear);
        tvGenre = findViewById(R.id.tvGenre);
        tvNext = findViewById(R.id.tvNext);
        tvKbps = findViewById(R.id.tvKbps);
        tvType = findViewById(R.id.tvType);
        tvRepeat = findViewById(R.id.tvRepeat);
        tvTrack = findViewById(R.id.tvTrack);
        tvKhz = findViewById(R.id.tvKhz);
        tvCurrentTime = findViewById(R.id.tvCurrentTime);
        tvTotalTime = findViewById(R.id.tvTotalTime);
        seekBar = findViewById(R.id.seekBar);
        visualizerView = findViewById(R.id.visualizer);
        btnMenu = findViewById(R.id.btnMenu);
        btnPrev = findViewById(R.id.btnPrev);
        btnPause = findViewById(R.id.btnPause);
        btnNext = findViewById(R.id.btnNext);
        btnClose = findViewById(R.id.btnClose);

        // Enable marquee
        tvFilename.setSelected(true);
    }

    private void setupListeners() {
        btnPause.setOnClickListener(v -> togglePlayPause());
        btnNext.setOnClickListener(v -> playNext());
        btnPrev.setOnClickListener(v -> playPrevious());
        btnClose.setOnClickListener(v -> finish());
        btnMenu.setOnClickListener(v -> showMenu());

        tvRepeat.setOnClickListener(v -> toggleRepeat());

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser && mediaPlayer != null) {
                    mediaPlayer.seekTo(progress);
                    tvCurrentTime.setText(formatTime(progress));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    private void loadSongs() {
        songList.clear();
        ContentResolver contentResolver = getContentResolver();
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.YEAR,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.DISPLAY_NAME
        };

        String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";
        String sortOrder = MediaStore.Audio.Media.TITLE + " ASC";

        Cursor cursor = contentResolver.query(uri, projection, selection, null, sortOrder);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                SongInfo song = new SongInfo();
                song.id = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID));
                song.title = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE));
                song.artist = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));
                song.album = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM));
                song.year = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.YEAR));
                song.path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));
                song.duration = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION));
                song.displayName = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME));
                songList.add(song);
            } while (cursor.moveToNext());
            cursor.close();
        }

        if (!songList.isEmpty()) {
            playSong(0);
        } else {
            Toast.makeText(this, "No music files found", Toast.LENGTH_LONG).show();
        }
    }

    private void playSong(int index) {
        if (songList.isEmpty()) return;

        currentSongIndex = index;
        SongInfo song = songList.get(currentSongIndex);

        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
        if (visualizer != null) {
            visualizer.release();
            visualizer = null;
        }

        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(song.path);
            mediaPlayer.prepare();
            mediaPlayer.start();
            isPlaying = true;
            btnPause.setText("PAUSE");

            updateSongInfo(song);
            setupVisualizer();
            handler.post(updateSeekBar);

            mediaPlayer.setOnCompletionListener(mp -> onSongComplete());
        } catch (Exception e) {
            Toast.makeText(this, "Error playing: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void updateSongInfo(SongInfo song) {
        tvFilename.setText(song.displayName != null ? song.displayName : song.title);
        tvTitle.setText("Title:  " + (song.title != null ? song.title : "Unknown"));
        tvArtist.setText("Artist:  " + (song.artist != null ? song.artist : "Unknown"));
        tvAlbum.setText("Album:  " + (song.album != null ? song.album : "Unknown"));
        tvYear.setText("Year:  " + (song.year != null ? song.year : "Unknown"));
        tvGenre.setText("Genre:  Unknown");

        // Show next song
        int nextIndex = (currentSongIndex + 1) % songList.size();
        SongInfo nextSong = songList.get(nextIndex);
        tvNext.setText("NEXT:  " + (nextSong.displayName != null ? nextSong.displayName : nextSong.title));

        // Track info
        tvTrack.setText((currentSongIndex + 1) + "/" + songList.size());

        // Get actual metadata using MediaMetadataRetriever
        String bitrate = "N/A";
        String sampleRate = "N/A";
        String genre = "Unknown";
        try {
            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            retriever.setDataSource(song.path);
            String bitrateStr = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_BITRATE);
            if (bitrateStr != null) {
                bitrate = String.valueOf(Integer.parseInt(bitrateStr) / 1000);
            }
            String genreStr = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_GENRE);
            if (genreStr != null) {
                genre = genreStr;
            }
            String mimeType = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_MIMETYPE);
            if (mimeType != null && mimeType.contains("flac")) {
                sampleRate = "96";
            } else {
                sampleRate = "44";
            }
            retriever.release();
        } catch (Exception e) {
            Log.w("MusicPlayer", "Could not retrieve metadata: " + e.getMessage());
        }

        tvKbps.setText(bitrate);
        tvKhz.setText(sampleRate);
        tvGenre.setText("Genre:  " + genre);

        // Determine file type
        String ext = "MP3";
        if (song.path != null) {
            int dotIndex = song.path.lastIndexOf('.');
            if (dotIndex > 0) {
                ext = song.path.substring(dotIndex + 1).toUpperCase(Locale.ROOT);
            }
        }
        tvType.setText(ext);
    }

    private void setupVisualizer() {
        if (mediaPlayer == null) return;

        try {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                    == PackageManager.PERMISSION_GRANTED) {
                visualizer = new Visualizer(mediaPlayer.getAudioSessionId());
                visualizer.setCaptureSize(Visualizer.getCaptureSizeRange()[1]);
                visualizer.setDataCaptureListener(new Visualizer.OnDataCaptureListener() {
                    @Override
                    public void onWaveFormDataCapture(Visualizer vis, byte[] waveform, int samplingRate) {
                    }

                    @Override
                    public void onFftDataCapture(Visualizer vis, byte[] fft, int samplingRate) {
                        visualizerView.updateFFT(fft);
                    }
                }, Visualizer.getMaxCaptureRate() / 2, false, true);
                visualizer.setEnabled(true);
            }
        } catch (Exception e) {
            Log.w("MusicPlayer", "Visualizer not available: " + e.getMessage());
        }
    }

    private void togglePlayPause() {
        if (mediaPlayer == null) return;

        if (isPlaying) {
            mediaPlayer.pause();
            isPlaying = false;
            btnPause.setText("PLAY");
            handler.removeCallbacks(updateSeekBar);
        } else {
            mediaPlayer.start();
            isPlaying = true;
            btnPause.setText("PAUSE");
            handler.post(updateSeekBar);
        }
    }

    private void playNext() {
        if (songList.isEmpty()) return;
        int nextIndex = (currentSongIndex + 1) % songList.size();
        playSong(nextIndex);
    }

    private void playPrevious() {
        if (songList.isEmpty()) return;
        int prevIndex = currentSongIndex - 1;
        if (prevIndex < 0) prevIndex = songList.size() - 1;
        playSong(prevIndex);
    }

    private void onSongComplete() {
        switch (repeatMode) {
            case 0: // OFF
                if (currentSongIndex < songList.size() - 1) {
                    playNext();
                } else {
                    isPlaying = false;
                    btnPause.setText("PLAY");
                }
                break;
            case 1: // ONE
                playSong(currentSongIndex);
                break;
            case 2: // ALL
                playNext();
                break;
        }
    }

    private void toggleRepeat() {
        repeatMode = (repeatMode + 1) % 3;
        switch (repeatMode) {
            case 0:
                tvRepeat.setText("OFF");
                break;
            case 1:
                tvRepeat.setText("ONE");
                break;
            case 2:
                tvRepeat.setText("ALL");
                break;
        }
    }

    private void showMenu() {
        Toast.makeText(this, "Song list: " + songList.size() + " songs", Toast.LENGTH_SHORT).show();
    }

    private String formatTime(int milliseconds) {
        int seconds = (milliseconds / 1000) % 60;
        int minutes = (milliseconds / 1000) / 60;
        return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
    }

    private boolean checkPermissions() {
        if (android.os.Build.VERSION.SDK_INT >= 33) {
            return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_AUDIO)
                    == PackageManager.PERMISSION_GRANTED;
        }
        return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions() {
        if (android.os.Build.VERSION.SDK_INT >= 33) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_MEDIA_AUDIO, Manifest.permission.RECORD_AUDIO},
                    PERMISSION_REQUEST_CODE);
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO},
                    PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                loadSongs();
            } else {
                Toast.makeText(this, "Permission denied. Cannot access music files.", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(updateSeekBar);
        if (visualizer != null) {
            visualizer.release();
        }
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
    }
}
