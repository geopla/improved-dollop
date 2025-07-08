package udemy.movies.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class MovieInfoTest {

    @Test
    @DisplayName("Should have immutable casts")
    void shouldhaveImmutableCasts() {
        var casts = new ArrayList<>(List.of("John Goodman", "Jeff Bridges", "Steve Buscemi"));

        var movieInfo = new MovieInfo(
                42L,
                "The Big Lebowski",
                casts,
                1998,
                LocalDate.of(1998, 3, 6)
        );

        casts.add("John Wayne");

        assertThat(movieInfo.casts()).hasSize(3);
    }
}