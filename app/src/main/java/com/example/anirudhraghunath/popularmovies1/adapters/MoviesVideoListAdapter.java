package com.example.anirudhraghunath.popularmovies1.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.example.anirudhraghunath.popularmovies1.R;
import com.example.anirudhraghunath.popularmovies1.resources.Constants;
import com.example.anirudhraghunath.popularmovies1.utilities.MovieVideoResult;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MoviesVideoListAdapter extends BaseAdapter {

    private List<MovieVideoResult> mVideosResults;
    private Context mContext;

    public MoviesVideoListAdapter(Context mContext, List<MovieVideoResult> mVideosResults) {
        this.mVideosResults = mVideosResults;
        this.mContext = mContext;
    }

    @Override
    public int getCount() {
        return mVideosResults.size();
    }

    @Override
    public MovieVideoResult getItem(int position) {
        return mVideosResults.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.video_list_item, parent, false);
            convertView.setTag(new ViewHolder(convertView));
        }
        ViewHolder holder = (ViewHolder) convertView.getTag();
        String url = Constants.URL_YOUTUBE_IMAGE_HEADER + getItem(position).getKey() + Constants.URL_YOUTUBE_IMAGE_FOOTER;
        Picasso.with(mContext).load(url).placeholder(R.drawable.loading).error(R.drawable.no_image)
                .into(holder.getVideoImageImageView());
        return convertView;
    }

    private class ViewHolder {

        private ImageView videoImageImageView;

        public ImageView getVideoImageImageView() {
            return videoImageImageView;
        }

        public ViewHolder(View view) {
            this.videoImageImageView = (ImageView) view.findViewById(R.id.video_image_image_view);
        }
    }

}
