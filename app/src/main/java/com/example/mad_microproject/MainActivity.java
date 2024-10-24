package com.example.mad_microproject;  // Package declaration for organizing code.
import android.Manifest;  // Imports Manifest constants to handle permissions.
import android.content.Intent;  // Used to launch new activities.
import android.os.Bundle;  // Contains data about the previous activity state.
import android.os.Environment;  // Provides access to external storage paths.
import android.view.View;  // Used to handle UI elements' interaction.
import android.widget.AdapterView;  // Handles item clicks in AdapterView.
import android.widget.ArrayAdapter;  // Adapter to display an array of data.
import android.widget.ListView;  // ListView to display a list of items.
import android.widget.Toast;  // Displays short messages on the screen.

import androidx.appcompat.app.AppCompatActivity;  // Base class for activities with modern app features.
import androidx.core.graphics.Insets;  // Handles window insets (e.g., system bars).
import androidx.core.view.ViewCompat;  // Provides compatibility for View operations.
import androidx.core.view.WindowInsetsCompat;  // Handles window insets across API levels.

import com.karumi.dexter.Dexter;  // Dexter library for managing runtime permissions.
import com.karumi.dexter.PermissionToken;  // Token to handle permission re-requests.
import com.karumi.dexter.listener.PermissionDeniedResponse;  // Listener for denied permissions.
import com.karumi.dexter.listener.PermissionGrantedResponse;  // Listener for granted permissions.
import com.karumi.dexter.listener.PermissionRequest;  // Represents a permission request.
import com.karumi.dexter.listener.single.PermissionListener;  // Handles single permission request events.

import java.io.File;  // Used to work with files and directories.
import java.util.ArrayList;  // ArrayList to store dynamic collections of songs.

public class MainActivity extends AppCompatActivity {  // Main activity class extending AppCompatActivity.

    @Override
    protected void onCreate(Bundle savedInstanceState) {  // Called when the activity is created.
        ListView listView;  // Declares a ListView variable.
        super.onCreate(savedInstanceState);  // Calls the parent class's onCreate method.
        setContentView(R.layout.activity_main);  // Sets the activity's layout.
        listView = findViewById(R.id.listView);  // Finds the ListView by its ID.

        Dexter.withContext(this)  // Initializes Dexter to manage permissions.
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)  // Requests permission to read external storage.
                .withListener(new PermissionListener() {  // Sets the listener for permission results.
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {  // If permission is granted.

                        ArrayList<File> mysongs = fetchSongs(Environment.getExternalStorageDirectory());  // Fetches all songs.
                        String[] items = new String[mysongs.size()];  // Creates an array to store song names.
                        for (int i = 0; i < mysongs.size(); i++) {  // Iterates over the songs.
                            items[i] = mysongs.get(i).getName().replace(".mp3", "");  // Removes ".mp3" extension.
                        }
                        // Sets up an adapter to display the song list.
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_list_item_1, items);
                        listView.setAdapter(adapter);  // Binds the adapter to the ListView.

                        // Handles clicks on list items.
                        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                                Intent intent = new Intent(MainActivity.this, PlaySong.class);  // Intent to launch PlaySong activity.
                                String currentSong = listView.getItemAtPosition(position).toString();  // Gets the selected song name.
                                intent.putExtra("songList", mysongs);  // Passes the song list.
                                intent.putExtra("currentSong", currentSong);  // Passes the selected song.
                                intent.putExtra("position", position);  // Passes the position of the selected song.
                                startActivity(intent);  // Starts the PlaySong activity.
                            }
                        });
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {  // If permission is denied.

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest request, PermissionToken token) {
                        token.continuePermissionRequest();  // Re-requests the permission if denied previously.
                    }
                }).check();  // Executes the permission check.
    }

    // Recursively fetches all .mp3 files from the given directory.
    public ArrayList<File> fetchSongs(File file) {
        ArrayList<File> arrayList = new ArrayList<>();  // Stores the songs.
        File[] songs = file.listFiles();  // Lists all files in the directory.
        if (songs != null) {
            for (File myFile : songs) {  // Iterates through each file.
                if (!myFile.isHidden() && myFile.isDirectory()) {  // If it's a directory, search inside it.
                    arrayList.addAll(fetchSongs(myFile));  // Recursively add songs from subdirectories.
                } else if (myFile.getName().endsWith(".mp3") && !myFile.getName().startsWith(".")) {
                    arrayList.add(myFile);  // Adds the file if it's an mp3.
                }
            }
        }
        return arrayList;  // Returns the list of songs.
    }
}
