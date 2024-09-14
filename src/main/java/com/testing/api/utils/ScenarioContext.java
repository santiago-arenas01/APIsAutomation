package com.testing.api.utils;
import java.util.HashMap;
import java.util.Map;

/**
 * The type Scenario context.
 */
public class ScenarioContext {
    private static final Map<String, Object> context = new HashMap<>();

    /**
     * Set.
     *
     * @param key   the key
     * @param value the value
     */
    public static void set(String key, Object value) {
        context.put(key, value);
    }

    /**
     * Get object.
     *
     * @param key the key
     * @return the object
     */
    public static Object get(String key) {
        return context.get(key);
    }

    /**
     * Clear.
     */
    public static void clear() {
        context.clear();
    }
}
