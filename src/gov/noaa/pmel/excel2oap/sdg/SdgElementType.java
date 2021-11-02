/**
 * 
 */
package gov.noaa.pmel.excel2oap.sdg;

import java.util.HashMap;
import java.util.Map;

import gov.noaa.pmel.excel2oap.ElementType;

public enum SdgElementType implements ElementType {
    
    // multi-item start keys
    PI("PI"),
    INVESTIGATOR("Investigator"),
    PLATFORM("Platform"),
    FUNDING("Funding"),
    VAR("Var"),
    // multi-line start keys
    DATA_SUBMITTER("Data submitter"),
    CRUISE_ID("Cruise ID"),
    DEPTH("Depth"),
    TEMPERATURE("Temperature"),
    SALINITY("Salinity"),
    DIC("DIC"),
    TA("TA"),
    PH("pH"),
    PCO2("pCO2"),
    FCO2("fCO2"),
    XCO2("xCO2"), // not called out in SDG 14.3.1
//    PCO2A("pCO2A"),
//    PCO2D("pCO2D"),
    // individual items
    SUBMISSION_DATE("Submission Date"),
    RELATED_ACCESSION("Accession no. of related data sets"), // with extra
    METADATA_URL("URL of metadata set"),
    ASSOCIATED_URL("URL of associated dataset"),
    DOI("DOI of dataset"), // with extra
    TITLE("Name of sampling site or title"),
    ABSTRACT("Short description including purpose of observation"),
//    PURPOSE("Purpose"),     // not in SDG 14.3.1
    METHOD("Method(s) applied"),
    START_DATE("First day of measurement"), // with extra
    END_DATE("Last day of measurement"),    // with extra
    WEST_LON("Transect measurement longitude westernmost"),
    EAST_LON("Transect measurement longitude easternmost"),
    NORTH_LAT("Transect measurement latitude northernmost"),
    SOUTH_LAT("Transect measurement latitude southernmost"),
    SITE_LAT("Site specific measurement latitude"),
    SITE_LON("Site specific measurement longitude"),
//    SPATIAL_REF("Spatial reference system"), // not in SDG
//    GEO_NAMES("Geographic names"),
//    ORGANISM_LOC("Location of organism collection"),
    RESEARCH_PROJECT("Research projects"),
    EXPOCODE("EXPOCODE"),
    SECTION("Section"),
    CITATION_LIST("Author list for citation"),
    REFERENCES("References"),
    SUPPLEMENTAL_INFO("Supplemental information");
    
    private SdgElementType(String spreadsheetKey) {
        ssKey = spreadsheetKey;
        addMethod = null;
    }
    private SdgElementType(String spreadsheetKey, String method) {
        ssKey = spreadsheetKey;
        addMethod = method;
    }
    private String ssKey;
    public String key() { return ssKey; }
    private String addMethod;
    public String addMethod() { return addMethod; }
    
    @SuppressWarnings("serial")
    private static Map<String, SdgElementType> rowCellNameMap = initMap();
    
    private static Map<String, SdgElementType> initMap() {
        if ( rowCellNameMap == null ) {
            rowCellNameMap = new HashMap<String, SdgElementType>();
            for (SdgElementType t : values()) {
                rowCellNameMap.put(t.ssKey, t);
            }
        }
        return rowCellNameMap;
    }
    public static ElementType fromSsRowName(String rowName) {
        if ( rowCellNameMap.containsKey(rowName)) {
            return rowCellNameMap.get(rowName);
        }
        for (String key : rowCellNameMap.keySet()) {
            if ( rowName.startsWith(key)) {
                return rowCellNameMap.get(rowName);
            }
        }
        throw new IllegalStateException("No element found for cell name key: " + rowName);
    }
}