package com.example.anirudhraghunath.popularmovies1.fragments;


import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.anirudhraghunath.popularmovies1.MovieVideos;
import com.example.anirudhraghunath.popularmovies1.R;
import com.example.anirudhraghunath.popularmovies1.adapters.MoviesReviewListAdapter;
import com.example.anirudhraghunath.popularmovies1.adapters.MoviesVideoListAdapter;
import com.example.anirudhraghunath.popularmovies1.contracts.MoviesContract;
import com.example.anirudhraghunath.popularmovies1.network.APIClient;
import com.example.anirudhraghunath.popularmovies1.resources.Constants;
import com.example.anirudhraghunath.popularmovies1.utilities.MovieReviewResult;
import com.example.anirudhraghunath.popularmovies1.utilities.MovieReviews;
import com.example.anirudhraghunath.popularmovies1.utilities.Result;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import it.sephiroth.android.library.widget.AdapterView;
import it.sephiroth.android.library.widget.HListView;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import static android.content.Intent.EXTRA_TEXT;

/**
 * A simple {@link Fragment} subclass.
 */
public class MovieDetailFragment extends Fragment {

    Result mMovieResults;
    ImageView mToolbarPosterImageView;
    Toolbar toolbar;
    CardView mOverviewCardView;
    TextView mTitleTextView, mReleaseDateTextView, mRatingsTextView;
    HListView mVideosListView, mReviewsListView;
    FloatingActionButton mFavouriteFab;
    ProgressDialog mProgressDialog;
    private MoviesVideoListAdapter mMoviesVideoListAdapter;
    private MoviesReviewListAdapter mMoviesReviewListAdapter;
    private String mVideoURL, mId, mMovieTitle, mOverview, mRating, mRelease;
    private ContentResolver mContentResolver;
    private boolean local, inDB = false;

    public static MovieDetailFragment newInstance(Result result, boolean local) {

        MovieDetailFragment fragment = new MovieDetailFragment();
        fragment.mMovieResults = result;
        fragment.local = local;
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_movie_detail, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (!getResources().getBoolean(R.bool.is_tablet)) {
            toolbar = (Toolbar) view.findViewById(R.id.toolbar);
            ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        }
        mContentResolver = getActivity().getContentResolver();

        mToolbarPosterImageView = (ImageView) view.findViewById(R.id.toolbar_poster_image_view);
        mOverviewCardView = (CardView) view.findViewById(R.id.overview_card_view);
        mTitleTextView = (TextView) view.findViewById(R.id.title_text_view);
        mReleaseDateTextView = (TextView) view.findViewById(R.id.release_date_text_view);
        mRatingsTextView = (TextView) view.findViewById(R.id.ratings_text_view);
        mVideosListView = (HListView) view.findViewById(R.id.videos_list_view);
        mReviewsListView = (HListView) view.findViewById(R.id.reviews_list_view);
        mFavouriteFab = (FloatingActionButton) view.findViewById(R.id.favourite_fab);

        mId = String.valueOf(mMovieResults.getId());
        checkIifMovieIsInDatabase();

        if (local) {
            view.findViewById(R.id.video_card_view).setVisibility(View.GONE);
            view.findViewById(R.id.review_card_view).setVisibility(View.GONE);
            Cursor cursor = getActivity().getContentResolver().query(MoviesContract.BASE_CONTENT_URI,
                    new String[]{MoviesContract.MoviesEntry.COLUMN_ID, MoviesContract.MoviesEntry.COLUMN_TITLE,
                            MoviesContract.MoviesEntry.COLUMN_OVERVIEW, MoviesContract.MoviesEntry.COLUMN_RATING,
                            MoviesContract.MoviesEntry.COLUMN_RELEASE_DATE}, MoviesContract.MoviesEntry.COLUMN_ID
                            + "=?", new String[]{mId}, null);
            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst();
                mMovieTitle = cursor.getString(1);
                mOverview = cursor.getString(2);
                mRating = cursor.getString(3);
                mRelease = cursor.getString(4);

                mTitleTextView.setText(mMovieTitle);
                String rating = "Rating: "
                        + mRating + "/10";
                mRatingsTextView.setText(rating);
                Calendar cal = Calendar.getInstance();
                SimpleDateFormat sdfFrom = new SimpleDateFormat("yyyy-mm-dd", Locale.ENGLISH);
                try {
                    cal.setTime(sdfFrom.parse(mRelease));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                SimpleDateFormat sdfTo = new SimpleDateFormat("dd MMM, yyyy", Locale.ENGLISH);
                String release = "Release Date: " + sdfTo.format(cal.getTime());
                mReleaseDateTextView.setText(release);
                cursor.close();

                Picasso.with(getContext()).load(readImageFromFile(mMovieTitle, Constants.KEY_POSTER))
                        .placeholder(R.drawable.loading).error(R.drawable.no_image)
                        .into(mToolbarPosterImageView);
                initFab();
            }
        } else {

            mProgressDialog = new ProgressDialog(getContext());
            mProgressDialog.setMessage("Please Wait..");
            mProgressDialog.show();

            mTitleTextView.setText(mMovieResults.getTitle());
            mReleaseDateTextView.setText(mMovieResults.getReleaseDate());
            mRatingsTextView.setText(mMovieResults.getVoteAverage().toString() + "/10");
            Picasso.with(getContext()).load(Constants.URL_POSTER_IMAGE + mMovieResults.getPosterPath())
                    .placeholder(R.drawable.loading)
                    .into(mToolbarPosterImageView);
            initFab();
            APIClient.getApi().getMovieVideosFromId(mId, new Callback<MovieVideos>() {
                @Override
                public void success(final MovieVideos movieVideos, Response response) {
                    mMoviesVideoListAdapter = new MoviesVideoListAdapter(getContext(), movieVideos.getResults());
                    mVideosListView.setAdapter(mMoviesVideoListAdapter);
                    mMoviesVideoListAdapter.notifyDataSetChanged();
                    if (movieVideos.getResults().size() > 0)
                        mVideoURL = Constants.URL_YOUTUBE + movieVideos.getResults().get(0).getKey();
                    else mVideoURL = "none";
                    mVideosListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            String url = Constants.URL_YOUTUBE + movieVideos.getResults().get(position).getKey();
                            startActivity((new Intent(Intent.ACTION_VIEW)).setData(Uri.parse(url)));
                        }
                    });

                    APIClient.getApi().getMovieReviewsFromId(mId, new Callback<MovieReviews>() {
                        @Override
                        public void success(final MovieReviews movieReviews, Response response) {
                            mMoviesReviewListAdapter = new MoviesReviewListAdapter(getContext(), movieReviews.getResults());
                            mReviewsListView.setAdapter(mMoviesReviewListAdapter);
                            mMoviesReviewListAdapter.notifyDataSetChanged();
                            mReviewsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    MovieReviewResult result = movieReviews.getResults().get(position);
                                    new AlertDialog.Builder(getContext())
                                            .setTitle(result.getAuthor())
                                            .setMessage(result.getContent())
                                            .setPositiveButton(R.string.close, null)
                                            .create().show();
                                }
                            });
                            if (mProgressDialog.isShowing())
                                mProgressDialog.dismiss();
                        }

                        @Override
                        public void failure(RetrofitError error) {
                            if (mProgressDialog.isShowing())
                                mProgressDialog.dismiss();
                            Snackbar.make(getView(), "Connection Error", Snackbar.LENGTH_SHORT).show();
                            error.printStackTrace();

                        }
                    });
                }

                @Override
                public void failure(RetrofitError error) {
                    if (mProgressDialog.isShowing())
                        mProgressDialog.dismiss();
                    Snackbar.make(getView(), "Connection Error", Snackbar.LENGTH_SHORT).show();
                    error.printStackTrace();
                }
            });
        }

        mOverviewCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new AlertDialog.Builder(getContext())
                        .setTitle(mMovieResults.getTitle())
                        .setMessage(mMovieResults.getOverview())
                        .setPositiveButton(getString(R.string.close), null)
                        .create().show();
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (!local) {
            inflater.inflate(R.menu.menu_movie_detail, menu);
            super.onCreateOptionsMenu(menu, inflater);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_share_movie) {
            if (!mVideoURL.equalsIgnoreCase("none")) {
                String text = "Get Movie Trailer" + mMovieResults.getOriginalTitle() + "\n\n" + mVideoURL;
                startActivity(Intent.createChooser(new Intent(Intent.ACTION_SEND)
                                .setType("text/plain")
                                .putExtra(EXTRA_TEXT, text)
                        , "Share with"));
            } else {
                Snackbar.make(getView(), "No Share Options..", Snackbar.LENGTH_SHORT).show();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void checkIifMovieIsInDatabase() {
        Cursor c = mContentResolver.query(MoviesContract.BASE_CONTENT_URI, new String[]{MoviesContract.MoviesEntry.COLUMN_ID},
                MoviesContract.MoviesEntry.COLUMN_ID + "=?", new String[]{mId}, null);
        if (c != null && c.getCount() > 0) {
            inDB = c.moveToFirst();
            c.close();
            mFavouriteFab.setImageResource(R.drawable.ic_fav_icon_selected);
        } else {
            inDB = false;
            mFavouriteFab.setImageResource(R.drawable.ic_fav_icon);
        }
    }

    private void initFab() {
        mFavouriteFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!inDB) {
                    ContentValues values = new ContentValues();
                    values.put(MoviesContract.MoviesEntry.COLUMN_ID, mId);
                    values.put(MoviesContract.MoviesEntry.COLUMN_TITLE, mMovieResults.getTitle());
                    values.put(MoviesContract.MoviesEntry.COLUMN_OVERVIEW, mMovieResults.getOverview());
                    values.put(MoviesContract.MoviesEntry.COLUMN_RATING, String.valueOf(mMovieResults.getVoteAverage()));
                    values.put(MoviesContract.MoviesEntry.COLUMN_RELEASE_DATE, mMovieResults.getReleaseDate());

                    saveImageToFile(mToolbarPosterImageView, mMovieResults.getOriginalTitle(), Constants.KEY_POSTER);

                    mContentResolver.insert(MoviesContract.BASE_CONTENT_URI, values);
                    Snackbar.make(view, mMovieResults.getTitle() + " Favourite Added"
                            , Snackbar.LENGTH_SHORT).show();
                    checkIifMovieIsInDatabase();
                } else {
                    new AlertDialog.Builder(getContext())
                            .setMessage("Remove "
                                    + mMovieTitle
                                    + " from Favourites?")
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    mContentResolver.delete(MoviesContract.BASE_CONTENT_URI,
                                            MoviesContract.MoviesEntry.COLUMN_ID
                                                    + "=?",
                                            new String[]{mId});
                                    Snackbar.make(getView(), mMovieTitle
                                                    + "Favourite Removed",
                                            Snackbar.LENGTH_SHORT).show();
                                    deleteImageFile(mMovieTitle, Constants.KEY_POSTER);
                                    deleteImageFile(mMovieTitle, Constants.KEY_BACKDROP);
                                    checkIifMovieIsInDatabase();
                                }
                            })
                            .setNegativeButton(android.R.string.no, null)
                            .create().show();
                }
            }
        });
    }

    private void saveImageToFile(ImageView imageView, String title, String type) {
        imageView.setDrawingCacheEnabled(true);
        Bitmap bitmap = imageView.getDrawingCache();
        try {
            FileOutputStream output = getActivity().openFileOutput(title + type, Context.MODE_PRIVATE);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, output);
            output.flush();
            output.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private File readImageFromFile(String title, String type) {
        File file = null;
        try {
            file = new File(getActivity().getFilesDir(), title + type);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file;
    }

    private boolean deleteImageFile(String title, String type) {
        File file;
        try {
            file = new File(getActivity().getFilesDir(), title + type);
            return file.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
