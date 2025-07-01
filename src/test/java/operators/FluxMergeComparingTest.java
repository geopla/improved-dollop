package operators;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.Comparator;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class FluxMergeComparingTest {

    record Thingy(int key, String value) {}

    @Test
    @DisplayName("")
    void shouldDoSomething() {
        var belege = Flux.just(new Thingy(253, "green"), new Thingy(254, "brown"), new Thingy(255, "blue"));
        var belegsaetzte = Flux.just(new Thingy(253, "red"), new Thingy(254, "pink"), new Thingy(255, "transparent"));

        StepVerifier.create(
        Flux.mergeComparing(Comparator.comparingInt(Thingy::key),
                belege,
                belegsaetzte
                ))
                .assertNext(thingy -> assertThat(List.of("green", "red")).contains(thingy.value))
                .assertNext(thingy -> assertThat(List.of("green", "red")).contains(thingy.value))
                .assertNext(thingy -> assertThat(List.of("brown", "pink")).contains(thingy.value))
                .assertNext(thingy -> assertThat(List.of("brown", "pink")).contains(thingy.value))
                .expectNextCount(2)
                .verifyComplete();
    }
}