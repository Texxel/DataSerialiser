package com.github.texxel.data;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * A map between a classes and objects. If a class does not have a mapping, then the map will
 * walk up the class' hierarchy until a class that is registered will be found. The map will first
 * walk up the class' concrete hierarchy (if the class is a class) and then walk the interface
 * hierarchy. The interfaces of the requested class will be walked before the interfaces of the
 * super classes.
 * @param <K> the type of classes that can be registered
 * @param <V> the type of values that can be registered
 */
class ClassMap<K, V> implements Map<Class<? extends K>, V> {

    private HashMap<Class<? extends K>, V> map = new HashMap<>();
    private HashMap<Class<? extends K>, V> cache = new HashMap<>();

    @Override
    public int size() {
        return map.size();
    }

    @Override
    public boolean isEmpty() {
        return map.isEmpty();
    }

    @Override
    public boolean containsKey( Object key ) {
        return false;
    }

    @Override
    public boolean containsValue( Object value ) {
        return false;
    }

    @Override
    public V get( Object key ) {
        if (!(key instanceof Class))
            return null;
        Class clazz = (Class)key;

        // look up the cache first
        if ( cache.containsKey( clazz ))
            return cache.get( clazz );

        // find the value
        V value = find(clazz);

        cache.put( clazz, value );
        return value;
    }

    private V find(Class clazz) {
        V value = map.get( clazz );
        if ( value != null )
            return value;

        // walk the concrete class hierarchy
        Class parent = clazz.getSuperclass();
        while (parent != null) {
            value = map.get(parent);
            if (value != null)
                return value;
            parent = parent.getSuperclass();
        }

        // walk the interface hierarchy
        parent = clazz;
        while (parent != null) {
            value = findInterface(parent);
            if (value != null)
                return value;
            parent = parent.getSuperclass();
        }
        return null;
    }

    private V findInterface(Class clazz) {
        Class[] ifaces = clazz.getInterfaces();
        // test each interface directly
        for ( Class iface : ifaces ) {
            V value = map.get(iface);
            if ( value != null ) {
                return value;
            }
        }
        // walk up the tree
        for ( Class iface : ifaces ) {
            V value = findInterface(iface);
            if ( value != null ) {
                return value;
            }
        }
        return null;
    }

    @Override
    public V put( Class<? extends K> key, V value ) {
        cache.clear();
        return map.put( key, value );
    }

    @Override
    public V remove( Object key ) {
        cache.clear();
        return map.remove( key );
    }

    @Override
    public void putAll( Map<? extends Class<? extends K>, ? extends V> m ) {
        cache.clear();
        map.putAll( m );
    }

    @Override
    public void clear() {
        cache.clear();
        map.clear();
    }

    /**
     * A key set backed by this map. The set cannot be modified.
     * @return the unmodifiable set
     */
    @Override
    public Set<Class<? extends K>> keySet() {
        // we cannot let the set be modified since we need to clear the cache
        return Collections.unmodifiableSet( map.keySet() );
    }

    /**
     * The values collection backed by this map. The collection cannot be modified
     * @return the values in this map
     */
    @Override
    public Collection<V> values() {
        return Collections.unmodifiableCollection( map.values() );
    }

    /**
     * The entry set backed by this map. The set cannot be modified.
     * @return the entry set
     */
    @Override
    public Set<Entry<Class<? extends K>, V>> entrySet() {
        return Collections.unmodifiableSet( map.entrySet() );
    }

}