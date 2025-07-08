package udemy.movies.domain;

import java.util.ArrayList;
import java.util.List;

public class Movie {

    private final MovieInfo movieInfo;

    private List<Review> reviews = new ArrayList<>();
    private Revenue revenue;

    public Movie(MovieInfo movieInfo) {
        this.movieInfo = movieInfo;
        this.revenue = new Revenue(movieInfo.id(), 0, 0);
    }

    public void addReview(Review review) {
        reviews.add(review);
    }

    public Revenue revenue() {
        return revenue;
    }
}
