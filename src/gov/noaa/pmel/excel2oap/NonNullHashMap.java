/**
 * 
 */
package gov.noaa.pmel.excel2oap;

import java.util.TreeMap;

/**
 * @author kamb
 *
 */
public class NonNullHashMap<K, V> extends TreeMap<String, String> {

    private static final long serialVersionUID = 8695661407529893952L;

    public NonNullHashMap() { super(String.CASE_INSENSITIVE_ORDER); }

//    public NonNullHashMap(int initialCapacity) { super(initialCapacity); }

//    public NonNullHashMap(Map<String, String> m) { super(m); }

//    @Override
    public String get(String key) {
        String v = containsKey(key) ? super.get(key) : "";
        return v;
    }
}
