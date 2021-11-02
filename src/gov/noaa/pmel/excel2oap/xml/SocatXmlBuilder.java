/**
 * 
 */
package gov.noaa.pmel.excel2oap.xml;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import gov.noaa.ncei.oads.xml.v_a0_2_2s.Co2Socat;
import gov.noaa.ncei.oads.xml.v_a0_2_2s.Co2Socat.Co2SocatBuilder;
import gov.noaa.ncei.oads.xml.v_a0_2_2s.EquilibratorMeasurementType;
import gov.noaa.ncei.oads.xml.v_a0_2_2s.EquilibratorType;
import gov.noaa.ncei.oads.xml.v_a0_2_2s.InstrumentType;
import gov.noaa.ncei.oads.xml.v_a0_2_2s.OadsMetadataDocumentType.OadsMetadataDocumentTypeBuilder;
import gov.noaa.ncei.oads.xml.v_a0_2_2s.StandardGasType;
import gov.noaa.ncei.oads.xml.v_a0_2_2s.StandardizationType;
import gov.noaa.pmel.excel2oap.ElementType;
import gov.noaa.pmel.excel2oap.NonNullHashMap;
import gov.noaa.pmel.excel2oap.SpreadSheetKeys;
import gov.noaa.pmel.excel2oap.sdg.socat.SocatElementType;
import gov.noaa.pmel.excel2oap.sdg.socat.SocatKeys;

/**
 * @author kamb
 *
 */
public class SocatXmlBuilder extends SdgXmlBuilder {

    
    private SocatKeys socatKeys;
    private static final String SOCAT_COMMON_FULL_NAME = "CO2 Common";
    
    /**
     * @param multiItems
     * @param simpleItems
     * @param keys
     * @param omitEmptyElements
     */
    public SocatXmlBuilder(Map<ElementType, Collection<Map<String, String>>> multiItems,
            Map<String, String> simpleItems, SpreadSheetKeys keys, boolean omitEmptyElements) {
        super(multiItems, simpleItems, keys, omitEmptyElements);
        socatKeys = (SocatKeys)keys;
    }

    public void addVariables(OadsMetadataDocumentTypeBuilder doc) {
       addDepth(doc);
       addXCO2(doc);
       this.addPCO2(doc);
       this.addFCO2(doc);
       addCO2common(doc);
       addTemperature(doc);
       addSalinity(doc);
       addVARs(doc);
    }

    private StringBuilder co2vars = new StringBuilder();
    private String comma = "";
    
    /**
     * @param doc
     */
    protected void addXCO2(OadsMetadataDocumentTypeBuilder doc) {
        Map<String, String> parts = getSingularItem(SocatElementType.XCO2.key());
        if ( parts == null || parts.isEmpty()) { return; }
        addVar(doc, parts);
        co2vars.append(comma).append("xCO2");
        comma = ",";
    }
    protected void addPCO2(OadsMetadataDocumentTypeBuilder doc) {
        Map<String, String> parts = getSingularItem(SocatElementType.PCO2.key());
        if ( parts == null || parts.isEmpty()) { return; }
        addVar(doc, parts);
        co2vars.append(comma).append("pCO2");
        comma = ",";
    }
    protected void addFCO2(OadsMetadataDocumentTypeBuilder doc) {
        Map<String, String> parts = getSingularItem(SocatElementType.FCO2.key());
        if ( parts == null || parts.isEmpty()) { return; }
        addVar(doc, parts);
        co2vars.append(comma).append("fCO2");
        comma = ",";
    }
    private void addCO2common(OadsMetadataDocumentTypeBuilder doc) {
        Map<String, String> parts = getSingularItem(SocatElementType.CO2.key());
        if ( parts == null || parts.isEmpty()) { 
            System.err.println("No CO2common in SOCAT file");
            return; 
        }
        Co2SocatBuilder<?, ?> var = Co2Socat.builder();
        parts.put(ssKeys.getKeyForName(ssKeys.name_VarX_Variable_abbreviation_in_data_files), co2vars.toString());
        fillCO2aSections(var, parts);
        Co2Socat co2 = fillCO2socat(var, parts);
        doc.addVariable(co2);
    }
    /**
     * @param var
     * @param parts
     */
    private Co2Socat fillCO2socat(Co2SocatBuilder<?, ?> var, Map<String, String> parts) {
        // uncertainty of inside temp
        Co2Socat co2 = 
            var.calculationMethodForPCO2(parts.get(socatKeys.getKeyForName(socatKeys.name_r_fpxCO2_pCO2_Calculation_Method)))
               .calculationMethodForFCO2(parts.get(socatKeys.getKeyForName(socatKeys.name_r_fpxCO2_fCO2_Calculation_Method)))
               .fullName(SOCAT_COMMON_FULL_NAME)
            // calibration of eq temp sensor
            // total pressure measure
            // calibration method and frequency of pressure sensors
            // traced gas to WMO standards
            // pCO2 calc method
            // fCO2 calc method
            .build();
        StandardizationType std = co2.getStandardization();
        List<StandardGasType> gases = std.getStandardGas();
        if ( gases != null && ! gases.isEmpty()) {
            StandardGasType gas = gases.get(0);
            gas.setTraceabilityToWmoStandards(parts.get(socatKeys.r_fpxCO2_Gas_Traceability));
        }
        co2.setTotalMeasurementPressure(EquilibratorMeasurementType.builder()
                                        .method(parts.get(socatKeys.r_fpxCO2_Total_Measured_Pressure))
                                        .sensor(InstrumentType.builder()
                                                .uncertainty(parts.get(socatKeys.r_fpxCO2_Total_Measured_Pressure_Uncertainty))
                                                .build())
                                        .build()
                                        );
        EquilibratorType eq = co2.getEquilibrator();
        if ( eq != null ) {
            eq.setTemperatureMeasurement(EquilibratorMeasurementType.builder()
                                        .method(parts.get(ssKeys.getKeyForName(ssKeys.name_pCO2AX_How_was_temperature_inside_the_equilibrator_measured)))
                                        .sensor(InstrumentType.builder()
                                                .uncertainty(parts.get(ssKeys.getKeyForName(ssKeys.name_pCO2AX_How_was_temperature_inside_the_equilibrator_measured)))
                                                .build())
                                        .build());
            eq.setPressureMeasurement(EquilibratorMeasurementType.builder()
                                    .method(parts.get(ssKeys.getKeyForName(ssKeys.name_pCO2AX_How_was_pressure_inside_the_equilibrator_measured)))
                                    .build());
        }
        return co2;
    }

}
