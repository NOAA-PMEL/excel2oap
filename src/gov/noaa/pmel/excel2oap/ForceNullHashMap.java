/**
 * 
 */
package gov.noaa.pmel.excel2oap;

import java.util.Set;
import java.util.TreeMap;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import gov.noaa.pmel.tws.util.StringUtils;

/**
 * @author kamb
 *
 */
public class ForceNullHashMap<K, V> extends TreeMap<String, String> {

    private static final Logger logger = LogManager.getLogger(ForceNullHashMap.class);
    
    private static final long serialVersionUID = 8695661407529893952L;

    public ForceNullHashMap() { super(String.CASE_INSENSITIVE_ORDER); }

//    public NonNullHashMap(int initialCapacity) { super(initialCapacity); }

//    public NonNullHashMap(Map<String, String> m) { super(m); }

    @Override
    public String get(Object key) {
        String v = containsKey(key) ? super.get(key) : lookForIt(key);
        return StringUtils.emptyOrNull(v) ? null : v;
    }
    
    private String lookForIt(Object key) {
        logger.debug("Looking for key: " + key);
        String keyStr = (String)key;
        Set<String> keys = super.keySet();
        for (String k : keys) {
            if (k.toLowerCase().startsWith(keyStr.toLowerCase())) {
                logger.debug("found match: " + k);
                return super.get(k);
            }
        }
        return null;
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