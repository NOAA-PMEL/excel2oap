/**
 * 
 */
package gov.noaa.pmel.excel2oap.ocads;


import gov.noaa.pmel.excel2oap.BaseSpreadSheetHandler;
import gov.noaa.pmel.excel2oap.ElementType;
import gov.noaa.pmel.excel2oap.SpreadSheetKeys;
import gov.noaa.pmel.excel2oap.SpreadSheetType;

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
//            OcadsElementType.FUNDING.key(), // Not supported yet. (In SDIMetadata)
            OcadsElementType.PLATFORM.key(), 
            OcadsElementType.VAR.key() 
    };
    private static final String[] multiLineFields = { 
            OcadsElementType.DATA_SUBMITTER.key(), 
//            OcadsElementType.FUNDING.key(), // Not actually supported here either.  Only 1 funding in spreadsheet.
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
    
}
