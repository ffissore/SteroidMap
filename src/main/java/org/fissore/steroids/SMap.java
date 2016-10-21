package org.fissore.steroids;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * SMap is the default implementation of {@link SteroidMap}. By default it's backed by a {@link HashMap}.
 * It provides some useful constructors as well as implementations to {@link #map(String)}, {@link #subMap(Stream)} and {@link #subMap(Map, Stream)} methods
 */
public class SMap implements SteroidMap<String> {

  private final Map<String, Object> map;

  /**
   * Creates a new SMap, using {@link HashMap} as backing map
   */
  public SMap() {
    this(new HashMap<>());
  }

  /**
   * Creates a new SMap, using given map as backing map
   *
   * @param map the map to use as backing map
   */
  public SMap(Map<String, Object> map) {
    if (map == null) {
      throw new NullPointerException();
    }
    this.map = map;
  }

  /**
   * Creates a new SMap with one initial key/value mapping
   *
   * @param key   the first key
   * @param value the first value
   * @see #SMap()
   */
  public SMap(String key, Object value) {
    this();
    add(key, value);
  }

  /**
   * Creates a new SMap with two initial key/value mappings
   *
   * @param key1   the first key
   * @param value1 the first value
   * @param key2   the second key
   * @param value2 the second value
   * @see #SMap()
   */
  public SMap(String key1, Object value1, String key2, Object value2) {
    this();
    add(key1, value1);
    add(key2, value2);
  }

  /**
   * Creates a new SMap with three initial key/value mappings
   *
   * @param key1   the first key
   * @param value1 the first value
   * @param key2   the second key
   * @param value2 the second value
   * @param key3   the third key
   * @param value3 the third value
   * @see #SMap()
   */
  public SMap(String key1, Object value1, String key2, Object value2, String key3, Object value3) {
    this();
    add(key1, value1);
    add(key2, value2);
    add(key3, value3);
  }

  @SuppressWarnings("unchecked")
  @Override
  public SMap subMap(Stream<String> keys) {
    Map<String, Object> map = newInnerMapInstance();
    return subMap(map, keys);
  }

  private Map newInnerMapInstance() {
    try {
      return this.map.getClass().newInstance();
    } catch (Exception e) {
      String error = "Error while using reflection to create a new instance of " + this.map.getClass() + ". Consider using a backing map with a public default constructor or providing backing map on your own and calling subMap(Map, keys...)";
      throw new RuntimeException(error, e);
    }
  }

  @Override
  public SMap subMap(Map<String, Object> backingMap, Stream<String> keys) {
    SMap subMap = new SMap(backingMap);
    keys.forEach(key -> subMap.add(key, get(key)));
    return subMap;
  }

  @Override
  public SMap map(String key) {
    return ensureMapIsOnSteroid(get(key));
  }

  @SuppressWarnings("unchecked")
  @Override
  public SMap ensureMapIsOnSteroid(Object value) {
    if (value == null) {
      return null;
    }
    if (value instanceof SMap) {
      return (SMap) value;
    }
    if (value instanceof Map) {
      return new SMap((Map<String, Object>) value);
    }
    throw new IllegalArgumentException(value + " is neither a Map or a SteroidMap");
  }

  @Override
  public SMap add(String key, Object value) {
    return (SMap) SteroidMap.super.add(key, value);
  }

  @Override
  public SMap addAll(Map<String, Object>... sources) {
    return (SMap) SteroidMap.super.addAll(sources);
  }

  @Override
  public SMap addAll(Collection<Map<String, Object>> sources) {
    return (SMap) SteroidMap.super.addAll(sources);
  }

  @Override
  public SMap addAll(Stream<Map<String, Object>> sources) {
    return (SMap) SteroidMap.super.addAll(sources);
  }

  @Override
  public SMap addFrom(Map<String, Object> source, String... keys) {
    return (SMap) SteroidMap.super.addFrom(source, keys);
  }

  @Override
  public SMap addFrom(Map<String, Object> source, Collection<String> keys) {
    return (SMap) SteroidMap.super.addFrom(source, keys);
  }

  @Override
  public SMap renameKey(String oldKey, String newKey) {
    return (SMap) SteroidMap.super.renameKey(oldKey, newKey);
  }

  @Override
  public SMap del(String... keys) {
    return (SMap) SteroidMap.super.del(keys);
  }

  @Override
  public SMap del(Collection<String> keys) {
    return (SMap) SteroidMap.super.del(keys);
  }

  @Override
  public SMap del(Stream<String> keys) {
    return (SMap) SteroidMap.super.del(keys);
  }

  @Override
  public SMap subMap(String... keys) {
    return (SMap) SteroidMap.super.subMap(keys);
  }

  @Override
  public SMap subMap(Collection<String> keys) {
    return (SMap) SteroidMap.super.subMap(keys);
  }

  @Override
  public SMap subMap(Map<String, Object> backingMap, String... keys) {
    return (SMap) SteroidMap.super.subMap(backingMap, keys);
  }

  @Override
  public SMap subMap(Map<String, Object> backingMap, Collection<String> keys) {
    return (SMap) SteroidMap.super.subMap(backingMap, keys);
  }

  @Override
  public SMap map(String key, SteroidMap<String> defaultValue) {
    return (SMap) SteroidMap.super.map(key, defaultValue);
  }

  @Override
  public Stream<SMap> maps(String key) {
    return SteroidMap.super.maps(key).map(s -> (SMap) s);
  }

  @Override
  public Stream<? extends SMap> maps(String key, Stream<? extends SteroidMap<String>> defaultValue) {
    return SteroidMap.super.maps(key, defaultValue).map(s -> (SMap) s);
  }

  @Override
  public SMap copy() {
    return new SMap().addAll(this);
  }

  /* GENERATED DELEGATE METHODS */

  @Override
  public void clear() {
    map.clear();
  }

  @Override
  public Object compute(String key, BiFunction<? super String, ? super Object, ?> remappingFunction) {
    return map.compute(key, remappingFunction);
  }

  @Override
  public Object computeIfAbsent(String key, Function<? super String, ?> mappingFunction) {
    return map.computeIfAbsent(key, mappingFunction);
  }

  @Override
  public Object computeIfPresent(String key, BiFunction<? super String, ? super Object, ?> remappingFunction) {
    return map.computeIfPresent(key, remappingFunction);
  }

  @Override
  public boolean containsKey(Object key) {
    return map.containsKey(key);
  }

  @Override
  public boolean containsValue(Object value) {
    return map.containsValue(value);
  }

  @Override
  public Set<Entry<String, Object>> entrySet() {
    return map.entrySet();
  }

  @Override
  public boolean equals(Object o) {
    return map.equals(o);
  }

  @Override
  public void forEach(BiConsumer<? super String, ? super Object> action) {
    map.forEach(action);
  }

  @Override
  public Object get(Object key) {
    return map.get(key);
  }

  @Override
  public Object getOrDefault(Object key, Object defaultValue) {
    return map.getOrDefault(key, defaultValue);
  }

  @Override
  public int hashCode() {
    return map.hashCode();
  }

  @Override
  public boolean isEmpty() {
    return map.isEmpty();
  }

  @Override
  public Set<String> keySet() {
    return map.keySet();
  }

  @Override
  public Object merge(String key, Object value, BiFunction<? super Object, ? super Object, ?> remappingFunction) {
    return map.merge(key, value, remappingFunction);
  }

  @Override
  public Object put(String key, Object value) {
    return map.put(key, value);
  }

  @Override
  public void putAll(Map<? extends String, ?> m) {
    map.putAll(m);
  }

  @Override
  public Object putIfAbsent(String key, Object value) {
    return map.putIfAbsent(key, value);
  }

  @Override
  public Object remove(Object key) {
    return map.remove(key);
  }

  @Override
  public boolean remove(Object key, Object value) {
    return map.remove(key, value);
  }

  @Override
  public boolean replace(String key, Object oldValue, Object newValue) {
    return map.replace(key, oldValue, newValue);
  }

  @Override
  public Object replace(String key, Object value) {
    return map.replace(key, value);
  }

  @Override
  public void replaceAll(BiFunction<? super String, ? super Object, ?> function) {
    map.replaceAll(function);
  }

  @Override
  public int size() {
    return map.size();
  }

  @Override
  public Collection<Object> values() {
    return map.values();
  }

  @Override
  public String toString() {
    return map.toString();
  }

}
