/**
 * 
 */
package gov.noaa.pmel.excel2oap;

import java.util.HashMap;
import java.util.Map;

/**
 * @author kamb
 *
 */
public class NonNullHashMap<K, V> extends HashMap<String, String> {

    private static final long serialVersionUID = 8695661407529893952L;

    public NonNullHashMap() { super(); }

    public NonNullHashMap(int initialCapacity) { super(initialCapacity); }

    public NonNullHashMap(Map<String, String> m) { super(m); }

//    @Override
    public String get(String key) {
        String v = containsKey(key) ? super.get(key) : "";
        return v;
    }
}
