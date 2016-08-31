package org.fissore.steroids;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class SteroidMapTest {

  private static class Something {
  }

  private static class PrivateMap extends HashMap<String, Object> {
    private PrivateMap() {
    }
  }

  private Something something;
  private Date date;
  private List<Integer> list;
  private HashSet<String> coll;
  private SMap submap;
  private SMap map;
  private Map<String, Object> simpleMap;

  @Before
  public void setUp() {
    something = new Something();
    date = new Date();
    list = Arrays.asList(1, 2, 3);
    coll = new HashSet<>(Arrays.asList("3", "4", "5", "6"));
    submap = new SMap().add("key1", "hello").add("key2", 42);
    simpleMap = new HashMap<>();
    map = new SMap()
        .add("key1", "string1")
        .add("key2", "string2")
        .add("list", list)
        .add("coll", coll)
        .add("int", Integer.MAX_VALUE)
        .add("long", Long.MAX_VALUE)
        .add("double", Double.MAX_VALUE)
        .add("float", Float.MAX_VALUE)
        .add("boolean", true)
        .add("date", date)
        .add("object", something)
        .add("submap", submap)
        .add("simpleMap", simpleMap);
  }

  @Test
  public void shouldBuildAnSMapWithTheGivenEntries() {
    SMap key1 = new SMap("key1", 1);
    assertTrue(key1.valued("key1"));
    assertFalse(key1.valued("key2"));
    assertFalse(key1.valued("key3"));

    SMap key2 = new SMap("key1", 1, "key2", 2);
    assertTrue(key2.valued("key1"));
    assertTrue(key2.valued("key2"));
    assertFalse(key2.valued("key3"));

    SMap key3 = new SMap("key1", 1, "key2", 2, "key3", 3);
    assertTrue(key3.valued("key1"));
    assertTrue(key3.valued("key2"));
    assertTrue(key3.valued("key3"));
  }

  @Test(expected = NullPointerException.class)
  public void shouldFailToWrapANullMap() {
    new SMap(null);
  }

  @Test
  public void shouldReportIfValuedNotValued() {
    assertTrue(map.valued("key1"));
    assertFalse(map.notValued("key1"));
    assertFalse(map.valued("nonexistent"));
    assertTrue(map.notValued("nonexistent"));
  }

  @Test
  public void shouldAddSingleElement() {
    assertTrue(map.notValued("key3"));
    map.add("key3", new LinkedList<>());
    assertTrue(map.valued("key3"));
    assertTrue(map.notValued("empty_key"));
    map.add("empty_key", null);
    assertTrue(map.notValued("empty_key"));
  }

  @Test
  public void shouldAddMultipleElements() {
    Map<String, Object> other = new HashMap<>();
    other.put("hello", "world");
    other.put("1plus2", 3);

    assertTrue(map.valued("key1"));
    assertTrue(map.notValued("hello"));
    assertTrue(map.notValued("1plus2"));

    map.addAll(other).add("onemore", 2);

    assertTrue(map.valued("key1"));
    assertTrue(map.valued("hello"));
    assertTrue(map.valued("1plus2"));
  }

  @Test
  public void shouldAllAllElementsFromOtherMaps() {
    Map<String, Object> other1 = new HashMap<>();
    other1.put("one", 1);
    other1.put("two", 2);

    Map<String, Object> other2 = new HashMap<>();
    other2.put("three", 3);
    other2.put("four", 4);
    other2.put("null", null);

    SMap mergedMap = map.addAll(other1, other2);
    assertNotNull(mergedMap);
    assertTrue(mergedMap.containsKey("key1"));
    assertTrue(mergedMap.containsKey("one"));
    assertTrue(mergedMap.containsKey("two"));
    assertTrue(mergedMap.containsKey("three"));
    assertTrue(mergedMap.containsKey("four"));
    assertFalse(mergedMap.containsKey("null"));
  }

  @Test
  public void shouldAllAllElementsFromCollectionOfOtherMaps() {
    Map<String, Object> other1 = new HashMap<>();
    other1.put("one", 1);
    other1.put("two", 2);

    Map<String, Object> other2 = new HashMap<>();
    other2.put("three", 3);
    other2.put("four", 4);
    other2.put("null", null);

    SMap mergedMap = map.addAll(Arrays.asList(other1, other2));
    assertNotNull(mergedMap);
    assertTrue(mergedMap.containsKey("key1"));
    assertTrue(mergedMap.containsKey("one"));
    assertTrue(mergedMap.containsKey("two"));
    assertTrue(mergedMap.containsKey("three"));
    assertTrue(mergedMap.containsKey("four"));
    assertFalse(mergedMap.containsKey("null"));
  }

  @Test
  public void shouldRenameAKey() {
    map.renameKey("nonexistent", "whatever").renameKey("key1", "key3").add("onemore", 2);
    assertNull(map.s("key1"));
    assertEquals("string2", map.s("key2"));
    assertEquals("string1", map.s("key3"));
  }

  @Test
  public void shouldMergeSomeKeysOfAnotherMap() {
    Map<String, Object> other = new HashMap<>();
    other.put("one", "1");
    other.put("two", 2);
    other.put("three", 3.0f);

    assertTrue(map.valued("key1"));
    assertTrue(map.notValued("hello"));
    assertTrue(map.notValued("1plus2"));

    map.addFrom(other, "one", "three", "nonexistent").add("onemore", 2);

    assertTrue(map.valued("key1"));
    assertTrue(map.valued("one"));
    assertTrue(map.notValued("two"));
    assertTrue(map.valued("three"));
    assertTrue(map.notValued("nonexistent"));

    Map<String, Object> yetAnother = new HashMap<>();
    yetAnother.put("four", "4");
    yetAnother.put("five", 5);
    yetAnother.put("six", 6);

    assertFalse(map.valued("four"));
    assertFalse(map.valued("five"));
    assertFalse(map.valued("six"));
    assertFalse(map.valued("yetanother"));
    map.addFrom(yetAnother).add("yetanother", "");
    assertTrue(map.valued("four"));
    assertTrue(map.valued("five"));
    assertTrue(map.valued("six"));
    assertTrue(map.valued("yetanother"));
  }

  @Test
  public void shouldNotMergeNullMaps() {
    assertEquals(13, map.size());
    map.addFrom(null).add("addthis", 1);
    assertEquals(14, map.size());
  }

  @Test
  public void shouldDeleteAKey() {
    assertTrue(map.valued("key1"));
    map.del("key1").add("onemore", 2);
    assertTrue(map.notValued("key1"));
  }

  @Test
  public void shouldDeleteCollectionOfKeys() {
    assertTrue(map.valued("key1"));
    assertTrue(map.valued("key2"));
    map.del(Arrays.asList("key1", "key2")).add("onemore", 2);
    assertTrue(map.notValued("key1"));
    assertTrue(map.notValued("key2"));
  }

  @Test
  public void shouldReturnALong() {
    assertEquals(Long.MAX_VALUE, map.l("long"));
    assertEquals(1L, map.l("long1", 1L));
  }

  @Test(expected = NullPointerException.class)
  public void shouldFailToReturnALong() {
    map.l("non existent");
  }

  @Test
  public void shouldReturnAnInteger() {
    assertEquals(Integer.MAX_VALUE, map.i("int"));
    assertEquals(1, map.i("int1", 1));
  }

  @Test(expected = NullPointerException.class)
  public void shouldFailToReturnAnInteger() {
    map.i("non existent");
  }

  @Test
  public void shouldReturnADouble() {
    assertEquals(Double.MAX_VALUE, map.d("double"), 0d);
    assertEquals(1.1d, map.d("double1", 1.1d), 0d);
  }

  @Test(expected = NullPointerException.class)
  public void shouldFailToReturnADouble() {
    map.d("non existent");
  }

  @Test
  public void shouldReturnAFloat() {
    assertEquals(Float.MAX_VALUE, map.f("float"), 0f);
    assertEquals(1.1f, map.f("float1", 1.1f), 0f);
  }

  @Test(expected = NullPointerException.class)
  public void shouldFailToReturnAFloat() {
    map.f("non existent");
  }

  @Test
  public void shouldReturnAString() {
    assertEquals("string1", map.s("key1"));
    assertEquals("string2", map.s("key2"));
    assertNull(map.s("key3"));
    assertEquals("string3", map.s("key3", "string3"));
  }

  @Test
  public void shouldReturnABoolean() {
    assertTrue(map.b("boolean"));
    assertTrue(map.b("boolean1", true));
  }

  @Test(expected = NullPointerException.class)
  public void shouldFailToReturnABoolean() {
    map.b("non existent");
  }

  @Test
  public void shouldReturnAnObject() {
    Something somethingElse = new Something();
    assertEquals(something, map.o("object", somethingElse));
    assertNull(map.o("object1"));
    assertEquals(somethingElse, map.o("object1", somethingElse));

    Something something1 = map.o("object", Something.class);
    assertNotNull(something1);
    Something somethingElse1 = map.o("object1", somethingElse);
    assertNotNull(somethingElse1);
  }

  @Test
  public void shouldReturnADate() {
    assertEquals(date, map.date("date"));
    assertNull(map.date("date1"));
    Date now = new Date();
    assertEquals(now, map.date("date1", now));
  }

  @Test
  public void shouldReturnACollection() {
    assertEquals(coll, map.collection("coll"));
    List<String> list = new LinkedList<>(map.collection("coll"));
    assertEquals(4, list.size());
    assertEquals("3", list.get(0));
    assertEquals("4", list.get(1));
    assertEquals("5", list.get(2));
    assertEquals("6", list.get(3));

    assertNull(map.collection("coll1"));
    Collection<String> coll1 = new LinkedList<>(coll);
    assertEquals(coll1, map.collection("coll1", coll1));
  }

  @Test
  public void shouldReturnAList() {
    assertEquals(list, map.collection("list"));
    List<Integer> thatList = map.list("list");
    assertEquals(3, thatList.size());
    assertEquals(1, thatList.get(0).intValue());
    assertEquals(2, thatList.get(1).intValue());
    assertEquals(3, thatList.get(2).intValue());

    assertNull(map.list("list1"));
    List<Integer> list1 = new LinkedList<>(list);
    assertEquals(list1, map.list("list1", list1));
  }

  @Test
  public void shouldReturnAStream() {
    assertEquals(list, map.stream("list").collect(Collectors.toList()));
    Stream<Integer> list = map.stream("list");
    List<Integer> thatList = list.collect(Collectors.toList());
    assertEquals(3, thatList.size());
    assertEquals(1, thatList.get(0).intValue());
    assertEquals(2, thatList.get(1).intValue());
    assertEquals(3, thatList.get(2).intValue());

    assertNull(map.list("list1"));
    List<Integer> list1 = new LinkedList<>(this.list);
    assertEquals(list1, map.list("list1", list1));

    List<Integer> nonexistent = map.stream("nonexistent", Arrays.asList(1, 2, 3)).collect(Collectors.toList());
    assertEquals(nonexistent, Arrays.asList(1, 2, 3));
  }

  @Test
  public void shouldReturnAMap() {
    assertEquals(submap, map.map("submap"));
    assertTrue(submap == map.map("submap"));
    assertEquals("hello", map.map("submap").s("key1"));
    assertEquals(42, map.map("submap").i("key2"));

    assertEquals(simpleMap, map.map("simpleMap"));

    assertNull(map.list("map1"));
    SMap map1 = new SMap();
    assertEquals(map1, map.map("map1", map1));
  }

  @Test
  public void shouldReturnMaps() throws Exception {
    List<SMap> maps = Arrays.asList(new SMap("key", "value1"), new SMap("key", "value2"));
    map.add("maps", maps);

    List<SMap> maps1 = map.maps("maps").collect(Collectors.toList());
    assertEquals(maps, maps1);

    List<SMap> maps2 = map.maps("maps")
        .filter(map -> map.s("key").equals("value1"))
        .collect(Collectors.toList());

    assertEquals(1, maps2.size());
    assertEquals(maps2.get(0), new SMap("key", "value1"));
  }

  @Test(expected = IllegalArgumentException.class)
  public void shouldFailToCallMapsWithNonMaps() throws Exception {
    map.maps("list").count();
  }

  @Test
  public void shouldNavigateSubKeys() {
    submap.add("subsubmap", new SMap().add("hello", "world"));

    assertEquals("world", map.map("submap").map("subsubmap").s("hello"));
  }

  @Test
  public void shouldReturnASubMapOfKeys() {
    SMap listColl = map.subMap("list", "coll");
    assertEquals(2, listColl.size());
    assertTrue(listColl.valued("list"));
    assertTrue(listColl.valued("coll"));
  }

  @Test
  public void shouldReturnASubMapOfCollectionOfKeys() {
    SMap listColl = map.subMap(Arrays.asList("list", "coll"));
    assertEquals(2, listColl.size());
    assertTrue(listColl.valued("list"));
    assertTrue(listColl.valued("coll"));
  }

  @Test
  public void shouldReturnASubMapOfKeysWithSpecialMapImpl() {
    SMap listColl = map.subMap(new ConcurrentHashMap<>(), "list", "coll");
    assertEquals(2, listColl.size());
    assertTrue(listColl.valued("list"));
    assertTrue(listColl.valued("coll"));
  }

  @Test
  public void shouldReturnASubMapOfCollectionOfKeysWithSpecialMapImpl() {
    SMap listColl = map.subMap(new ConcurrentHashMap<>(), Arrays.asList("list", "coll"));
    assertEquals(2, listColl.size());
    assertTrue(listColl.valued("list"));
    assertTrue(listColl.valued("coll"));
  }

  @Test(expected = RuntimeException.class)
  public void shouldThrowExceptionWhenAttemptingToReturnASubMap() {
    new SMap(new PrivateMap()).subMap();
  }

  @Test
  public void shouldReturnACopy() throws Exception {
    SMap copy = map.copy();
    map.keySet().forEach(key -> {
      assertTrue(copy.valued(key));
      assertTrue(map.valued(key));
    });

    copy.renameKey("key1", "key one");
    assertTrue(copy.notValued("key1"));
    assertTrue(copy.valued("key one"));

    assertTrue(map.valued("key1"));
    assertTrue(map.notValued("key one"));
  }
}
