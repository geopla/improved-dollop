package udemy.movies.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;
import udemy.movies.domain.Review;

import java.util.ArrayList;
import java.util.List;

class ReviewServiceTest {

    ReviewService reviewService = new ReviewService();

    @Test
    @DisplayName("Should have reviews for 'Batman Begins'")
    void shouldHaveReviewsForBatmanBegins() {
        List<Review> reviews = new ArrayList<>();
        var movieIdBatmanBegins = 100;

        StepVerifier.create(reviewService.reviews(movieIdBatmanBegins))
                .recordWith(() -> reviews)
                .expectNextCount(3)
                .expectRecordedMatches(rs -> rs.stream()
                        .allMatch(r -> r.movieInfoId() == movieIdBatmanBegins)
                )
                .verifyComplete();
    }

    @Test
    @DisplayName("Should have NO reviews for 'Dark Knight Rises'")
    void shouldHaveNoReviewsForDarkKnightRises() {
        var movieIdDarkKnightRises = 102;

        StepVerifier.create(reviewService.reviews(movieIdDarkKnightRises))
                .verifyComplete();
    }
}