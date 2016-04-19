package com.example.anirudhraghunath.popularmovies1.fragments;


import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.anirudhraghunath.popularmovies1.R;
import com.example.anirudhraghunath.popularmovies1.activities.MovieDetailActivity;
import com.example.anirudhraghunath.popularmovies1.adapters.DashBoardGridAdapter;
import com.example.anirudhraghunath.popularmovies1.contracts.MoviesContract;
import com.example.anirudhraghunath.popularmovies1.network.APIClient;
import com.example.anirudhraghunath.popularmovies1.utilities.Movies;
import com.example.anirudhraghunath.popularmovies1.utilities.Result;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class MovieListFragment extends Fragment {

    GridView moviesGridView;
    ProgressDialog pgDialog;
    boolean isTablet, local;
    private DashBoardGridAdapter mMoviesGridAdapter;
    List<Result> mMovieResults;
    private ProgressBar mMoviesProgressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_movie_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mMoviesProgressBar = (ProgressBar) view.findViewById(R.id.movies_progress_bar);
        moviesGridView = (GridView) view.findViewById(R.id.movies_grid_view);
        getActivity().setTitle(getString(R.string.popularMovies));
        isTablet = getResources().getBoolean(R.bool.is_tablet);
        moviesGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                if (!isTablet)
                    startActivity(new Intent(getContext(), MovieDetailActivity.class)
                            .putExtra(getString(R.string.detailsExtra), mMovieResults.get(i))
                            .putExtra("local", local));
                else {
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.movie_detail_frame, MovieDetailFragment.newInstance(mMovieResults.get(i), local))
                            .commit();
                }
            }
        });
        getPopular();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        inflater.inflate(R.menu.menu_dash_board, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.action_popular:
                getPopular();
                return true;
            case R.id.action_top_rated:
                getTopRated();
                return true;
            case R.id.action_fav:
                getFavourites();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    public void getPopular() {

        local = false;
        mMoviesProgressBar.setVisibility(View.VISIBLE);
        APIClient.getApi().getPopular(new Callback<Movies>() {
            @Override
            public void success(final Movies movies, Response response) {

                getActivity().setTitle(getString(R.string.popularMovies));
                mMovieResults = movies.getResults();
                mMoviesGridAdapter = new DashBoardGridAdapter(getContext(), movies.getResults(), local);
                mMoviesGridAdapter.notifyDataSetChanged();
                moviesGridView.setAdapter(mMoviesGridAdapter);
                moviesGridView.setVisibility(View.VISIBLE);
                mMoviesProgressBar.setVisibility(View.GONE);
            }

            @Override
            public void failure(RetrofitError error) {


                Toast.makeText(getContext(), R.string.connectionFailed, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void getTopRated() {

        local = false;
        mMoviesProgressBar.setVisibility(View.VISIBLE);
        APIClient.getApi().getTopRated(new Callback<Movies>() {
            @Override
            public void success(final Movies movies, Response response) {

                getActivity().setTitle(getString(R.string.topRatedMovies));
                mMovieResults = movies.getResults();
                mMoviesGridAdapter = new DashBoardGridAdapter(getContext(), movies.getResults(), local);
                mMoviesGridAdapter.notifyDataSetChanged();
                moviesGridView.setAdapter(mMoviesGridAdapter);
                moviesGridView.setVisibility(View.VISIBLE);
                mMoviesProgressBar.setVisibility(View.GONE);
            }

            @Override
            public void failure(RetrofitError error) {

                Toast.makeText(getContext(), R.string.connectionFailed, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getFavourites() {

        local = true;
        getActivity().invalidateOptionsMenu();
        getActivity().setTitle("Favourites");
        Cursor cursor = getActivity().getContentResolver().query(MoviesContract.BASE_CONTENT_URI, null, null, null, null);
        if (cursor != null && cursor.getCount() > 0) {
            moviesGridView.setVisibility(View.GONE);
            mMoviesProgressBar.setVisibility(View.VISIBLE);
            final List<Result> results = new ArrayList<>();
            cursor.moveToFirst();
            do {
                Result moviesResults = new Result();
                moviesResults.setId(Integer.valueOf(cursor.getString(1)));
                moviesResults.setTitle(cursor.getString(2));
                moviesResults.setOverview(cursor.getString(3));
                moviesResults.setReleaseDate(cursor.getString(4));
                moviesResults.setVoteAverage(Double.valueOf(cursor.getString(5)));
                results.add(moviesResults);
            } while (cursor.moveToNext());
            mMoviesGridAdapter = new DashBoardGridAdapter(getContext(), results, local);
            mMoviesGridAdapter.notifyDataSetChanged();
            moviesGridView.setAdapter(mMoviesGridAdapter);
            moviesGridView.setVisibility(View.VISIBLE);
            mMoviesProgressBar.setVisibility(View.GONE);
            mMovieResults = results;
            cursor.close();
        } else {
            Snackbar.make(getView(), "Favourites empty..", Snackbar.LENGTH_SHORT).show();
            getPopular();
        }
    }
}
