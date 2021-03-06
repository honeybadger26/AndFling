package com.example.andfling;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;

import com.example.andfling.database.AppDatabase;
import com.example.andfling.database.Message;
import com.example.andfling.database.Room;
import com.google.android.material.navigation.NavigationView;

import java.util.List;
import java.util.concurrent.Executors;


public class MainActivity extends AppCompatActivity {

    private Server server;
    private AppDatabase db;

    public Server getServer() {
        return server;
    }

    public AppDatabase getDb() {
        return db;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return false;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        NavController navController = navHostFragment.getNavController();
        NavigationView navView = findViewById(R.id.nav_view);
        NavigationUI.setupWithNavController(navView, navController);

        AppBarConfiguration appBarConfiguration =
                new AppBarConfiguration.Builder(R.id.chat_page_fragment, R.id.remote_page_fragment)
                        .setDrawerLayout(findViewById(R.id.drawer_layout)).build();
        NavigationUI.setupWithNavController(toolbar, navController, appBarConfiguration);

        server = new Server();
        db = AppDatabase.getInstance(this);

        server.setDb(db);
        db.messageDao().getAll(1).observe(this, server::setMessages);

        // Execute on first run TODO: Move elsewhere
        Executors.newSingleThreadExecutor().execute(() -> {
            List<Room> allRooms = db.roomDao().getAll();
            if (allRooms.isEmpty()) {
                Room defaultRoom = new Room();
                defaultRoom.name = "default";
                db.roomDao().insertAll(defaultRoom);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return false;
    }
}