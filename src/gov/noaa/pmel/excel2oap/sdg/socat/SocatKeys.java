/**
 * 
 */
package gov.noaa.pmel.excel2oap.sdg.socat;

import java.lang.reflect.Field;
import java.util.HashMap;

import gov.noaa.pmel.excel2oap.ElementType;
import gov.noaa.pmel.excel2oap.SpreadSheetKeys;
import gov.noaa.pmel.excel2oap.sdg.SDG_14_3_Keys;

/**
 * @author kamb
 *
 */
public class SocatKeys extends SDG_14_3_Keys {

    public SocatKeys() {
        super(true);
        declaredFields = getDeclaredFields(new HashMap<String, Field>(), this.getClass());
    }
    @Override
    public ElementType getElementForKey(String key) {
        return SocatElementType.fromSsRowName(key);
    }
        
//     From Metadata-template_example_for_SOCAT from socat.info 
//    ...
//    Previous lines are the same as the SDG 14.3.1
//    
//*   SOCAT   xCO2: Variable abbreviation in data files  // new key
//+   SOCAT   xCO2: Variable unit
//    133 pCO2: Variable abbreviation in data files
//    135 pCO2: Variable unit
//    167 fCO2: Variable abbreviation in data files
//    169 fCO2: Variable unit
//    168 fCO2/pCO2/xCO2: Observation type  // NOTE that this line has to be moved
//*   170 fCO2/pCO2/xCO2: Collection method (e.g. with pump) // new key
//    171 fCO2/pCO2/xCO2: Location of seawater intake
//    172 fCO2/pCO2/xCO2: Depth of seawater intake
//    173 fCO2/pCO2/xCO2: Analyzing instrument
//    174 fCO2/pCO2/xCO2: Analyzing information with citation (SOP etc)
//    175 fCO2/pCO2/xCO2: Quality control
//    176 fCO2/pCO2/xCO2: Abbreviation of data quality flag scheme
//    177 fCO2/pCO2/xCO2: Data quality flag scheme
//    178 fCO2/pCO2/xCO2: Uncertainty
//    179 fCO2/pCO2/xCO2: Equilbrator type
//    180 fCO2/pCO2/xCO2: Equilibrator volume (L)
//    181 fCO2/pCO2/xCO2: Equilibrator vented or not
//    182 fCO2/pCO2/xCO2: Equilibrator water flow rate (L min-1)
//    183 fCO2/pCO2/xCO2: Equilibrator headspace gas flow rate (L min-1)
//    184 fCO2/pCO2/xCO2: How was temperature inside the equilibrator measured (i.e. which sensor)?
//+   SOCAT   fCO2/pCO2/xCO2: Uncertainty of temperature measured inside the equlibrator
    public String r_fpxCO2_Eq_Temp_Uncertainty = "Uncertainty of temperature measured inside the equlibrator";
//+   SOCAT   fCO2/pCO2/xCO2: Calibration method and frequency for temperature sensor inside the equlibrator
    public String r_fpxCO2_Eq_Temp_Sensor_Calibration = "Calibration method and frequency for temperature sensor inside the equlibrator";
//    185 fCO2/pCO2/xCO2: How was pressure inside the equilibrator measured (i.e. which sensor)?
//+   SOCAT   fCO2/pCO2/xCO2: How was the total measurement pressure determined?
    public String r_fpxCO2_Total_Measured_Pressure = "How was the total measurement pressure determined?";
//+   SOCAT   fCO2/pCO2/xCO2: Uncertainty of total measurement pressure, and how was this calculated?
    public String r_fpxCO2_Total_Measured_Pressure_Uncertainty = "Uncertainty of total measurement pressure, and how was this calculated?";
//+   SOCAT   fCO2/pCO2/xCO2: Calibration method and frequency for pressure sensor(s)
    public String r_fpxCO2_Pressure_Sensor_Calibration = "Calibration method and frequency for pressure sensor(s)";
//    186 fCO2/pCO2/xCO2: Drying method for CO2 gas
//    187 fCO2/pCO2/xCO2: Manufacturer of the gas detector
//    188 fCO2/pCO2/xCO2: Model of the gas detector
//    189 fCO2/pCO2/xCO2: Resolution of the gas detector
//    190 fCO2/pCO2/xCO2: Uncertainty of the gas detector
//    191 fCO2/pCO2/xCO2: Calibration method
//    192 fCO2/pCO2/xCO2: Frequency of calibration
//    193 fCO2/pCO2/xCO2: Manufacturer of standard gas
//+   SOCAT   fCO2/pCO2/xCO2: Traceability of standard gases to WMO standards
    public String r_fpxCO2_Gas_Traceability = "Traceability of standard gases to WMO standards";
//    194 fCO2/pCO2/xCO2: Concentrations of standard gas
//    195 fCO2/pCO2/xCO2: Uncertainties of standard gas
//    196 fCO2/pCO2/xCO2: Water vapor correction method
//+   SOCAT   fCO2/pCO2/xCO2: Method to calculate pCO2 from xCO2 (reference)
    public String r_fpxCO2_pCO2_Calculation_Method = "Method to calculate pCO2 from xCO2 (reference)";
    public String name_r_fpxCO2_pCO2_Calculation_Method = "r_fpxCO2_pCO2_Calculation_Method";
//+   SOCAT   fCO2/pCO2/xCO2: Method to calculate fCO2 from pCO2 (reference)
    public String r_fpxCO2_fCO2_Calculation_Method = "Method to calculate fCO2 from pCO2 (reference)";
    public String name_r_fpxCO2_fCO2_Calculation_Method = "r_fpxCO2_fCO2_Calculation_Method"; 
//    197 fCO2/pCO2/xCO2: Temperature correction method (from measurement temperature in the equilibrator to SST)
//    198 fCO2.pCO2/xCO2: At what temperature was fCO2 reported?
//    199 fCO2/pCO2/xCO2: Method reference (citation)
//    200 fCO2/pCO2/xCO2: Changes to Method or SOP
}
