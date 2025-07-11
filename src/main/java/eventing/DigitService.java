package eventing;

import eventing.SplitterService.SplitterResult;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.function.Consumer;

public class DigitService {

    private final Flux<SplitterResult> splitterResults;

    public DigitService(Flux<SplitterResult> splitterResults) {
        this.splitterResults = splitterResults;
        this.splitterResults.subscribe(solitterResultConsumer);
    }

    static List<Integer> filterDigitsFrom(List<String> chars) {
        return chars.stream()
                .filter(c -> Character.isDigit(c.codePointAt(0)))
                .map(Integer::valueOf)
                .toList();
    }

    Consumer<SplitterResult> solitterResultConsumer = splitterResult -> {
        // TODO when splitter service signals success ...

        // ... fetch the split result from the key value store

        // ... filter the digits from the splitter result

        // ... emit an event containing uuid and digits list

    };
}
