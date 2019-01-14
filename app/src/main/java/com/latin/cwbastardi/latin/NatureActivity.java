package com.latin.cwbastardi.latin;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

public class NatureActivity extends AppCompatActivity {

    //global reference to media player for playing audio files
    private MediaPlayer mediaPlayer;

    //global reference to on completion listener for media player to avoid having
    //to recreate the object every time a sound file is clicked
    private MediaPlayer.OnCompletionListener mediaPlayerCompletionListener = new MediaPlayer.OnCompletionListener() {
        public void onCompletion(MediaPlayer mp) {
            releaseMediaPlayer(); // release media player resources on finish
        }
    };

    //global reference to audio manager for checking audio focus
    private AudioManager audioManager;
    //global reference to audio manager audio focus change listener for managing loss and gain of audio focus
    private AudioManager.OnAudioFocusChangeListener onAudioFocusChangeListener =
            new AudioManager.OnAudioFocusChangeListener() {
                public void onAudioFocusChange(int focusChange) {
                    if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
                        //permanent loss of audio focus
                        //stop playback and clean up media resources
                        releaseMediaPlayer();
                    }
                    else if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT || focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK) {
                        //temporary loss of audio focus
                        //pause playback
                        mediaPlayer.pause();
                        mediaPlayer.seekTo(0); //start at beginning of audio file
                    }
                    else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
                        // regained audio focus
                        // resume playback
                        mediaPlayer.start();
                    }
                }
            };

    @Override
    protected void onStop() {
        super.onStop();
        //release media resources when application is stopped so that it doesn't
        //continue playing any sound it might be in the middle of playing
        releaseMediaPlayer();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.word_list);

        //create and setup to request audio focus
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        final ArrayList<Word> words = new ArrayList<>();
        words.add(new Word("aestas", "summer", R.raw.aestas));
        words.add(new Word("animal", "animal", R.raw.animal));
        words.add(new Word("aqua", "water", R.raw.aqua));
        words.add(new Word("arbor", "tree", R.raw.arbor));
        words.add(new Word("caelum", "sky", R.raw.caelum));
        words.add(new Word("dea", "goddess", R.raw.dea));
        words.add(new Word("deus", "god", R.raw.deus));
        words.add(new Word("elephantus", "elephant", R.raw.elephantus));
        words.add(new Word("equus", "horse", R.raw.equus));
        words.add(new Word("ferrum", "iron", R.raw.ferrum));
        words.add(new Word("flumen", "river", R.raw.flumen));
        words.add(new Word("fructus", "fruit", R.raw.fructus));
        words.add(new Word("humus", "soil", R.raw.humus));
        words.add(new Word("insula", "island", R.raw.insula));
        words.add(new Word("litus", "coastline", R.raw.litus));
        words.add(new Word("luna", "moon", R.raw.luna));
        words.add(new Word("lux", "light", R.raw.lux));
        words.add(new Word("mons", "mountain", R.raw.mons));
        words.add(new Word("mors", "death", R.raw.mors));
        words.add(new Word("mundus", "world", R.raw.mundus));
        words.add(new Word("natura", "nature", R.raw.natura));
        words.add(new Word("nox", "nox", R.raw.nox));
        words.add(new Word("nubes", "cloud", R.raw.nubes));
        words.add(new Word("rosa", "rose", R.raw.rosa));
        words.add(new Word("sal", "salt", R.raw.sal));
        words.add(new Word("saxum", "stone", R.raw.saxum));
        words.add(new Word("sol", "sun", R.raw.sol));
        words.add(new Word("somnus", "sleep", R.raw.somnus));
        words.add(new Word("stella", "star", R.raw.stella));
        words.add(new Word("ventus", "wind", R.raw.ventus));
        words.add(new Word("vita", "life", R.raw.vita));

        WordAdapter wordAdapter = new WordAdapter(this, words, R.color.category_nature);

        ListView listView = findViewById(R.id.list);

        listView.setAdapter(wordAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView <?> parent, View view, int position, long id) {

                //release media player if currently exists because we are about to play a different sound file
                releaseMediaPlayer();
                // Request audio focus for playback
                int result = audioManager.requestAudioFocus(onAudioFocusChangeListener,
                        // Use the music stream.
                        AudioManager.STREAM_MUSIC,
                        // Request short temporary focus.
                        AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);

                if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                    // we have audio focus
                    mediaPlayer = MediaPlayer.create(NatureActivity.this, words.get(position).getAudioResource());
                    mediaPlayer.start();
                    //release media player resources after sound has finished playing
                    mediaPlayer.setOnCompletionListener(mediaPlayerCompletionListener);
                }
            }
        });
    }

    /**
     * Clean up the media player by releasing its resources.
     */
    private void releaseMediaPlayer() {
        // If the media player is not null, then it may be currently playing a sound.
        if (mediaPlayer != null) {
            // Regardless of the current state of the media player, release its resources
            // because we no longer need it.
            mediaPlayer.release();

            // Set the media player back to null. For our code, we've decided that
            // setting the media player to null is an easy way to tell that the media player
            // is not configured to play an audio file at the moment.
            mediaPlayer = null;

            // Abandon audio focus when on release
            audioManager.abandonAudioFocus(onAudioFocusChangeListener);
        }
    }
}

