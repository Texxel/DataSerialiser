package com.github.texxel.data;

import com.github.texxel.data.ClassMap;
import com.github.texxel.data.PData;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ClassMapTest {

    // Create some classes to test with
    //
    //           Alive                   Falloffable
    //             |                          |
    //           Animal          Thing    Mountable
    //    +--------+-------+         |    |
    //   Moose   Squid   Horse ---> Rideable
    //

    interface Alive {};
    interface Falloffable {};
    interface Mountable extends Falloffable {};
    interface Thing {};
    interface Rideable extends Thing {};

    class Animal implements Alive {}
    class Squid extends Animal {};
    class Moose extends Animal {};
    class Horse extends Animal implements Rideable, Mountable {};

    @Test
    public void testDirectPut() {
        ClassMap<Animal, String> map = new ClassMap<>();
        map.put( Squid.class, "squid" );
        map.put( Moose.class, "moose" );

        assertEquals( "squid", map.get( Squid.class ) );
        assertEquals( "moose", map.get( Moose.class ) );
    }

    @Test
    public void testClassHeirachicalPut() {
        ClassMap<Animal, String> map = new ClassMap<>();
        map.put( Squid.class, "squid" );
        map.put( Animal.class, "animal" );

        assertEquals( "squid", map.get( Squid.class ) );
        assertEquals( "animal", map.get( Moose.class ) );
    }

    @Test
    public void testInterfaceHeirachicalPut() {
        ClassMap<Object, String> map = new ClassMap<>();
        map.put( Squid.class, "squid" );
        map.put( Rideable.class, "rideable" );

        assertEquals( "squid", map.get( Squid.class ) );
        assertEquals( "rideable", map.get( Horse.class ) );
    }

    @Test
    public void testClassHierarchyBeforeInterface() {
        ClassMap<Object, String> map = new ClassMap<>();
        map.put( Rideable.class, "rideable" );
        map.put( Animal.class, "animal" );

        assertEquals( "animal", map.get( Horse.class ) );
    }

    @Test
    public void testDirectInterfaceBeforeHighInterface() {
        ClassMap<Object, String> map = new ClassMap<>();
        map.put(Rideable.class, "rideable" );
        map.put(Thing.class, "thing");

        assertEquals("rideable", map.get(Horse.class));
    }

    @Test
    public void testChildInterfacesBeforeParentInterfaces() {
        ClassMap<Object, String> map = new ClassMap<>();
        map.put(Falloffable.class, "fall" );
        map.put(Thing.class, "thing");

        assertEquals("thing", map.get(Horse.class));
    }

    @Test
    public void testClassInterfacesBeforeParentClassInterfaces() {
        ClassMap<Object, String> map = new ClassMap<>();
        map.put(Falloffable.class, "fall" );
        map.put(Alive.class, "alive");

        assertEquals("fall", map.get(Horse.class));
    }

    @Test
    public void testArraysAreObjectsToo() {
        ClassMap<Object, String> map = new ClassMap<>();
        map.put(Object.class, "object");
        map.put(Object[].class, "object[]");
        map.put(int[][].class, "int[][]");

        // maybe int[][] should go to int[] ?

        assertEquals("object", map.get(float[][].class));
        assertEquals("int[][]", map.get(int[][].class));
        assertEquals("object", map.get(int[].class));
    }

    @Test
    public void testEnumsExtendEnums() {
        ClassMap<Object, String> map = new ClassMap<>();
        map.put(Object.class, "object");
        map.put(Enum.class, "enum");

        Class testEnum = PData.Type.class;
        assertEquals("enum", map.get(testEnum));
    }

}