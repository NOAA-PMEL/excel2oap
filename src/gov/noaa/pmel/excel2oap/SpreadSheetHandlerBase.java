/**
 * 
 */
package gov.noaa.pmel.excel2oap;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author kamb
 *
 */
public abstract class SpreadSheetHandlerBase {

    protected SpreadSheetKeys ssKeys;
    
    protected Map<ElementType, Collection<Map<String, String>>> multiItems = 
                        new HashMap<ElementType, Collection<Map<String,String>>>();

    protected Map<String, String> simpleItems;

    protected Map<String, String> getSingularItem(String keyName) {
        ElementType type = ssKeys.getElementForKey(keyName);
        Collection<Map<String, String>> c = multiItems.get(type);
        if ( c == null || c.isEmpty()) {
            return null;
        }
        if ( c.size() > 1 ) {
            System.err.println("More than one item for getSingular " + type.name());
        }
        return c.iterator().next();
    }

    protected Collection<Map<String, String>> getMultiItem(String keyName) {
        ElementType type = ssKeys.getElementForKey(keyName);
        Collection<Map<String, String>> c = multiItems.get(type);
        if ( c == null || c.isEmpty()) {
            return null;
        }
        return c;
    }

    /**
     * 
     */
    protected SpreadSheetHandlerBase(Map<ElementType, Collection<Map<String, String>>> multiItems,
                                     Map<String, String> simpleItems,
                                     SpreadSheetKeys keys) {
        this.ssKeys = keys;
        this.multiItems = multiItems;
        this.simpleItems = simpleItems;
    }
    
}
