/**
 * 
 */
package gov.noaa.pmel.excel2oap;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import gov.noaa.pmel.tws.util.StringUtils;

/**
 * @author kamb
 *
 */
public class NonNullHashMap<K, V> extends TreeMap<String, String> {

    private static final Logger logger = LogManager.getLogger(NonNullHashMap.class);
    
    private static final long serialVersionUID = 8695661407529893952L;

    public NonNullHashMap() { super(String.CASE_INSENSITIVE_ORDER); }

//    public NonNullHashMap(int initialCapacity) { super(initialCapacity); }

    public NonNullHashMap(Map<String, String> m) { super(m); }

    @Override
    public String get(Object key) {
        String v = containsKey(key) ? super.get(key) : lookForIt(key);
        return v;
    }
    
    private String lookForIt(Object key) {
        logger.trace("Looking for key: " + key);
        String keyStr = (String)key;
        Set<String> keys = super.keySet();
        for (String k : keys) {
            if (k.toLowerCase().startsWith(keyStr.toLowerCase())) {
                logger.trace("\t- found match: " + k);
                return super.get(k);
            }
        }
        return "";
    }
    
    @Override
    public boolean isEmpty() {
        if ( super.isEmpty()) { return true; }
        for (String key : keySet()) {
            if ( ! StringUtils.emptyOrNull(get(key))) { return false; }
        }
        return true;
    }
}
