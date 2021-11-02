/**
 * 
 */
package gov.noaa.pmel.excel2oap.ocads;

import java.util.HashMap;
import java.util.Map;

import gov.noaa.pmel.excel2oap.ElementType;

public enum OcadsElementType implements ElementType {
    
    // multiitem elements
    PI("PI"),
    INVESTIGATOR("Investigator"),
    FUNDING("Funding"),
    PLATFORM("Platform"),
    VAR("Var"),
    // multiline elements
    DATA_SUBMITTER("Data submitter"),
    DIC("DIC"),
    TA("TA"),
    PH("pH"),
    PCO2A("pCO2A"),
    PCO2D("pCO2D"),
    // individual elements
    SUBMISSION_DATE("Submission Date"),
    RELATED_ACCESSION("Accession no. of related data sets"),
    TITLE("Title"),
    ABSTRACT("Abstract"),
    PURPOSE("Purpose"),
    START_DATE("Start date"),
    END_DATE("End date"),
    WEST_LON("Westbd longitude"),
    EAST_LON("Eastbd longitude"),
    NORTH_LAT("Northbd latitude"),
    SOUTH_LAT("Southbd latitude"),
    SPATIAL_REF("Spatial reference system"),
    GEO_NAMES("Geographic names"),
    ORGANISM_LOC("Location of organism collection"),
    RESEARCH_PROJECT("Research projects"),
    EXPOCODE("EXPOCODE"),
    CRUISE_ID("Cruise ID"),
    SECTION("Section"),
    CITATION_LIST("Author list for citation"),
    REFERENCES("References"),
    SUPPLEMENTAL_INFO("Supplemental information");
    
    private OcadsElementType(String spreadsheetKey) {
        ssKey = spreadsheetKey;
    }
    private String ssKey;
    public String key() { return ssKey; }
    
    @SuppressWarnings("serial")
    private static Map<String, ElementType> rowCellNameMap = initMap();
    
    private static Map<String, ElementType> initMap() {
        if ( rowCellNameMap == null ) {
            rowCellNameMap = new HashMap<String, ElementType>();
            for (OcadsElementType t : values()) {
                rowCellNameMap.put(t.ssKey, t);
            }
        }
        return rowCellNameMap;
    }
    public static OcadsElementType fromSsRowName(String rowName) {
        return (OcadsElementType)ElementType.forKey(rowName, rowCellNameMap);
    }
}