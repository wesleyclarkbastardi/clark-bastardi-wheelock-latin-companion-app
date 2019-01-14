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

public class VerbsActivity extends AppCompatActivity {

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
        words.add(new Word("adiuvo", "to help", R.raw.adiuvo));
        words.add(new Word("ambulo", "to walk", R.raw.ambulo));
        words.add(new Word("appello", "to call", R.raw.appello));
        words.add(new Word("bibo", "to drink", R.raw.bibo));
        words.add(new Word("capio", "to take", R.raw.capio));
        words.add(new Word("cedo", "to withdraw", R.raw.cedo));
        words.add(new Word("cognosco", "to learn", R.raw.cognosco));
        words.add(new Word("debeo", "to have to or to owe", R.raw.debeo));
        words.add(new Word("decerno", "to decide", R.raw.decerno));
        words.add(new Word("defendo", "to protect", R.raw.defendo));
        words.add(new Word("dico", "to say", R.raw.dico));
        words.add(new Word("eo", "to go", R.raw.eo));
        words.add(new Word("eripio", "to rescue", R.raw.eripio));
        words.add(new Word("erro", "to wander or to be wrong", R.raw.erro));
        words.add(new Word("expono", "to explain", R.raw.expono));
        words.add(new Word("facio", "to do or to make", R.raw.facio));
        words.add(new Word("fero", "to carry or to bring", R.raw.fero));
        words.add(new Word("fugio", "to flee", R.raw.fugio));
        words.add(new Word("habeo", "to have", R.raw.habeo));
        words.add(new Word("intellego", "to understand", R.raw.intellego));
        words.add(new Word("lego", "to read", R.raw.lego));
        words.add(new Word("metuo", "to fear", R.raw.metuo));
        words.add(new Word("nascor", "to be born", R.raw.nascor));
        words.add(new Word("possum", "to be able to", R.raw.possum));
        words.add(new Word("scio", "to know", R.raw.scio));
        words.add(new Word("scribo", "to write", R.raw.scribo));
        words.add(new Word("sum", "to be", R.raw.sum));
        words.add(new Word("volo", "to want", R.raw.volo));


        WordAdapter wordAdapter = new WordAdapter(this, words, R.color.category_verbs);

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
                    mediaPlayer = MediaPlayer.create(VerbsActivity.this, words.get(position).getAudioResource());
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

