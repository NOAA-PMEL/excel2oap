/**
 * 
 */
package gov.noaa.pmel.excel2oap;

import java.util.Map;

/**
 * @author kamb
 *
 */
public interface ElementType {

    public String name();
    
    static ElementType forKey(String key, Map<String, ElementType> elementMap) {
        if ( elementMap.containsKey(key)) {
            return elementMap.get(key);
        }
        for (String k : elementMap.keySet()) {
            if ( key.startsWith(k)) {
                return elementMap.get(k);
            }
        }
//        throw new IllegalStateException("No element found for cell name key: " + key);
        return null;
    }
}
