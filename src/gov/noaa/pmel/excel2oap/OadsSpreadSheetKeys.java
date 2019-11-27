/**
 * 
 */
package gov.noaa.pmel.excel2oap;

import java.util.HashMap;
import java.util.Map;

/**
 * @author kamb
 *
 */
public class OadsSpreadSheetKeys {

    public static enum ElementType {
        
        INVESTIGATOR("Investigator"),
        PI("PI"),
        DATA_SUBMITTER("Data submitter"),
        PLATFORM("Platform"),
        DIC("DIC"),
        TA("TA"),
        PH("pH"),
        PCO2A("pCO2A"),
        PCO2D("pCO2D"),
        VAR("Var"),
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
        FUNDING("Funding"),
        RESEARCH_PROJECT("Research projects"),
        EXPOCODE("EXPOCODE"),
        CRUISE_ID("Cruise ID"),
        SECTION("Section"),
        CITATION_LIST("Author list for citation"),
        REFERENCES("References"),
        SUPPLEMENTAL_INFO("Supplemental information");
        
        private ElementType(String spreadsheetKey) {
            ssKey = spreadsheetKey;
        }
        private String ssKey;
        public String key() { return ssKey; }
        
        @SuppressWarnings("serial")
        private static Map<String, ElementType> rowCellNameMap = null;
        
        private static synchronized Map<String, ElementType> initMap() {
            if ( rowCellNameMap == null ) {
                rowCellNameMap = new HashMap<String, OadsSpreadSheetKeys.ElementType>();
                for (ElementType t : values()) {
                    rowCellNameMap.put(t.ssKey, t);
                }
            }
            return rowCellNameMap;
        }
        public static ElementType fromSsRowName(String rowName) {
            if ( rowCellNameMap == null ) { rowCellNameMap = initMap(); }
            if ( ! rowCellNameMap.containsKey(rowName)) {
                throw new IllegalStateException("No element found for cell name key: " + rowName);
            }
            return rowCellNameMap.get(rowName);
        }
    }
    // Processing-need-based keys
    // Person-type "remainder" keys
	public static final String PersonX_name = "name";
	public static final String PersonX_institution = "institution";
	public static final String PersonX_address = "address";
	public static final String PersonX_phone = "phone";
	public static final String PersonX_email = "email";
	public static final String PersonX_researcher_ID = "researcher ID";
	public static final String PersonX_ID_type = "ID type  (ORCID, Researcher ID, etc.)";
    
    // Platform "remainder" keys
	public static final String PlatformX_name = "name";
	public static final String PlatformX_ID = "ID";
	public static final String PlatformX_type = "type";
	public static final String PlatformX_owner = "owner";
	public static final String PlatformX_country = "country";
    
    // Var "remainder" keys
	public static final String VarX_Variable_abbreviation_in_data_files = "Variable abbreviation in data files";
	public static final String VarX_Full_variable_name = "Full variable name";
	public static final String VarX_Observation_type = "Observation type";
	public static final String VarX_In_situ_observation_X_manipulation_condition_X_response_variable = "In-situ observation / manipulation condition / response variable";
	public static final String VarX_Variable_unit = "Variable unit";
	public static final String VarX_Measured_or_calculated = "Measured or calculated";
	public static final String VarX_Calculation_method_and_parameters = "Calculation method and parameters";
	public static final String VarX_Sampling_instrument = "Sampling instrument";
	public static final String VarX_Analyzing_instrument = "Analyzing instrument";
	public static final String VarX_Duration = "Duration (for settlement/colonization methods)";
	public static final String VarX_Detailed_sampling_and_analyzing_information = "Detailed sampling and analyzing information";
	public static final String VarX_Field_replicate_information = "Field replicate information";
	public static final String VarX_Uncertainty = "Uncertainty";
	public static final String VarX_Data_quality_flag_description = "Data quality flag description";
	public static final String VarX_Method_reference = "Method reference (citation)";
	public static final String VarX_Biological_subject = "Biological subject";
	public static final String VarX_Species_Identification_code = "Species Identification code";
	public static final String VarX_Life_Stage = "Life stage of the Biological subject";
	public static final String VarX_Researcher_Name = "Researcher Name";
	public static final String VarX_Researcher_Institution = "Researcher Institution";
	
    // DIC "remainder" keys
	public static final String DICX_Variable_abbreviation_in_data_files = VarX_Variable_abbreviation_in_data_files ;
	public static final String DICX_Observation_type = VarX_Observation_type ;
	public static final String DICX_In_situ_observation_X_manipulation_condition_X_response_variable = VarX_In_situ_observation_X_manipulation_condition_X_response_variable ;
	public static final String DICX_Manipulation_method = "Manipulation method";
	public static final String DICX_Variable_unit = VarX_Variable_unit ;
	public static final String DICX_Measured_or_calculated = VarX_Measured_or_calculated ;
	public static final String DICX_Calculation_method_and_parameters = VarX_Calculation_method_and_parameters ;
	public static final String DICX_Sampling_instrument = VarX_Sampling_instrument ;
	public static final String DICX_Analyzing_instrument = VarX_Analyzing_instrument ;
	public static final String DICX_Detailed_sampling_and_analyzing_information = VarX_Detailed_sampling_and_analyzing_information ;
	public static final String DICX_Field_replicate_information = VarX_Field_replicate_information ;
	public static final String DICX_Standardization_technique_description = "Standardization technique description";
	public static final String DICX_Frequency_of_standardization = "Frequency of standardization";
	public static final String DICX_CRM_manufacturer = "CRM manufacturer";
	public static final String DICX_Batch_number = "Batch number";
	public static final String DICX_Poison_used_to_kill_the_sample = "Poison used to kill the sample";
	public static final String DICX_Poison_volume = "Poison volume";
	public static final String DICX_Poisoning_correction_description = "Poisoning correction description";
	public static final String DICX_Uncertainty = VarX_Uncertainty ;
	public static final String DICX_Data_quality_flag_description = VarX_Data_quality_flag_description ;
	public static final String DICX_Method_reference = VarX_Method_reference ;
	public static final String DICX_Researcher_Name = VarX_Researcher_Name ;
	public static final String DICX_Researcher_Institution = VarX_Researcher_Institution ;
    
    // TA "remainder" keys
	public static final String TAX_Variable_abbreviation_in_data_files = VarX_Variable_abbreviation_in_data_files ;
	public static final String TAX_Observation_type = VarX_Observation_type ;
	public static final String TAX_In_situ_observation_X_manipulation_condition_X_response_variable = VarX_In_situ_observation_X_manipulation_condition_X_response_variable ;
	public static final String TAX_Manipulation_method = DICX_Manipulation_method ;
	public static final String TAX_Variable_unit = VarX_Variable_unit ;
	public static final String TAX_Measured_or_calculated = VarX_Measured_or_calculated ;
	public static final String TAX_Calculation_method_and_parameters = VarX_Calculation_method_and_parameters ;
	public static final String TAX_Sampling_instrument = VarX_Sampling_instrument ;
	public static final String TAX_Analyzing_instrument = VarX_Analyzing_instrument ;
	public static final String TAX_Type_of_titration = "Type of titration";
	public static final String TAX_Cell_type = "Cell type (open or closed)";
	public static final String TAX_Curve_fitting_method = "Curve fitting method";
	public static final String TAX_Detailed_sampling_and_analyzing_information = VarX_Detailed_sampling_and_analyzing_information ;
	public static final String TAX_Field_replicate_information = VarX_Field_replicate_information ;
	public static final String TAX_Standardization_technique_description = DICX_Standardization_technique_description ;
	public static final String TAX_Frequency_of_standardization = DICX_Frequency_of_standardization ;
	public static final String TAX_CRM_manufacturer = DICX_CRM_manufacturer ;
	public static final String TAX_Batch_number = DICX_Batch_number;
	public static final String TAX_Poison_used_to_kill_the_sample = DICX_Poison_used_to_kill_the_sample ;
	public static final String TAX_Poison_volume = DICX_Poison_volume ;
	public static final String TAX_Poisoning_correction_description = DICX_Poisoning_correction_description ;
	public static final String TAX_Magnitude_of_blank_correction = "Magnitude of blank correction";
	public static final String TAX_Uncertainty = VarX_Uncertainty ;
	public static final String TAX_Data_quality_flag_description = VarX_Data_quality_flag_description ;
	public static final String TAX_Method_reference = VarX_Method_reference ;
	public static final String TAX_Researcher_Name = VarX_Researcher_Name ;
	public static final String TAX_Researcher_Institution = VarX_Researcher_Institution ;
    
    // pH "remainder" keys
	public static final String PHX_Variable_abbreviation_in_data_files = VarX_Variable_abbreviation_in_data_files; 
	public static final String PHX_Observation_type = VarX_Observation_type; 
	public static final String PHX_In_situ_observation_X_manipulation_condition_X_response_variable = VarX_In_situ_observation_X_manipulation_condition_X_response_variable; 
	public static final String PHX_Manipulation_method = DICX_Manipulation_method; 
	public static final String PHX_Measured_or_calculated = VarX_Measured_or_calculated; 
	public static final String PHX_Calculation_method_and_parameters = VarX_Calculation_method_and_parameters; 
	public static final String PHX_Sampling_instrument = VarX_Sampling_instrument; 
	public static final String PHX_Analyzing_instrument = VarX_Analyzing_instrument; 
	public static final String PHX_pH_scale = "pH scale";
	public static final String PHX_Temperature_of_measurement = "Temperature of measurement";
	public static final String PHX_Detailed_sampling_and_analyzing_information = VarX_Detailed_sampling_and_analyzing_information; 
	public static final String PHX_Field_replicate_information = VarX_Field_replicate_information; 
	public static final String PHX_Standardization_technique_description = DICX_Standardization_technique_description; 
	public static final String PHX_Frequency_of_standardization = DICX_Frequency_of_standardization; 
	public static final String PHX_pH_values_of_the_standards = "pH values of the standards";
	public static final String PHX_Temperature_of_standardization = "Temperature of standardization";
	public static final String PHX_Temperature_correction_method = "Temperature correction method";
	public static final String PHX_at_what_temperature_was_pH_reported = "at what temperature was pH reported";
	public static final String PHX_Uncertainty = VarX_Uncertainty; 
	public static final String PHX_Data_quality_flag_description = VarX_Data_quality_flag_description; 
	public static final String PHX_Method_reference = VarX_Method_reference; 
	public static final String PHX_Researcher_Name = VarX_Researcher_Name; 
	public static final String PHX_Researcher_Institution = VarX_Researcher_Institution; 
	
	// pCO2 common "remainder" keys
	public static final String pCO2Common_Manufacturer_of_the_gas_detector = "Manufacturer of the gas detector";
	public static final String pCO2Common_Model_of_the_gas_detector = "Model of the gas detector";
	public static final String pCO2Common_Resolution_of_the_gas_detector = "Resolution of the gas detector";
	public static final String pCO2Common_Uncertainty_of_the_gas_detector = "Uncertainty of the gas detector";
	public static final String pCO2Common_Standardization_technique_description = "Standardization technique description";
	public static final String pCO2Common_Frequency_of_standardization = "Frequency of standardization";
	public static final String pCO2Common_Manufacturer_of_standard_gas = "Manufacturer of standard gas";
	public static final String pCO2Common_Concentrations_of_standard_gas = "Concentrations of standard gas";
	public static final String pCO2Common_Uncertainties_of_standard_gas = "Uncertainties of standard gas";
	public static final String pCO2Common_Water_vapor_correction_method = "Water vapor correction method";
	public static final String pCO2Common_Temperature_correction_method = "Temperature correction method";
	public static final String pCO2Common_at_what_temperature_was_pCO2_reported = "at what temperature was pCO2 reported";

    // pCO2A "remainder" keys
	public static final String pCO2AX_Variable_abbreviation_in_data_files = VarX_Variable_abbreviation_in_data_files; 
	public static final String pCO2AX_Observation_type = VarX_Observation_type; 
	public static final String pCO2AX_In_situ_observation_X_manipulation_condition_X_response_variable = VarX_In_situ_observation_X_manipulation_condition_X_response_variable; 
	public static final String pCO2AX_Manipulation_method = DICX_Manipulation_method; 
	public static final String pCO2AX_Variable_unit = VarX_Variable_unit; 
	public static final String pCO2AX_Measured_or_calculated = VarX_Measured_or_calculated; 
	public static final String pCO2AX_Calculation_method_and_parameters = VarX_Calculation_method_and_parameters; 
	public static final String pCO2AX_Sampling_instrument = VarX_Sampling_instrument; 
	public static final String pCO2AX_Location_of_seawater_intake = "Location of seawater intake";
	public static final String pCO2AX_Depth_of_seawater_intake = "Depth of seawater intake";
	public static final String pCO2AX_Analyzing_instrument = VarX_Analyzing_instrument; 
	public static final String pCO2AX_Detailed_sampling_and_analyzing_information = VarX_Detailed_sampling_and_analyzing_information; 
	public static final String pCO2AX_Equilibrator_type = "Equilbrator type";
	public static final String pCO2AX_Equilibrator_volume = "Equilibrator volume (L)";
	public static final String pCO2AX_Vented_or_not = "Vented or not";
	public static final String pCO2AX_Water_flow_rate = "Water flow rate (L/min)";
	public static final String pCO2AX_Headspace_gas_flow_rate = "Headspace gas flow rate (L/min)";
	public static final String pCO2AX_How_was_temperature_inside_the_equilibrator_measured = "How was temperature inside the equilibrator measured .";
	public static final String pCO2AX_How_was_pressure_inside_the_equilibrator_measured = "How was pressure inside the equilibrator measured.";
	public static final String pCO2AX_Drying_method_for_CO2_gas = "Drying method for CO2 gas";
	public static final String pCO2AX_Manufacturer_of_the_gas_detector = pCO2Common_Manufacturer_of_the_gas_detector; 
	public static final String pCO2AX_Model_of_the_gas_detector = pCO2Common_Model_of_the_gas_detector; 
	public static final String pCO2AX_Resolution_of_the_gas_detector = pCO2Common_Resolution_of_the_gas_detector; 
	public static final String pCO2AX_Uncertainty_of_the_gas_detector = pCO2Common_Uncertainty_of_the_gas_detector; 
	public static final String pCO2AX_Standardization_technique_description = pCO2Common_Standardization_technique_description; 
	public static final String pCO2AX_Frequency_of_standardization = pCO2Common_Frequency_of_standardization; 
	public static final String pCO2AX_Manufacturer_of_standard_gas = pCO2Common_Manufacturer_of_standard_gas; 
	public static final String pCO2AX_Concentrations_of_standard_gas = pCO2Common_Concentrations_of_standard_gas; 
	public static final String pCO2AX_Uncertainties_of_standard_gas = pCO2Common_Uncertainties_of_standard_gas; 
	public static final String pCO2AX_Water_vapor_correction_method = pCO2Common_Water_vapor_correction_method; 
	public static final String pCO2AX_Temperature_correction_method = pCO2Common_Temperature_correction_method; 
	public static final String pCO2AX_at_what_temperature_was_pCO2_reported = pCO2Common_at_what_temperature_was_pCO2_reported; 
	public static final String pCO2AX_Uncertainty = VarX_Uncertainty; 
	public static final String pCO2AX_Data_quality_flag_description = VarX_Data_quality_flag_description; 
	public static final String pCO2AX_Method_reference = VarX_Method_reference; 
	public static final String pCO2AX_Researcher_Name = VarX_Researcher_Name; 
	public static final String pCO2AX_Researcher_Institution = VarX_Researcher_Institution; 
	
    // pCO2D "remainder" keys
	public static final String pCO2DX_Variable_abbreviation_in_data_files = VarX_Variable_abbreviation_in_data_files; 
	public static final String pCO2DX_Observation_type = VarX_Observation_type; 
	public static final String pCO2DX_In_situ_observation_X_manipulation_condition_X_response_variable = VarX_In_situ_observation_X_manipulation_condition_X_response_variable; 
	public static final String pCO2DX_Manipulation_method = DICX_Manipulation_method; 
	public static final String pCO2DX_Variable_unit = VarX_Variable_unit; 
	public static final String pCO2DX_Measured_or_calculated = VarX_Measured_or_calculated; 
	public static final String pCO2DX_Calculation_method_and_parameters = VarX_Calculation_method_and_parameters; 
	public static final String pCO2DX_Sampling_instrument = VarX_Sampling_instrument; 
	public static final String pCO2DX_Analyzing_instrument = VarX_Analyzing_instrument; 
	public static final String pCO2DX_Storage_method = "Storage method";
	public static final String pCO2DX_Seawater_volume = "Seawater volume (mL)";
	public static final String pCO2DX_Headspace_volume = "Headspace volume (mL)";
	public static final String pCO2DX_Temperature_of_measurement = "Temperature of measurement";
	public static final String pCO2DX_Detailed_sampling_and_analyzing_information = VarX_Detailed_sampling_and_analyzing_information; 
	public static final String pCO2DX_Field_replicate_information = VarX_Field_replicate_information; 
	public static final String pCO2DX_Manufacturer_of_the_gas_detector = pCO2Common_Manufacturer_of_the_gas_detector; 
	public static final String pCO2DX_Model_of_the_gas_detector = pCO2Common_Model_of_the_gas_detector; 
	public static final String pCO2DX_Resolution_of_the_gas_detector = pCO2Common_Resolution_of_the_gas_detector; 
	public static final String pCO2DX_Uncertainty_of_the_gas_detector = pCO2Common_Uncertainty_of_the_gas_detector; 
	public static final String pCO2DX_Standardization_technique_description = pCO2Common_Standardization_technique_description; 
	public static final String pCO2DX_Frequency_of_standardization = pCO2Common_Frequency_of_standardization; 
	public static final String pCO2DX_Temperature_of_standardization = "Temperature of standardization";
	public static final String pCO2DX_Manufacturer_of_standard_gas = pCO2Common_Manufacturer_of_standard_gas; 
	public static final String pCO2DX_Concentrations_of_standard_gas = pCO2Common_Concentrations_of_standard_gas; 
	public static final String pCO2DX_Uncertainties_of_standard_gas = pCO2Common_Uncertainties_of_standard_gas; 
	public static final String pCO2DX_Water_vapor_correction_method = pCO2Common_Water_vapor_correction_method; 
	public static final String pCO2DX_Temperature_correction_method = pCO2Common_Temperature_correction_method; 
	public static final String pCO2DX_at_what_temperature_was_pCO2_reported = pCO2Common_at_what_temperature_was_pCO2_reported; 
	public static final String pCO2DX_Uncertainty = VarX_Uncertainty; 
	public static final String pCO2DX_Data_quality_flag_description = VarX_Data_quality_flag_description; 
	public static final String pCO2DX_Method_reference = VarX_Method_reference; 
	public static final String pCO2DX_Researcher_Name = VarX_Researcher_Name; 
	public static final String pCO2DX_Researcher_Institution = VarX_Researcher_Institution; 
	
	// Raw row name values
	public static final String r_Submission_Date = "Submission Date";
	public static final String r_Accession_no_of_related_data_sets = "Accession no. of related data sets";
	public static final String r_Investigator1_name = "Investigator-1 name";
	public static final String r_Investigator1_institution = "Investigator-1 institution";
	public static final String r_Investigator1_address = "Investigator-1 address";
	public static final String r_Investigator1_phone = "Investigator-1 phone";
	public static final String r_Investigator1_email = "Investigator-1 email";
	public static final String r_Investigator1_researcher_ID = "Investigator-1 researcher ID";
	public static final String r_Investigator1_ID_type = "Investigator-1 ID type  (ORCID, Researcher ID, etc.)";
	public static final String r_Investigator2_name = "Investigator-2 name";
	public static final String r_Investigator2_institution = "Investigator-2 institution";
	public static final String r_Investigator2_address = "Investigator-2 address";
	public static final String r_Investigator2_phone = "Investigator-2 phone";
	public static final String r_Investigator2_email = "Investigator-2 email";
	public static final String r_Investigator2_researcher_ID = "Investigator-2 researcher ID";
	public static final String r_Investigator2_ID_type = "Investigator-2 ID type  (ORCID, Researcher ID, etc.)";
	public static final String r_Investigator3_name = "Investigator-3 name";
	public static final String r_Investigator3_institution = "Investigator-3 institution";
	public static final String r_Investigator3_address = "Investigator-3 address";
	public static final String r_Investigator3_phone = "Investigator-3 phone";
	public static final String r_Investigator3_email = "Investigator-3 email";
	public static final String r_Investigator3_researcher_ID = "Investigator-3 researcher ID";
	public static final String r_Investigator3_ID_type = "Investigator-3 ID type  (ORCID, Researcher ID, etc.)";
	public static final String r_Data_submitter_name = "Data submitter name";
	public static final String r_Data_submitter_institution = "Data submitter institution";
	public static final String r_Data_submitter_address = "Data submitter address";
	public static final String r_Data_submitter_phone = "Data submitter phone";
	public static final String r_Data_submitter_email = "Data submitter email";
	public static final String r_Data_submitter_researcher_ID = "Data submitter researcher ID";
	public static final String r_Data_submitter_ID_typ = "Data submitter ID type  (ORCID, Researcher ID, etc.)";
	public static final String r_Title = "Title";
	public static final String r_Abstract = "Abstract";
	public static final String r_Purpose = "Purpose";
	public static final String r_Start_date = "Start date";
	public static final String r_End_date = "End date";
	public static final String r_Westbd_longitude = "Westbd longitude";
	public static final String r_Eastbd_longitude = "Eastbd longitude";
	public static final String r_Northbd_latitude = "Northbd latitude";
	public static final String r_Southbd_latitude = "Southbd latitude";
	public static final String r_Spatial_reference_system = "Spatial reference system";
	public static final String r_Geographic_names = "Geographic names";
	public static final String r_Location_of_organism_collection = "Location of organism collection";
	public static final String r_Funding_agency_name = "Funding agency name";
	public static final String r_Funding_project_title = "Funding project title";
	public static final String r_Funding_project_ID = "Funding project ID (Grant no.)";
	public static final String r_Research_projects = "Research projects";
	public static final String r_Platform1_name = "Platform-1 name";
	public static final String r_Platform1_ID = "Platform-1 ID";
	public static final String r_Platform1_type = "Platform-1 type";
	public static final String r_Platform1_owner = "Platform-1 owner";
	public static final String r_Platform1_country = "Platform-1 country";
	public static final String r_Platform2_name = "Platform-2 name";
	public static final String r_Platform2_ID = "Platform-2 ID";
	public static final String r_Platform2_type = "Platform-2 type";
	public static final String r_Platform2_owner = "Platform-2 owner";
	public static final String r_Platform2_country = "Platform-2 country";
	public static final String r_Platform3_name = "Platform-3 name";
	public static final String r_Platform3_ID = "Platform-3 ID";
	public static final String r_Platform3_type = "Platform-3 type";
	public static final String r_Platform3_owner = "Platform-3 owner";
	public static final String r_Platform3_country = "Platform-3 country";
	public static final String r_EXPOCODE = "EXPOCODE";
	public static final String r_Cruise_ID = "Cruise ID";
	public static final String r_Section = "Section";
	public static final String r_Author_list_for_citation = "Author list for citation";
	public static final String r_References = "References";
	public static final String r_Supplemental_information = "Supplemental information";
	public static final String r_DIC_Variable_abbreviation_in_data_files = "DIC: Variable abbreviation in data files";
	public static final String r_DIC_Observation_type = "DIC: Observation type";
	public static final String r_DIC_In_situ_observation_X_manipulation_condition_X_response_variable = "DIC: In_situ observation / manipulation condition / response variable";
	public static final String r_DIC_Manipulation_method_ = "DIC: Manipulation method";
	public static final String r_DIC_Variable_unit = "DIC: Variable unit";
	public static final String r_DIC_Measured_or_calculated = "DIC: Measured or calculated";
	public static final String r_DIC_Calculation_method_and_parameters = "DIC: Calculation method and parameters";
	public static final String r_DIC_Sampling_instrument = "DIC: Sampling instrument";
	public static final String r_DIC_Analyzing_instrument = "DIC: Analyzing instrument";
	public static final String r_DIC_Detailed_sampling_and_analyzing_information = "DIC: Detailed sampling and analyzing information";
	public static final String r_DIC_Field_replicate_information = "DIC: Field replicate information";
	public static final String r_DIC_Standardization_technique_description = "DIC: Standardization technique description";
	public static final String r_DIC_Frequency_of_standardization = "DIC: Frequency of standardization";
	public static final String r_DIC_CRM_manufacturer = "DIC: CRM manufacturer";
	public static final String r_DIC_Batch_number = "DIC: Batch number";
	public static final String r_DIC_Poison_used_to_kill_the_sample = "DIC: Poison used to kill the sample";
	public static final String r_DIC_Poison_volume = "DIC: Poison volume";
	public static final String r_DIC_Poisoning_correction_description = "DIC Poisoning correction description";
	public static final String r_DIC_Uncertainty = "DIC: Uncertainty";
	public static final String r_DIC_Data_quality_flag_description = "DIC: Data quality flag description";
	public static final String r_DIC_Method_reference = "DIC: Method reference (citation)";
	public static final String r_DIC_Researcher_Name = "DIC: Researcher Name";
	public static final String r_DIC_Researcher_Institution = "DIC: Researcher Institution";
	public static final String r_TA_Variable_abbreviation_in_data_files = "TA: Variable abbreviation in data files";
	public static final String r_TA_Observation_type = "TA: Observation type";
	public static final String r_TA_In_situ_observation_X_manipulation_condition_X_response_variable = "TA: In_situ observation / manipulation condition / response variable";
	public static final String r_TA_Manipulation_method_ = "TA: Manipulation method";
	public static final String r_TA_Variable_unit = "TA: Variable unit";
	public static final String r_TA_Measured_or_calculated = "TA: Measured or calculated";
	public static final String r_TA_Calculation_method_and_parameters = "TA: Calculation method and parameters";
	public static final String r_TA_Sampling_instrument = "TA: Sampling instrument";
	public static final String r_TA_Analyzing_instrument = "TA: Analyzing instrument";
	public static final String r_TA_Type_of_titration = "TA: Type of titration";
	public static final String r_TA_Cell_type = "TA: Cell type (open or closed)";
	public static final String r_TA_Curve_fitting_method = "TA: Curve fitting method";
	public static final String r_TA_Detailed_sampling_and_analyzing_information = "TA: Detailed sampling and analyzing information";
	public static final String r_TA_Field_replicate_information = "TA: Field replicate information";
	public static final String r_TA_Standardization_technique_description = "TA: Standardization technique description";
	public static final String r_TA_Frequency_of_standardization = "TA: Frequency of standardization";
	public static final String r_TA_CRM_manufacturer = "TA: CRM manufacturer";
	public static final String r_TA_Batch_Number = "TA: Batch Number";
	public static final String r_TA_Poison_used_to_kill_the_sample = "TA: Poison used to kill the sample";
	public static final String r_TA_Poison_volume = "TA: Poison volume";
	public static final String r_TA_Poisoning_correction_description = "TA: Poisoning correction description";
	public static final String r_TA_Magnitude_of_blank_correction = "TA: Magnitude of blank correction";
	public static final String r_TA_Uncertainty = "TA: Uncertainty";
	public static final String r_TA_Data_quality_flag_description = "TA: Data quality flag description";
	public static final String r_TA_Method_reference = "TA: Method reference (citation)";
	public static final String r_TA_Researcher_Name = "TA: Researcher Name";
	public static final String r_TA_Researcher_Institution = "TA: Researcher Institution";
	public static final String r_pH_Variable_abbreviation_in_data_files = "pH: Variable abbreviation in data files";
	public static final String r_pH_Observation_type = "pH: Observation type";
	public static final String r_pH_In_situ_observation_X_manipulation_condition_X_response_variable = "pH: In_situ observation / manipulation condition / response variable";
	public static final String r_pH_Manipulation_method = "pH: Manipulation method";
	public static final String r_pH_Measured_or_calculated = "pH: Measured or calculated";
	public static final String r_pH_Calculation_method_and_parameters = "pH: Calculation method and parameters";
	public static final String r_pH_Sampling_instrument = "pH: Sampling instrument";
	public static final String r_pH_Analyzing_instrument = "pH: Analyzing instrument";
	public static final String r_pH_pH_scale = "pH: pH scale";
	public static final String r_pH_Temperature_of_measurement = "pH: Temperature of measurement";
	public static final String r_pH_Detailed_sampling_and_analyzing_information = "pH: Detailed sampling and analyzing information";
	public static final String r_pH_Field_replicate_information = "pH: Field replicate information";
	public static final String r_pH_Standardization_technique_description = "pH: Standardization technique description";
	public static final String r_pH_Frequency_of_standardization = "pH: Frequency of standardization";
	public static final String r_pH_pH_values_of_the_standards = "pH: pH values of the standards";
	public static final String r_pH_Temperature_of_standardization = "pH: Temperature of standardization";
	public static final String r_pH_Temperature_correction_method = "pH: Temperature correction method";
	public static final String r_pH_at_what_temperature_was_pH_reported = "pH: at what temperature was pH reported";
	public static final String r_pH_Uncertainty = "pH: Uncertainty";
	public static final String r_pH_Data_quality_flag_description = "pH: Data quality flag description";
	public static final String r_pH_Method_reference = "pH: Method reference (citation)";
	public static final String r_pH_Researcher_Name = "pH: Researcher Name";
	public static final String r_pH_Researcher_Institution = "pH: Researcher Institution";
	public static final String r_pCO2A_Variable_abbreviation_in_data_files = "pCO2A: Variable abbreviation in data files";
	public static final String r_pCO2A_Observation_type = "pCO2A: Observation type";
	public static final String r_pCO2A_In_situ_observation_X_manipulation_condition_X_response_variable = "pCO2A: In_situ observation / manipulation condition / response variable";
	public static final String r_pCO2A_Manipulation_method = "pCO2A: Manipulation method";
	public static final String r_pCO2A_Variable_unit = "pCO2A: Variable unit";
	public static final String r_pCO2A_Measured_or_calculated = "pCO2A: Measured or calculated";
	public static final String r_pCO2A_Calculation_method_and_parameters = "pCO2A: Calculation method and parameters";
	public static final String r_pCO2A_Sampling_instrument = "pCO2A: Sampling instrument";
	public static final String r_pCO2A_Location_of_seawater_intake = "pCO2A: Location of seawater intake";
	public static final String r_pCO2A_Depth_of_seawater_intake = "pCO2A: Depth of seawater intake";
	public static final String r_pCO2A_Analyzing_instrument = "pCO2A: Analyzing instrument";
	public static final String r_pCO2A_Detailed_sampling_and_analyzing_information = "pCO2A: Detailed sampling and analyzing information";
	public static final String r_pCO2A_Equilbrator_type = "pCO2A: Equilbrator type";
	public static final String r_pCO2A_Equilibrator_volume = "pCO2A: Equilibrator volume (L)";
	public static final String r_pCO2A_Vented_or_not = "pCO2A: Vented or not";
	public static final String r_pCO2A_Water_flow_rate = "pCO2A: Water flow rate (L/min)";
	public static final String r_pCO2A_Headspace_gas_flow_rate = "pCO2A: Headspace gas flow rate (L/min)";
	public static final String r_pCO2A_How_was_temperature_inside_the_equilibrator_measured = "pCO2A: How was temperature inside the equilibrator measured .";
	public static final String r_pCO2A_How_was_pressure_inside_the_equilibrator_measured = "pCO2A: How was pressure inside the equilibrator measured.";
	public static final String r_pCO2A_Drying_method_for_CO2_gas = "pCO2A: Drying method for CO2 gas";
	public static final String r_pCO2A_Manufacturer_of_the_gas_detector = "pCO2A: Manufacturer of the gas detector";
	public static final String r_pCO2A_Model_of_the_gas_detector = "pCO2A: Model of the gas detector";
	public static final String r_pCO2A_Resolution_of_the_gas_detector = "pCO2A: Resolution of the gas detector";
	public static final String r_pCO2A_Uncertainty_of_the_gas_detector = "pCO2A: Uncertainty of the gas detector";
	public static final String r_pCO2A_Standardization_technique_description = "pCO2A: Standardization technique description";
	public static final String r_pCO2A_Frequency_of_standardization = "pCO2A: Frequency of standardization";
	public static final String r_pCO2A_Manufacturer_of_standard_gas = "pCO2A: Manufacturer of standard gas";
	public static final String r_pCO2A_Concentrations_of_standard_gas = "pCO2A: Concentrations of standard gas";
	public static final String r_pCO2A_Uncertainties_of_standard_gas = "pCO2A: Uncertainties of standard gas";
	public static final String r_pCO2A_Water_vapor_correction_method = "pCO2A: Water vapor correction method";
	public static final String r_pCO2A_Temperature_correction_method = "pCO2A: Temperature correction method";
	public static final String r_pCO2A_at_what_temperature_was_pCO2_reported = "pCO2A: at what temperature was pCO2 reported";
	public static final String r_pCO2A_Uncertainty = "pCO2A: Uncertainty";
	public static final String r_pCO2A_Data_quality_flag_description = "pCO2A: Data quality flag description";
	public static final String r_pCO2A_Method_reference = "pCO2A: Method reference (citation)";
	public static final String r_pCO2A_Researcher_Name = "pCO2A: Researcher Name";
	public static final String r_pCO2A_Researcher_Institution = "pCO2A: Researcher Institution";
	public static final String r_pCO2D_Variable_abbreviation_in_data_files = "pCO2D: Variable abbreviation in data files";
	public static final String r_pCO2D_Observation_type = "pCO2D: Observation type";
	public static final String r_pCO2D_In_situ_observation_X_manipulation_condition_X_response_variable = "pCO2D: In_situ observation / manipulation condition / response variable";
	public static final String r_pCO2D_Manipulation_method = "pCO2D: Manipulation method";
	public static final String r_pCO2D_Variable_unit = "pCO2D: Variable unit";
	public static final String r_pCO2D_Measured_or_calculated = "pCO2D: Measured or calculated";
	public static final String r_pCO2D_Calculation_method_and_parameters = "pCO2D: Calculation method and parameters";
	public static final String r_pCO2D_Sampling_instrument = "pCO2D: Sampling instrument";
	public static final String r_pCO2D_Analyzing_instrument = "pCO2D: Analyzing instrument";
	public static final String r_pCO2D_Storage_method = "pCO2D: Storage method";
	public static final String r_pCO2D_Seawater_volume = "pCO2D: Seawater volume (mL)";
	public static final String r_pCO2D_Headspace_volume = "pCO2D: Headspace volume (mL)";
	public static final String r_pCO2D_Temperature_of_measurement = "pCO2D: Temperature of measurement";
	public static final String r_pCO2D_Detailed_sampling_and_analyzing_information = "pCO2D: Detailed sampling and analyzing information";
	public static final String r_pCO2D_Field_replicate_information = "pCO2D: Field replicate information";
	public static final String r_pCO2D_Manufacturer_of_the_gas_detector = "pCO2D: Manufacturer of the gas detector";
	public static final String r_pCO2D_Model_of_the_gas_detector = "pCO2D: Model of the gas detector";
	public static final String r_pCO2D_Resolution_of_the_gas_detector = "pCO2D: Resolution of the gas detector";
	public static final String r_pCO2D_Uncertainty_of_the_gas_detector = "pCO2D: Uncertainty of the gas detector";
	public static final String r_pCO2D_Standardization_technique_description = "pCO2D: Standardization technique description";
	public static final String r_pCO2D_Frequency_of_standardization = "pCO2D: Frequency of standardization";
	public static final String r_pCO2D_Temperature_of_standardization = "pCO2D: Temperature of standardization";
	public static final String r_pCO2D_Manufacturer_of_standard_gas = "pCO2D: Manufacturer of standard gas";
	public static final String r_pCO2D_Concentrations_of_standard_gas = "pCO2D: Concentrations of standard gas";
	public static final String r_pCO2D_Uncertainties_of_standard_gas = "pCO2D: Uncertainties of standard gas";
	public static final String r_pCO2D_Water_vapor_correction_method = "pCO2D: Water vapor correction method";
	public static final String r_pCO2D_Temperature_correction_method = "pCO2D: Temperature correction method";
	public static final String r_pCO2D_at_what_temperature_was_pCO2_reported = "pCO2D: at what temperature was pCO2 reported";
	public static final String r_pCO2D_Uncertainty = "pCO2D: Uncertainty";
	public static final String r_pCO2D_Data_quality_flag_description = "pCO2D: Data quality flag description";
	public static final String r_pCO2D_Method_reference = "pCO2D: Method reference (citation)";
	public static final String r_pCO2D_Researcher_Name = "pCO2D: Researcher Name";
	public static final String r_pCO2D_Researcher_Institution = "pCO2D: Researcher Institution";
	public static final String r_Var1_Variable_abbreviation_in_data_files = "Var1: Variable abbreviation in data files";
	public static final String r_Var1_Full_variable_name = "Var1: Full variable name";
	public static final String r_Var1_Observation_type = "Var1: Observation type";
	public static final String r_Var1_In_situ_observation_X_manipulation_condition_X_response_variable = "Var1: In_situ observation / manipulation condition / response variable";
	public static final String r_Var1_Variable_unit = "Var1: Variable unit";
	public static final String r_Var1_Measured_or_calculated = "Var1: Measured or calculated";
	public static final String r_Var1_Calculation_method_and_parameters = "Var1: Calculation method and parameters";
	public static final String r_Var1_Sampling_instrument = "Var1: Sampling instrument";
	public static final String r_Var1_Analyzing_instrument = "Var1: Analyzing instrument";
	public static final String r_Var1_Duration = "Var1: Duration (for settlement/colonization methods)";
	public static final String r_Var1_Detailed_sampling_and_analyzing_information = "Var1: Detailed sampling and analyzing information";
	public static final String r_Var1_Field_replicate_information = "Var1: Field replicate information";
	public static final String r_Var1_Uncertainty = "Var1: Uncertainty";
	public static final String r_Var1_Data_quality_flag_description = "Var1: Data quality flag description";
	public static final String r_Var1_Method_reference = "Var1: Method reference (citation)";
	public static final String r_Var1_Biological_subject = "Var1: Biological subject";
	public static final String r_Var1_Species_Identification_code = "Var1: Species Identification code";
	public static final String r_Var1_Life_Stage = "Var1: Life stage of the Biological subject";
	public static final String r_Var1_Researcher_Name = "Var1: Researcher Name";
	public static final String r_Var1_Researcher_Institution = "Var1: Researcher Institution";
	public static final String r_Var2_Variable_abbreviation_in_data_files = "Var2: Variable abbreviation in data files";
	public static final String r_Var2_Full_variable_name = "Var2: Full variable name";
	public static final String r_Var2_Observation_type = "Var2: Observation type";
	public static final String r_Var2_In_situ_observation_X_manipulation_condition_X_response_variable = "Var2: In_situ observation / manipulation condition / response variable";
	public static final String r_Var2_Variable_unit = "Var2: Variable unit";
	public static final String r_Var2_Measured_or_calculated = "Var2: Measured or calculated";
	public static final String r_Var2_Calculation_method_and_parameters = "Var2: Calculation method and parameters";
	public static final String r_Var2_Sampling_instrument = "Var2: Sampling instrument";
	public static final String r_Var2_Analyzing_instrument = "Var2: Analyzing instrument";
	public static final String r_Var2_Duration = "Var2: Duration (for settlement/colonization methods)";
	public static final String r_Var2_Detailed_sampling_and_analyzing_information = "Var2: Detailed sampling and analyzing information";
	public static final String r_Var2_Field_replicate_information = "Var2: Field replicate information";
	public static final String r_Var2_Uncertainty = "Var2: Uncertainty";
	public static final String r_Var2_Data_quality_flag_description = "Var2: Data quality flag description";
	public static final String r_Var2_Method_reference = "Var2: Method reference (citation)";
	public static final String r_Var2_Biological_subject = "Var2: Biological subject";
	public static final String r_Var2_Species_Identification_code = "Var2: Species Identification code";
	public static final String r_Var2_Life_stage_of_the_Biological_subject = "Var2: Life stage of the Biological subject";
	public static final String r_Var2_Researcher_Name = "Var2: Researcher Name";
	public static final String r_Var2_Researcher_Institution = "Var2: Researcher Institution";
	public static final String r_Var3_Variable_abbreviation_in_data_files = "Var3: Variable abbreviation in data files";
	public static final String r_Var3_Full_variable_name = "Var3: Full variable name";
	public static final String r_Var3_Observation_type = "Var3: Observation type";
	public static final String r_Var3_In_situ_observation_X_manipulation_condition_X_response_variable = "Var3: In_situ observation / manipulation condition / response variable";
	public static final String r_Var3_Variable_unit = "Var3: Variable unit";
	public static final String r_Var3_Measured_or_calculated = "Var3: Measured or calculated";
	public static final String r_Var3_Calculation_method_and_parameters = "Var3: Calculation method and parameters";
	public static final String r_Var3_Sampling_instrument = "Var3: Sampling instrument";
	public static final String r_Var3_Analyzing_instrument = "Var3: Analyzing instrument";
	public static final String r_Var3_Duration = "Var3: Duration (for settlement/colonization methods)";
	public static final String r_Var3_Detailed_sampling_and_analyzing_information = "Var3: Detailed sampling and analyzing information";
	public static final String r_Var3_Field_replicate_information = "Var3: Field replicate information";
	public static final String r_Var3_Uncertainty = "Var3: Uncertainty";
	public static final String r_Var3_Data_quality_flag_description = "Var3: Data quality flag description";
	public static final String r_Var3_Method_reference = "Var3: Method reference (citation)";
	public static final String r_Var3_Biological_subject = "Var3: Biological subject";
	public static final String r_Var3_Species_Identification_code = "Var3: Species Identification code";
	public static final String r_Var3_Life_stage_of_the_Biological_subject = "Var3: Life stage of the Biological subject";
	public static final String r_Var3_Researcher_Name = "Var3: Researcher Name";
	public static final String r_Var3_Researcher_Institution = "Var3: Researcher Institution";
	public static final String r_Var4_Variable_abbreviation_in_data_files = "Var4: Variable abbreviation in data files";
	public static final String r_Var4_Full_variable_name = "Var4: Full variable name";
	public static final String r_Var4_Observation_type = "Var4: Observation type";
	public static final String r_Var4_In_situ_observation_X_manipulation_condition_X_response_variable = "Var4: In_situ observation / manipulation condition / response variable";
	public static final String r_Var4_Variable_unit = "Var4: Variable unit";
	public static final String r_Var4_Measured_or_calculated = "Var4: Measured or calculated";
	public static final String r_Var4_Calculation_method_and_parameters = "Var4: Calculation method and parameters";
	public static final String r_Var4_Sampling_instrument = "Var4: Sampling instrument";
	public static final String r_Var4_Analyzing_instrument = "Var4: Analyzing instrument";
	public static final String r_Var4_Duration = "Var4: Duration (for settlement/colonization methods)";
	public static final String r_Var4_Detailed_sampling_and_analyzing_information = "Var4: Detailed sampling and analyzing information";
	public static final String r_Var4_Field_replicate_information = "Var4: Field replicate information";
	public static final String r_Var4_Uncertainty = "Var4: Uncertainty";
	public static final String r_Var4_Data_quality_flag_description = "Var4: Data quality flag description";
	public static final String r_Var4_Method_reference = "Var4: Method reference (citation)";
	public static final String r_Var4_Biological_subject = "Var4: Biological subject";
	public static final String r_Var4_Species_Identification_code = "Var4: Species Identification code";
	public static final String r_Var4_Life_stage_of_the_Biological_subject = "Var4: Life stage of the Biological subject";
	public static final String r_Var4_Researcher_Name = "Var4: Researcher Name";
	public static final String r_Var4_Researcher_Institution = "Var4: Researcher Institution";
	public static final String r_Var5_Variable_abbreviation_in_data_files = "Var5: Variable abbreviation in data files";
	public static final String r_Var5_Full_variable_name = "Var5: Full variable name";
	public static final String r_Var5_Observation_type = "Var5: Observation type";
	public static final String r_Var5_In_situ_observation_X_manipulation_condition_X_response_variable = "Var5: In_situ observation / manipulation condition / response variable";
	public static final String r_Var5_Variable_unit = "Var5: Variable unit";
	public static final String r_Var5_Measured_or_calculated = "Var5: Measured or calculated";
	public static final String r_Var5_Calculation_method_and_parameters = "Var5: Calculation method and parameters";
	public static final String r_Var5_Sampling_instrument = "Var5: Sampling instrument";
	public static final String r_Var5_Analyzing_instrument = "Var5: Analyzing instrument";
	public static final String r_Var5_Duration = "Var5: Duration (for settlement/colonization methods)";
	public static final String r_Var5_Detailed_sampling_and_analyzing_information = "Var5: Detailed sampling and analyzing information";
	public static final String r_Var5_Field_replicate_information = "Var5: Field replicate information";
	public static final String r_Var5_Uncertainty = "Var5: Uncertainty";
	public static final String r_Var5_Data_quality_flag_description = "Var5: Data quality flag description";
	public static final String r_Var5_Method_reference = "Var5: Method reference (citation)";
	public static final String r_Var5_Biological_subject = "Var5: Biological subject";
	public static final String r_Var5_Species_Identification_code = "Var5: Species Identification code";
	public static final String r_Var5_Life_stage_of_the_Biological_subject = "Var5: Life stage of the Biological subject";
	public static final String r_Var5_Researcher_Name = "Var5: Researcher Name";
	public static final String r_Var5_Researcher_Institution = "Var5: Researcher Institution";
	public static final String r_Var6_Variable_abbreviation_in_data_files = "Var6: Variable abbreviation in data files";
	public static final String r_Var6_Full_variable_name = "Var6: Full variable name";
	public static final String r_Var6_Observation_type = "Var6: Observation type";
	public static final String r_Var6_In_situ_observation_X_manipulation_condition_X_response_variable = "Var6: In_situ observation / manipulation condition / response variable";
	public static final String r_Var6_Variable_unit = "Var6: Variable unit";
	public static final String r_Var6_Measured_or_calculated = "Var6: Measured or calculated";
	public static final String r_Var6_Calculation_method_and_parameters = "Var6: Calculation method and parameters";
	public static final String r_Var6_Sampling_instrument = "Var6: Sampling instrument";
	public static final String r_Var6_Analyzing_instrument = "Var6: Analyzing instrument";
	public static final String r_Var6_Duration = "Var6: Duration (for settlement/colonization methods)";
	public static final String r_Var6_Detailed_sampling_and_analyzing_information = "Var6: Detailed sampling and analyzing information";
	public static final String r_Var6_Field_replicate_information = "Var6: Field replicate information";
	public static final String r_Var6_Uncertainty = "Var6: Uncertainty";
	public static final String r_Var6_Data_quality_flag_description = "Var6: Data quality flag description";
	public static final String r_Var6_Method_reference = "Var6: Method reference (citation)";
	public static final String r_Var6_Biological_subject = "Var6: Biological subject";
	public static final String r_Var6_Species_Identification_code = "Var6: Species Identification code";
	public static final String r_Var6_Life_stage_of_the_Biological_subject = "Var6: Life stage of the Biological subject";
	public static final String r_Var6_Researcher_Name = "Var6: Researcher Name";
	public static final String r_Var6_Researcher_Institution = "Var6: Researcher Institution";
	public static final String r_Var7_Variable_abbreviation_in_data_files = "Var7: Variable abbreviation in data files";
	public static final String r_Var7_Full_variable_name = "Var7: Full variable name";
	public static final String r_Var7_Observation_type = "Var7: Observation type";
	public static final String r_Var7_In_situ_observation_X_manipulation_condition_X_response_variable = "Var7: In_situ observation / manipulation condition / response variable";
	public static final String r_Var7_Variable_unit = "Var7: Variable unit";
	public static final String r_Var7_Measured_or_calculated = "Var7: Measured or calculated";
	public static final String r_Var7_Calculation_method_and_parameters = "Var7: Calculation method and parameters";
	public static final String r_Var7_Sampling_instrument = "Var7: Sampling instrument";
	public static final String r_Var7_Analyzing_instrument = "Var7: Analyzing instrument";
	public static final String r_Var7_Duration = "Var7: Duration (for settlement/colonization methods)";
	public static final String r_Var7_Detailed_sampling_and_analyzing_information = "Var7: Detailed sampling and analyzing information";
	public static final String r_Var7_Field_replicate_information = "Var7: Field replicate information";
	public static final String r_Var7_Uncertainty = "Var7: Uncertainty";
	public static final String r_Var7_Data_quality_flag_description = "Var7: Data quality flag description";
	public static final String r_Var7_Method_reference = "Var7: Method reference (citation)";
	public static final String r_Var7_Biological_subject = "Var7: Biological subject";
	public static final String r_Var7_Species_Identification_code = "Var7: Species Identification code";
	public static final String r_Var7_Life_stage_of_the_Biological_subject = "Var7: Life stage of the Biological subject";
	public static final String r_Var7_Researcher_Name = "Var7: Researcher Name";
	public static final String r_Var7_Researcher_Institution = "Var7: Researcher Institution";
	public static final String r_Var8_Variable_abbreviation_in_data_files = "Var8: Variable abbreviation in data files";
	public static final String r_Var8_Full_variable_name = "Var8: Full variable name";
	public static final String r_Var8_Observation_type = "Var8: Observation type";
	public static final String r_Var8_In_situ_observation_X_manipulation_condition_X_response_variable = "Var8: In_situ observation / manipulation condition / response variable";
	public static final String r_Var8_Variable_unit = "Var8: Variable unit";
	public static final String r_Var8_Measured_or_calculated = "Var8: Measured or calculated";
	public static final String r_Var8_Calculation_method_and_parameters = "Var8: Calculation method and parameters";
	public static final String r_Var8_Sampling_instrument = "Var8: Sampling instrument";
	public static final String r_Var8_Analyzing_instrument = "Var8: Analyzing instrument";
	public static final String r_Var8_Duration = "Var8: Duration (for settlement/colonization methods)";
	public static final String r_Var8_Detailed_sampling_and_analyzing_information = "Var8: Detailed sampling and analyzing information";
	public static final String r_Var8_Field_replicate_information = "Var8: Field replicate information";
	public static final String r_Var8_Uncertainty = "Var8: Uncertainty";
	public static final String r_Var8_Data_quality_flag_description = "Var8: Data quality flag description";
	public static final String r_Var8_Method_reference = "Var8: Method reference (citation)";
	public static final String r_Var8_Biological_subject = "Var8: Biological subject";
	public static final String r_Var8_Species_Identification_code = "Var8: Species Identification code";
	public static final String r_Var8_Life_stage_of_the_Biological_subject = "Var8: Life stage of the Biological subject";
	public static final String r_Var8_Researcher_Name = "Var8: Researcher Name";
	public static final String r_Var8_Researcher_Institution = "Var8: Researcher Institution";
	public static final String r_Var9_Variable_abbreviation_in_data_files = "Var9: Variable abbreviation in data files";
	public static final String r_Var9_Full_variable_name = "Var9: Full variable name";
	public static final String r_Var9_Observation_type = "Var9: Observation type";
	public static final String r_Var9_In_situ_observation_X_manipulation_condition_X_response_variable = "Var9: In_situ observation / manipulation condition / response variable";
	public static final String r_Var9_Variable_unit = "Var9: Variable unit";
	public static final String r_Var9_Measured_or_calculated = "Var9: Measured or calculated";
	public static final String r_Var9_Calculation_method_and_parameters = "Var9: Calculation method and parameters";
	public static final String r_Var9_Sampling_instrument = "Var9: Sampling instrument";
	public static final String r_Var9_Analyzing_instrument = "Var9: Analyzing instrument";
	public static final String r_Var9_Duration = "Var9: Duration (for settlement/colonization methods)";
	public static final String r_Var9_Detailed_sampling_and_analyzing_information = "Var9: Detailed sampling and analyzing information";
	public static final String r_Var9_Field_replicate_information = "Var9: Field replicate information";
	public static final String r_Var9_Uncertainty = "Var9: Uncertainty";
	public static final String r_Var9_Data_quality_flag_description = "Var9: Data quality flag description";
	public static final String r_Var9_Method_reference = "Var9: Method reference (citation)";
	public static final String r_Var9_Biological_subject = "Var9: Biological subject";
	public static final String r_Var9_Species_Identification_code = "Var9: Species Identification code";
	public static final String r_Var9_Life_stage_of_the_Biological_subject = "Var9: Life stage of the Biological subject";
	public static final String r_Var9_Researcher_Name = "Var9: Researcher Name";
	public static final String r_Var9_Researcher_Institution = "Var9: Researcher Institution";
	public static final String r_Var10_Variable_abbreviation_in_data_files = "Var10: Variable abbreviation in data files";
	public static final String r_Var10_Full_variable_name = "Var10: Full variable name";
	public static final String r_Var10_Observation_type = "Var10: Observation type";
	public static final String r_Var10_In_situ_observation_X_manipulation_condition_X_response_variable = "Var10: In_situ observation / manipulation condition / response variable";
	public static final String r_Var10_Variable_unit = "Var10: Variable unit";
	public static final String r_Var10_Measured_or_calculated = "Var10: Measured or calculated";
	public static final String r_Var10_Calculation_method_and_parameters = "Var10: Calculation method and parameters";
	public static final String r_Var10_Sampling_instrument = "Var10: Sampling instrument";
	public static final String r_Var10_Analyzing_instrument = "Var10: Analyzing instrument";
	public static final String r_Var10_Duration = "Var10: Duration (for settlement/colonization methods)";
	public static final String r_Var10_Detailed_sampling_and_analyzing_information = "Var10: Detailed sampling and analyzing information";
	public static final String r_Var10_Field_replicate_information = "Var10: Field replicate information";
	public static final String r_Var10_Uncertainty = "Var10: Uncertainty";
	public static final String r_Var10_Data_quality_flag_description = "Var10: Data quality flag description";
	public static final String r_Var10_Method_reference = "Var10: Method reference (citation)";
	public static final String r_Var10_Biological_subject = "Var10: Biological subject";
	public static final String r_Var10_Species_Identification_code = "Var10: Species Identification code";
	public static final String r_Var10_Life_stage_of_the_Biological_subject = "Var10: Life stage of the Biological subject";
	public static final String r_Var10_Researcher_Name = "Var10: Researcher Name";
	public static final String r_Var10_Researcher_Institution = "Var10: Researcher Institution";
	public static final String r_Var11_Variable_abbreviation_in_data_files = "Var11: Variable abbreviation in data files";
	public static final String r_Var11_Full_variable_name = "Var11: Full variable name";
	public static final String r_Var12_Variable_abbreviation_in_data_files = "Var12: Variable abbreviation in data files";
	public static final String r_Var12_Full_variable_name = "Var12: Full variable name";

}
