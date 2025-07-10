package eventing;

import reactor.core.publisher.Mono;

import java.util.*;

public class KeyValueStore {

    private static Map<UUID, List<String>> store = new HashMap<>();

    public static void store(UUID uuid, List<String> characters) {
        store.put(uuid, characters);
    }

    public static Mono<List<String>> retrieve(UUID uuid) {
        return Mono.justOrEmpty(store.get(uuid));
    }
}
