/**
 * 
 */
package gov.noaa.pmel.excel2oap.sdg;

import java.lang.reflect.Field;
import java.util.HashMap;

import gov.noaa.pmel.excel2oap.ElementType;
import gov.noaa.pmel.excel2oap.SpreadSheetKeys;

/**
 * @author kamb
 *
 */
public class SDG_14_3_Keys extends SpreadSheetKeys {

    public SDG_14_3_Keys() {
        declaredFields = getDeclaredFields(new HashMap<String, Field>(), this.getClass());
    }
    protected SDG_14_3_Keys(boolean subclassed) {
        if ( !subclassed ) {
            declaredFields = getDeclaredFields(new HashMap<String, Field>(), this.getClass());
        }
    }
    public ElementType getElementForKey(String key) {
        return SdgElementType.fromSsRowName(key);
    }
        
//     From 1212019_SDG14_3_1_Metadata_submission_template_drop_QC
//    + : row added
//    - : change in wording or characters, likely same meaning (from OCADS)
//    ? : change in wording or characters, maybe different meaning (from OCADS)
//    ? : change in wording or characters, maybe different meaning (from OCADS)
//    1   Submission Date
//-   2   Accession no. of related data sets on the 14.3.1 data platform or any other data base
//+   3   URL of metadata set
    public static final String MetadataURL = "URL of metadata set";
//+   4   URL of associated data set
    public static final String DatasetURL = "URL of associated dataset";
//+   5   DOI of dataset (if applicable)
    public static final String DatasetDOI = "DOI of metadata set";
//    6   Investigator-1 name
//    7   Investigator-1 institution
//    8   Investigator-1 institution ID (OceanExpert)
//    9   Investigator-1 address
//    10  Investigator-1 phone
//    11  Investigator-1 email
//    12  Investigator-1 researcher ID
//    13  Investigator-1 ID type  (OceanExpert, ORCID, ResearcherID, etc.)
//    14  Investigator-2 name
//    15  Investigator-2 institution
//    16  Investigator-2 institution ID (OceanExpert)
//    17  Investigator-2 address
//    18  Investigator-2 phone
//    19  Investigator-2 email
//    20  Investigator-2 researcher ID
//    21  Investigator-2 ID type  (OceanExpert, ORCID, ResearcherID, etc.)
//    22  Investigator-3 name
//    23  Investigator-3 institution
//    24  Investigator-2 institution ID (OceanExpert)
//    25  Investigator-3 address
//    26  Investigator-3 phone
//    27  Investigator-3 email
//    28  Investigator-3 researcher ID
//    29  Investigator-3 ID type  (OceanExpert, ORCID, ResearcherID, etc.)
//    30  Data submitter name
//    31  Data submitter institution
//+   32  Data submitter - institution ID (OceanExpert)
//    33  Data submitter address
//    34  Data submitter phone
//    35  Data submitter email
//    36  Data submitter researcher ID
//-   37  Data submitter ID type  (OceanExpert, ORCID, ResearcherID, etc.)
//?   38  Name of sampling site or title of related research project
    @SuppressWarnings("hiding")
    public String r_Title = "Name of sampling site or title of related research project";
//?   39  Short description including purpose of observation
    @SuppressWarnings("hiding")
    public String r_Abstract = "Short description including purpose of observation";
//+   40  Method(s) applied
//->  41  First day of measurement included in data file (YYYY-MM-DD or YYYY-MM-DDTHH:MM:SS)
    @SuppressWarnings("hiding")
    public String r_Start_date = "First day of measurement included in data file (YYYY-MM-DD or YYYY-MM-DDTHH:MM:SS)";
//->  42  Last day of measurement included in data file (YYYY-MM-DD or YYYY-MM-DDTHH:MM:SS)
    @SuppressWarnings("hiding")
    public String r_End_date = "Last day of measurement included in data file (YYYY-MM-DD or YYYY-MM-DDTHH:MM:SS)";
//+   43  Site specific measurement longitude
//+   44  Site specific measurement latitude
//?   45  Transect measurement longitude easternmost
    @SuppressWarnings("hiding")
    public String r_Eastbd_longitude = "Transect measurement longitude easternmost";
//?   46  Transect measurement longitude westernmost
    @SuppressWarnings("hiding")
    public String r_Westbd_longitude = "Transect measurement longitude westernmost";
//?   47  Transect measurement latitude northernmost
    @SuppressWarnings("hiding")
    public String r_Northbd_latitude = "Transect measurement latitude northernmost";
//?   48  Transect measurement latitude southernmost
    @SuppressWarnings("hiding")
    public String r_Southbd_latitude = "Transect measurement latitude southernmost";
//    49  Funding agency name
//    50  Funding project title
//    51  Funding project ID (Grant number)
//-   52  Platform name
    /////// These are now "fixed" on the fly.
////////    @SuppressWarnings("hiding")
////////    public String r_Platform1_name = "Platform name";
//-   53  Platform category
////////    @SuppressWarnings("hiding")
////////    public String r_Platform1_type = "Platform category";
    @SuppressWarnings("hiding")
    public String PlatformX_type = "category";
//-   54  Platform ID
////////    @SuppressWarnings("hiding")
////////    public String r_Platform1_ID = "Platform ID";
//-   55  Platform ID type
    public String PlatformX_ID_type = "ID type";
    public String r_Platform1_ID_type = "Platform ID type";
//    56  Platform-1 owner
//    57  Platform-1 country
//    58  EXPOCODE
//    59  Cruise ID
//+    60  Cruise ID type
    public String r_CruiseID_type = "Cruise ID type";
//    61  Author list for citation
//    62  References
//    63  Supplemental information
//    64  Depth: Variable abbreviation in data files // new variable key
//    65  Depth: Variable unit
//    66  DIC: Variable abbreviation in data files
//    ...
    // from VAR:
	@SuppressWarnings("hiding")
    public String VarX_Sampling_instrument = "Collection method";
	@SuppressWarnings("hiding")
	public String VarX_Detailed_sampling_and_analyzing_information = "Analyzing information with citation";
    
//?   69  DIC: Collection method (e.g. bottle sampling)
//?   71  DIC: Analyzing information with citation
//+   72  DIC: Quality control 
//+   73  DIC: Abbreviation of data quality flag scheme
//?   74  DIC: Data quality scheme (name of scheme)
//?   77  DIC: Calibration method
    @SuppressWarnings("hiding")
    public String DICX_Standardization_technique_description = "Calibration method";
//?   78  DIC: Frequency of calibration
	@SuppressWarnings("hiding")
    public String DICX_Frequency_of_standardization = "Frequency of calibration";
//->  80  DIC: Batch number(s)
//-   81  DIC: Poison used to kill the sample
//-   82  DIC: Poison volume
//-   83  DIC: Poisoning correction description
//    86  TA: ...
//        Poison rows
//    86  pH: ...
	public String PHX_Temperature_of_standardization = "Temperature of calibration";
//*   133 pCO2: Variable abbreviation in data files // dropped A or D
//    ... // Similar to fCO2 below
//++  167 fCO2: Variable abbreviation in data files  // pCO2, fCO2 -- new keys 
//        Note no Full variable name
//    168 fCO2: Observation type
//    169 fCO2: Variable unit
//?   170 fCO2: Collection method (e.g. with pump)
//    171 fCO2: Location of seawater intake
//    172 fCO2: Depth of seawater intake
//    173 fCO2: Analyzing instrument
//-   174 fCO2: Analyzing information with citation (SOP etc)
//+   175 fCO2: Quality control
//+   176 fCO2: Abbreviation of data quality flag scheme
//?   177 fCO2: Data quality flag scheme
//    178 fCO2: Uncertainty
//    179 fCO2: Equilbrator type
//    180 fCO2: Equilibrator volume (L)
//    181 fCO2: Equilibrator vented or not
	public String pCO2AX_Vented_or_not = "Equilibrator vented or not";
//    182 fCO2: Equilibrator water flow rate (L min-1)
    @SuppressWarnings("hiding")
	public String pCO2AX_Water_flow_rate = "Equilibrator water flow rate"; //  (L/min)";
//    183 fCO2: Equilibrator headspace gas flow rate (L min-1)
    @SuppressWarnings("hiding")
	public String pCO2AX_Headspace_gas_flow_rate = "Equilibrator headspace gas flow rate"; //  (L/min)";
//-   184 fCO2: How was temperature inside the equilibrator measured (i.e. which sensor)?
//-   185 fCO2: How was pressure inside the equilibrator measured (i.e. which sensor)?
//    186 fCO2: Drying method for CO2 gas
//    187 fCO2: Manufacturer of the gas detector
//    188 fCO2: Model of the gas detector
//    189 fCO2: Resolution of the gas detector
//    190 fCO2: Uncertainty of the gas detector
//    191 fCO2: Calibration method
//    192 fCO2: Frequency of calibration
//    193 fCO2: Manufacturer of standard gas
//    194 fCO2: Concentrations of standard gas
//    195 fCO2: Uncertainties of standard gas
//    196 fCO2: Water vapor correction method
//->  197 fCO2: Temperature correction method (from measurement temperature in the equilibrator to SST)
//    198 fCO2: At what temperature was fCO2 reported?
//    199 fCO2: Method reference (citation)
//+   200 fCO2: Changes to Method or SOP
//v   201 Temperature: Variable abbreviation in data files
//        Note no Full variable name
//v   202 Temperature: Observation type
//v   203 Temperature: Variable unit
//v   204 Temperature: Collection method (e.g. bottle sampling)
//v   205 Temperature: Analyzing instrument
//v   206 Temperature: Analyzing information with citation (SOP etc)
//v   207 Temperature: Quality control
//v   208 Temperature: Abbreviation of data quality flag scheme
//v   209 Temperature: Data quality flag scheme
//v   210 Temperature: Uncertainty
//v   211 Temperature: Field replicate information
//v   212 Temperature: Method reference (citation)
//v   213 Temperature: Changes to Method or SOP
//v   214 Salinity: Variable abbreviation in data files
//v   215 Salinity: Observation type
//v   216 Salinity: Variable unit
//v   217 Salinity: Collection method (e.g. bottle sampling)
//v   218 Salinity: Analyzing instrument
//v   219 Salinity: Analyzing information with citation (SOP etc)
//v   220 Salinity: Quality control
//v   221 Salinity: Abbreviation of data quality flag scheme
//v   222 Salinity: Data quality flag scheme
//v   223 Salinity: Uncertainty
//v   224 Salinity: Field replicate information
//v   225 Salinity: Method reference (citation)
//v   226 Salinity: Changes to Method or SOP
//    227 Var1: Variable abbreviation in data files
//    228 Var1: Full variable name
//    229 Var1: Observation type
//    230 Var1: Variable unit
//?   231 Var1: Collection method (e.g. bottle sampling)
    public String VarX_Sampling_Instument = "Collection method"; // plus more
//    232 Var1: Analyzing instrument
//-   233 Var1: Analyzing information with citation (SOP etc)
//+   234 Var1: Quality control
    @SuppressWarnings("hiding")
    public String VarX_Data_quality_flag_description = "Quality control";
    public String VarX_Data_Quality = "Quality control";
//+   235 Var1: Abbreviation of data quality flag scheme
    public String VarX_QC_VarName = "Abbreviation of data quality flag scheme";
//?   236 Var1: Data quality flag scheme 
    public String VarX_QC_Scheme = "Data quality flag scheme";
//    237 Var1: Uncertainty
//    238 Var1: Field replicate information
//    239 Var1: Method reference (citation)
//++  240 Var1: Changes to Method or SOP
//    public String VarX_SOP_Changes = "Changes to Method or SOP";
    
}
