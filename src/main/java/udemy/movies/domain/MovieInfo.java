package udemy.movies.domain;

import java.time.LocalDate;
import java.util.List;

public record MovieInfo(
        long id,
        String name,
        List<String> casts,
        int year,
        LocalDate releaseDate
) {
    public MovieInfo {
        casts = List.copyOf(casts);
    }
}
