package udemy.movies.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

class MovieTest {

    Movie movie;

    @BeforeEach
    void setUp() {
        movie = new Movie(new MovieInfo(
                42,
                "The Big Lebowski",
                List.of("John Goodman", "Jeff Bridges", "Steve Buscemi"),
                1998,
                LocalDate.of(1998, 3, 6)
        ));
    }

    @Test
    @DisplayName("Should add some reviews")
    void shouldAddSomeReviews() {
        var theVeryFirstReview = new Review(1005, 42, "Funny", 9.0);
        var anotherReview = new Review(1005, 42, "Funny", 9.0);

        movie
                .addReview(theVeryFirstReview)
                .addReview(anotherReview);

        assertThat(movie.reviews()).containsExactlyInAnyOrder(theVeryFirstReview, anotherReview);
    }

    @Test
    @DisplayName("Should not allow external modification of reviews")
    void shouldReturnUnmodifieableReviews() {
        var evilReview = new Review(3000, 42, "Naah", 0.0);

        assertThatExceptionOfType(UnsupportedOperationException.class).isThrownBy(
                () -> movie.reviews().add(evilReview)
        );
    }
}