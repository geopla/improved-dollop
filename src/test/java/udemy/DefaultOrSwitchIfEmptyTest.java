package udemy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.params.provider.Arguments.arguments;

class DefaultOrSwitchIfEmptyTest {

    DefaultOrSwitchIfEmpty service = new DefaultOrSwitchIfEmpty();

    @Test
    @DisplayName("Should stream uppercase characters of names filtered by length constraint")
    void shouldStreamNamesTransformedByFilterNapAndSplitted() {
        var nameLengthGreaterThen = 3;

        StepVerifier.create(service.namesFluxTransformAndSplitOrDefault(nameLengthGreaterThen))
                .expectNext("A", "L", "E", "X", "C", "H", "L", "O", "E")
                .verifyComplete();
    }

    @Test
    @DisplayName("Should stream uppercase String 'default' when empty")
    void shouldStreamDefaultWhenEmpty() {
        var nameLengthGreaterThen = 5;

        StepVerifier.create(service.namesFluxTransformAndSplitOrDefault(nameLengthGreaterThen))
                .expectNext("default")
                .verifyComplete();
    }

    @Test
    @DisplayName("Should stream uppercase characters of 'default when empty")
    void shouldStreamCharactersOfDefaultWhenEmpty() {
        var nameLengthGreaterThen = 5;

        StepVerifier.create(service.namesFluxTransformAndSplitOrApplyDefault(nameLengthGreaterThen))
                .expectNext("D", "E", "F","A", "U", "L", "T")
                .verifyComplete();
    }

    @ParameterizedTest
    @MethodSource("streamNamesUppercaseByMinimumSize")
    @DisplayName("Should stream names uppercased filtered length constraint")
    void shouldStreamNamesUppercaseByMinimumSize(int lengthGreaterThen, List<String> expectedNames) {
        StepVerifier.create(service.namesFluxTransformWithFilterMapFunction(lengthGreaterThen))
                .expectNextSequence(expectedNames)
                .verifyComplete();
    }

    static Stream<Arguments> streamNamesUppercaseByMinimumSize() {
        return Stream.of(
                arguments(2, List.of("ALEX", "BEN", "CHLOE")),
                arguments(3, List.of("ALEX", "CHLOE")),
                arguments(4, List.of("CHLOE")),
                arguments(5, List.of())
        );
    }

    @Test
    @DisplayName("Should stream characters of name (as String's)")
    void shouldStreamCharactersOfName() {
        StepVerifier.create(service.splitString("ALEX"))
                .expectNext("A", "L", "E", "X")
                .verifyComplete();
    }

    @Test
    @DisplayName("Should stream single name uppercased")
    void shouldStreamSingleNameUppercased() {
        StepVerifier.create(service.namesMonoTransform(3))
                .expectNext("ALEX")
                .verifyComplete();
    }

    @Test
    @DisplayName("Should stream splitted name list")
    void shouldStreamSplittedNameList() {
        StepVerifier.create(service.namesMonoTransformAndSplit(3))
                .expectNext(List.of("A", "L", "E", "X"))
                .verifyComplete();
    }
}