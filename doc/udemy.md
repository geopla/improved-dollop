# Reactive Course

## Section 10: Build Movie Reactive Service

### All Movies
**Reasoning**
- there is a 1:1 relationship between `MovieInfo` and the resulting `Movie`
- to create a `Movie` its `Review` list needs to be obtained
    - obtaining that list is an asynchronous call
- `MovieInfo` gives us the id required for requesting the movies review list

**Solution**
- start by obtaining a `Flux<MovieInfo>`
- apply to the `Flux<MovieInfo>` an operator returning a `Flux<Movie>`
  - within the operators mapper function a `Movie` is constructed passing parameters `MovieInfo` and `List<Review>`
  - requesting the reviews is an asynchronous call, the operator needs to be kinda `flatMap()` therefor

Let us take a look at the signature of `flatMap()`:
```
<R> Flux<R> flatMap(java.util.function.Function<? super T,? extends Publisher<? extends R>> mapper)
```
Applying our concrete types the signature would look as follows
```
Publisher<Movie> flatMap(java.util.function.Function<? super MovieInfo,? extends Publisher<? extends Movie>> mapper)
```
Stripping it further down to the implementation
```
Publisher<Movie> flatMap(Function<MovieInfo, Publisher<Movie>> movieMapper)
```
We can even make it more concrete, reasoning about the return type. Will this be a `Mono` or a `Flux`? As there
is a 1:1 relationship between `MovieInfo` and `Movie` it will be a `Mono` obviously. So we need to provide a
mapper function passing in a parameter of type `MovieInfo` and returning a `Mono<Movie>` 
```java
private Flux<Movie> movieMapper(MovieInfo movieInfo) {
    // TODO retrieve reviews and create the movie
}
```
As we know we need the movies review list, fetch them from the reviews service.
```java
private Flux<Movie> movieMapper(MovieInfo movieInfo) {
    Mono<List<Review>> movieReviews = reviewServiceallReviews(movieInfo.id());
    
    // TODO return a Mono<Movie> of the movie, passing in movieInfo and the reviews list
}
```
Now we are goin' asynchronous, we need to wait for the movie reviews. When the (single) reviews list is
emitted, we just map it to the `Movie`.
```java
private Flux<Movie> movieMapper(MovieInfo movieInfo) {
    Mono<List<Review>> movieReviews = reviewServiceallReviews(movieInfo.id());

    return movieReviews.map(review -> new Movie(movieInfo, review));
}
```
That's it, just refactor it now ;-)