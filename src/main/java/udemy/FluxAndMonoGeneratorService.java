package udemy;

import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

import static reactor.core.publisher.Flux.just;

class FluxAndMonoGeneratorService {

    private static final Logger LOGGER = LoggerFactory.getLogger(FluxAndMonoGeneratorService.class);

    Mono<String> exception_mono_onErrorContinue(String input) {
        return Mono.just(input)
                .map(i -> {
                    if ("abc".equals(i)) {
                        throw new RuntimeException("Exception Occurred");
                    }
                    else {
                        return i;
                    }
                })
                .onErrorContinue(
                        (t, o) -> {
                            LOGGER.info("onErrorContinue({}) with exception {}", o, t.toString());
                        }
                );
    }

    public Mono<Object> exception_mono_onErrorMap(Exception e) {
        return Mono.just("B")
                .map(value -> {
                    throw new RuntimeException("Exception Occurred");
                })
                .onErrorMap(ex
                        -> new ReactorException(ex, ex.getMessage())
                );
    }


    Mono<String> monoErrorHandling() {
        return Mono.just("TODO")
                .map(value -> {
                    throw new RuntimeException("");
                });
    }

    Mono<String> monoErrorHandlingTheBetterWay() {
        return Mono.just("TODO")
                .<String>handle((value, sink) -> sink.error(new IllegalArgumentException("Oopsie")))
                .onErrorMap(IllegalArgumentException.class, RuntimeException::new);
    }


    private String throwNumberFormatException(String message) {
        if ("still goode".equals(message)) {
            throw new NumberFormatException("oopsie");
        } else {
            return message;
        }
    }

    Mono<String> exploreMonoOnErrorMap() {
        return Mono.just("still goode")
                .map(this::throwNumberFormatException)
                .onErrorMap(numberFormatToIllegalArgumentException);
    }

    static Function<? super Throwable, ? extends Throwable> numberFormatToIllegalArgumentException = t ->
            t instanceof NumberFormatException ? new IllegalArgumentException("damn") : t;

    Mono<String> exploreMonoErrorMapWithType() {
        return Mono.just("still goode")
                .map(this::throwNumberFormatException)
                .onErrorMap(NumberFormatException.class, actualNumberFormatException);
    }

    static Function<? super NumberFormatException, IllegalArgumentException> actualNumberFormatException = t ->
            new IllegalArgumentException("umph");


    Mono<String> exploreMonoOnErrorReturn(String allButX) {
        return Mono.just(allButX)
                .map(v -> {
                    if ("X".equals(v)) {
                        throw new IllegalArgumentException("X");
                    } else {
                        return v;
                    }
                })
                .onErrorReturn("q")
                .map(String::toUpperCase);
    }

    Flux<String> exploreSkippingErrors() {
        Flux<Mono<String>> flux = Flux.just(
                Mono.just("A").delayElement(Duration.ofMillis(10)), // Schnell
                Mono.just("B").delayElement(Duration.ofMillis(20)), // Schnell
                Mono.<String>error(new IllegalArgumentException("X")).delaySubscription(Duration.ofMillis(50)), // Fehler nach "a" und "b"
                Mono.just("C").delayElement(Duration.ofMillis(100)) // Nach dem Fehler
        );

        // fix publisher errors at the source - not always possible
        return flux
                .flatMap(ms -> ms
                        .onErrorResume(e -> Mono.empty())
                );
    }

    Flux<String> exploreSkippingErrorsWithContinue() {
        Flux<Mono<String>> flux = Flux.just(
                Mono.just("A").delayElement(Duration.ofMillis(10)), // Schnell
                Mono.just("B").delayElement(Duration.ofMillis(20)), // Schnell
                Mono.<String>error(new IllegalArgumentException("X")).delaySubscription(Duration.ofMillis(50)), // Fehler nach "a" und "b"
                Mono.just("C").delayElement(Duration.ofMillis(100)) // Nach dem Fehler
        );

        // jump over error elements
        return flux
                .flatMap(ms -> ms)
                .onErrorContinue((throwable, obj) -> {
                    System.out.println(obj);
                });
    }

    Flux<String> exploreOnErrorReturn() {
        return Flux.just("A", "B", "C")
                .concatWith(Flux.error(new IllegalArgumentException("X")))
                .onErrorReturn("D");
    }

    Flux<String> exploreOnErrorReturnOnExceptionType() {
        return Flux.just("A", "B", "C")
                .concatWith(Flux.error(new IllegalArgumentException("X")))
                .onErrorReturn(IllegalArgumentException.class, "recovered from illegal argument");
    }

    Flux<String> exploreOnErrorReturnOnExceptionTypeNotHandled() {
        return Flux.just("A", "B", "C")
                .concatWith(Flux.error(new IllegalStateException("X")))
                .onErrorReturn(IllegalArgumentException.class, "recovered from illegal argument");
    }

    Flux<String> exploreOnErrorReturnOnExceptionPredicate() {
        return Flux.just("A", "B", "C")
                .concatWith(Flux.error(new IllegalArgumentException("X")))
                .onErrorReturn(illegalArgumentX, "recovered from illegal argument");
    }

    Flux<String> exploreOnErrorReturnOnExceptionPredicateNotHandled() {
        return Flux.just("A", "B", "C")
                .concatWith(Flux.error(new IllegalArgumentException("#")))
                .onErrorReturn(illegalArgumentX, "recovered from illegal argument");
    }

    Predicate<Throwable> illegalArgumentX = t -> "X".equals(t.getMessage());

    Flux<String> exploreOnErrorResume() {
        return Flux.just("A", "B", "C")
                .concatWith(Flux.error(new NoSuchElementException("X")))
                .onErrorResume(withFallbackIgnoringAnyException);
    }

    Function<Throwable, Flux<String>> withFallbackIgnoringAnyException =
            t -> Flux.just("D", "E");


    Flux<String> exploreOnErrorResumeWithSelectedFallback() {
        return Flux.just("A", "B", "C")
                .concatWith(Flux.error(new NoSuchElementException("X")))
                .onErrorResume(selectedFallback);
    }

    Function<Throwable, Flux<String>> selectedFallback = t -> {
        return "X".equals(t.getMessage()) ? Flux.just("X", "Y") : Flux.just("D", "E");
    };

    Mono<Tuple2<String, Integer>> exploreMonoZipWith() {
        var first = Mono.just("A");
        var second = Mono.just(1);

        return first.zipWith(second);
    }

    Mono<String> exploreMonoZipWithCombinator() {
        var firstMono = Mono.just("A");
        var secondMono = Mono.just(1);

        BiFunction<String, Integer, String> combinator = "%s-%d"::formatted;

        return firstMono.zipWith(secondMono, combinator);
    }

    Flux<Tuple2<String, Integer>> exploreFluxZipWith() {
        var firstFlux = Flux.just("A", "B");
        var secondPublisher = Flux.just(1, 2);

        return firstFlux.zipWith(secondPublisher);
    }

    Flux<String> exploreFluxZipWithCombinator() {
        var firstFlux = Flux.just("A", "B");
        var secondPublisher = Flux.just(1, 2);

        BiFunction<String, Integer, String> combinator = "%s-%d"::formatted;

        return firstFlux.zipWith(secondPublisher, combinator);
    }

// zipWith() parameter doesn't need to be a Publisher at all

    Flux<Tuple2<String, Integer>> exploreFluxZipWithIterable() {
        var firstFlux = Flux.just("A", "B");
        var secondIterable = List.of(1, 2);

        return firstFlux.zipWithIterable(secondIterable);
    }

    Flux<String> exploreFluxZipWithIteratorCombinator() {
        var firstFlux = Flux.just("A", "B");
        var secondIterable = List.of(1, 2);

        BiFunction<String, Integer, String> combinator = "%s-%d"::formatted;

        return firstFlux.zipWithIterable(secondIterable, combinator);
    }


    Flux<String> exploreConcatWith(Publisher<String> monoOrFlux) {
        var aFlux = just("A");

        return aFlux.concatWith(monoOrFlux);
    }

    Flux<String> exploreConcatWithMono(Publisher<String> monoOrFlux) {
        var aMono = just("A");

        return aMono.concatWith(monoOrFlux);
    }

    Flux<String> exploreMerge() {
        var abcPublisher = Flux.just("A-100", "B-200", "C-300").delayElements(Duration.ofMillis(100));
        var defPublisher = Flux.just("d-125", "e-250", "f-375").delayElements(Duration.ofMillis(125));

        return Flux.merge(abcPublisher, defPublisher);
    }

    Flux<String> exploreMergeWith() {
        var abcPublisher = Flux.just("A-100", "B-200", "C-300").delayElements(Duration.ofMillis(100));
        var defFlux = Flux.just("d-125", "e-250", "f-375").delayElements(Duration.ofMillis(125));

        return defFlux.mergeWith(abcPublisher);
    }

    Flux<String> exploreMergeWithMono() {
        var aMono = Mono.just("A");
        var bPublisher = Mono.just("B");

        return aMono.mergeWith(bPublisher);
    }
}
