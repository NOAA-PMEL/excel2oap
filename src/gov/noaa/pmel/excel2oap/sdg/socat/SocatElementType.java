/**
 * 
 */
package gov.noaa.pmel.excel2oap.sdg.socat;

import java.util.HashMap;
import java.util.Map;

import gov.noaa.pmel.excel2oap.ElementType;

public enum SocatElementType implements ElementType {
    
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
//    DIC("DIC"),
//    TA("TA"),
//    PH("pH"),
    CO2("fCO2/pCO2/xCO2","add_CO2common"),
    PCO2("pCO2"),
    FCO2("fCO2"),
    XCO2("xCO2"),
//    PCO2A("pCO2A"),
//    PCO2D("pCO2D"),
    // individual items
    SUBMISSION_DATE("Submission Date"),
    RELATED_ACCESSION("Accession no. of related data sets"),
    METADATA_URL("URL of associated metadata set"),
    ASSOCIATED_URL("URL of associated dataset"),
    DOI("DOI of dataset"),
    TITLE("Name of sampling site or title"),
    ABSTRACT("Short description including purpose of observation"),
    PURPOSE("Purpose"),
    METHOD("Method(s) applied"),
    START_DATE("First day of measurement"),
    END_DATE("Last day of measurement"),
    WEST_LON("Transect measurement longitude westernmost"),
    EAST_LON("Transect measurement longitude easternmost"),
    NORTH_LAT("Transect measurement latitude northernmost"),
    SOUTH_LAT("Transect measurement latitude southernmost"),
    SITE_LAT("Site specific measurement latitude"),
    SITE_LON("Site specific measurement longitude"),
    SPATIAL_REF("Spatial reference system"),
    GEO_NAMES("Geographic names"),
    ORGANISM_LOC("Location of organism collection"),
    RESEARCH_PROJECT("Research projects"),
    EXPOCODE("EXPOCODE"),
    SECTION("Section"),
    CITATION_LIST("Author list for citation"),
    REFERENCES("References"),
    SUPPLEMENTAL_INFO("Supplemental information");
    
    private SocatElementType(String spreadsheetKey) {
        ssKey = spreadsheetKey;
        addMethod = null;
    }
    private SocatElementType(String spreadsheetKey, String method) {
        ssKey = spreadsheetKey;
        addMethod = method;
    }
    private String ssKey;
    public String key() { return ssKey; }
    private String addMethod;
    public String addMethod() { return addMethod; }
    
    @SuppressWarnings("serial")
    private static Map<String, SocatElementType> rowCellNameMap = initMap();
    
    private static Map<String, SocatElementType> initMap() {
        if ( rowCellNameMap == null ) {
            rowCellNameMap = new HashMap<String, SocatElementType>();
            for (SocatElementType t : values()) {
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