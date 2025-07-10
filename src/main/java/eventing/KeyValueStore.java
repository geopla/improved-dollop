package eventing;

import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class KeyValueStore {

    private static Map<UUID, List<String>> store = new HashMap<>();

    public static Mono<UUID> store(UUID uuid, List<String> characters) {
        store.put(uuid, characters);
        return Mono.just(uuid);
    }

    public static Mono<List<String>> retrieve(UUID uuid) {
        return Mono.justOrEmpty(store.get(uuid));
    }
}
