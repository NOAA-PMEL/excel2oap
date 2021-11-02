/**
 * 
 */
package gov.noaa.pmel.excel2oap.xml;

import java.util.Collection;
import java.util.Map;

import gov.noaa.pmel.excel2oap.ElementType;
import gov.noaa.pmel.excel2oap.NonNullHashMap;
import gov.noaa.pmel.excel2oap.SpreadSheetHandlerBase;
import gov.noaa.pmel.excel2oap.SpreadSheetKeys;
import gov.noaa.pmel.excel2oap.ifc.XmlBuilder;


/**
 * @author kamb
 *
 */
public abstract class XmlBuilderBase extends SpreadSheetHandlerBase implements XmlBuilder {

//    protected boolean omitEmptyElements = true;
    
    protected XmlBuilderBase(Map<ElementType, Collection<Map<String, String>>> multiItems,
                              Map<String, String> simpleItems,
                              SpreadSheetKeys keys) { //, boolean omitEmptyElements) {
        super(multiItems, simpleItems, keys);
//        this.omitEmptyElements = omitEmptyElements;
    }
    
}
