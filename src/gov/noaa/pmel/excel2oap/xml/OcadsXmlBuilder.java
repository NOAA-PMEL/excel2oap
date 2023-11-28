/**
 * 
 */
package gov.noaa.pmel.excel2oap.xml;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.DOMBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.xml.sax.InputSource;

import gov.noaa.pmel.excel2oap.ElementType;
import gov.noaa.pmel.excel2oap.NonNullHashMap;
import gov.noaa.pmel.excel2oap.SpreadSheetKeys;
import gov.noaa.pmel.excel2oap.SpreadSheetType;
import gov.noaa.pmel.excel2oap.ifc.SSParser;
import gov.noaa.pmel.excel2oap.ifc.XmlBuilder;
import gov.noaa.pmel.excel2oap.ocads.OcadsElementType;
import gov.noaa.pmel.excel2oap.ocads.OcadsKeys;
import gov.noaa.pmel.sdimetadata.Coverage;
import gov.noaa.pmel.sdimetadata.MiscInfo;
import gov.noaa.pmel.sdimetadata.SDIMetadata;
import gov.noaa.pmel.sdimetadata.MiscInfo.MiscInfoBuilder;
import gov.noaa.pmel.sdimetadata.person.Investigator;
import gov.noaa.pmel.sdimetadata.person.Person;
import gov.noaa.pmel.sdimetadata.person.Submitter;
import gov.noaa.pmel.sdimetadata.platform.Platform;
import gov.noaa.pmel.sdimetadata.util.Datestamp;
import gov.noaa.pmel.sdimetadata.util.NumericString;
import gov.noaa.pmel.sdimetadata.xml.OcadsWriter;
import gov.noaa.pmel.tws.util.StringUtils;

/**
 * @author kamb
 *
 */
public class OcadsXmlBuilder extends XmlBuilderBase implements XmlBuilder  {

    private static final Logger logger = LogManager.getLogger(OcadsXmlBuilder.class);
    
    private static OcadsKeys ocKeys = new OcadsKeys();
    
    private SDIMetadata sdi;
    private Document xmlJdoc;

    public static SpreadSheetType getSpreadSheetType() { return SpreadSheetType.OCADS; }
    
    public OcadsXmlBuilder(Map<ElementType, Collection<Map<String, String>>> multiItems,
                              Map<String, String> simpleItems,
                              SpreadSheetKeys keys, boolean omitEmptyElements) {
        super(multiItems, forceNotNull(simpleItems), keys, omitEmptyElements);
        sdi = new SDIMetadata();
    }
    
    static Map<String, String> forceNotNull(Map<String, String>items) {
        return new NonNullHashMap<>(items);
    }


    public static ElementType elementForKey(String key) {
        return OcadsElementType.fromSsRowName(key);
    }

    /* (non-Javadoc)
     * @see gov.noaa.pmel.excel2oap.SpreadSheetTypeFlavor#getKeys()
     */
    public static SpreadSheetKeys getKeys() {
        return ocKeys;
    }
    
    public void addOtherStuff(Document doc) {
        add_DIC(doc);
        add_TA(doc);
        add_PH(doc);
        add_PCO2A(doc);
        add_PCO2D(doc);
        add_VARs(doc);
    }
    

    protected static boolean addIfNotNull(Element var, String childName, String childContent) {
        if ( StringUtils.emptyOrNull(childContent)) { return false; }
        var.addContent(new Element(childName).addContent(childContent));
        return true;
    }
    
    protected boolean maybeAdd(Element var, String childName, String childContent) {
        if ( StringUtils.emptyOrNull(childContent) && omitEmptyElements ) { return false; }
        var.addContent(new Element(childName).addContent(childContent));
        return true;
    }
    
    protected boolean isEmpty(Element elem) {
        if ( elem == null ) { return true; }
        List<Attribute> attrs = elem.getAttributes();
        List<Element> children = elem.getChildren();
                
        if ( ! StringUtils.emptyOrNull(elem.getTextTrim())) { 
            return false;
        }
        if (children.size() == 0 && attrs.size() == 0) {
            return true;
        }
        return isEmpty(children);
    }
              
    protected boolean isEmpty(List<Element> elems) {
        for (Element elem : elems) {
            if (!isEmpty(elem)) { return false; }
        }
        return true;
    }

    public void add_VARs(Document doc) {
        Collection<Map<String, String>> vars = multiItems.get(elementForKey("Var")); // XXX String Constant!
        if ( vars == null || vars.isEmpty()) {
            return;
        }
        for (Map<String, String> parts : vars) {
            add_VAR(doc, parts);
        }
    }
    protected void add_VAR(Document doc, Map<String, String> parts) {
        add_VAR(doc, parts, "0");
    }
    protected void add_VAR(Document doc, Map<String, String> parts, String internal) {
        if ( parts == null || parts.isEmpty()) { return; }
        Element root = doc.getRootElement();
        Element var = new Element("variable");
        fill_VAR(var, parts, internal);
        if ( !isEmpty(var)) {                         
            root.addContent(var);
        }
    }
    protected void fill_VAR(Element var, Map<String, String> parts) {
        fill_VAR(var, parts, "0");
    }
    protected void fill_VAR(Element var, Map<String, String> parts, String internal) {
        if ( parts.get(ssKeys.getKeyForName(ssKeys.name_VarX_Variable_abbreviation_in_data_files)) == null) {
            logger.warn("No abbreviation for variable from: " + parts.toString());
        }
        maybeAdd(var,"abbrev",parts.get(ssKeys.getKeyForName(ssKeys.name_VarX_Variable_abbreviation_in_data_files)));
        if ( parts.get(ssKeys.getKeyForName(ssKeys.name_VarX_Full_variable_name)) == null) {
            logger.info("No full name for variable " + 
                        parts.get(ssKeys.getKeyForName(ssKeys.name_VarX_Variable_abbreviation_in_data_files)));
            parts.put(ssKeys.getKeyForName(ssKeys.name_VarX_Full_variable_name), "Depth");
        }
        maybeAdd(var,"fullname",parts.get(ssKeys.getKeyForName(ssKeys.name_VarX_Full_variable_name)));
        maybeAdd(var,"observationType",parts.get(ssKeys.getKeyForName(ssKeys.name_VarX_Observation_type)));
        maybeAdd(var,"insitu",parts.get(ssKeys.getKeyForName(ssKeys.name_VarX_In_situ_observation_X_manipulation_condition_X_response_variable)));
        maybeAdd(var,"unit",parts.get(ssKeys.getKeyForName(ssKeys.name_VarX_Variable_unit)));
        maybeAdd(var,"measured",parts.get(ssKeys.getKeyForName(ssKeys.name_VarX_Measured_or_calculated)));
        maybeAdd(var,"calcMethod",parts.get(ssKeys.getKeyForName(ssKeys.name_VarX_Calculation_method_and_parameters)));
        maybeAdd(var,"samplingInstrument",parts.get(ssKeys.getKeyForName(ssKeys.name_VarX_Sampling_instrument)));
        maybeAdd(var,"analyzingInstrument",parts.get(ssKeys.getKeyForName(ssKeys.name_VarX_Analyzing_instrument)));
        maybeAdd(var,"duration",parts.get(ssKeys.getKeyForName(ssKeys.name_VarX_Duration)));
        maybeAdd(var,"detailedInfo",parts.get(ssKeys.getKeyForName(ssKeys.name_VarX_Detailed_sampling_and_analyzing_information)));
        maybeAdd(var,"replicate",parts.get(ssKeys.getKeyForName(ssKeys.name_VarX_Field_replicate_information)));
        maybeAdd(var,"uncertainty",parts.get(ssKeys.getKeyForName(ssKeys.name_VarX_Uncertainty)));
        maybeAdd(var,"flag",parts.get(ssKeys.getKeyForName(ssKeys.name_VarX_Data_quality_flag_description)));
        maybeAdd(var,"methodReference",parts.get(ssKeys.getKeyForName(ssKeys.name_VarX_Method_reference)));
        maybeAdd(var,"biologicalSubject",parts.get(ssKeys.getKeyForName(ssKeys.name_VarX_Biological_subject)));
        maybeAdd(var,"speciesID",parts.get(ssKeys.getKeyForName(ssKeys.name_VarX_Species_Identification_code)));
        maybeAdd(var,"lifeStage",parts.get(ssKeys.getKeyForName(ssKeys.name_VarX_Life_Stage)));
        maybeAdd(var,"researcherName",parts.get(ssKeys.getKeyForName(ssKeys.name_VarX_Researcher_Name)));
        maybeAdd(var,"researcherInstitution",parts.get(ssKeys.getKeyForName(ssKeys.name_VarX_Researcher_Institution)));
        maybeAdd(var,"internal",internal);
    }

   /*
   variable
      fullname
      abbrev
      observationType
      insitu
      manipulationMethod
      unit
      measured
      calcMethod
      samplingInstrument
      analyzingInstrument
      detailedInfo
      replicate
      standard
         description
         frequency
         crm
            manufacturer
            batch
         crm
      poison
         poisonName
         volume
         correction
      uncertainty
      flag
      methodReference
      researcherName
      researcherInstitution
      internal
   */
    public void add_DIC(Document doc) {
        Map<String, String> parts = getSingularItem(OcadsElementType.DIC.key());
        if ( parts == null || parts.isEmpty()) { return; }
        Element root = doc.getRootElement();
        
        Element var = new Element("variable");
        maybeAdd(var,"fullname","Dissolved inorganic carbon");
        maybeAdd(var,"abbrev",parts.get(ocKeys.DICX_Variable_abbreviation_in_data_files));
        maybeAdd(var,"observationType",parts.get(ocKeys.DICX_Observation_type));
        maybeAdd(var,"insitu",parts.get(ocKeys.DICX_In_situ_observation_X_manipulation_condition_X_response_variable));
        maybeAdd(var,"manipulationMethod",parts.get(ocKeys.DICX_Manipulation_method));
        maybeAdd(var,"unit",parts.get(ocKeys.DICX_Variable_unit));
        maybeAdd(var,"measured",parts.get(ocKeys.DICX_Measured_or_calculated));
        maybeAdd(var,"calcMethod",parts.get(ocKeys.DICX_Calculation_method_and_parameters));
        maybeAdd(var,"samplingInstrument",parts.get(ocKeys.DICX_Sampling_instrument));
        maybeAdd(var,"analyzingInstrument",parts.get(ocKeys.DICX_Analyzing_instrument));
        maybeAdd(var,"detailedInfo",parts.get(ocKeys.DICX_Detailed_sampling_and_analyzing_information));
        maybeAdd(var,"replicate",parts.get(ocKeys.DICX_Field_replicate_information));
        Element standard = new Element("standard");
        maybeAdd(standard,"description",parts.get(ocKeys.DICX_Standardization_technique_description));
        maybeAdd(standard,"frequency",parts.get(ocKeys.DICX_Frequency_of_standardization));
        Element crm = new Element("crm");
        maybeAdd(crm,"manufacturer",parts.get(ocKeys.DICX_CRM_manufacturer));
        maybeAdd(crm,"batch",parts.get(ocKeys.DICX_Batch_number));
        standard.addContent(crm);
        var.addContent(standard);
        Element poison = new Element("poison");
        if ( ! addIfNotNull(poison,"poisonName",parts.get(ocKeys.DICX_Poison_used_to_kill_the_sample))) {
            maybeAdd(poison,"poisonName",parts.get(ocKeys.DICX_Poison_used_to_kill_the_sample_ALT));
        }
        if ( ! addIfNotNull(poison,"volume",parts.get(ocKeys.DICX_Poison_volume))) {
            maybeAdd(poison,"volume",parts.get(ocKeys.DICX_Poison_volume_ALT));
        }
        if ( ! addIfNotNull(poison,"correction",parts.get(ocKeys.DICX_Poisoning_correction_description))) {
            maybeAdd(poison,"correction",parts.get(ocKeys.DICX_Poisoning_correction_description_ALT)); 
        }
        var.addContent(poison);
        maybeAdd(var,"uncertainty",parts.get(ocKeys.DICX_Uncertainty));
        maybeAdd(var,"flag",parts.get(ocKeys.DICX_Data_quality_flag_description));
        maybeAdd(var,"methodReference",parts.get(ocKeys.DICX_Method_reference));
        maybeAdd(var,"researcherName",parts.get(ocKeys.DICX_Researcher_Name));
        maybeAdd(var,"researcherInstitution",parts.get(ocKeys.DICX_Researcher_Institution));
        var.addContent(new Element("internal").addContent("1"));
        if ( !isEmpty(var)) {                         
            root.addContent(var);
        }
    }
    public void add_TA(Document doc) {
        Map<String, String> parts = getSingularItem(OcadsElementType.TA.key());
        if ( parts == null || parts.isEmpty()) { return; }
        
        Element root = doc.getRootElement();
        
        Element var = new Element("variable");
        var.addContent(new Element("fullname").addContent("Total alkalinity"));
        maybeAdd(var,"abbrev",parts.get(ocKeys.TAX_Variable_abbreviation_in_data_files));
        maybeAdd(var,"observationType",parts.get(ocKeys.TAX_Observation_type));
        maybeAdd(var,"insitu",parts.get(ocKeys.TAX_In_situ_observation_X_manipulation_condition_X_response_variable));
        maybeAdd(var,"manipulationMethod",parts.get(ocKeys.TAX_Manipulation_method));
        maybeAdd(var,"unit",parts.get(ocKeys.TAX_Variable_unit));
        maybeAdd(var,"measured",parts.get(ocKeys.TAX_Measured_or_calculated));
        maybeAdd(var,"calcMethod",parts.get(ocKeys.TAX_Calculation_method_and_parameters));
        maybeAdd(var,"samplingInstrument",parts.get(ocKeys.TAX_Sampling_instrument));
        maybeAdd(var,"analyzingInstrument",parts.get(ocKeys.TAX_Analyzing_instrument));
        maybeAdd(var,"titrationType",parts.get(ocKeys.TAX_Type_of_titration));
        maybeAdd(var,"cellType",parts.get(ocKeys.TAX_Cell_type));
        maybeAdd(var,"curveFitting",parts.get(ocKeys.TAX_Curve_fitting_method));
        maybeAdd(var,"detailedInfo",parts.get(ocKeys.TAX_Detailed_sampling_and_analyzing_information));
        maybeAdd(var,"replicate",parts.get(ocKeys.TAX_Field_replicate_information));
        Element standard = new Element("standard");
        maybeAdd(standard,"description",parts.get(ocKeys.TAX_Standardization_technique_description));
        maybeAdd(standard,"frequency",parts.get(ocKeys.TAX_Frequency_of_standardization));
        Element crm = new Element("crm");
        maybeAdd(crm,"manufacturer",parts.get(ocKeys.TAX_CRM_manufacturer));
        maybeAdd(crm,"batch",parts.get(ocKeys.TAX_Batch_number));
        standard.addContent(crm);
        var.addContent(standard);
        Element poison = new Element("poison");
        if ( ! addIfNotNull(poison,"poisonName",parts.get(ocKeys.TAX_Poison_used_to_kill_the_sample))) {
            maybeAdd(poison,"poisonName",parts.get(ocKeys.TAX_Poison_used_to_kill_the_sample_ALT));
        }
        if ( ! addIfNotNull(poison,"volume",parts.get(ocKeys.TAX_Poison_volume))) {
            maybeAdd(poison,"volume",parts.get(ocKeys.TAX_Poison_volume_ALT));
        }
        if ( ! addIfNotNull(poison,"correction",parts.get(ocKeys.TAX_Poisoning_correction_description))) {
            maybeAdd(poison,"correction",parts.get(ocKeys.TAX_Poisoning_correction_description_ALT)); 
        }
        var.addContent(poison);
        maybeAdd(var,"uncertainty",parts.get(ocKeys.TAX_Uncertainty));
        maybeAdd(var,"flag",parts.get(ocKeys.TAX_Data_quality_flag_description));
        maybeAdd(var,"methodReference",parts.get(ocKeys.TAX_Method_reference));
        maybeAdd(var,"researcherName",parts.get(ocKeys.TAX_Researcher_Name));
        maybeAdd(var,"researcherInstitution",parts.get(ocKeys.TAX_Researcher_Institution));
        var.addContent(new Element("internal").addContent("2"));
                                 
        if ( !isEmpty(var)) {                         
            root.addContent(var);
        }
    }
    public void add_PH(Document doc) {
        Map<String, String> parts = getSingularItem(OcadsElementType.PH.key());
        if ( parts == null || parts.isEmpty()) { return; }

        Element root = doc.getRootElement();
        
        Element var = new Element("variable");
        var.addContent(new Element("fullname").addContent("pH"));
        maybeAdd(var,"abbrev",parts.get(ocKeys.PHX_Variable_abbreviation_in_data_files));
        maybeAdd(var,"observationType",parts.get(ocKeys.PHX_Observation_type));
        maybeAdd(var,"insitu",parts.get(ocKeys.PHX_In_situ_observation_X_manipulation_condition_X_response_variable));
        maybeAdd(var,"manipulationMethod",parts.get(ocKeys.PHX_Manipulation_method));
        maybeAdd(var,"measured",parts.get(ocKeys.PHX_Measured_or_calculated));
        maybeAdd(var,"calcMethod",parts.get(ocKeys.PHX_Calculation_method_and_parameters));
        maybeAdd(var,"samplingInstrument",parts.get(ocKeys.PHX_Sampling_instrument));
        maybeAdd(var,"analyzingInstrument",parts.get(ocKeys.PHX_Analyzing_instrument));
        maybeAdd(var,"phscale",parts.get(ocKeys.PHX_pH_scale));
        maybeAdd(var,"temperatureMeasure",parts.get(ocKeys.PHX_Temperature_of_measurement));
        maybeAdd(var,"detailedInfo",parts.get(ocKeys.PHX_Detailed_sampling_and_analyzing_information));
        maybeAdd(var,"replicate",parts.get(ocKeys.PHX_Field_replicate_information));
        Element standard = new Element("standard");
        maybeAdd(standard,"description",parts.get(ocKeys.PHX_Standardization_technique_description));
        maybeAdd(standard,"frequency",parts.get(ocKeys.PHX_Frequency_of_standardization));
        maybeAdd(standard,"standardphvalues",parts.get(ocKeys.PHX_pH_values_of_the_standards));
        maybeAdd(standard,"temperatureStandardization",parts.get(ocKeys.PHX_Temperature_of_standardization));
        var.addContent(standard);
        maybeAdd(var,"temperatureCorrectionMethod",parts.get(ocKeys.PHX_Temperature_correction_method));
        maybeAdd(var,"phReportTemperature",parts.get(ocKeys.PHX_at_what_temperature_was_pH_reported));
        maybeAdd(var,"uncertainty",parts.get(ocKeys.PHX_Uncertainty));
        maybeAdd(var,"flag",parts.get(ocKeys.PHX_Data_quality_flag_description));
        maybeAdd(var,"methodReference",parts.get(ocKeys.PHX_Method_reference));
        maybeAdd(var,"researcherName",parts.get(ocKeys.PHX_Researcher_Name));
        maybeAdd(var,"researcherInstitution",parts.get(ocKeys.PHX_Researcher_Institution));
        var.addContent(new Element("internal").addContent("3"));
                                 
        if ( !isEmpty(var)) {                         
            root.addContent(var);
        }
    }
/*   
   variable
      fullname
      abbrev
      observationType
      insitu
      manipulationMethod
      unit
      measured
      calcMethod
      samplingInstrument
      locationSeawaterIntake
      DepthSeawaterIntake
      analyzingInstrument
      detailedInfo
      equilibrator
         type
         volume
         vented
         waterFlowRate
         gasFlowRate
         temperatureEquilibratorMethod
         pressureEquilibratorMethod
         dryMethod
      gasDetector
         manufacturer
         model
         resolution
         uncertainty
      standardization
         description
         frequency
         standardgas
            manufacturer
            concentration
            uncertainty
      waterVaporCorrection
      temperatureCorrection
      co2ReportTemperature
      uncertainty
      flag
      methodReference
      researcherName
      researcherInstitution
      internal
   variable
*/
    public void add_PCO2A(Document doc) {
        Map<String, String> parts = getSingularItem(OcadsElementType.PCO2A.key());
        if ( parts == null || parts.isEmpty()) { return; }
        
        Element root = doc.getRootElement();
        
        Element var = new Element("variable");
        var.addContent(new Element("fullname").addContent("pCO2 (fCO2) autonomous"));
        maybeAdd(var,"abbrev",parts.get(ocKeys.pCO2AX_Variable_abbreviation_in_data_files));
        maybeAdd(var,"observationType",parts.get(ocKeys.pCO2AX_Observation_type));
        maybeAdd(var,"insitu",parts.get(ocKeys.pCO2AX_In_situ_observation_X_manipulation_condition_X_response_variable));
        maybeAdd(var,"manipulationMethod",parts.get(ocKeys.pCO2AX_Manipulation_method));
        maybeAdd(var,"unit",parts.get(ocKeys.pCO2AX_Variable_unit));
        maybeAdd(var,"measured",parts.get(ocKeys.pCO2AX_Measured_or_calculated));
        maybeAdd(var,"calcMethod",parts.get(ocKeys.pCO2AX_Calculation_method_and_parameters));
        maybeAdd(var,"samplingInstrument",parts.get(ocKeys.pCO2AX_Sampling_instrument));
        maybeAdd(var,"locationSeawaterIntake",parts.get(ocKeys.pCO2AX_Location_of_seawater_intake));
        maybeAdd(var,"DepthSeawaterIntake",parts.get(ocKeys.pCO2AX_Depth_of_seawater_intake));
        maybeAdd(var,"analyzingInstrument",parts.get(ocKeys.pCO2AX_Analyzing_instrument));
        maybeAdd(var,"detailedInfo",parts.get(ocKeys.pCO2AX_Detailed_sampling_and_analyzing_information));
        Element equilibrator = new Element("equilibrator");
        maybeAdd(equilibrator,"type",parts.get(ocKeys.pCO2AX_Equilibrator_type));
        maybeAdd(equilibrator,"volume",parts.get(ocKeys.pCO2AX_Equilibrator_volume));
        maybeAdd(equilibrator,"vented",parts.get(ocKeys.pCO2AX_Vented_or_not));
        maybeAdd(equilibrator,"waterFlowRate",parts.get(ocKeys.pCO2AX_Water_flow_rate));
        maybeAdd(equilibrator,"gasFlowRate",parts.get(ocKeys.pCO2AX_Headspace_gas_flow_rate));
        maybeAdd(equilibrator,"temperatureEquilibratorMethod",parts.get(ocKeys.pCO2AX_How_was_temperature_inside_the_equilibrator_measured));
        maybeAdd(equilibrator,"pressureEquilibratorMethod",parts.get(ocKeys.pCO2AX_How_was_pressure_inside_the_equilibrator_measured));
        maybeAdd(equilibrator,"dryMethod",parts.get(ocKeys.pCO2AX_Drying_method_for_CO2_gas));
        var.addContent(equilibrator);
        Element gasDetector = new Element("gasDetector");
         maybeAdd(gasDetector,"manufacturer",parts.get(ocKeys.pCO2AX_Manufacturer_of_standard_gas));
         maybeAdd(gasDetector,"model",parts.get(ocKeys.pCO2AX_Model_of_the_gas_detector));
         maybeAdd(gasDetector,"resolution",parts.get(ocKeys.pCO2AX_Resolution_of_the_gas_detector));
         maybeAdd(gasDetector,"uncertainty",parts.get(ocKeys.pCO2AX_Uncertainty_of_the_gas_detector));
        var.addContent(gasDetector);
        Element standard = new Element("standard");
        maybeAdd(standard,"description",parts.get(ocKeys.pCO2AX_Standardization_technique_description));
        maybeAdd(standard,"frequency",parts.get(ocKeys.pCO2AX_Frequency_of_standardization));
        Element standardGas = new Element("standardgas");
            maybeAdd(standardGas,"manufacturer",parts.get(ocKeys.pCO2AX_Manufacturer_of_standard_gas));
            maybeAdd(standardGas,"concentration",parts.get(ocKeys.pCO2AX_Concentrations_of_standard_gas));
            maybeAdd(standardGas,"uncertainty",parts.get(ocKeys.pCO2AX_Uncertainties_of_standard_gas));
        standard.addContent(standardGas);
        var.addContent(standard);
        maybeAdd(var,"waterVaporCorrection",parts.get(ocKeys.pCO2AX_Water_vapor_correction_method));
        maybeAdd(var,"temperatureCorrection",parts.get(ocKeys.pCO2AX_Temperature_correction_method));
        maybeAdd(var,"co2ReportTemperature",parts.get(ocKeys.pCO2AX_at_what_temperature_was_pCO2_reported));
        maybeAdd(var,"uncertainty",parts.get(ocKeys.pCO2AX_Uncertainty));
        maybeAdd(var,"flag",parts.get(ocKeys.pCO2AX_Data_quality_flag_description));
        maybeAdd(var,"methodReference",parts.get(ocKeys.pCO2AX_Method_reference));
        maybeAdd(var,"researcherName",parts.get(ocKeys.pCO2AX_Researcher_Name));
        maybeAdd(var,"researcherInstitution",parts.get(ocKeys.pCO2AX_Researcher_Institution));
        var.addContent(new Element("internal").addContent("4"));
                                 
        if ( !isEmpty(var)) {                         
            root.addContent(var);
        }
    }
    /*
   variable
      fullname
      abbrev
      observationType
      insitu
      manipulationMethod
      unit
      measured
      calcMethod
      samplingInstrument
      analyzingInstrument
      storageMethod
      seawatervol
      headspacevol
      temperatureMeasure
      detailedInfo
      replicate
      gasDetector
         manufacturer
         model
         resolution
         uncertainty
      standardization
         description
         frequency
         temperatureStd
         standardgas
            manufacturer
            concentration
            uncertainty
      waterVaporCorrection
      temperatureCorrection
      co2ReportTemperature
      uncertainty
      flag
      methodReference
      researcherName
      researcherInstitution
      internal
   variable
   */
    public void add_PCO2D(Document doc) {
        Map<String, String> parts = getSingularItem(OcadsElementType.PCO2D.key());
        if ( parts == null || parts.isEmpty()) { return; }
        
        Element root = doc.getRootElement();
        
        Element var = new Element("variable");
        var.addContent(new Element("fullname").addContent("pCO2 (fCO2) discrete"));
        maybeAdd(var,"abbrev",parts.get(ocKeys.pCO2DX_Variable_abbreviation_in_data_files));
        maybeAdd(var,"observationType",parts.get(ocKeys.pCO2DX_Observation_type));
        maybeAdd(var,"insitu",parts.get(ocKeys.pCO2DX_In_situ_observation_X_manipulation_condition_X_response_variable));
        maybeAdd(var,"manipulationMethod",parts.get(ocKeys.pCO2DX_Manipulation_method));
        maybeAdd(var,"unit",parts.get(ocKeys.pCO2DX_Variable_unit));
        maybeAdd(var,"measured",parts.get(ocKeys.pCO2DX_Measured_or_calculated));
        maybeAdd(var,"calcMethod",parts.get(ocKeys.pCO2DX_Calculation_method_and_parameters));
        maybeAdd(var,"samplingInstrument",parts.get(ocKeys.pCO2DX_Sampling_instrument));
        maybeAdd(var,"analyzingInstrument",parts.get(ocKeys.pCO2DX_Analyzing_instrument));
        maybeAdd(var,"storageMethod",parts.get(ocKeys.pCO2DX_Storage_method));
        maybeAdd(var,"seawatervol",parts.get(ocKeys.pCO2DX_Seawater_volume));
        maybeAdd(var,"headspacevol",parts.get(ocKeys.pCO2DX_Headspace_volume));
        maybeAdd(var,"temperatureMeasure",parts.get(ocKeys.pCO2DX_Temperature_of_measurement));
        maybeAdd(var,"detailedInfo",parts.get(ocKeys.pCO2DX_Detailed_sampling_and_analyzing_information));
        Element gasDetector = new Element("gasDetector");
         maybeAdd(gasDetector,"manufacturer",parts.get(ocKeys.pCO2DX_Manufacturer_of_standard_gas));
         maybeAdd(gasDetector,"model",parts.get(ocKeys.pCO2DX_Model_of_the_gas_detector));
         maybeAdd(gasDetector,"resolution",parts.get(ocKeys.pCO2DX_Resolution_of_the_gas_detector));
         maybeAdd(gasDetector,"uncertainty",parts.get(ocKeys.pCO2DX_Uncertainty_of_the_gas_detector));
        var.addContent(gasDetector);
        Element standard = new Element("standard");
        maybeAdd(standard,"description",parts.get(ocKeys.pCO2DX_Standardization_technique_description));
        maybeAdd(standard,"frequency",parts.get(ocKeys.pCO2DX_Frequency_of_standardization));
        maybeAdd(standard,"temperature",parts.get(ocKeys.pCO2DX_Temperature_of_standardization));
        Element standardGas = new Element("standardgas");
            maybeAdd(standardGas,"manufacturer",parts.get(ocKeys.pCO2DX_Manufacturer_of_standard_gas));
            maybeAdd(standardGas,"concentration",parts.get(ocKeys.pCO2DX_Concentrations_of_standard_gas));
            maybeAdd(standardGas,"uncertainty",parts.get(ocKeys.pCO2DX_Uncertainties_of_standard_gas));
        standard.addContent(standardGas);
        var.addContent(standard);
        maybeAdd(var,"waterVaporCorrection",parts.get(ocKeys.pCO2DX_Water_vapor_correction_method));
        maybeAdd(var,"temperatureCorrection",parts.get(ocKeys.pCO2DX_Temperature_correction_method));
        maybeAdd(var,"co2ReportTemperature",parts.get(ocKeys.pCO2DX_at_what_temperature_was_pCO2_reported));
        maybeAdd(var,"uncertainty",parts.get(ocKeys.pCO2DX_Uncertainty));
        maybeAdd(var,"replicate",parts.get(ocKeys.pCO2DX_Field_replicate_information));
        maybeAdd(var,"flag",parts.get(ocKeys.pCO2DX_Data_quality_flag_description));
        maybeAdd(var,"methodReference",parts.get(ocKeys.pCO2DX_Method_reference));
        maybeAdd(var,"researcherName",parts.get(ocKeys.pCO2DX_Researcher_Name));
        maybeAdd(var,"researcherInstitution",parts.get(ocKeys.pCO2DX_Researcher_Institution));
        var.addContent(new Element("internal").addContent("5"));
                                 
        if ( !isEmpty(var)) {                         
            root.addContent(var);
        }
    }
    /* (non-Javadoc)
     * @see gov.noaa.pmel.excel2oap.ifc.XmlBuilder#buildXml(gov.noaa.pmel.excel2oap.ifc.SSParser)
     */
    @Override
    public void buildDocument(SSParser spreadSheet) {
         processDocument();
    }
    
    private void processDocument() {
        putGeneralFields(simpleItems);
        add_INVESTIGATORs();
        add_DATA_SUBMITTER(getSingularItem("Data submitter"));
        add_PLATFORMs();
        add_FUNDING(getSingularItem("Funding"));
        try {
            xmlJdoc = writeSdiMetadata(sdi);
            addVariables(xmlJdoc);
        } catch (Exception ex) {
            // TODO: handle exception
            ex.printStackTrace();
        }
    }
    private void addVariables(Document doc) {
        add_DIC(doc);
        add_TA(doc);
        add_PH(doc);
        add_PCO2A(doc);
        add_PCO2D(doc);
        add_VARs(doc);
    }

    
    private Document writeSdiMetadata(SDIMetadata sdi) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        OutputStreamWriter os = new OutputStreamWriter(baos,"UTF-8");
        OcadsWriter oc = new OcadsWriter(omitEmptyElements);
        oc.writeSDIMetadata(sdi, os);
        InputStream is = new ByteArrayInputStream(baos.toByteArray());
        Document doc = buildJDocument(is);
        return doc;
    }
    
    public static Document buildJDocument(InputStream is) throws Exception {
        // create the w3c DOM document from which JDOM is to be created
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        // we are interested in making it namespace aware.
        factory.setNamespaceAware(true);
        DocumentBuilder dombuilder = factory.newDocumentBuilder();
            
        InputSource insource = new InputSource(is);
//            Charset defaultCs = Charset.defaultCharset();
//            logger.debug("Default charset: " + defaultCs.displayName() + "("+defaultCs.name()+")");
        insource.setEncoding("UTF-8");
     
        org.w3c.dom.Document w3cDocument = dombuilder.parse(insource);
     
        // the DOMBuilder uses the DefaultJDOMFactory to create the JDOM2 objects.
        DOMBuilder jdomBuilder = new DOMBuilder();
     
        // jdomDocument is the JDOM2 Object
        Document jdomDocument = jdomBuilder.build(w3cDocument);
     
        return jdomDocument;
    }



    /* (non-Javadoc)
     * @see gov.noaa.pmel.excel2oap.ifc.XmlBuilder#outputXml(java.io.OutputStream)
     */
    @Override
    public void outputXml(OutputStream outputXmlStream) throws IOException {
        XMLOutputter xout = new XMLOutputter(Format.getPrettyFormat().setEncoding("UTF-8"));
        xout.output(xmlJdoc, outputXmlStream);
    }
    private void add_INVESTIGATORs() {
        ElementType investigatorKey = ssKeys.getElementForKey("Investigator");
        if ( ! multiItems.containsKey(investigatorKey)) {
            logger.info("no Investigator elements found.");
            investigatorKey = ssKeys.getElementForKey("PI");
            if ( ! multiItems.containsKey(investigatorKey)) {
                logger.info("No investigator or PI elements found!");
                return;
            }
        }
        Collection<Map<String, String>> investigators = multiItems.get(investigatorKey);
        if ( investigators == null || investigators.size() == 0 ) {
            logger.info("No investigators found.");
            return;
        }
        for (Map<String, String> investigator : investigators) {
            add_INVESTIGATOR(investigator);
        }
    }
    
    private void add_PLATFORMs() {
        ElementType platformKey = ssKeys.getElementForKey("Platform");
        Collection<Map<String, String>> platforms = multiItems.get(platformKey);
        if ( platforms == null || platforms.size() == 0 ) {
            logger.info("No platforms found.");
            return;
        }
        for (Map<String, String> platform : platforms) {
            add_PLATFORM(platform);
        }
    }
    
//    private void add_FUNDINGs() {
        // Not supported yet...
//    }
    
    /* (non-Javadoc)
     * @see gov.noaa.pmel.excel2oap.ifc.XmlBuilder#outputXml(java.io.OutputStream)
     */

    /**
     * @param sdi
     * @return
     */
    public void add_INVESTIGATOR(Map<String, String> parts) {
        if ( parts == null || parts.isEmpty()) { return; }
        Person person = Person.personBuilder()
                .firstName(parts.get(ssKeys.getKeyForName(ssKeys.name_PersonX_name)))
                .organization(parts.get(ssKeys.getKeyForName(ssKeys.name_PersonX_institution)))
                .id(parts.get(ssKeys.getKeyForName(ssKeys.name_PersonX_researcher_ID)))
                .idType(parts.get(ssKeys.getKeyForName(ssKeys.name_PersonX_ID_type)))
                .build();
        Investigator investigator = new Investigator(person);
        investigator.setEmail(parts.get(ssKeys.getKeyForName(ssKeys.name_PersonX_email)));
        investigator.setPhone(parts.get(ssKeys.getKeyForName(ssKeys.name_PersonX_phone)));
        investigator.addStreet(parts.get(ssKeys.getKeyForName(ssKeys.name_PersonX_address)));
        sdi.addInvestigator(investigator);
    }

    public void add_DATA_SUBMITTER(Map<String, String> parts) {
        if ( parts == null || parts.isEmpty()) { return; }
        Person person = Person.personBuilder()
                .firstName(parts.get(ssKeys.getKeyForName(ssKeys.name_PersonX_name)))
                .organization(parts.get(ssKeys.getKeyForName(ssKeys.name_PersonX_institution)))
                .id(parts.get(ssKeys.getKeyForName(ssKeys.name_PersonX_researcher_ID)))
                .idType(parts.get(ssKeys.getKeyForName(ssKeys.name_PersonX_ID_type)))
                .build();
        Submitter submitter = new Submitter(person);
        submitter.setEmail(parts.get(ssKeys.getKeyForName(ssKeys.name_PersonX_email)));
        submitter.setPhone(parts.get(ssKeys.getKeyForName(ssKeys.name_PersonX_phone)));
        submitter.addStreet(parts.get(ssKeys.getKeyForName(ssKeys.name_PersonX_address)));
        sdi.setSubmitter(submitter);
    }
    public void add_FUNDING(Map<String, String> parts) {
        if ( parts == null || parts.isEmpty()) { return; }
        // in addMiscInfo()  // XXX only supports 1
    }
    public void add_PLATFORM(Map<String, String> parts) {
        if ( parts == null || parts.isEmpty()) { return; }
        String platformName = parts.get(ssKeys.getKeyForName(ssKeys.name_PlatformX_name));
        String platformId = parts.get(ssKeys.getKeyForName(ssKeys.name_PlatformX_ID));
        if ( (StringUtils.emptyOrNull(platformName) || "none".equalsIgnoreCase(platformName)) && 
             (StringUtils.emptyOrNull(platformId) || "none".equalsIgnoreCase(platformId))) {
            return;
        }
        Platform platform = new Platform();
        platform.setPlatformName(platformName);
        platform.setPlatformId(platformId);
        platform.setPlatformTypeStr(parts.get(ssKeys.getKeyForName(ssKeys.name_PlatformX_type)));
        platform.setPlatformOwner(parts.get(ssKeys.getKeyForName(ssKeys.name_PlatformX_owner)));
        platform.setPlatformCountry(parts.get(ssKeys.getKeyForName(ssKeys.name_PlatformX_country)));
        sdi.setPlatform(platform);
    }


    private void putGeneralFields(Map<String, String> generalFields) {
        addMiscInfo(generalFields);
        addCoverage(generalFields);
    }
    private void addMiscInfo(Map<String, String> generalFields) {
        Datestamp startDatestamp = tryDatestamp(generalFields.get(ssKeys.getKeyForName(ssKeys.name_r_Start_date)));
        Datestamp endDatestamp = tryDatestamp(generalFields.get(ssKeys.getKeyForName(ssKeys.name_r_End_date)));
        String fundingAgency = generalFields.get(ssKeys.getKeyForName(ssKeys.name_r_Funding_agency_name)) != "" ?
                                generalFields.get(ssKeys.getKeyForName(ssKeys.name_r_Funding_agency_name)) :
                                generalFields.get(ssKeys.getKeyForName(ssKeys.name_r_Funding_agency_name_ALT));
        String fundingProjectId = generalFields.get(ssKeys.getKeyForName(ssKeys.name_r_Funding_project_ID)) != "" ?
                                   generalFields.get(ssKeys.getKeyForName(ssKeys.name_r_Funding_project_ID)) :
                                   generalFields.get(ssKeys.getKeyForName(ssKeys.name_r_Funding_project_ID_ALT));
        MiscInfoBuilder mib = new MiscInfo().toBuilder()
                .datasetId(generalFields.get(ssKeys.getKeyForName(ssKeys.name_r_EXPOCODE)))
                .datasetName(generalFields.get(ssKeys.getKeyForName(ssKeys.name_r_Cruise_ID)))
                .sectionName(generalFields.get(ssKeys.getKeyForName(ssKeys.name_r_Section)))
                .fundingAgency(fundingAgency)
                .fundingId(fundingProjectId)
                .fundingTitle(generalFields.get(ssKeys.getKeyForName(ssKeys.name_r_Funding_project_title)))
                .researchProject(generalFields.get(ssKeys.getKeyForName(ssKeys.name_r_Research_projects)))
    //            .datasetDoi(generalFields.get(nothing))
                .accessId(generalFields.get(ssKeys.getKeyForName(ssKeys.name_r_Accession_no_of_related_data_sets)))
                .citation(generalFields.get(ssKeys.getKeyForName(ssKeys.name_r_Author_list_for_citation)))
                .synopsis(generalFields.get(ssKeys.getKeyForName(ssKeys.name_r_Abstract)))
                .purpose(generalFields.get(ssKeys.getKeyForName(ssKeys.name_r_Purpose)))
                .title(generalFields.get(ssKeys.getKeyForName(ssKeys.name_r_Title)))
                .addReference(generalFields.get(ssKeys.getKeyForName(ssKeys.name_r_References)))
                .addInfo(generalFields.get(ssKeys.getKeyForName(ssKeys.name_r_Supplemental_information)))
                .startDatestamp(startDatestamp)
                .endDatestamp(endDatestamp);
        String submissionDateStr = generalFields.get(ssKeys.getKeyForName(ssKeys.name_r_Submission_Date));
        if ( !StringUtils.emptyOrNull(submissionDateStr)) {
            Datestamp submissionDate = tryDatestamp(submissionDateStr);
            mib.history(new ArrayList<Datestamp>() {{ add(submissionDate); }});
        }
        MiscInfo mi = mib.build();
        sdi.setMiscInfo(mi);
    }
    
    private void addCoverage(Map<String, String> generalFields) {
        Datestamp miscStart = sdi.getMiscInfo().getStartDatestamp();
        Datestamp miscEnd = sdi.getMiscInfo().getEndDatestamp();
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, miscStart.getYear().intValue());
        cal.set(Calendar.MONTH, miscStart.getMonth().intValue()-1);
        cal.set(Calendar.DAY_OF_MONTH, miscStart.getDay().intValue());
        cal.set(Calendar.HOUR, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        Date cStart = cal.getTime();
        cal.set(Calendar.YEAR, miscEnd.getYear().intValue());
        cal.set(Calendar.MONTH, miscEnd.getMonth().intValue()-1);
        cal.set(Calendar.DAY_OF_MONTH, miscEnd.getDay().intValue());
        Date cEnd = cal.getTime();
                
        Coverage coverage = new Coverage().toBuilder()
                .westernLongitude(new NumericString(generalFields.get(ssKeys.getKeyForName(ssKeys.name_r_Westbd_longitude)), Coverage.LONGITUDE_UNITS))
                .easternLongitude(new NumericString(generalFields.get(ssKeys.getKeyForName(ssKeys.name_r_Eastbd_longitude)), Coverage.LONGITUDE_UNITS))
                .northernLatitude(new NumericString(generalFields.get(ssKeys.getKeyForName(ssKeys.name_r_Northbd_latitude)), Coverage.LATITUDE_UNITS))
                .southernLatitude(new NumericString(generalFields.get(ssKeys.getKeyForName(ssKeys.name_r_Southbd_latitude)), Coverage.LATITUDE_UNITS))
                .earliestDataTime(cStart)
                .latestDataTime(cEnd)
                .spatialReference(generalFields.get(ssKeys.getKeyForName(ssKeys.name_r_Spatial_reference_system)))
                .addGeographicName(generalFields.get(ssKeys.getKeyForName(ssKeys.name_r_Geographic_names)))
                .build();
        sdi.setCoverage(coverage);
    }
    
    /*
    variable
    abbrev
    fullname
    observationType
    insitu
    unit
    measured
    calcMethod
    samplingInstrument
    analyzingInstrument
    duration
    detailedInfo
    replicate
    uncertainty
    flag
    methodReference
    biologicalSubject
    speciesID
    lifeStage
    researcherName
    researcherInstitution
    public void add_VAR(Document doc, Map<String, String> parts) {
        Element root = doc.getRootElement();
        Element var = new Element("variable");
        maybeAdd(var,"abbrev",parts.get(keys.getKey(keys.name_VarX_Variable_abbreviation_in_data_files)));
        maybeAdd(var,"fullname",parts.get(keys.getKey(keys.name_VarX_Full_variable_name)));
        maybeAdd(var,"observationType",parts.get(keys.getKey(keys.name_VarX_Observation_type)));
        maybeAdd(var,"insitu",parts.get(keys.getKey(keys.name_VarX_In_situ_observation_X_manipulation_condition_X_response_variable)));
        maybeAdd(var,"unit",parts.get(keys.getKey(keys.name_VarX_Variable_unit)));
        maybeAdd(var,"measured",parts.get(keys.getKey(keys.name_VarX_Measured_or_calculated)));
        maybeAdd(var,"calcMethod",parts.get(keys.getKey(keys.name_VarX_Calculation_method_and_parameters)));
        maybeAdd(var,"samplingInstrument",parts.get(keys.getKey(keys.name_VarX_Sampling_instrument)));
        maybeAdd(var,"analyzingInstrument",parts.get(keys.getKey(keys.name_VarX_Analyzing_instrument)));
        maybeAdd(var,"duration",parts.get(keys.getKey(keys.name_VarX_Duration)));
        maybeAdd(var,"detailedInfo",parts.get(keys.getKey(keys.name_VarX_Detailed_sampling_and_analyzing_information)));
        maybeAdd(var,"replicate",parts.get(keys.getKey(keys.name_VarX_Field_replicate_information)));
        maybeAdd(var,"uncertainty",parts.get(keys.getKey(keys.name_VarX_Uncertainty)));
        maybeAdd(var,"flag",parts.get(keys.getKey(keys.name_VarX_Data_quality_flag_description)));
        maybeAdd(var,"methodReference",parts.get(keys.getKey(keys.name_VarX_Method_reference)));
        maybeAdd(var,"biologicalSubject",parts.get(keys.getKey(keys.name_VarX_Biological_subject)));
        maybeAdd(var,"speciesID",parts.get(keys.getKey(keys.name_VarX_Species_Identification_code)));
        maybeAdd(var,"lifeStage",parts.get(keys.getKey(keys.name_VarX_Life_Stage)));
        maybeAdd(var,"researcherName",parts.get(keys.getKey(keys.name_VarX_Researcher_Name)));
        maybeAdd(var,"researcherInstitution",parts.get(keys.getKey(keys.name_VarX_Researcher_Institution)));
        var.addContent(new Element("internal").addContent("0"));
      
        root.addContent(var);
    }
    */

    /**
     * @param string
     * @return
     */
    protected static Datestamp tryDatestamp(String string) {
        logger.debug("Trying datastamp for " + string);
        Datestamp ds = new Datestamp();
        if ( ! StringUtils.emptyOrNull(string)) {
            String[] parts = string.split("[/ -]");
            if ( parts.length == 3 ) {
                String p0 = parts[0];
                String p1 = parts[1];
                String p2 = parts[2];
                try {
                    int i0 = Integer.parseInt(p0);
                    int i1 = Integer.parseInt(p1);
                    int i2 = Integer.parseInt(p2);
                    int month, day, year = -1;
                    month = day = year;
                    if ( i0 > 12 ) { // assume not month
                        if ( i0 > 31 ) { // assume year
                            year = i0;   // then assume yyyy-mm-dd
                            month = i1;
                            day = i2;
                        } else {
                            day = i0;  // else assume dd-mm-yyyy
                            month = i1;
                            year = i2;
                        }
                    } else { // assume mm-dd-yyyy
                        month = i0;
                        day = i1;
                        year = i2;
                    } 
                    
                    if ( year < 1000 ) {
                        if ( year < 42 ) { // because that's the answer
                            year += 2000;
                        } else {
                            year += 1900;
                        }
                    }
                    ds.setMonth(month);
                    ds.setDay(day);
                    ds.setYear(year);
                }
                catch (Exception ex) {
                    logger.info("Excel2Oap Exception parsing date string:"+ string + ":"+ex.toString());
                    if ( ex instanceof NumberFormatException ) {
                        SimpleDateFormat sdf = new SimpleDateFormat("MMMMM d y");
                        try {
                            Date d = sdf.parse(string);
                            Calendar c = Calendar.getInstance();
                            c.setTime(d);
                            ds.setMonth(c.get(Calendar.MONTH)+1);
                            ds.setDay(c.get(Calendar.DAY_OF_MONTH));
                            ds.setYear(c.get(Calendar.YEAR));
                        } catch (ParseException pex) {
                            try {
                                sdf = new SimpleDateFormat("yyyy-mm-dd'T'hh:MM:ss");
                                sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
                                Date d = sdf.parse(string);
                                Calendar c = Calendar.getInstance();
                                c.setTime(d);
                                ds.setMonth(c.get(Calendar.MONTH)+1);
                                ds.setDay(c.get(Calendar.DAY_OF_MONTH));
                                ds.setYear(c.get(Calendar.YEAR));
                            } catch (Exception e3) {
                                logger.info("Absolute Final failure to parse date string " + 
                                 string + ": " + pex.toString());
                            }
                        } catch (Exception e2) {
                            logger.info("Final failure to parse date string " + string + ": " + e2.toString());
                        }
                    }
                }
            } else {
                logger.info("Excel2Oap: Cannot parse date string:" + string);
            }
        }
        if ( ds.getYear() <= 0 ) {
            logger.info("Unable to parse date string: "+ string);
        }
        return ds;
    }

}
