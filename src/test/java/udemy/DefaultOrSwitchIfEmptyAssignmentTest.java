package udemy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.params.provider.Arguments.arguments;

class DefaultOrSwitchIfEmptyAssignmentTest {

    DefaultOrSwitchIfEmptyAssignment service = new DefaultOrSwitchIfEmptyAssignment();

    @Test
    @DisplayName("Should filter but retain alex and map then")
    void shouldFilterAndMap() {
        Mono<String> alex = DefaultOrSwitchIfEmptyAssignment.filterMap(3).apply(Mono.just("alex"));

        StepVerifier.create(alex)
                .expectNext("ALEX")
                .verifyComplete();
    }

    @Test
    @DisplayName("Should filter to empty mono")
    void shouldFilterAndMapToEmptyMono() {
        Mono<String> alexDitched = DefaultOrSwitchIfEmptyAssignment
                .filterMap(100)
                .apply(Mono.just("alex"));

        StepVerifier.create(alexDitched)
                .verifyComplete();
    }

    @Test
    @DisplayName("Should filter but retain alex, map and split")
    void shouldFilterRetainMapAndSplit() {
        Mono<List<String>> alex = DefaultOrSwitchIfEmptyAssignment
                .filterMapSplit(3)
                .apply(Mono.just("alex"));

        StepVerifier.create(alex)
                .expectNext(List.of("A", "L", "E", "X"))
                .verifyComplete();
    }

    @Test
    @DisplayName("Should filter, map and split to empty mono")
    void shouldFilteMapAndSplitToEmptyMono() {
        Mono<List<String>> alexDitched = DefaultOrSwitchIfEmptyAssignment
                .filterMapSplit(100)
                .apply(Mono.just("alex"));

        StepVerifier.create(alexDitched)
                .verifyComplete();
    }

    @ParameterizedTest
    @CsvSource({
            "3, 'ALEX'",
            "4, 'DEFAULT'"
    })
    @DisplayName("Should filter and map with default")
    void shouldFilterMapWithDefault(int greaterThen, String expectedValue) {
        StepVerifier.create(service.nameMonoMapFilter(greaterThen))
                .expectNext(expectedValue)
                .verifyComplete();
    }

    @ParameterizedTest
    @MethodSource("filterMapWithDefaultSwitch")
    @DisplayName("Should filter map with switch to default")
    void shouldFilterMapWithDefaultSwitch(int greatherThen, List<String> expectedValue) {
        StepVerifier.create(service.nameMonoMapFilterSwitch(greatherThen))
                .expectNext(expectedValue)
                .verifyComplete();
    }

    public static Stream<Arguments> filterMapWithDefaultSwitch() {
        return Stream.of(
                arguments(3, List.of("A", "L", "E", "X")),
                arguments(4, List.of("D", "E", "F", "A", "U", "L", "T"))
        );
    }
}