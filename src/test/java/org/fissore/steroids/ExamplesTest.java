package org.fissore.steroids;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class ExamplesTest {

  private SMap mymap;

  @Before
  public void setUp() throws Exception {
    mymap =
        new SMap()
            .add("name", "John")
            .add("surname", "Smith")
            .add("address", new SMap()
                .add("streetname", "One way")
                .add("number", 1))
            .add("friends", Arrays.asList(
                new SMap()
                    .add("name", "Jane")
                    .add("surname", "Doe")
                    .add("social", "twitter handle"),
                new SMap()
                    .add("name", "John")
                    .add("surname", "Doe")
                    .add("social", "facebook profile"),
                new SMap()
                    .add("name", "Jane")
                    .add("surname", "Smith")
            ));
  }

  @Test
  public void addWeightAndHeight() throws Exception {
    mymap
        .add("height", 187)
        .add("weight", 90);

    assertEquals(187, mymap.i("height"));
    assertEquals(90, mymap.i("weight"));
  }

  @Test
  public void getName() throws Exception {
    String name = mymap.s("name");
    assertEquals("John", name);
  }

  @Test
  public void renameKey() throws Exception {
    mymap
        .map("address")
        .renameKey("streetname", "street");
    assertTrue(mymap.map("address").valued("street"));
    assertFalse(mymap.map("address").valued("streetname"));
  }

  @Test
  public void getStreetName() throws Exception {
    String streetname = mymap
        .map("address")
        .s("streetname");

    assertEquals("One way", streetname);
  }

  @Test
  public void nameOfDoeFriends() throws Exception {
    List<String> names = mymap
        .maps("friends")
        .filter(friend -> "Doe".equals(friend.s("surname")))
        .map(friend -> friend.s("name"))
        .collect(Collectors.toList());

    Collections.sort(names);

    assertEquals(Arrays.asList("Jane", "John"), names);
  }

  @Test
  public void socialFriends() throws Exception {
    List<SMap> friends = mymap
        .maps("friends")
        .filter(friend -> friend.valued("social"))
        .collect(Collectors.toList());

    assertEquals(2, friends.size());
  }

  @Test
  public void transformUserAndFriendsMaps() throws Exception {
    List<SMap> friendsSubMaps = mymap
        .maps("friends")
        .map(friend -> friend.subMap("name", "surname"))
        .collect(Collectors.toList());

    SMap submap = mymap
        .subMap("name", "surname")
        .add("friends", friendsSubMaps);

    assertTrue(submap.valued("name"));
    assertTrue(submap.valued("surname"));
    assertFalse(submap.valued("address"));
    submap.maps("friends").forEach(friend -> {
      assertTrue(friend.valued("name"));
      assertTrue(submap.valued("surname"));
      assertFalse(friend.valued("address"));
    });

    System.out.println(submap);
  }
}
