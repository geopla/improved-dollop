package udemy.movies.domain;

public record Review(
        long id,
        long movieInfoId,
        String comment,
        double rating
) {
}
