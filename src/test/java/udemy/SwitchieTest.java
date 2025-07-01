package udemy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

class SwitchieTest {

    Switchie switchie = new Switchie();

    @Test
    @DisplayName("Should return upper case short river names when requested")
    void shouldReturnUppercaseShortNames() {
        var riverNames = new ArrayList<String>();

        StepVerifier.create(switchie.rivers(true))
                .recordWith(() -> riverNames)
                .expectNextCount(3)
                .verifyComplete();

        assertThat(riverNames).containsExactlyInAnyOrder("MEKONG", "GANGES", "NIL");
    }

    @Test
    @DisplayName("Should return upper case long river names when requested")
    void shouldReturnUppercaseLongNames() {
        var riverNames = new ArrayList<String>();

        StepVerifier.create(switchie.rivers(false))
                .recordWith(() -> riverNames)
                .expectNextCount(2)
                .verifyComplete();

        assertThat(riverNames).containsExactlyInAnyOrder("SAMBESI", "MISSISSIPPI");
    }

    @Test
    @DisplayName("Should return upper case long river names by default")
    void shouldReturnUppercaseLongNamesByDefault() {
        var riverNames = new ArrayList<String>();

        StepVerifier.create(switchie.rivers(null))
                .recordWith(() -> riverNames)
                .expectNextCount(2)
                .verifyComplete();

        assertThat(riverNames).containsExactlyInAnyOrder("SAMBESI", "MISSISSIPPI");
    }

    @Test
    @DisplayName("Should should drink beer if no tasks are available")
    void shouldDrinkBeerIfNoTasksAreAvailable() {
        StepVerifier.create(switchie.monoPublisherInFluxSwitchIfEmpty(Flux.empty()))
                .expectNext("drinking beer")
                .verifyComplete();
    }
}