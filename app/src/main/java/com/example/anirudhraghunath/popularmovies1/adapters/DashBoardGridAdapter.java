package com.example.anirudhraghunath.popularmovies1.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.anirudhraghunath.popularmovies1.R;
import com.example.anirudhraghunath.popularmovies1.resources.Constants;
import com.example.anirudhraghunath.popularmovies1.utilities.Result;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;

/**
 * Created by anirudhraghunath on 31/01/16.
 */
public class DashBoardGridAdapter extends BaseAdapter {

    private List<Result> mMovieDataList;
    private Context mContext;
    private LayoutInflater mInflater;
    private boolean local;

    public DashBoardGridAdapter(Context context, List<Result> movieDataList, boolean local) {

        mInflater = LayoutInflater.from(context);
        this.mMovieDataList = movieDataList;
        this.mContext = context;
        this.local = local;
    }

    @Override
    public int getCount() {

        return mMovieDataList.size();
    }

    @Override
    public Result getItem(int position) {
        return mMovieDataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {

        if (convertView == null) {

            convertView = mInflater.inflate(R.layout.movie_grid_item, viewGroup, false);
            convertView.setTag(new ViewHolder(convertView));
        }

        final ViewHolder viewHolder = (ViewHolder) convertView.getTag();
        viewHolder.titleTextView.setText(mMovieDataList.get(position).getTitle());
        if (local) {
            Picasso.with(mContext).load(viewHolder.readImageFromFile(mContext, mMovieDataList.get(position).getTitle(), Constants.KEY_POSTER))
                    .placeholder(R.drawable.loading).error(R.drawable.no_image)
                    .into(viewHolder.posterImageView);
        } else {
            Picasso.with(mContext).load(Constants.URL_POSTER_IMAGE + mMovieDataList.get(position)
                    .getPosterPath()).placeholder(R.drawable.loading)
                    .into(viewHolder.posterImageView);
        }
        return convertView;
    }

    private static class ViewHolder {

        TextView titleTextView;
        ImageView posterImageView;

        public ViewHolder(View view) {

            this.titleTextView = (TextView) view.findViewById(R.id.title_text_view);
            this.posterImageView = (ImageView) view.findViewById(R.id.poster_image_view);
        }

        public File readImageFromFile(Context context, String title, String type) {
            File file = null;
            try {
                file = new File(context.getFilesDir(), title + type);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return file;
        }
    }
}
