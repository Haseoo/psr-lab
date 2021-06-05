package pl.kielce.tu.weaii.psr.cassandra;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class NonEmptyHashData implements Map<String, String> {
    private final HashMap<String, String> map = new HashMap<>();

    @Override
    public int size() {
        return map.size();
    }

    @Override
    public boolean isEmpty() {
        return map.isEmpty();
    }

    @Override
    public boolean containsKey(Object o) {
        return map.containsKey(o);
    }

    @Override
    public boolean containsValue(Object o) {
        return map.containsValue(o);
    }

    @Override
    public String get(Object o) {
        return map.get(o);
    }

    @Override
    public String put(String key, String value) {
        if(Main.isNotNullNorEmpty(value)) {
            return map.put(key, value);
        }
        else return null;
    }

    @Override
    public String remove(Object o) {
        return map.remove(o);
    }

    @Override
    public void putAll(Map<? extends String, ? extends String> map) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        map.clear();
    }

    @Override
    public Set<String> keySet() {
        return map.keySet();
    }

    @Override
    public Collection<String> values() {
        return map.values();
    }

    @Override
    public Set<Entry<String, String>> entrySet() {
        return map.entrySet();
    }
}
