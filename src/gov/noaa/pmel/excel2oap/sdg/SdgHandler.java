/**
 * 
 */
package gov.noaa.pmel.excel2oap.sdg;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.jdom2.Document;
import org.jdom2.Element;

import gov.noaa.pmel.excel2oap.BaseSpreadSheetHandler;
import gov.noaa.pmel.excel2oap.ElementType;
import gov.noaa.pmel.excel2oap.SpreadSheetKeys;
import gov.noaa.pmel.excel2oap.SpreadSheetType;
import gov.noaa.pmel.excel2oap.SsRow;

/**
 * @author kamb
 *
 */
public class SdgHandler extends BaseSpreadSheetHandler {

    private static final Logger logger = LogManager.getLogger(SdgHandler.class);
    
    public SpreadSheetType getSpreadSheetType() { return SpreadSheetType.SDG_14_3_1; }
    
    private static final String[] multiItemFields = { 
            SdgElementType.PI.key(), 
            SdgElementType.INVESTIGATOR.key(), 
            SdgElementType.FUNDING.key(),
            SdgElementType.PLATFORM.key(), 
            SdgElementType.VAR.key() 
    };
    private static final String[] multiLineFields = { 
            SdgElementType.DATA_SUBMITTER.key(), 
//            SdgElementType.CRUISE_ID.key(), 
            SdgElementType.DIC.key(),
            SdgElementType.TA.key(),
            SdgElementType.PH.key(),
            SdgElementType.FCO2.key(),
            SdgElementType.PCO2.key(),
            SdgElementType.XCO2.key(),
            SdgElementType.TEMPERATURE.key(),
            SdgElementType.SALINITY.key(),
            SdgElementType.DEPTH.key(),
    };
    
//    static SpreadSheetKeys sdgKeys = new SDG_14_3_Keys();
    SpreadSheetKeys sdgKeys;
    
    public SdgHandler(boolean omitEmptyItems, SDG_14_3_Keys keys) {
        super(multiLineFields, multiItemFields, keys, true);
        sdgKeys = keys;
    }
    protected SdgHandler(String[] multiLine, String[] multiItem,
                         SpreadSheetKeys keys, boolean omitEmptyItems) {
        super(multiLine, multiItem, keys, omitEmptyItems);
    }

    
    /* (non-Javadoc)
     * @see gov.noaa.pmel.excel2oap.SpreadSheetTypeFlavor#getKeys()
     */
    @Override
    public SpreadSheetKeys getKeys() {
        return sdgKeys;
    }
    
    /* (non-Javadoc)
     * @see gov.noaa.pmel.excel2oap.SpreadSheetTypeFlavor#elementForKey(java.lang.String)
     */
    @Override
    public ElementType elementForKey(String key) {
        return SdgElementType.fromSsRowName(key);
    }
    
    @Override
    public void processRows(List<SsRow> rows) {
        if ( fixInvestigatorRows(rows)) {
            logger.info("Fixed mis-numbered Investigator row.");
        }
        if ( fixPlatformRows(rows)) {
            logger.info("Fixed platform rows.");
        }
        if ( fixDicRows(rows)) {
            logger.info("Fixed DIC rows.");
        }
        if ( fixPhRows(rows)) {
            logger.info("Fixed pH rows.");
        }
        super.processRows(rows);
    }

    /**
     * @param rows
     * @return
     */
    private static boolean fixDicRows(List<SsRow> rows) {
        SsRow thisRow;
        for (int i = 0; i < rows.size() -1; i++) {
            thisRow = rows.get(i);
            String rowName = thisRow.name();
            if (rowName.startsWith("DIC: Data quality scheme")) {
                rowName = "DIC: Data quality flag scheme (name of scheme)";
                SsRow newRow = thisRow.toBuilder()
                                .name(rowName)
                                .build();
                rows.set(i, newRow);
                return true;
            }
        }
        return false;
    }
    /**
     * @param rows
     * @return
     */
    private static boolean fixPhRows(List<SsRow> rows) {
        SsRow thisRow;
        boolean fixedIt = false;
        Pattern pattern = Pattern.compile("((pH)(?:.)?(\\s))(.*)");
        for (int i = 0; i < rows.size() -1; i++) {
            thisRow = rows.get(i);
            String rowName = thisRow.name();
            Matcher matcher = pattern.matcher(rowName);
            if (rowName.startsWith("pH") &&
                ! matcher.matches()) {
                String remainder = rowName.substring(("pH:".length()));
                rowName = "pH: " + remainder;
                SsRow newRow = thisRow.toBuilder().name(rowName).build();
                rows.set(i, newRow);
                fixedIt = true;
            }
        }
        return fixedIt;
    }
    /**
     * @param rows
     * @return
     */
    private static boolean fixPlatformRows(List<SsRow> rows) {
        int whichOne = 1;
        SsRow thisRow;
        boolean foundOne = false;
        Pattern pattern = Pattern.compile("((Platform)(?:.)?(\\d))(.*)");
        for (int i = 0; i < rows.size() -1; i++) {
            thisRow = rows.get(i);
            String rowName = thisRow.name();
            Matcher matcher = pattern.matcher(rowName);
            if (rowName.startsWith("Platform")) {
                if ( ! matcher.matches() || 
                     ! String.valueOf(whichOne).equals(matcher.group(ITEM_GROUPS.NUM.position))) {
                    String remainder = matcher.matches() ? 
                                        rowName.substring("Platform-n".length()) :
                                        rowName.substring("Platform ".length());
                    rowName = "Platform-" + whichOne +" " + remainder;
                    SsRow newRow = thisRow.toBuilder().name(rowName).build();
                    rows.set(i, newRow);
                    foundOne = true;
                } else if ( "country".equals(matcher.group(ITEM_GROUPS.REMAINDER.position).trim())) { // foundOne && !incremented ) {
                    whichOne += 1;
                }
            }
        }
        return foundOne;
    }
    
    /**
     * @param rows
     * @return
     */
    private static boolean fixInvestigatorRows(List<SsRow> rows) {
        SsRow thisRow;
        boolean foundOne = false;
        int rowIdx = 5;
        do {
            thisRow = rows.get(rowIdx);
            String rowName = thisRow.name();
            if (rowName.startsWith("Investigator-3")) {
                SsRow checkRow = rows.get(rowIdx+2);
                String checkRowName = checkRow.name();
                if ( checkRowName.startsWith("Investigator-2")) {
                    String newRowName = checkRowName.replace("-2", "-3");
                    SsRow newRow = checkRow.toBuilder().name(newRowName).build();
                    rows.set(rowIdx+2, newRow);
                    foundOne = true;
                }
            }
            rowIdx += 1;
        } while (!foundOne && rowIdx < rows.size());
        return foundOne;
    }
    
    private void addOtherStuff(Document doc) {
        add_DIC(doc);
        add_TA(doc);
        add_PH(doc);
        add_xCO2(doc);
        add_fCO2(doc);
        add_pCO2(doc);
//        add_CO2common(doc);
        add_Depth(doc);
        add_Salinity(doc);
        add_Temperature(doc);
        add_VARs(doc);
    }
    
    /* (non-Javadoc)
     * @see gov.noaa.pmel.excel2oap.SpreadSheetTypeFlavor#add_xCO2()
     */
    @Override
    public void add_xCO2(Document doc) {
        Map<String, String> parts = getSingularItem(SdgElementType.XCO2.key());
        if ( parts != null ) {
            Element root = doc.getRootElement();
            Element var = new Element("variable");
            if ( parts.get(sdgKeys.getKeyForName(sdgKeys.name_VarX_Full_variable_name)) == null) {
                parts.put(sdgKeys.getKeyForName(sdgKeys.name_VarX_Full_variable_name), "xCO2");
            }
            fill_VAR(var, parts, null);
            fillCO2common(var, parts);
            root.addContent(var);
        }
    }

    /* (non-Javadoc)
     * @see gov.noaa.pmel.excel2oap.SpreadSheetTypeFlavor#add_pCO2()
     */
    @Override
    public void add_fCO2(Document doc) {
        Map<String, String> parts = getSingularItem(SdgElementType.FCO2.key());
        if ( parts != null ) {
            Element root = doc.getRootElement();
            Element var = new Element("variable");
            if ( parts.get(sdgKeys.getKeyForName(sdgKeys.name_VarX_Full_variable_name)) == null) {
                parts.put(sdgKeys.getKeyForName(sdgKeys.name_VarX_Full_variable_name), "fCO2");
            }
            fill_VAR(var, parts, null);
            fillCO2common(var, parts);
            root.addContent(var);
        }
    }

    /* (non-Javadoc)
     * @see gov.noaa.pmel.excel2oap.SpreadSheetTypeFlavor#add_fCO2()
     */
    @Override
    public void add_pCO2(Document doc) {
        Map<String, String> parts = getSingularItem(SdgElementType.PCO2.key());
        if ( parts != null ) {
            Element root = doc.getRootElement();
            Element var = new Element("variable");
            if ( parts.get(sdgKeys.getKeyForName(sdgKeys.name_VarX_Full_variable_name)) == null) {
                parts.put(sdgKeys.getKeyForName(sdgKeys.name_VarX_Full_variable_name), "pCO2");
            }
            fill_VAR(var, parts, null);
            fillCO2common(var, parts);
            root.addContent(var);
        }
    }

    /* (non-Javadoc)
     * @see gov.noaa.pmel.excel2oap.SpreadSheetTypeFlavor#add_CO2common()
    @Override
    public void add_CO2common(Document doc) {
        Map<String, String> parts = getSingularItem(SdgElementType.CO2);
        if ( parts != null ) {
            Element root = doc.getRootElement();
            Element var = new Element("variable");
            parts.put(sdgKeys.getKeyForName(sdgKeys.name_VarX_Variable_abbreviation_in_data_files), co2vars.toString());
            fill_VAR(var, parts, null);
            fillCO2common(var, parts);
            root.addContent(var);
        }
    }
     */

    /**
     * @param var
     * @param parts
     */
    private void fillCO2common(Element var, Map<String, String> parts) {
        maybeAdd(var,"locationSeawaterIntake",parts.get(sdgKeys.pCO2AX_Location_of_seawater_intake));
        maybeAdd(var,"DepthSeawaterIntake",parts.get(sdgKeys.pCO2AX_Depth_of_seawater_intake));
        maybeAdd(var,"analyzingInstrument",parts.get(sdgKeys.pCO2AX_Analyzing_instrument));
        maybeAdd(var,"detailedInfo",parts.get(sdgKeys.pCO2AX_Detailed_sampling_and_analyzing_information));
        Element equilibrator = new Element("equilibrator");
        maybeAdd(equilibrator,"type",parts.get(sdgKeys.pCO2AX_Equilibrator_type));
        maybeAdd(equilibrator,"volume",parts.get(sdgKeys.pCO2AX_Equilibrator_volume));
        maybeAdd(equilibrator,"vented",parts.get(sdgKeys.pCO2AX_Vented_or_not));
        maybeAdd(equilibrator,"waterFlowRate",parts.get(sdgKeys.pCO2AX_Water_flow_rate));
        maybeAdd(equilibrator,"gasFlowRate",parts.get(sdgKeys.pCO2AX_Headspace_gas_flow_rate));
        maybeAdd(equilibrator,"temperatureEquilibratorMethod",parts.get(sdgKeys.pCO2AX_How_was_temperature_inside_the_equilibrator_measured));
        maybeAdd(equilibrator,"pressureEquilibratorMethod",parts.get(sdgKeys.pCO2AX_How_was_pressure_inside_the_equilibrator_measured));
        maybeAdd(equilibrator,"dryMethod",parts.get(sdgKeys.pCO2AX_Drying_method_for_CO2_gas));
        var.addContent(equilibrator);
        Element gasDetector = new Element("gasDetector");
         maybeAdd(gasDetector,"manufacturer",parts.get(sdgKeys.pCO2AX_Manufacturer_of_standard_gas));
         maybeAdd(gasDetector,"model",parts.get(sdgKeys.pCO2AX_Model_of_the_gas_detector));
         maybeAdd(gasDetector,"resolution",parts.get(sdgKeys.pCO2AX_Resolution_of_the_gas_detector));
         maybeAdd(gasDetector,"uncertainty",parts.get(sdgKeys.pCO2AX_Uncertainty_of_the_gas_detector));
        var.addContent(gasDetector);
        Element standard = new Element("standard");
        maybeAdd(standard,"description",parts.get(sdgKeys.pCO2AX_Standardization_technique_description));
        maybeAdd(standard,"frequency",parts.get(sdgKeys.pCO2AX_Frequency_of_standardization));
        Element standardGas = new Element("standardgas");
            maybeAdd(standardGas,"manufacturer",parts.get(sdgKeys.pCO2AX_Manufacturer_of_standard_gas));
            maybeAdd(standardGas,"concentration",parts.get(sdgKeys.pCO2AX_Concentrations_of_standard_gas));
            maybeAdd(standardGas,"uncertainty",parts.get(sdgKeys.pCO2AX_Uncertainties_of_standard_gas));
        standard.addContent(standardGas);
        var.addContent(standard);
        maybeAdd(var,"waterVaporCorrection",parts.get(sdgKeys.pCO2AX_Water_vapor_correction_method));
        maybeAdd(var,"temperatureCorrection",parts.get(sdgKeys.pCO2AX_Temperature_correction_method));
        maybeAdd(var,"co2ReportTemperature",parts.get(sdgKeys.pCO2AX_at_what_temperature_was_pCO2_reported));
        maybeAdd(var,"uncertainty",parts.get(sdgKeys.pCO2AX_Uncertainty));
        maybeAdd(var,"flag",parts.get(sdgKeys.pCO2AX_Data_quality_flag_description));
        maybeAdd(var,"methodReference",parts.get(sdgKeys.pCO2AX_Method_reference));
    }

    @Override
    public void add_Depth(Document doc) {
        Map<String, String> parts = getSingularItem(SdgElementType.DEPTH.key());
        if ( parts != null && ! parts.isEmpty() ) {
            add_VAR(doc, parts, null);
        }
    }

    @Override
    public void add_Temperature(Document doc) {
        Map<String, String> parts = getSingularItem(SdgElementType.TEMPERATURE.key());
        if ( parts != null && ! parts.isEmpty() ) {
            add_VAR(doc, parts, null);
        }
    }

    @Override
    public void add_Salinity(Document doc) {
        Map<String, String> parts = getSingularItem(SdgElementType.SALINITY.key());
        if ( parts != null && ! parts.isEmpty() ) {
            add_VAR(doc, parts, null);
        }
    }

}
