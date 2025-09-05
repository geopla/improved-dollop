package udemy.movies.domain;

import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.unmodifiableList;

public class Movie {

    private final MovieInfo movieInfo;

    private List<Review> reviews = new ArrayList<>();
    private Revenue revenue;

    public Movie(MovieInfo movieInfo) {
        this.movieInfo = movieInfo;
        this.revenue = new Revenue(movieInfo.id(), 0, 0);
    }

    public Movie addReview(Review review) {
        reviews.add(review);
        return this;
    }

    public List<Review> reviews() {
        return unmodifiableList(reviews);
    }

    public Revenue revenue() {
        return revenue;
    }
}
