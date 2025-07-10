package udemy.movies.service;

import reactor.core.publisher.Flux;
import udemy.movies.domain.Review;

import java.util.List;

public class ReviewService {

    final static List<Review> reviews = List.of(
            new Review(1, 100, "Awesome", 8.9),
            new Review(2, 100, "Excellent", 9.0),
            new Review(3, 100, "Really good", 7.0),

            new Review(4, 101, "Okish", 6.5),
            new Review(5, 101, "Not bad", 7.0)
    );

    public Flux<Review> reviews(long movieId) {
        return Flux.fromIterable(reviews.stream()
                .filter(review -> review.movieInfoId() == movieId)
                .toList());
    }
}
