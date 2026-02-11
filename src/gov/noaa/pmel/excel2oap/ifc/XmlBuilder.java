/**
 * 
 */
package gov.noaa.pmel.excel2oap.ifc;

import java.io.IOException;
import java.io.OutputStream;


/**
 * @author kamb
 *
 */
public interface XmlBuilder {
    
    public void buildDocument(SSParser spreadSheet);
    public void outputXml(OutputStream out) throws IOException;
    
    /*
    public void add_SubmissionDate(String date);
    public void add_Accession(String accn);
    public void add_MetadataURL(String url);
    public void add_DatasetURL(String url);
    public void add_DatasetDOI(String doi);
    
    public void add_DatasetTitle(String title);
    public void add_DatasetDescription(String description);
    
    public void add_DataStartDate(String startDate);
    public void add_DataEndDate(String endDate);
    
    public default void add_SiteLatitude(String siteLatitude) {}
    public default void add_SiteLongitude(String siteLongitude) {}
    
    public void add_WesternBounds(String westBound);
    public void add_EasternBounds(String eastBound);
    public void add_NorthernBounds(String northBound);
    public void add_SouthernBounds(String southBound);
    
    public void add_SpatialReferenceSystem(String spatialReference);
    public void add_GeographicNames(String spatialReference);
    public void add_Section(String section);
    
    public void add_OrganismCollectionLocation(String location);
    
    public void add_ResearchProject(String projectName);
    
    public void add_ExpoCode(String expoCode);
    public void add_CruiseId(String cruiseId);
    
    public void add_CitationAuthor(String authorName);
    public void add_Reference(String reference);
    public void add_SupplementalInfo(String supplementalInfo);
    
    public void add_INVESTIGATOR(Map<String, String> parts);
    public void add_DATA_SUBMITTER(Map<String, String> parts);
    public void add_PLATFORM(Map<String, String> parts);
    public void add_FUNDING(Map<String, String> parts);
    
    public default void add_DIC(Map<String, String> parts) {}
    public default void add_TA(Map<String, String> parts) {}
    public default void add_PH(Map<String, String> parts) {}
    public default void add_PCO2A(Map<String, String> parts) {}
    public default void add_PCO2D(Map<String, String> parts) {}
    public void add_VAR(Map<String, String> parts);
    public void add_VARs(Collection<Map<String, String>> vars);
    public default void add_xCO2(Map<String, String> parts) {}
    public default void add_pCO2(Map<String, String> parts) {}
    public default void add_fCO2(Map<String, String> parts) {}
    public default void add_CO2common(Map<String, String> parts) {}
    public default void add_Temperature(Map<String, String> parts) {}
    public default void add_Salinity(Map<String, String> parts) {}
    public default void add_Depth(Map<String, String> parts) {}
*/
}
