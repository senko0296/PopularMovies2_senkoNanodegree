package com.example.anirudhraghunath.popularmovies1.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import com.example.anirudhraghunath.popularmovies1.R;
import com.example.anirudhraghunath.popularmovies1.fragments.DefaultFragment;
import com.example.anirudhraghunath.popularmovies1.fragments.MovieListFragment;

public class DashBoardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash_board);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportFragmentManager().beginTransaction().replace(R.id.movie_list_frame, new MovieListFragment()).commit();

        if (getResources().getBoolean(R.bool.is_tablet))
            getSupportFragmentManager().beginTransaction().replace(R.id.movie_detail_frame, new DefaultFragment()).commit();
    }
}
