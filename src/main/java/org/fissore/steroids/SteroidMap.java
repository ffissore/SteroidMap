package org.fissore.steroids;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * SteroidMap is a {@link Map} on steroids.
 * It decorates {@link Map} providing a fluent interface and a shorter syntax. It allows you to navigate a map structure without using casts.
 * It doesn't require any code to work with other frameworks/libraries: if they know how to deal with {@link Map}s, they work with SteroidMap as well.
 * Custom implementations should implement SteroidMap and delegate the actual storage to a backing map implementation, such as {@link HashMap} or {@link java.util.concurrent.ConcurrentHashMap}.
 *
 * @param <K> the type of the keys
 */
public interface SteroidMap<K> extends Map<K, Object> {

  /* content manipulation */

  /**
   * {@link #put(Object, Object) Puts} a value if not null. Fluent version of {@link #put(Object, Object)}
   *
   * @param key   key with which the specified value is to be associated
   * @param value value to be associated with the specified key
   * @return this instance
   */
  default SteroidMap<K> add(K key, Object value) {
    if (value != null) {
      put(key, value);
    }
    return this;
  }

  /**
   * {@link #put(Object, Object) Puts} all non null values from given maps into this map. Fluent version of {@link #putAll(Map)}
   *
   * @param sources mappings to be stored in this map
   * @return this instance
   * @see #addAll(Stream)
   */
  default SteroidMap<K> addAll(Map<K, Object>... sources) {
    return addAll(Stream.of(sources));
  }

  /**
   * {@link #put(Object, Object) Puts} all non null values from given maps into this map. Fluent version of {@link #putAll(Map)}
   *
   * @param sources mappings to be stored in this map
   * @return this instance
   * @see #addAll(Stream)
   */
  default SteroidMap<K> addAll(Collection<Map<K, Object>> sources) {
    return addAll(sources.stream());
  }

  /**
   * {@link #put(Object, Object) Puts} all non null values from given maps into this map. Fluent version of {@link #putAll(Map)}
   *
   * @param sources mappings to be stored in this map
   * @return this instance
   */
  default SteroidMap<K> addAll(Stream<Map<K, Object>> sources) {
    synchronized (this) {
      sources.forEach(source -> source.forEach(this::add));
    }
    return this;
  }

  /**
   * {@link #put(Object, Object) Puts} non null keys from source into this map. If keys is not specified, it behaves as {@link #addAll(Map[])}
   *
   * @param source the map to add to this map
   * @param keys   if specified, only given keys will be added
   * @return this instance
   */
  default SteroidMap<K> addFrom(Map<K, Object> source, K... keys) {
    return addFrom(source, new HashSet<>(Arrays.asList(keys)));
  }

  /**
   * {@link #put(Object, Object) Puts} non null keys from source into this map. If keys is not specified, it behaves as {@link #addAll(Map[])}
   *
   * @param source the map to add to this map
   * @param keys   if specified, only these keys will be added
   * @return this instance
   */
  default SteroidMap<K> addFrom(Map<K, Object> source, Collection<K> keys) {
    if (source == null) {
      return this;
    }

    if (keys.size() == 0) {
      return addAll(source);
    }

    synchronized (this) {
      source.entrySet().stream()
          .filter(e -> keys.contains(e.getKey()))
          .forEach(e -> add(e.getKey(), e.getValue()));
    }

    return this;
  }

  /**
   * Renames the specified key, if present. It runs a in a synchronized block
   *
   * @param oldKey the old key
   * @param newKey the new key
   * @return this instance
   */
  default SteroidMap<K> renameKey(K oldKey, K newKey) {
    synchronized (this) {
      if (!containsKey(oldKey)) {
        return this;
      }
      put(newKey, get(oldKey));
      remove(oldKey);
    }
    return this;
  }

  /**
   * Removes given keys from this map. Fluent version of {@link #remove(Object)}
   *
   * @param keys the keys to remove
   * @return this instance
   * @see #del(Stream)
   */
  default SteroidMap<K> del(K... keys) {
    return del(Stream.of(keys));
  }

  /**
   * Removes given keys from this map. Fluent version of {@link #remove(Object)}
   *
   * @param keys the keys to remove
   * @return this instance
   * @see #del(Stream)
   */
  default SteroidMap<K> del(Collection<K> keys) {
    return del(keys.stream());
  }

  /**
   * Removes given keys from this map. Fluent version of {@link #remove(Object)}
   *
   * @param keys the keys to remove
   * @return this instance
   */
  default SteroidMap<K> del(Stream<K> keys) {
    synchronized (this) {
      keys.forEach(this::remove);
    }
    return this;
  }

  /* content extraction */

  /**
   * If given key is {@link #valued(Object) valued}, it's applied to provided valueReturner. Otherwise, defaultValue is returned.
   * Both checking if the key is {@link #valued(Object) valued} and applying valueReturner run in a synchronized block
   *
   * @param key           the key
   * @param defaultValue  the value to return if key is not {@link #valued(Object) valued}
   * @param valueReturner the function to apply the key to
   * @param <V>           the type of the value
   * @return either value from map or defaultValue
   */
  default <V> V defaultIfMissing(K key, V defaultValue, Function<K, V> valueReturner) {
    synchronized (this) {
      if (valued(key)) {
        return valueReturner.apply(key);
      }
    }
    return defaultValue;
  }

  /**
   * {@link #get(Object) Gets} given key and cast it to a Long
   *
   * @param key the key
   * @return value associated to key casted to Long
   */
  default Long l(K key) {
    return (Long) get(key);
  }

  /**
   * {@link #get(Object) Gets} given key and cast it to a Long. If key is not {@link #valued(Object) valued}, it returns defaultValue
   *
   * @param key          the key
   * @param defaultValue the defaultValue to return if key is not {@link #valued(Object) valued}
   * @return value associated to key, casted to Long. defaultValue if key is not {@link #valued(Object) valued}
   */
  default Long l(K key, Long defaultValue) {
    return defaultIfMissing(key, defaultValue, this::l);
  }

  /**
   * {@link #get(Object) Gets} given key and cast it to a Integer
   *
   * @param key the key
   * @return value associated to key casted to Integer
   */
  default Integer i(K key) {
    return (Integer) get(key);
  }

  /**
   * {@link #get(Object) Gets} given key and cast it to a Integer. If key is not {@link #valued(Object) valued}, it returns defaultValue
   *
   * @param key          the key
   * @param defaultValue the defaultValue to return if key is not {@link #valued(Object) valued}
   * @return value associated to key, casted to Integer. defaultValue if key is not {@link #valued(Object) valued}
   */
  default Integer i(K key, Integer defaultValue) {
    return defaultIfMissing(key, defaultValue, this::i);
  }

  /**
   * {@link #get(Object) Gets} given key and cast it to a Double
   *
   * @param key the key
   * @return value associated to key casted to Double
   */
  default Double d(K key) {
    return (Double) get(key);
  }

  /**
   * {@link #get(Object) Gets} given key and cast it to a Double. If key is not {@link #valued(Object) valued}, it returns defaultValue
   *
   * @param key          the key
   * @param defaultValue the defaultValue to return if key is not {@link #valued(Object) valued}
   * @return value associated to key, casted to Double. defaultValue if key is not {@link #valued(Object) valued}
   */
  default Double d(K key, Double defaultValue) {
    return defaultIfMissing(key, defaultValue, this::d);
  }

  /**
   * {@link #get(Object) Gets} given key and cast it to a Float
   *
   * @param key the key
   * @return value associated to key casted to Float
   */
  default Float f(K key) {
    return (Float) get(key);
  }

  /**
   * {@link #get(Object) Gets} given key and cast it to a Float. If key is not {@link #valued(Object) valued}, it returns defaultValue
   *
   * @param key          the key
   * @param defaultValue the defaultValue to return if key is not {@link #valued(Object) valued}
   * @return value associated to key, casted to Float. defaultValue if key is not {@link #valued(Object) valued}
   */
  default Float f(K key, Float defaultValue) {
    return defaultIfMissing(key, defaultValue, this::f);
  }

  /**
   * {@link #get(Object) Gets} given key and cast it to a String
   *
   * @param key the key
   * @return value associated to key casted to String
   */
  default String s(K key) {
    return (String) get(key);
  }

  /**
   * {@link #get(Object) Gets} given key and cast it to a String. If key is not {@link #valued(Object) valued}, it returns defaultValue
   *
   * @param key          the key
   * @param defaultValue the defaultValue to return if key is not {@link #valued(Object) valued}
   * @return value associated to key, casted to String. defaultValue if key is not {@link #valued(Object) valued}
   */
  default String s(K key, String defaultValue) {
    return defaultIfMissing(key, defaultValue, this::s);
  }

  /**
   * {@link #get(Object) Gets} given key and cast it to a Boolean
   *
   * @param key the key
   * @return value associated to key casted to Boolean
   */
  default Boolean b(K key) {
    return (Boolean) get(key);
  }

  /**
   * {@link #get(Object) Gets} given key and cast it to a Boolean. If key is not {@link #valued(Object) valued}, it returns defaultValue
   *
   * @param key          the key
   * @param defaultValue the defaultValue to return if key is not {@link #valued(Object) valued}
   * @return value associated to key, casted to Boolean. defaultValue if key is not {@link #valued(Object) valued}
   */
  default Boolean b(K key, Boolean defaultValue) {
    return defaultIfMissing(key, defaultValue, this::b);
  }

  /**
   * {@link #get(Object) Gets} given key and cast it to type V
   *
   * @param key the key
   * @return value associated to key casted to type V
   */
  @SuppressWarnings("unchecked")
  default <V> V o(K key) {
    return (V) get(key);
  }

  /**
   * {@link #get(Object) Gets} given key and cast it to type of given Class
   *
   * @param key   the key
   * @param clazz the clazz, useful only to make the compiler happy about type V
   * @param <V>   the type of the value associated with key
   * @return value associated to key casted to type V
   */
  @SuppressWarnings("unchecked")
  default <V> V o(K key, Class<V> clazz) {
    return (V) get(key);
  }

  /**
   * {@link #get(Object) Gets} given key and cast it to a Object. If key is not {@link #valued(Object) valued}, it returns defaultValue
   *
   * @param key          the key
   * @param defaultValue the defaultValue to return if key is not {@link #valued(Object) valued}
   * @return value associated to key, casted to Object. defaultValue if key is not {@link #valued(Object) valued}
   */
  @SuppressWarnings("unchecked")
  default <V> V o(K key, V defaultValue) {
    return defaultIfMissing(key, defaultValue, (key1) -> o(key1));
  }

  /**
   * {@link #get(Object) Gets} given key and cast it to a Date
   *
   * @param key the key
   * @return value associated to key casted to Date
   */
  default Date date(K key) {
    return (Date) get(key);
  }

  /**
   * {@link #get(Object) Gets} given key and cast it to a Date. If key is not {@link #valued(Object) valued}, it returns defaultValue
   *
   * @param key          the key
   * @param defaultValue the defaultValue to return if key is not {@link #valued(Object) valued}
   * @return value associated to key, casted to Date. defaultValue if key is not {@link #valued(Object) valued}
   */
  default Date date(K key, Date defaultValue) {
    return defaultIfMissing(key, defaultValue, this::date);
  }

  /**
   * A key is valued if <code>{@link #get(Object) get(key)} != null</code>
   *
   * @param key the key
   * @return true if {@link #get(Object) get(key)} != null
   */
  default boolean valued(K key) {
    return get(key) != null;
  }

  /**
   * Opposite of {@link #valued(Object)}
   *
   * @param key the key
   * @return true if {@link #get(Object) get(key)} == null
   */
  default boolean notValued(K key) {
    return !valued(key);
  }

  /**
   * {@link #get(Object) Gets} given key and cast it to a Collection<T>
   *
   * @param key the key
   * @return value associated to key casted to Collection<T>
   */
  @SuppressWarnings("unchecked")
  default <T> Collection<T> collection(K key) {
    return (Collection<T>) get(key);
  }

  /**
   * {@link #get(Object) Gets} given key and cast it to a Collection<T>. If key is not {@link #valued(Object) valued}, it returns defaultValue
   *
   * @param key          the key
   * @param defaultValue the defaultValue to return if key is not {@link #valued(Object) valued}
   * @return value associated to key, casted to Collection<T>. defaultValue if key is not {@link #valued(Object) valued}
   */
  default <T> Collection<T> collection(K key, Collection<T> defaultValue) {
    return defaultIfMissing(key, defaultValue, this::collection);
  }

  /**
   * {@link #get(Object) Gets} given key and cast it to a List<T>
   *
   * @param key the key
   * @return value associated to key casted to List<T>
   */
  @SuppressWarnings("unchecked")
  default <T> List<T> list(K key) {
    return (List<T>) get(key);
  }

  /**
   * {@link #get(Object) Gets} given key and cast it to a List<T>. If key is not {@link #valued(Object) valued}, it returns defaultValue
   *
   * @param key          the key
   * @param defaultValue the defaultValue to return if key is not {@link #valued(Object) valued}
   * @return value associated to key, casted to List<T>. defaultValue if key is not {@link #valued(Object) valued}
   */
  default <T> List<T> list(K key, List<T> defaultValue) {
    return defaultIfMissing(key, defaultValue, this::list);
  }

  /**
   * {@link #get(Object) Gets} given key, cast it to a Collection<T> and returns its Stream<T>
   *
   * @param key the key
   * @return value associated to key casted to a Collection<T> and converted to a Stream<T>
   */
  @SuppressWarnings("unchecked")
  default <T> Stream<T> stream(K key) {
    return (Stream<T>) collection(key).stream();
  }

  /**
   * {@link #get(Object) Gets} given key, cast it to a Collection<T> and returns its Stream<T>. If key is not {@link #valued(Object) valued}, it returns defaultValue
   *
   * @param key          the key
   * @param defaultValue the defaultValue to return if key is not {@link #valued(Object) valued}
   * @return value associated to key casted to a Collection<T> and converted to a Stream<T>. defaultValue if key is not {@link #valued(Object) valued}
   */
  default <T> Stream<T> stream(K key, Collection<T> defaultValue) {
    return collection(key, defaultValue).stream();
  }

  /**
   * Creates a new SteroidMap made of given keys only. Keys must be {@link #valued(Object) valued}
   *
   * @param keys the keys used to create a new, filtered SteroidMap
   * @return a new, filtered, SteroidMap
   * @see #subMap(Stream)
   */
  default SteroidMap<K> subMap(K... keys) {
    return subMap(Stream.of(keys));
  }

  /**
   * Creates a new SteroidMap made of given keys only. Keys must be {@link #valued(Object) valued}
   *
   * @param keys the keys used to create a new, filtered SteroidMap
   * @return a new, filtered, SteroidMap
   * @see #subMap(Stream)
   */
  default SteroidMap<K> subMap(Collection<K> keys) {
    return subMap(keys.stream());
  }

  /**
   * Creates a new SteroidMap made of given keys only. Keys must be {@link #valued(Object) valued}
   *
   * @param keys the keys used to create a new, filtered SteroidMap
   * @return a new, filtered, SteroidMap
   */
  SteroidMap<K> subMap(Stream<K> keys);

  /**
   * Creates a new SteroidMap, backed by given backingMap and made of given keys only. Keys must be {@link #valued(Object) valued}
   *
   * @param backingMap the actual map to use as storage
   * @param keys       the keys used to create a new, filtered SteroidMap
   * @return a new, filtered, SteroidMap
   * @see #subMap(Map, Stream)
   */
  default SteroidMap<K> subMap(Map<K, Object> backingMap, K... keys) {
    return subMap(backingMap, Stream.of(keys));
  }

  /**
   * Creates a new SteroidMap, backed by given backingMap and made of given keys only. Keys must be {@link #valued(Object) valued}
   *
   * @param backingMap the actual map to use as storage
   * @param keys       the keys used to create a new, filtered SteroidMap
   * @return a new, filtered, SteroidMap
   * @see #subMap(Map, Stream)
   */
  default SteroidMap<K> subMap(Map<K, Object> backingMap, Collection<K> keys) {
    return subMap(backingMap, keys.stream());
  }

  /**
   * Creates a new SteroidMap, backed by given backingMap and made of given keys only. Keys must be {@link #valued(Object) valued}
   *
   * @param backingMap the actual map to use as storage
   * @param keys       the keys used to create a new, filtered SteroidMap
   * @return a new, filtered, SteroidMap
   */
  SteroidMap<K> subMap(Map<K, Object> backingMap, Stream<K> keys);

  /**
   * {@link #get(Object) Gets} given key and, if value is not of type SteroidMap, a new SteroidMap is created using value as backing map. Otherwise value is casted to SteroidMap and returned
   *
   * @param key the key
   * @return value associated to key, either casted to SteroidMap or used as backing map for a new SteroidMap
   */
  SteroidMap<K> map(K key);

  /**
   * {@link #get(Object) Gets} given key and, if value is not of type SteroidMap, a new SteroidMap is created using value as backing map. Otherwise value is casted to SteroidMap and returned. If key is not {@link #valued(Object) valued}, it returns defaultValue
   *
   * @param key          the key
   * @param defaultValue the defaultValue to return if key is not {@link #valued(Object) valued}
   * @return value associated to key, either casted to SteroidMap or used as backing map for a new SteroidMap
   */
  default SteroidMap<K> map(K key, SteroidMap<K> defaultValue) {
    return defaultIfMissing(key, defaultValue, this::map);
  }

  default Stream<? extends SteroidMap<K>> maps(K key) {
    return stream(key).map(this::ensureMapIsOnSteroid);
  }

  SteroidMap<K> ensureMapIsOnSteroid(Object value);

}
