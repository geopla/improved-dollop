package udemy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import reactor.util.function.Tuple2;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.params.provider.Arguments.arguments;


class FluxAndMonoGeneratorServiceTest {

    FluxAndMonoGeneratorService service = new FluxAndMonoGeneratorService();

    @ParameterizedTest
    @CsvSource({
            "'a', 'A'",
            "'X', 'Q'"
    })
    @DisplayName("Should return a default when a Mono pipeline throws an exception")
    void shouldExploreMonoOnErrorReturn(String allButX, String expectedResult) {
        StepVerifier.create(service.exploreMonoOnErrorReturn(allButX))
                .expectNext(expectedResult)
                .verifyComplete();
    }

    String[] successfulValues = { "A", "B", "C"};
    String defaultValue = "D";
    String recoveredFromExceptionValue = "recovered from illegal argument";
    String[] resumedValues = { "D", "E"};
    String[] resumedValuesFromX = { "X", "Y"};

    @Test
    @DisplayName("Should recover from exception with default value")
    void shouldRecoverFromExceptionWithDefaultValue() {
        StepVerifier.create(service.exploreOnErrorReturn())
                .expectNext(successfulValues)
                .expectNext(defaultValue)
                .verifyComplete();
    }

    @Test
    @DisplayName("Should recover from certain exception type with a default value")
    void shouldRecoverFromIllegalArgumentException() {
        StepVerifier.create(service.exploreOnErrorReturnOnExceptionType())
                .expectNext(successfulValues)
                .expectNext(recoveredFromExceptionValue)
                .verifyComplete();
    }

    @Test
    @DisplayName("Should emit an error when exception cant be handled due to exception type")
    void shouldEmitErrorWhenExceptionCannotBeHandled() {
        StepVerifier.create(service.exploreOnErrorReturnOnExceptionTypeNotHandled())
                .expectNext(successfulValues)
                .expectError(IllegalStateException.class)
                .verify();
    }

    @Test
    @DisplayName("Should recover from certain exception matching a predicate")
    void shouldShouldRecoverFromExceptionWithPredicateMatch() {
        StepVerifier.create(service.exploreOnErrorReturnOnExceptionPredicate())
                .expectNext(successfulValues)
                .expectNext(recoveredFromExceptionValue)
                .verifyComplete();
    }

    @Test
    @DisplayName("Should emit an error when exception can't be handled due to predicate")
    void shouldEmitAnErrorWhenExceptionCannotBeHandledDueToPredicate() {
        StepVerifier.create(service.exploreOnErrorReturnOnExceptionPredicateNotHandled())
                .expectNext(successfulValues)
                .expectError(IllegalArgumentException.class)
                .verify();
    }

    @Test
    @DisplayName("Should resume with values from a fallback publisher")
    void shouldResumeOnAnyException() {
        StepVerifier.create(service.exploreOnErrorResume())
                .expectNext(successfulValues)
                .expectNext(resumedValues)
                .verifyComplete();
    }

    @Test
    @DisplayName("Should resume with selected fallback publisher")
    void shouldSelectFallbackPublisher() {
        StepVerifier.create(service.exploreOnErrorResumeWithSelectedFallback())
                .expectNext(successfulValues)
                .expectNext(resumedValuesFromX)
                .verifyComplete();
    }

    @Test
    @DisplayName("Should skip publishers emitting an error")
    void shouldExploreSkippingErrors() {
        StepVerifier.create(service.exploreSkippingErrors().log())
                .expectNext(successfulValues)
                .verifyComplete();
    }

    @Test
    @DisplayName("Should skip errors emitted from upstream")
    void shouldExploreSkippingErrorsWithContinue() {
        StepVerifier.create(service.exploreSkippingErrorsWithContinue().log())
                .expectNext(successfulValues)
                .verifyComplete();
    }

    @Test
    @DisplayName("Should zip Mono with other Mono")
    void shouldMonoZipWith() {
        StepVerifier.create(service.exploreMonoZipWith())
                .expectNextMatches(t ->
                        "A".equals(t.getT1()) && 1 == t.getT2())
                .verifyComplete();
    }

    @Test
    @DisplayName("Should zip Mono with other Mono using a combinator")
    void shouldMonoZipWithCombinator() {
        StepVerifier.create(service.exploreMonoZipWithCombinator())
                .expectNext("A-1")
                .verifyComplete();
    }

    @Test
    @DisplayName("Should zip Flux with other Publisher")
    void shouldFluxZipWith() {
        StepVerifier.create(service.exploreFluxZipWith())
                .consumeNextWith(assertTupleEquals("A", 1))
                .consumeNextWith(assertTupleEquals("B", 2))
                .verifyComplete();
    }

    Consumer<Tuple2<String, Integer>> assertTupleEquals(String expectedFirstValue, Integer expectedSecondValue) {
        return tuple -> {
            if (!Objects.equals(tuple.getT1(), expectedFirstValue) || !Objects.equals(tuple.getT2(), expectedSecondValue)) {
                throw new AssertionError("failed on element %s".formatted(tuple.toString()));
            }
        };
    }

    @Test
    @DisplayName("Should zip Flux with other Publisher using a combinator")
    void shouldFluxZipWithCombinator() {
        StepVerifier.create(service.exploreFluxZipWithCombinator())
                .expectNext("A-1", "B-2")
                .verifyComplete();
    }

    @Test
    @DisplayName("Should zip Flux with Iterable")
    void shouldFluxZipWithIterable() {
        StepVerifier.create(service.exploreFluxZipWithIterable())
                .consumeNextWith(assertTupleEquals("A", 1))
                .consumeNextWith(assertTupleEquals("B", 2))
                .verifyComplete();
    }

    @Test
    @DisplayName("Should zip Flux with Iterable using a combinator")
    void shouldFluxZipWithIterableCombinator() {
        StepVerifier.create(service.exploreFluxZipWithIteratorCombinator())
                .expectNext("A-1", "B-2")
                .verifyComplete();
    }

    @ParameterizedTest
    @MethodSource("concatWithPublisher")
    @DisplayName("Should concat Flux with either Mono or Flux")
    void shouldConcatFluxWithPublisher(Publisher<String> monoOrFlux, List<String> expectedResult) {
        StepVerifier.create(service.exploreConcatWith(monoOrFlux))
                .expectNextSequence(expectedResult)
                .verifyComplete();
    }

    @ParameterizedTest
    @MethodSource("concatWithPublisher")
    @DisplayName("Should concat Mono with either Mono or Flux")
    void shouldConcatMonoWithPublisher(Publisher<String> monoOrFlux, List<String> expectedResult) {
        StepVerifier.create(service.exploreConcatWithMono(monoOrFlux))
                .expectNextSequence(expectedResult)
                .verifyComplete();
    }

    static Stream<Arguments> concatWithPublisher() {
        return Stream.of(
                arguments(Mono.just("B"), List.of("A", "B")),
                arguments(Flux.just("B", "C"), List.of("A", "B", "C"))
        );
    }

    @Test
    @DisplayName("Should merge interleaved")
    void shouldMergeInterleaved() {
        StepVerifier.create(service.exploreMerge())
                .expectNext(
                        "A-100",
                        "d-125",
                        "B-200",
                        "e-250",
                        "C-300",
                        "f-375"
                )
                .verifyComplete();
    }

    @Test
    @DisplayName("Should merge with interleaved")
    void shouldMergeWithInterleaved() {
        StepVerifier.create(service.exploreMergeWith())
                .expectNext(
                        "A-100",
                        "d-125",
                        "B-200",
                        "e-250",
                        "C-300",
                        "f-375"
                )
                .verifyComplete();
    }

    @Test
    @DisplayName("Should merge Mono with Publisher interleaved")
    void shouldMergeMonoWithPublisherInterleaved() {
        List<String> emittedValues = new ArrayList<>();

        StepVerifier.create(service.exploreMergeWithMono())
                .recordWith(() -> emittedValues)
                .expectNextCount(2)
                .expectRecordedMatches(emitted -> emitted.containsAll(List.of("B", "A")))
                .verifyComplete();
    }


    // NOTE: zip with combinator function results into a Publisher containing the combinator result(s)
    @Test
    @DisplayName("Should zip Mono's with combinator (using varargs publishers")
    void shouldZipMonosVarargs() {
        var leftOperator = Mono.just(2);
        var rightOperator = Mono.just(5);

        Function<Object[], Integer> combinator = operators -> (int) operators[0] + (int) operators[1];

        // note the operator prefix style contrary to the Iterable variant
        Mono<Integer> resulMono = Mono.zip(combinator, leftOperator, rightOperator);
        var result = resulMono.block();

        assertThat(result).isEqualTo(7);
    }

    @Test
    @DisplayName("Should zip Mono's with combinator (using Iterable publishers")
    void shouldZipMonosIterable() {
        var leftOperator = Mono.just(2);
        var rightOperator = Mono.just(5);

        Function<Object[], Integer> combinator = operators -> (int) operators[0] + (int) operators[1];

        // note the operator postfix style contrary to the varargs variant
        Mono<Integer> resulMono = Mono.zip(List.of(leftOperator, rightOperator), combinator);
        var result = resulMono.block();

        assertThat(result).isEqualTo(7);
    }

    @Test
    @DisplayName("Should zip TWO Monos with BiFunction combinator")
    void shouldZipTwoMonosWithBiFunctionCombinatopr() {
        var leftOperator = Mono.just(2);
        var rightOperator = Mono.just(5);

        // note that the bifunction variant works in general with different argument and result types
        BiFunction<Integer, Integer, Integer> combinator = Integer::sum;
        Mono<Integer> resultMono = Mono.zip(leftOperator, rightOperator, combinator);
        var result = resultMono.block();

        assertThat(result).isEqualTo(7);
    }


    // NOTE: zip without combinator functions results into a Publisher containing the gather results (tuples)
    @Test
    @DisplayName("Should zip Mono's")
    void shouldZipMonosToTuple() {
        var leftOperator = Mono.just(2);
        var rightOperator = Mono.just(5);

        Mono<Tuple2<Integer, Integer>> resultMono = Mono.zip(leftOperator, rightOperator);
        Tuple2<Integer, Integer> result = resultMono.block();

        assertThat(result.toList()).isEqualTo(List.of(2, 5));
    }

    @Test
    @DisplayName("Should zip Mono with another one")
    void shouldZipWith() {
        Mono<Integer> key = Mono.just(42);
        Mono<String> value = Mono.just("the answer");

        Tuple2<Integer, String> zipResult = key.zipWith(value).block();

        assertAll(
                () -> assertThat(zipResult.getT1()).isEqualTo(42),
                () -> assertThat(zipResult.getT2()).isEqualTo("the answer")
        );
    }

    @Test
    @DisplayName("Should zip with combinator (all type parameters are of different types)")
    void shouldZipWithCombinator() {
        Mono<Integer> key = Mono.just(42);
        Mono<String> value = Mono.just("the answer");

        BiFunction<Integer, String, Boolean> combinator = (k, v) -> k == 42 && v.equals("the answer");

        StepVerifier.create(key.zipWith(value, combinator))
                .expectNext(true)
                .verifyComplete();
    }

    @Test
    @DisplayName("Should zip when asynchronous starter Mono emits")
    void shouldZipAsynchronousStarterMono() {
        Mono<Integer> key = Mono.just(42).delayElement(Duration.ofMillis(100));

        Function<Integer, Mono<? extends Boolean>> rightGenerator = answer -> Mono.just(answer == 42);

        BiFunction<Integer, Boolean, String> combinator = (answer, ok) ->
                ok  ? "%d is the answer".formatted(answer) : "try another answer";

        StepVerifier.create(key.zipWhen(rightGenerator, combinator))
                .expectNext("42 is the answer")
                .verifyComplete();
    }

    // zip what happens when ...

    @Test
    @DisplayName("Should handle edge cases")
    void shouldHandleEdgeCases() {
        StepVerifier.create(Mono.zip(Mono.just(42), Mono.just("the answer")))
                .expectNextMatches(tuple ->
                        tuple.getT1().equals(42) && tuple.getT2().equals("the answer"))
                .verifyComplete();

        StepVerifier.create(Mono.zip(Mono.just(42), Mono.empty()))
                .verifyComplete();

        StepVerifier.create(Mono.zip(Mono.empty(), Mono.just("the answer")))
                .verifyComplete();

        StepVerifier.create(Mono.zip(Mono.just(42), Mono.error(new IllegalArgumentException("bad value"))))
                .verifyErrorMatches(e -> e.getMessage().contains("bad value"));

        StepVerifier.create(Mono.zip(Mono.error(new IllegalArgumentException("bad key")), Mono.just("the answer")))
                .verifyErrorMatches(e -> e.getMessage().contains("bad key"));

        StepVerifier.create(Mono.zip(
                                Mono.error(new IllegalArgumentException("bad key")),
                                Mono.error(new IllegalArgumentException("bad value"))
                        )
                )
                .verifyErrorMatches(e -> e.getMessage().contains("bad key"));

        // ... but note the difference when using the delay variant
        StepVerifier.create(Mono.zipDelayError(
                                Mono.error(new IllegalArgumentException("bad key")),
                                Mono.error(new IllegalArgumentException("bad value"))
                        )
                )
                .verifyErrorMatches(e ->
                        Arrays.stream(e.getSuppressed())
                                .map(Throwable::getMessage)
                                .toList()
                                .containsAll(List.of("bad value", "bad key")));
    }


}