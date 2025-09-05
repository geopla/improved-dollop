package udemy.movies.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;
import udemy.movies.domain.Review;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ReviewServiceTest {

    ReviewService reviewService = new ReviewService();

    @Test
    @DisplayName("Should have reviews for 'Batman Begins'")
    void shouldHaveReviewsForBatmanBegins() {
        List<Review> reviews = new ArrayList<>();

        StepVerifier.create(reviewService.reviews(100))
                .recordWith(() -> reviews)
                .expectNextCount(3)
                .expectRecordedMatches(rs -> rs.stream().allMatch(r -> r.movieInfoId() == 100))
                .verifyComplete();
    }
}