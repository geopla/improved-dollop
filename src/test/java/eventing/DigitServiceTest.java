package eventing;

import eventing.SplitterService.SplitterResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.assertj.core.api.Assertions.assertThat;

class DigitServiceTest {

    // --- system logic

    @Test
    @DisplayName("Should subscribe to splitter results")
    void shouldSubscribeTiSplitterResults() {
        AtomicBoolean serviceHasSubscribed = new AtomicBoolean(false);

        Flux<SplitterResult> splitterResults = Flux.<SplitterResult>never()
                .doOnSubscribe(subscription -> serviceHasSubscribed.set(true));

        new DigitService(splitterResults);

        assertThat(serviceHasSubscribed).isTrue();
    }

    // --- business logic

    @Test
    @DisplayName("Should filter digits from characters")
    void shouldFilterDigitsFromCharacters() {
        List<String> chars = List.of("716dde0f-4736-46f6-bee4-e431fdd28d39".split(""));

        List<Integer> digits = DigitService.filterDigitsFrom(chars);

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

        assertThat(DigitService.filterDigitsFrom(chars)).isEmpty();
    }
}