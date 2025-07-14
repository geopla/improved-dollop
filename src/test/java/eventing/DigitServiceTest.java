package eventing;

import eventing.SplitterService.SplitterResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.assertj.core.api.Assertions.assertThat;

class DigitServiceTest {

    final static String THE_UUID_STRING = "716dde0f-4736-46f6-bee4-e431fdd28d39";
    final static List<String> THE_UUID_CHARS = Arrays.asList(THE_UUID_STRING.split(""));
    final static UUID THE_UUID  = UUID.fromString(THE_UUID_STRING);
    final static Integer[] THE_DIGITS = { 7, 1, 6, 0, 4, 7, 3, 6, 4, 6, 6, 4, 4, 3, 1, 2, 8, 3, 9 };

    // --- system logic

    // This test strategie should work for message driven services emitting a result event too.
    // Actually it is kinda integration test already, because a service is tested with the
    // associated infrastructure. In reality, it won't be that easy to set up the system the
    // service is embedded into. That's the reason why business logic is tested on its own.

    @Test
    @DisplayName("Should subscribe to splitter results")
    void shouldSubscribeToSplitterResults() {
        AtomicBoolean serviceHasSubscribed = new AtomicBoolean(false);

        Flux<SplitterResult> splitterResults = Flux.<SplitterResult>never()
                .doOnSubscribe(subscription -> serviceHasSubscribed.set(true));

        new DigitService(splitterResults);

        assertThat(serviceHasSubscribed).isTrue();
    }

    @Test
    @DisplayName("Should DigitsResult, triggered by receiving an event")
    void shouldEmitDigitResultTriggeredByEvent() {
        KeyValueStore.store(THE_UUID, THE_UUID_CHARS);

        Sinks.Many<SplitterResult> splitterResults = Sinks.many().replay().latest();
        var splitterResult = new SplitterResult(true, THE_UUID, "success");

        var digitService = new DigitService(splitterResults.asFlux());

        splitterResults.tryEmitNext(splitterResult);

        StepVerifier.create(digitService.digitResults())
                .expectNext(new DigitService.DigitResult(THE_UUID, List.of(THE_DIGITS)))
                .thenCancel()
                .verify();
    }

    // --- business logic

    @Test
    @DisplayName("Should filter digits from characters")
    void shouldFilterDigitsFromCharacters() {
        List<String> chars = List.of(THE_UUID_STRING.split(""));

        List<Integer> digits = DigitService.filterDigits(chars);

        assertThat(digits).containsExactly(
                7, 1, 6, 0,
                4, 7, 3, 6,
                4, 6, 6,
                4,
                4, 3, 1, 2, 8, 3, 9);
    }

    @Test
    @DisplayName("Should filter to empty list when no digits are present")
    void shouldFilterToEmptyList() {
        List<String> chars = List.of("abc-def".split(""));

        assertThat(DigitService.filterDigits(chars)).isEmpty();
    }
}