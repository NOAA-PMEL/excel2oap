/**
 * 
 */
package gov.noaa.pmel.excel2oap;

import java.util.List;

import gov.noaa.pmel.excel2oap.sdg.SDG_14_3_Keys;
import gov.noaa.pmel.excel2oap.sdg.socat.SocatElementType;

public enum SpreadSheetType {
    OCADS_v1,
    OCADS_v2,
    OCADS_v3,
    SDG_14_3_1,
    SOCAT;
    
    public static SpreadSheetType fromSheet(List<SsRow>rows) {
    	int nrow = -1;
        SpreadSheetType type = OCADS_v1;
        for (SsRow row : rows) {
        	nrow += 1;
        	if ( nrow > 1 && row.num() == 0 ) {
        		type = OCADS_v2;
        		break;
        	}
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