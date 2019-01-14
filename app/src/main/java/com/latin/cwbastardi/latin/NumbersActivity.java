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


public class NumbersActivity extends AppCompatActivity {

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
        words.add(new Word("unus", "one", R.raw.unus));
        words.add(new Word("duo", "two", R.raw.duo));
        words.add(new Word("tres", "three", R.raw.tres));
        words.add(new Word("quattuor", "four", R.raw.quattuor));
        words.add(new Word("quinque", "five", R.raw.quinque));
        words.add(new Word("sex", "six", R.raw.sex));
        words.add(new Word("septem", "seven", R.raw.septem));
        words.add(new Word("octo", "eight", R.raw.octo));
        words.add(new Word("novem", "nine", R.raw.novem));
        words.add(new Word("decem", "ten", R.raw.decem));

        WordAdapter wordAdapter = new WordAdapter(this, words, R.color.category_numbers);

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

                    // assign media resource to media player and start playback
                    mediaPlayer = MediaPlayer.create(NumbersActivity.this, words.get(position).getAudioResource());
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

            // Abandon audio focus on release
            audioManager.abandonAudioFocus(onAudioFocusChangeListener);
        }
    }
}
