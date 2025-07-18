package eventing;

import eventing.SplitterService.SplitterResult;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;
import reactor.core.scheduler.Schedulers;

import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;

public class DigitService {

    public record DigitResult(
        UUID uuid,
        List<Integer> digits
    ) { }

    private final Flux<SplitterResult> splitterResults;

    private final Sinks.Many<DigitResult> digitResults = Sinks.many().unicast().onBackpressureBuffer();

    public DigitService(Flux<SplitterResult> splitterResults) {
        this.splitterResults = splitterResults;
        this.splitterResults.subscribe(splitterResultConsumer);
    }

    Consumer<SplitterResult> splitterResultConsumer = splitterResult -> {
        var uuid = splitterResult.uuid();

        KeyValueStore.retrieve(uuid)
                .subscribeOn(Schedulers.boundedElastic())
                .map(DigitService::filterDigits)
                .map(createResultEvent(uuid))
                .subscribe(digitResults::tryEmitNext);
    };

    static List<Integer> filterDigits(List<String> list) {
        return list.stream()
                .filter(s -> Character.isDigit(s.charAt(0)))
                .map(Integer::valueOf)
                .toList();
    }

    static Function<List<Integer>, DigitResult> createResultEvent(UUID uuid) {
        return digits -> new DigitResult(uuid, digits);
    }

    public Flux<DigitResult> digitResults() {
        return digitResults.asFlux();
    }
}
