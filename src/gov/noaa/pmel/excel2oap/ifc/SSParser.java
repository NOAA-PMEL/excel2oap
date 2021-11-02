/**
 * 
 */
package gov.noaa.pmel.excel2oap.ifc;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import gov.noaa.pmel.excel2oap.ElementType;
import gov.noaa.pmel.excel2oap.NonNullHashMap;
import gov.noaa.pmel.excel2oap.SpreadSheetKeys;
import gov.noaa.pmel.excel2oap.SpreadSheetType;
import gov.noaa.pmel.excel2oap.SsRow;

/**
 * @author kamb
 *
 */
public interface SSParser {
    public void processRows(List<SsRow> rows);
    public SpreadSheetType getSpreadSheetType();
    public SpreadSheetKeys getSpreadSheetKeys();
    public Map<String, String> getSingleFields();
    public Map<ElementType, Collection<Map<String, String>>> getMultiItemFields();
}
