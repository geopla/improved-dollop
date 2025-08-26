package udemy.movies.domain;

import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.unmodifiableList;

public class Movie {

    private final MovieInfo movieInfo;
    private List<Review> reviews = new ArrayList<>();
    private Revenue revenue;

    public Movie(MovieInfo movieInfo) {
        this(movieInfo, new ArrayList<>());
    }

    public Movie(MovieInfo movieInfo, List<Review> reviews) {
        this(movieInfo, reviews, new Revenue(movieInfo.id(), 0, 0));
    }

    public Movie(MovieInfo movieInfo, List<Review> reviews, Revenue revenue) {
        this.movieInfo = movieInfo;
        this.reviews = reviews;  // shallow copy will do, we don't care
        this.revenue = revenue;
    }

    public Movie addReview(Review review) {
        reviews.add(review);
        return this;
    }

    public MovieInfo info() {
        return movieInfo;
    }

    public List<Review> reviews() {
        return unmodifiableList(reviews);
    }

    public Revenue revenue() {
        return revenue;
    }
}
