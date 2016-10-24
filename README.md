# Steroid Map

Steroid Map is a Java 8 fluent interface that makes working with maps much easier. So much easier, that you'll start
considering using maps instead of plain **old** java beans.

Steroid Map was born to make it easier to both consume and produce JSON API responses without the need to back each and everyone of them with annotated java beans. It ended up being a replacement for java beans in general.

Some advantages come from two technologies we were using: JSON APIs and SQL Mappers.

When using maps, JSON (de)serializers output only what's in the map: if you want more data, you put more data in the map. You don't need to add fields to a java bean and to annotate them properly.

When using maps, SQL mappers (like MyBatis) will produce lists of maps with the columns you put in your `select` statements. If you want more or less data, you change your `select` statements, thus modifying one single line of code.
Instead, when using java beans, you also need to add and annotate fields. And if that java beans ends up having too much data for some APIs, you end up writing different version of the same java bean (User, UserWithoutPassword, PublicUser...)

If you've just started with SteroidMap, its default implementation, [SMap](http://ffissore.github.io/SteroidMap/apidocs/index.html), has everything you need to play with it. And these are the only two classes of this project, in case you're wondering how much complex is it.

## POM snippet

```xml
<dependency>
  <groupId>org.fissore.steroids</groupId>
  <artifactId>steroidmap</artifactId>
  <version>3.0.0</version>
</dependency>
```

## Examples

Here are some example usages. [Actual code available on github](https://github.com/ffissore/SteroidMap/blob/master/src/test/java/org/fissore/steroids/ExamplesTest.java#L13).

Given the following map (here represented as JSON) stored in a variable `mymap` of type `SteroidMap<String>` or `SMap`:

```json
{
    "name": "John",
    "surname": "Smith",
    "address": {
        "streetname": "One way",
        "number": 1
    },
    "friends": [{
        "name": "Jane",
        "surname": "Doe",
        "social": "twitter handle"
    }, {
        "name": "John",
        "surname": "Doe",
        "social": "facebook profile"
    }, {
        "name": "Jane",
        "surname": "Smith"
    }]
}
```

### Adding height and weight
```java
mymap
    .add("height", 187)
    .add("weight", 90);
```

### Getting the name
```java
String name = mymap.s("name");
```

### Renaming a key
```java
mymap
    .map("address")
    .renameKey("streetname", "street");
```

### Getting the street name
```java
String streetname = mymap
    .map("address")
    .s("streetname");
```

### Collecting friends's names when their surname is "Doe"
```java
List<String> names = mymap
    .maps("friends")
    .filter(friend -> "Doe".equals(friend.s("surname")))
    .map(friend -> friend.s("name"))
    .collect(Collectors.toList());
```

### Collecting friends with a social account
```java
List<SMap> friends = mymap
    .maps("friends")
    .filter(friend -> friend.valued("social"))
    .collect(Collectors.toList());
```

### Create a new map filtering mymap
```java
List<SMap> friendsSubMaps = mymap
    .maps("friends")
    .map(friend -> friend.subMap("name", "surname"))
    .collect(Collectors.toList());

SMap submap = mymap
    .subMap("name", "surname")
    .add("friends", friendsSubMaps);
```

This code creates a new map picking some values from input map and friends submaps. It will produce this

```json
{
    "name": "John",
    "surname": "Smith",
    "friends": [{
        "name": "Jane",
        "surname": "Doe",
    }, {
        "name": "John",
        "surname": "Doe",
    }, {
        "name": "Jane",
        "surname": "Smith"
    }]
}
```

## Javadoc

SteroidMap comes with short methods names for every data type available. Consult the [javadoc](http://ffissore.github.io/SteroidMap/apidocs/index.html) to have a list of them.