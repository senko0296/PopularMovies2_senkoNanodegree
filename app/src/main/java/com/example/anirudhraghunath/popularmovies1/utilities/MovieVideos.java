
package com.example.anirudhraghunath.popularmovies1;

import java.util.ArrayList;
import java.util.List;
import com.example.anirudhraghunath.popularmovies1.utilities.MovieVideoResult;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MovieVideos {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("results")
    @Expose
    private List<MovieVideoResult> results = new ArrayList<MovieVideoResult>();

    /**
     *
     * @return
     * The id
     */
    public Integer getId() {
        return id;
    }

    /**
     *
     * @param id
     * The id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     *
     * @return
     * The results
     */
    public List<MovieVideoResult> getResults() {
        return results;
    }

    /**
     *
     * @param results
     * The results
     */
    public void setResults(List<MovieVideoResult> results) {
        this.results = results;
    }

}