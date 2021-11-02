/**
 * 
 */
package gov.noaa.pmel.excel2oap.ocads;

import java.util.Map;

import org.jdom2.Document;
import org.jdom2.Element;

import gov.noaa.pmel.excel2oap.BaseSpreadSheetHandler;
import gov.noaa.pmel.excel2oap.ElementType;
import gov.noaa.pmel.excel2oap.SpreadSheetKeys;
import gov.noaa.pmel.excel2oap.SpreadSheetType;
import gov.noaa.pmel.sdimetadata.variable.Variable;

/**
 * @author kamb
 *
 */
public class OcadsHandler extends BaseSpreadSheetHandler {

    private OcadsKeys ocKeys; //  = new OcadsKeys();
    
    public SpreadSheetType getSpreadSheetType() { return SpreadSheetType.OCADS; }
    
    private static final String[] multiItemFields = { 
            OcadsElementType.INVESTIGATOR.key(), 
            OcadsElementType.PI.key(), 
//            OcadsElementType.FUNDING.key(), // Not supported yet.
            OcadsElementType.PLATFORM.key(), 
            OcadsElementType.VAR.key() 
    };
    private static final String[] multiLineFields = { 
            OcadsElementType.DATA_SUBMITTER.key(), 
            OcadsElementType.DIC.key(), 
            OcadsElementType.TA.key(), 
            OcadsElementType.PH.key(), 
            OcadsElementType.PCO2A.key(), 
            OcadsElementType.PCO2D.key()
    };

    public OcadsHandler(boolean omitEmptyElements, OcadsKeys ocadsKeys) {
        super(multiLineFields, multiItemFields, ocadsKeys, omitEmptyElements);
    }
    protected OcadsHandler(String[] multiLine, String[] multiItem,
                             SpreadSheetKeys keys, boolean omitEmptyElements) {
        super(multiLine,multiItem,keys,omitEmptyElements);
    }

//    @Override
//    public String[] multiLineFields() {
//        // TODO Auto-generated method stub
//        return null;
//    }
//
//    @Override
//    public String[] multiItemFields() {
//        // TODO Auto-generated method stub
//        return null;
//    }

    @Override
    public ElementType elementForKey(String key) {
        return OcadsElementType.fromSsRowName(key);
    }

    /* (non-Javadoc)
     * @see gov.noaa.pmel.excel2oap.SpreadSheetTypeFlavor#getKeys()
     */
    @Override
    public SpreadSheetKeys getKeys() {
        return ocKeys;
    }
    
    @Override
    public void addOtherStuff(Document doc) {
        add_DIC(doc);
        add_TA(doc);
        add_PH(doc);
        add_PCO2A(doc);
        add_PCO2D(doc);
        add_VARs(doc);
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
        if ( parts == null ) {
            return;
        }
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
        if ( parts == null ) {
            return;
        }
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
                                 
        root.addContent(var);
    }
    public void add_PH(Document doc) {
        Map<String, String> parts = getSingularItem(OcadsElementType.PH.key());
        if ( parts == null ) {
            return;
        }
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
                                 
        root.addContent(var);
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
        if ( parts == null ) {
            return;
        }
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
                                 
        root.addContent(var);
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
        if ( parts == null ) {
            return;
        }
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
        Element standardGas = new Element("standardGas");
            maybeAdd(standardGas,"manufacturer",parts.get(ocKeys.pCO2DX_Manufacturer_of_standard_gas));
            maybeAdd(standardGas,"concentration",parts.get(ocKeys.pCO2DX_Concentrations_of_standard_gas));
            maybeAdd(standardGas,"uncertainty",parts.get(ocKeys.pCO2DX_Uncertainties_of_standard_gas));
        standard.addContent(standardGas);
        var.addContent(standard);
        maybeAdd(var,"waterVaporCorrection",parts.get(ocKeys.pCO2DX_Water_vapor_correction_method));
        maybeAdd(var,"temperatureCorrection",parts.get(ocKeys.pCO2DX_Temperature_correction_method));
        maybeAdd(var,"co2ReportTemperature",parts.get(ocKeys.pCO2DX_at_what_temperature_was_pCO2_reported));
        maybeAdd(var,"uncertainty",parts.get(ocKeys.pCO2DX_Uncertainty));
        maybeAdd(var,"flag",parts.get(ocKeys.pCO2DX_Data_quality_flag_description));
        maybeAdd(var,"methodReference",parts.get(ocKeys.pCO2DX_Method_reference));
        maybeAdd(var,"researcherName",parts.get(ocKeys.pCO2DX_Researcher_Name));
        maybeAdd(var,"researcherInstitution",parts.get(ocKeys.pCO2DX_Researcher_Institution));
        var.addContent(new Element("internal").addContent("5"));
                                 
        root.addContent(var);
    }
    private void add_DIC(Document doc, Map<String, String> parts) {
        Variable var = buildSDIvariable(parts);
        var.setFullName("Dissolved inorganic carbon");
    }
    @SuppressWarnings("unused")
    private void add_TA(Document doc, Map<String, String> parts) {
        Variable var = buildSDIvariable(parts);
        var.setFullName("Total alkalinity");
    }
    @SuppressWarnings("unused")
    private void add_PH(Document doc, Map<String, String> parts) {
        Variable var = buildSDIvariable(parts);
        var.setFullName("pH");
    }
    @SuppressWarnings("unused")
    private void add_PCO2A(Document doc, Map<String, String> parts) {
        Variable var = buildSDIvariable(parts);
        var.setFullName("pCO2 (fCO2) autonomous");
    }
    @SuppressWarnings("unused")
    private void add_PCO2D(Document doc, Map<String, String> parts) {
        Variable var = buildSDIvariable(parts);
        var.setFullName("pCO2 (fCO2) discrete");
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

}
