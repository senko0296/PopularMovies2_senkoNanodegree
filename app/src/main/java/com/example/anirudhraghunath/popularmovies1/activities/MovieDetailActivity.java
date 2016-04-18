package com.example.anirudhraghunath.popularmovies1.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.example.anirudhraghunath.popularmovies1.R;
import com.example.anirudhraghunath.popularmovies1.fragments.MovieDetailFragment;
import com.example.anirudhraghunath.popularmovies1.utilities.Result;

public class MovieDetailActivity extends AppCompatActivity {

    private Result mMovieResults;
    private boolean local;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        mMovieResults = (Result) getIntent().getSerializableExtra(getString(R.string.detailsExtra));
        local = getIntent().getBooleanExtra("local", false);

        getSupportFragmentManager().beginTransaction().replace(R.id.movie_detail_frame, MovieDetailFragment.newInstance(mMovieResults, local))
                .commit();
    }
}
