package com.example.anirudhraghunath.popularmovies1.contracts;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

import com.example.anirudhraghunath.popularmovies1.resources.Constants;

public class MoviesContract {

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + Constants.CONTENT_AUTHORITY);

    public static final class MoviesEntry implements BaseColumns {
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/" + Constants.CONTENT_AUTHORITY;

        public static final String TABLE_NAME = Constants.KEY_TABLE_MOVIES;
        public static final String COLUMN_ID = Constants.KEY_ID;
        public static final String COLUMN_TITLE = Constants.KEY_TITLE;
        public static final String COLUMN_OVERVIEW = Constants.KEY_OVERVIEW;
        public static final String COLUMN_RELEASE_DATE = Constants.KEY_RELEASE_DATE;
        public static final String COLUMN_RATING = Constants.KEY_RATING;
        public static final String COLUMN_POSTER = Constants.KEY_POSTER;
        public static final String COLUMN_BACKDROP = Constants.KEY_BACKDROP;

        public static Uri buildUri(long id) {
            return ContentUris.withAppendedId(BASE_CONTENT_URI, id);
        }

    }
}

