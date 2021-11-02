/**
 * 
 */
package gov.noaa.pmel.excel2oap.ocads;

import java.lang.reflect.Field;
import java.util.HashMap;

import gov.noaa.pmel.excel2oap.ElementType;
import gov.noaa.pmel.excel2oap.SpreadSheetKeys;

/**
 * @author kamb
 *
 */
public class OcadsKeys extends SpreadSheetKeys {

    public OcadsKeys() {
        declaredFields = getDeclaredFields(new HashMap<String, Field>(), this.getClass());
    }
        
    public ElementType getElementForKey(String key) {
        return OcadsElementType.fromSsRowName(key);
    }

    /*
     * !!!!
     * The definitive (starting, ocads) key list is in SpreadSheetKeys
     * !!!!
     */
    
}
