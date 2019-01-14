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

public class PeopleActivity extends AppCompatActivity {

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
        words.add(new Word("agricola", "farmer", R.raw.agricola));
        words.add(new Word("domina", "lady", R.raw.domina));
        words.add(new Word("dominus", "lord", R.raw.dominus));
        words.add(new Word("familia", "family", R.raw.familia));
        words.add(new Word("femina", "woman", R.raw.femina));
        words.add(new Word("filia", "daughter", R.raw.filia));
        words.add(new Word("filius", "son", R.raw.filius));
        words.add(new Word("frater", "brother", R.raw.frater));
        words.add(new Word("homo", "man", R.raw.homo));
        words.add(new Word("imperator", "emperor", R.raw.imperator));
        words.add(new Word("iudex", "judge", R.raw.iudex));
        words.add(new Word("magister", "master (male)", R.raw.magister));
        words.add(new Word("magistra", "master (female)", R.raw.magistra));
        words.add(new Word("maiores", "ancestors", R.raw.maiores));
        words.add(new Word("mater", "emperor", R.raw.mater));
        words.add(new Word("miles", "soldier", R.raw.miles));
        words.add(new Word("mulier", "woman", R.raw.mulier));
        words.add(new Word("nata", "daughter", R.raw.nata));
        words.add(new Word("nauta", "sailor", R.raw.nauta));
        words.add(new Word("nepos", "grandson", R.raw.nepos));
        words.add(new Word("orator", "speaker", R.raw.orator));
        words.add(new Word("pater", "father", R.raw.pater));
        words.add(new Word("philosopha", "philosopher (female)", R.raw.philosopha));
        words.add(new Word("philosophus", "philosopher (male)", R.raw.philosophus));
        words.add(new Word("poeta", "poet", R.raw.poeta));
        words.add(new Word("puella", "girl", R.raw.puella));
        words.add(new Word("puer", "boy", R.raw.puer));
        words.add(new Word("regina", "queen", R.raw.regina));
        words.add(new Word("rex", "king", R.raw.rex));
        words.add(new Word("sacerdos", "priest", R.raw.sacerdos));
        words.add(new Word("scriptor", "writer", R.raw.scriptor));
        words.add(new Word("senex", "old man", R.raw.senex));
        words.add(new Word("soror", "sister", R.raw.soror));
        words.add(new Word("tyrannus", "tyrant", R.raw.tyrannus));
        words.add(new Word("uxor", "wife", R.raw.uxor));
        words.add(new Word("vir", "man", R.raw.vir));

        WordAdapter wordAdapter = new WordAdapter(this, words, R.color.category_people);

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
                    mediaPlayer = MediaPlayer.create(PeopleActivity.this, words.get(position).getAudioResource());
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
