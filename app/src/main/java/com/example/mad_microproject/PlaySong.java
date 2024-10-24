package com.example.mad_microproject; // Package declaration to group related classes.
import android.content.Intent; // Handles launching new activities or passing data between activities.
import android.media.MediaPlayer; // Controls audio playback.
import android.net.Uri; // Used to handle URIs pointing to files.
import android.os.Bundle; // Contains data about the previous activity's state.
import android.view.View; // Handles interaction with UI elements.
import android.widget.ImageView; // Displays and controls play, next, and previous buttons.
import android.widget.SeekBar; // SeekBar to monitor and control audio playback progress.
import android.widget.TextView; // Displays the song title.

import androidx.activity.EdgeToEdge; // Manages edge-to-edge display of UI components.
import androidx.appcompat.app.AppCompatActivity; // Base class for activities with modern Android features.
import androidx.core.graphics.Insets; // Provides support for handling system bars (like status bar).
import androidx.core.view.ViewCompat; // Helps with backward-compatible View operations.
import androidx.core.view.WindowInsetsCompat; // Handles system window insets across Android versions.

import java.io.File; // Provides file handling capabilities.
import java.util.ArrayList; // Dynamic collection to store song files.

public class PlaySong extends AppCompatActivity { // Class for the song playback screen.

    @Override
    protected void onDestroy() { // Called when the activity is destroyed.
        super.onDestroy(); // Calls the parent class's onDestroy method.
        mediaPlayer.stop(); // Stops audio playback.
        mediaPlayer.release(); // Releases resources used by MediaPlayer.
        updateSeek.interrupt(); // Stops the SeekBar update thread.
    }

    TextView textView; // Displays the current song's title.
    ImageView play, previous, next; // Controls for play, previous, and next actions.
    ArrayList<File> songs; // List of song files.
    MediaPlayer mediaPlayer; // MediaPlayer instance to play audio.
    String textContent; // Stores the current song's title.
    int position; // Current position of the song in the list.
    SeekBar seekBar; // SeekBar to track and control song progress.
    Thread updateSeek; // Thread to update the SeekBar as the song plays.

    @Override
    protected void onCreate(Bundle savedInstanceState) { // Called when the activity is created.
        super.onCreate(savedInstanceState); // Calls the parent class's onCreate method.
        setContentView(R.layout.activity_play_song2); // Sets the layout for the activity.

        textView = findViewById(R.id.textView); // Links the TextView from the layout.
        play = findViewById(R.id.play); // Links the play button.
        previous = findViewById(R.id.previous); // Links the previous button.
        next = findViewById(R.id.next); // Links the next button.
        seekBar = findViewById(R.id.seekBar2); // Links the SeekBar.

        Intent intent = getIntent(); // Retrieves the intent that started this activity.
        Bundle bundle = intent.getExtras(); // Gets the extra data from the intent.
        songs = (ArrayList) bundle.getParcelableArrayList("songList"); // Retrieves the list of songs.
        textContent = intent.getStringExtra("currentSong"); // Gets the title of the current song.
        textView.setText(textContent); // Sets the song title in the TextView.
        textView.setSelected(true); // Enables marquee effect on the TextView.

        position = intent.getIntExtra("position", 0); // Retrieves the position of the selected song.
        Uri uri = Uri.parse(songs.get(position).toString()); // Converts the song's path to a URI.
        mediaPlayer = MediaPlayer.create(this, uri); // Creates a MediaPlayer instance for the selected song.
        mediaPlayer.start(); // Starts playing the song.
        seekBar.setMax(mediaPlayer.getDuration()); // Sets the maximum value of the SeekBar to the song's duration.

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() { // Listener for SeekBar changes.
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) { // Called when SeekBar progress changes.

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { // Called when user touches the SeekBar.

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { // Called when user releases the SeekBar.
                mediaPlayer.seekTo(seekBar.getProgress()); // Moves playback to the selected position.
            }
        });

        updateSeek = new Thread() { // Creates a new thread to update the SeekBar.
            @Override
            public void run() { // Thread's run method.
                int currentPosition = 0; // Tracks the current position of the song.
                try {
                    while (currentPosition < mediaPlayer.getDuration()) { // Updates while song is playing.
                        currentPosition = mediaPlayer.getCurrentPosition(); // Gets the current position.
                        seekBar.setProgress(currentPosition); // Updates the SeekBar's progress.
                        sleep(800); // Pauses for 800ms before updating again.
                    }
                } catch (Exception e) { // Catches any exceptions during thread execution.
                    e.printStackTrace(); // Prints the exception stack trace.
                }
            }
        };
        updateSeek.start(); // Starts the SeekBar update thread.

        play.setOnClickListener(new View.OnClickListener() { // Sets listener for play button.
            @Override
            public void onClick(View view) { // Called when play button is clicked.
                if (mediaPlayer.isPlaying()) { // Checks if the song is currently playing.
                    play.setImageResource(R.drawable.play); // Sets the icon to play.
                    mediaPlayer.pause(); // Pauses the song.
                } else {
                    play.setImageResource(R.drawable.pause); // Sets the icon to pause.
                    mediaPlayer.start(); // Resumes playing the song.
                }
            }
        });

        previous.setOnClickListener(new View.OnClickListener() { // Sets listener for previous button.
            @Override
            public void onClick(View view) { // Called when previous button is clicked.
                mediaPlayer.stop(); // Stops the current song.
                mediaPlayer.release(); // Releases resources used by MediaPlayer.
                if (position != 0) { // Checks if there is a previous song.
                    position = position - 1; // Moves to the previous song.
                } else {
                    position = songs.size() - 1; // Loops back to the last song if at the beginning.
                }
                Uri uri = Uri.parse(songs.get(position).toString()); // Gets the URI of the new song.
                mediaPlayer = MediaPlayer.create(getApplicationContext(), uri); // Creates a new MediaPlayer.
                mediaPlayer.start(); // Starts playing the new song.
                play.setImageResource(R.drawable.pause); // Sets the icon to pause.
                seekBar.setMax(mediaPlayer.getDuration()); // Updates the SeekBar max value.
                textContent = songs.get(position).getName(); // Gets the new song's title.
                textView.setText(textContent); // Displays the new song title.
            }
        });

        next.setOnClickListener(new View.OnClickListener() { // Sets listener for next button.
            @Override
            public void onClick(View view) { // Called when next button is clicked.
                mediaPlayer.stop(); // Stops the current song.
                mediaPlayer.release(); // Releases MediaPlayer resources.
                if (position != songs.size() - 1) { // Checks if there is a next song.
                    position = position + 1; // Moves to the next song.
                } else {
                    position = 0; // Loops back to the first song if at the end.
                }
                Uri uri = Uri.parse(songs.get(position).toString()); // Gets the URI of the new song.
                mediaPlayer = MediaPlayer.create(getApplicationContext(), uri); // Creates a new MediaPlayer instance.
                mediaPlayer.start(); // Starts playing the new song.
                play.setImageResource(R.drawable.pause); // Sets the icon to pause.
                seekBar.setMax(mediaPlayer.getDuration()); // Updates the SeekBar max value.
                textContent = songs.get(position).getName(); // Gets the new song's title.
                textView.setText(textContent); // Displays the new song title.
            }
        });
    }
}
