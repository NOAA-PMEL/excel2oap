/**
 * 
 */
package gov.noaa.pmel.excel2oap;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.jdom2.Document;

import gov.noaa.pmel.sdimetadata.SDIMetadata;

/**
 * @author kamb
 *
 */
public interface SpreadSheetFlavor {
    
    public SpreadSheetType getSpreadSheetType();
    
    public SpreadSheetKeys getKeys();
    
    public ElementType elementForKey(String key);
    
    public Map<String, String> getSingleFields();
    public Map<ElementType, Collection<Map<String, String>>> getMultiItemFields();

//    public SDIMetadata processRows(List<SsRow> rows);
    
    public void addOtherStuff(Document doc);
    
    public void add_PI(SDIMetadata sdi, Map<String, String> parts);
    public void add_INVESTIGATOR(SDIMetadata sdi, Map<String, String> parts);
    public void add_DATA_SUBMITTER(SDIMetadata sdi, Map<String, String> parts);
    public void add_PLATFORM(SDIMetadata sdi, Map<String, String> parts);
    public void add_FUNDING(SDIMetadata sdi, Map<String, String> parts);
    
    public default void add_DIC(Document doc) {}
    public default void add_TA(Document doc) {}
    public default void add_PH(Document doc) {}
    public default void add_PCO2A(Document doc) {}
    public default void add_PCO2D(Document doc) {}
//    public void add_VAR(Document doc);
    public void add_VARs(Document doc);
    public default void add_xCO2(Document doc) {}
    public default void add_pCO2(Document doc) {}
    public default void add_fCO2(Document doc) {}
    public default void add_CO2common(Document doc) {}
    public default void add_Temperature(Document doc) {}
    public default void add_Salinity(Document doc) {}
    public default void add_Depth(Document doc) {}
}
