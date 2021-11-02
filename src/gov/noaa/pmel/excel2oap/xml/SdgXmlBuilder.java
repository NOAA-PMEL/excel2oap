/**
 * 
 */
package gov.noaa.pmel.excel2oap.xml;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;
import java.util.Map;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import gov.noaa.ncei.oads.xml.v_a0_2_2s.Co2Autonomous;
import gov.noaa.ncei.oads.xml.v_a0_2_2s.QcFlagInfoType;
import gov.noaa.ncei.oads.xml.v_a0_2_2s.BaseVariableType.BaseVariableTypeBuilder;
import gov.noaa.ncei.oads.xml.v_a0_2_2s.Co2Autonomous.Co2AutonomousBuilder;
import gov.noaa.ncei.oads.xml.v_a0_2_2s.OadsMetadataDocumentType.OadsMetadataDocumentTypeBuilder;
import gov.noaa.pmel.excel2oap.ElementType;
import gov.noaa.pmel.excel2oap.NonNullHashMap;
import gov.noaa.pmel.excel2oap.SpreadSheetKeys;
import gov.noaa.pmel.excel2oap.ifc.SSParser;
import gov.noaa.pmel.excel2oap.sdg.SDG_14_3_Keys;
import gov.noaa.pmel.excel2oap.sdg.SdgElementType;

/**
 * @author kamb
 *
 */
public class SdgXmlBuilder extends OadsXmlBuilder {

    private static final Logger logger = LogManager.getLogger(SdgXmlBuilder.class);
    
    private SDG_14_3_Keys sdgKeys;
    
    /**
     * @param multiItems
     * @param simpleItems
     * @param keys
     * @param omitEmptyElements
     */
    public SdgXmlBuilder(Map<ElementType, Collection<Map<String, String>>> multiItems,
            Map<String, String> simpleItems, SpreadSheetKeys keys, boolean omitEmptyElements) {
        super(multiItems, simpleItems, keys, omitEmptyElements);
        sdgKeys = (SDG_14_3_Keys)keys;
    }

    public void addVariables(OadsMetadataDocumentTypeBuilder doc) {
       addDIC(doc);
       addTA(doc);
       addPH(doc);
       addPCO2(doc);
       addFCO2(doc);
       addDepth(doc);
       addTemperature(doc);
       addSalinity(doc);
       addVARs(doc);
    }

    /**
     * @param doc
     */
    protected void addPCO2(OadsMetadataDocumentTypeBuilder doc) {
        Map<String, String> parts = getSingularItem(SdgElementType.PCO2.key());
        if ( parts == null || parts.isEmpty()) { return; }
        Co2AutonomousBuilder<?,?> var = Co2Autonomous.builder();
        fillCO2aSections(var,parts);
        Co2Autonomous co2a = var.build();
        doc.addVariable(co2a);
    }

    /**
     * @param doc
     */
    protected void addFCO2(OadsMetadataDocumentTypeBuilder doc) {
        Map<String, String> parts = getSingularItem(SdgElementType.FCO2.key());
        if ( parts == null || parts.isEmpty()) { return; }
        Co2AutonomousBuilder<?,?> var = Co2Autonomous.builder();
        fillCO2aSections(var,parts);
        Co2Autonomous co2a = var.build();
        doc.addVariable(co2a);
    }

    public BaseVariableTypeBuilder<?,?> fillBaseVariable(BaseVariableTypeBuilder<?, ?> var, 
                                                         Map<String, String> parts) {
        if (parts == null) {
            logger.debug("null variable parts");
            return var;
        }
        super.fillBaseVariable(var, parts);
        var.qcFlag(QcFlagInfoType.builder()
                    .description(parts.get(sdgKeys.getKeyForName(sdgKeys.name_VarX_Data_quality_flag_description)))
                    .scheme(parts.get(sdgKeys.VarX_QC_Scheme))
                    .qcFlagVarName(parts.get(sdgKeys.VarX_QC_VarName))
                    .build());
        return var;
    }
    protected void addNamedVar(OadsMetadataDocumentTypeBuilder doc, String varName) {
        Map<String, String> parts = getSingularItem(varName);
        if (parts == null || parts.isEmpty()) { 
            logger.debug("No " + varName + " var elements found.");
            return;
        }
        if ( ! parts.containsKey(varName)) {
            parts.put(ssKeys.VarX_Full_variable_name, varName);
        }
        addVar(doc, parts);
    }
    /**
     * @param doc
     */
    protected void addDepth(OadsMetadataDocumentTypeBuilder doc) {
        addNamedVar(doc, SdgElementType.DEPTH.key());
//        Map<String, String> parts = getSingularItem("Depth");
//        if (parts == null || parts.isEmpty()) { 
//            logger.debug("No depth var elements found.");
//            return;
//        }
//        addVar(doc, parts);
    }

    /**
     * @param doc
     */
    protected void addTemperature(OadsMetadataDocumentTypeBuilder doc) {
        addNamedVar(doc, SdgElementType.TEMPERATURE.key());
//        Map<String, String> parts = getSingularItem(SdgElementType.TEMPERATURE.key());
//        if (parts == null || parts.isEmpty()) { 
//            logger.debug("No temperature var elements found.");
//            return;
//        }
//        parts.put(ssKeys.VarX_Full_variable_name, "Temperature");
//        addVar(doc, parts);
    }

    /**
     * @param doc
     */
    protected void addSalinity(OadsMetadataDocumentTypeBuilder doc) {
        addNamedVar(doc, SdgElementType.SALINITY.key());
//        Map<String, String> parts = getSingularItem(SdgElementType.SALINITY.key());
//        if (parts == null || parts.isEmpty()) { 
//            logger.debug("No Salinity var elements found.");
//            return;
//        }
//        parts.put(ssKeys.VarX_Full_variable_name, "Salinity");
//        addVar(doc, parts);
    }

}
