package udemy.movies.domain;

public record Revenue(
        long movieInfoId,
        double budget,
        double boxOffice
) {
}
