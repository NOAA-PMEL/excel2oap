/**
 * 
 */
package gov.noaa.pmel.excel2oap.sdg.socat;

import java.util.List;
import java.util.Map;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.jdom2.Document;
import org.jdom2.Element;

import gov.noaa.pmel.excel2oap.BaseSpreadSheetHandler;
import gov.noaa.pmel.excel2oap.ElementType;
import gov.noaa.pmel.excel2oap.SpreadSheetKeys;
import gov.noaa.pmel.excel2oap.SpreadSheetType;
import gov.noaa.pmel.excel2oap.SsRow;
import gov.noaa.pmel.excel2oap.sdg.SdgHandler;
import gov.noaa.pmel.sdimetadata.SDIMetadata;

/**
 * @author kamb
 *
 */
public class SocatHandler extends SdgHandler { // BaseSpreadSheetHandler {
    
    private static final Logger logger = LogManager.getLogger(SocatHandler.class);
    
    @Override
    public SpreadSheetType getSpreadSheetType() { return SpreadSheetType.SOCAT; }

    private static final String[] multiItemFields = { 
            SocatElementType.PI.key(), 
            SocatElementType.INVESTIGATOR.key(), 
//            ElementType.FUNDING.key(), // Not supported yet.
            SocatElementType.PLATFORM.key(), 
            SocatElementType.VAR.key() 
    };
    private static final String[] multiLineFields = { 
            SocatElementType.DATA_SUBMITTER.key(), 
            SocatElementType.CRUISE_ID.key(), 
            SocatElementType.CO2.key(), 
            SocatElementType.FCO2.key(),
            SocatElementType.PCO2.key(),
            SocatElementType.XCO2.key(),
            SocatElementType.TEMPERATURE.key(),
            SocatElementType.SALINITY.key(),
            SocatElementType.DEPTH.key(),
    };
    
    SpreadSheetKeys socatKeys;//  = new SocatKeys();
    
    private StringBuilder co2vars = new StringBuilder();
    private String co2comma = "";
    
    /**
     * @param omitEmpty
     * @param socatKeys2
     */
    public SocatHandler(boolean omitEmptyItems, SocatKeys socatKeys2) {
        super(multiLineFields, multiItemFields, socatKeys2, omitEmptyItems);
        socatKeys = socatKeys2;
    }
    /* (non-Javadoc)
     * @see gov.noaa.pmel.excel2oap.SpreadSheetTypeFlavor#getKeys()
     */
    @Override
    public SpreadSheetKeys getKeys() {
        return socatKeys;
    }
    
    /* (non-Javadoc)
     * @see gov.noaa.pmel.excel2oap.SpreadSheetTypeFlavor#elementForKey(java.lang.String)
     */
    @Override
    public ElementType elementForKey(String key) {
        return SocatElementType.fromSsRowName(key);
    }
    
    @Override
    public void processRows(List<SsRow> rows) {
        if (checkForMisplacedRow(rows)) {
            System.out.println("Found out-of-order fpxCO2 row.");
        }
        if (checkForProblemRows(rows)) {
            System.out.println("Found typo fpxCO2 row.");
        }
        super.processRows(rows);
    }
    /**
     * @param rows
     */
    protected static boolean checkForMisplacedRow(List<SsRow> rows) {
        SsRow thisRow, nextRow;
        for (int i = 0; i < rows.size() -1; i++) {
            thisRow = rows.get(i);
            if ( thisRow.name().startsWith("fCO2/pCO2/xCO2")) {
                nextRow = rows.get(i+1);
                if ( ! nextRow.name().startsWith("fCO2/pCO2/xCO2")) {
                    rows.set(i, nextRow);
                    rows.set(i+1, thisRow);
                }
                return true;
            }
        }
        return false;
    }
    protected static boolean checkForProblemRows(List<SsRow> rows) {
        boolean foundHiding = false;
        String problem = "fCO2\\.pCO2.*";
        SsRow thisRow, nextRow;
        for (int i = 0; i < rows.size()-1; i++) {
            thisRow = rows.get(i);
            nextRow = rows.get(i+1);
            if (! foundHiding &&
                  thisRow.name().startsWith("xCO2") &&
                ! nextRow.name().startsWith("xCO2")) {
                logger.info("Removing hidden row " + nextRow.num() + "("+(i+1)+")" + nextRow.name());
                foundHiding = true;
                rows.remove(i+1);
            }
            if ( thisRow.name().matches(problem)) {
                String newName = thisRow.name().replace('.', '/');
                SsRow fixedRow = new SsRow(thisRow.num(), newName, thisRow.value());
                rows.set(i, fixedRow);
                return true;
            }
        }
        return false;
    }
       
    @Override
    public void addOtherStuff(Document doc) {
        add_xCO2(doc);
        add_fCO2(doc);
        add_pCO2(doc);
        add_CO2common(doc);
        add_Depth(doc);
        add_Salinity(doc);
        add_Temperature(doc);
    }
    
    void addVarCommon(Document doc, Map<String, String> parts) {
        Element root = doc.getRootElement();
    }
    
    /* (non-Javadoc)
     * @see gov.noaa.pmel.excel2oap.SpreadSheetTypeFlavor#add_VARs(org.jdom2.Document)
     */
    @Override
    public void add_VARs(Document doc) {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see gov.noaa.pmel.excel2oap.SpreadSheetTypeFlavor#add_xCO2()
     */
    @Override
    public void add_xCO2(Document doc) {
        Map<String, String> parts = getSingularItem(SocatElementType.XCO2.key());
        if ( parts != null ) {
            add_VAR(doc, parts, null);
            co2vars.append(co2comma).append(parts.get(socatKeys.getKeyForName(socatKeys.name_VarX_Variable_abbreviation_in_data_files)));
            co2comma = ",";
        }
    }

    /* (non-Javadoc)
     * @see gov.noaa.pmel.excel2oap.SpreadSheetTypeFlavor#add_pCO2()
     */
    @Override
    public void add_fCO2(Document doc) {
        Map<String, String> parts = getSingularItem(SocatElementType.FCO2.key());
        if ( parts != null ) {
            add_VAR(doc, parts, null);
            co2vars.append(co2comma).append(parts.get(socatKeys.getKeyForName(socatKeys.name_VarX_Variable_abbreviation_in_data_files)));
            co2comma = ",";
        }
    }

    /* (non-Javadoc)
     * @see gov.noaa.pmel.excel2oap.SpreadSheetTypeFlavor#add_fCO2()
     */
    @Override
    public void add_pCO2(Document doc) {
        Map<String, String> parts = getSingularItem(SocatElementType.PCO2.key());
        if ( parts != null ) {
            add_VAR(doc, parts, null);
            co2vars.append(co2comma).append(parts.get(socatKeys.getKeyForName(socatKeys.name_VarX_Variable_abbreviation_in_data_files)));
            co2comma = ",";
        }
    }

    /* (non-Javadoc)
     * @see gov.noaa.pmel.excel2oap.SpreadSheetTypeFlavor#add_CO2common()
     */
    @Override
    public void add_CO2common(Document doc) {
        Map<String, String> parts = getSingularItem(SocatElementType.CO2.key());
        if ( parts != null ) {
            Element root = doc.getRootElement();
            Element var = new Element("variable");
            parts.put(socatKeys.getKeyForName(socatKeys.name_VarX_Variable_abbreviation_in_data_files), co2vars.toString());
            fill_VAR(var, parts, null);
            fillCO2common(var, parts);
            root.addContent(var);
        }
    }

    /**
     * @param var
     * @param parts
     */
    private void fillCO2common(Element var, Map<String, String> parts) {
        maybeAdd(var,"locationSeawaterIntake",parts.get(socatKeys.pCO2AX_Location_of_seawater_intake));
        maybeAdd(var,"DepthSeawaterIntake",parts.get(socatKeys.pCO2AX_Depth_of_seawater_intake));
        maybeAdd(var,"analyzingInstrument",parts.get(socatKeys.pCO2AX_Analyzing_instrument));
        maybeAdd(var,"detailedInfo",parts.get(socatKeys.pCO2AX_Detailed_sampling_and_analyzing_information));
        Element equilibrator = new Element("equilibrator");
        maybeAdd(equilibrator,"type",parts.get(socatKeys.pCO2AX_Equilibrator_type));
        maybeAdd(equilibrator,"volume",parts.get(socatKeys.pCO2AX_Equilibrator_volume));
        maybeAdd(equilibrator,"vented",parts.get(socatKeys.pCO2AX_Vented_or_not));
        maybeAdd(equilibrator,"waterFlowRate",parts.get(socatKeys.pCO2AX_Water_flow_rate));
        maybeAdd(equilibrator,"gasFlowRate",parts.get(socatKeys.pCO2AX_Headspace_gas_flow_rate));
        maybeAdd(equilibrator,"temperatureEquilibratorMethod",parts.get(socatKeys.pCO2AX_How_was_temperature_inside_the_equilibrator_measured));
        maybeAdd(equilibrator,"pressureEquilibratorMethod",parts.get(socatKeys.pCO2AX_How_was_pressure_inside_the_equilibrator_measured));
        maybeAdd(equilibrator,"dryMethod",parts.get(socatKeys.pCO2AX_Drying_method_for_CO2_gas));
        var.addContent(equilibrator);
        Element gasDetector = new Element("gasDetector");
         maybeAdd(gasDetector,"manufacturer",parts.get(socatKeys.pCO2AX_Manufacturer_of_standard_gas));
         maybeAdd(gasDetector,"model",parts.get(socatKeys.pCO2AX_Model_of_the_gas_detector));
         maybeAdd(gasDetector,"resolution",parts.get(socatKeys.pCO2AX_Resolution_of_the_gas_detector));
         maybeAdd(gasDetector,"uncertainty",parts.get(socatKeys.pCO2AX_Uncertainty_of_the_gas_detector));
        var.addContent(gasDetector);
        Element standard = new Element("standard");
        maybeAdd(standard,"description",parts.get(socatKeys.pCO2AX_Standardization_technique_description));
        maybeAdd(standard,"frequency",parts.get(socatKeys.pCO2AX_Frequency_of_standardization));
        Element standardGas = new Element("standardgas");
            maybeAdd(standardGas,"manufacturer",parts.get(socatKeys.pCO2AX_Manufacturer_of_standard_gas));
            maybeAdd(standardGas,"concentration",parts.get(socatKeys.pCO2AX_Concentrations_of_standard_gas));
            maybeAdd(standardGas,"uncertainty",parts.get(socatKeys.pCO2AX_Uncertainties_of_standard_gas));
        standard.addContent(standardGas);
        var.addContent(standard);
        maybeAdd(var,"waterVaporCorrection",parts.get(socatKeys.pCO2AX_Water_vapor_correction_method));
        maybeAdd(var,"temperatureCorrection",parts.get(socatKeys.pCO2AX_Temperature_correction_method));
        maybeAdd(var,"co2ReportTemperature",parts.get(socatKeys.pCO2AX_at_what_temperature_was_pCO2_reported));
        maybeAdd(var,"uncertainty",parts.get(socatKeys.pCO2AX_Uncertainty));
        maybeAdd(var,"flag",parts.get(socatKeys.pCO2AX_Data_quality_flag_description));
        maybeAdd(var,"methodReference",parts.get(socatKeys.pCO2AX_Method_reference));
    }

}
