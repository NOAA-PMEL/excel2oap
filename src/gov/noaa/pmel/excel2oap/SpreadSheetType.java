/**
 * 
 */
package gov.noaa.pmel.excel2oap;

import java.util.List;

import gov.noaa.pmel.excel2oap.sdg.SDG_14_3_Keys;
import gov.noaa.pmel.excel2oap.sdg.socat.SocatElementType;

public enum SpreadSheetType {
    OCADS,
    SDG_14_3_1,
    SOCAT;
    
    public static SpreadSheetType fromSheet(List<SsRow>rows) {
        SpreadSheetType type = OCADS;
        for (SsRow row : rows) {
            if (SDG_14_3_Keys.MetadataURL.equals(row.name())) {
                type = SDG_14_3_1;
            }
            if (row.name().startsWith(SocatElementType.CO2.key())) {
                type = SOCAT;
                break;
            }
        }
        return type;
    }
}