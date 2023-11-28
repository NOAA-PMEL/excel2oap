/**
 * 
 */
package gov.noaa.pmel.excel2oap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;

import gov.noaa.pmel.excel2oap.ifc.SSParser;
import gov.noaa.pmel.tws.util.StringUtils;


/**
 * @author kamb
 *
 */
public abstract class BaseSpreadSheetHandler 
        implements SSParser, SpreadSheetFlavor {
    
    private static final Logger logger = LogManager.getLogger(Excel2OAP.class);
    
    private final CharSequence VAR_FIRST_LINE_OPENING_LC = "variable abbreviation";
    
    private boolean _omitEmptyElements = true; // XXX 
    
    private String[] multiItemFields;
    private String[] multiLineFields;
    
    private String multiItemDetectorRegex; // = buildLineStartDetector(multiItemFields, true);
    private Pattern multiItemPattern; // = Pattern.compile(multiItemDetectorRegex, Pattern.CASE_INSENSITIVE);
    
    private String multiLineDetectorRegex; // = buildLineStartDetector((String[]) all.toArray(new String[all.size()]), false);
    private Pattern multiLinePattern; // = Pattern.compile(multiLineDetectorRegex, Pattern.CASE_INSENSITIVE);

    private SpreadSheetKeys _ssKeys;
//    private NonNullHashMap<String, String> generalFields = new NonNullHashMap<>();
    private Map<String, String> generalFields;
    private Map<ElementType, Collection<Map<String, String>>> multiItems = 
                        new HashMap<ElementType, Collection<Map<String,String>>>();
    
    public Map<String, String> getSingleFields() {
        return generalFields;
    }
    public Map<ElementType, Collection<Map<String, String>>> getMultiItemFields() {
        return multiItems;
    }
    public SpreadSheetKeys getSpreadSheetKeys() {
        return _ssKeys;
    }
    
    public enum ITEM_GROUPS {
        ALL(0),
        WHICH(1),
        TYPE(2),
        NUM(3),
        REMAINDER(4);
        
        public final int position;
        private ITEM_GROUPS(int pos) { position = pos; }
    }
    
    protected BaseSpreadSheetHandler(String[] multiLine, String[] multiItem,
                                     SpreadSheetKeys keys, boolean omitEmptyItems) {
        _ssKeys = keys;
        _omitEmptyElements = omitEmptyItems;
        generalFields = new NonNullHashMap<>(); //  ? new ForceNullHashMap<>() : new NonNullHashMap<>();
        
        multiLineFields = multiLine;
        multiItemFields = multiItem;
        List<String> all = new ArrayList<String>() 
            {{ 
                addAll(Arrays.asList(multiItemFields)); 
                addAll(Arrays.asList(multiLineFields));
            }};
        
        multiItemDetectorRegex = buildLineStartDetector(multiItemFields, true);
        multiItemPattern = Pattern.compile(multiItemDetectorRegex, Pattern.CASE_INSENSITIVE);
        
        multiLineDetectorRegex = buildLineStartDetector((String[]) all.toArray(new String[all.size()]), false);
        multiLinePattern = Pattern.compile(multiLineDetectorRegex, Pattern.CASE_INSENSITIVE);
    }
        
//    String exampleMultiItemMatchPattern = "((Investigator|Platform|Var|DIC|TA|pH|pCO2A|pCO2D)(?:.)?(\\d))(.*)";
//    String exampleMultiLineMatchPattern = "((Investigator|Platform|Var|DIC|TA|pH|pCO2A|pCO2D))(?:.*?)( )(.*)";

    private static String buildLineStartDetector(String[] possibilities, boolean isMultiItem) {
        StringBuilder sb = new StringBuilder();
        String div = "";
        sb.append("((");
        for (String possible : possibilities) {
            sb.append(div)
              .append(possible);
            div = "|";
        }
        sb.append(")");
        if ( isMultiItem ) {
            sb.append("(?:.)?(\\d+))(?:\\:?)");  // "(?:.*)?(\\d| ?)");
        } else {
            sb.append(")(?:.*?)( )");
        }
        sb.append("(.*)");
        return sb.toString();
    }
    
    public void processRows(List<SsRow> rows) {
        for (int curRow = 0; curRow < rows.size(); curRow++) {
            SsRow row = rows.get(curRow);
            String rowName = row.name().trim();
            String rowValue = row.value().trim();
            if ( StringUtils.emptyOrNull(rowValue)) { 
//                logger.debug("Skipping empty value for " + row.name);
                continue; 
            }
            Matcher multiLineMatcher = multiLinePattern.matcher(rowName);
            if ( multiLineMatcher.matches()) {
                Pattern matchedPattern = multiLinePattern;
                String which = multiLineMatcher.group(ITEM_GROUPS.WHICH.position);
//                logger.debug("multiLineMatch: "+ multiLineMatcher);
//                for (ITEM_GROUPS groups : ITEM_GROUPS.values()) { logger.debug(groups+":"+multiLineMatcher.group(groups.position)+" "); } logger.debug();
                Matcher multiItemMatcher = multiItemPattern.matcher(rowName);
                if ( multiItemMatcher.matches()) {
//                    for (ITEM_GROUPS groups : ITEM_GROUPS.values()) { logger.debug(groups+":"+multiItemMatcher.group(groups.position)+" "); } logger.debug();
                    which = multiItemMatcher.group(ITEM_GROUPS.WHICH.position);
                    matchedPattern = multiItemPattern;
                }
                curRow = processMultiLineItem(which, matchedPattern, rows, curRow);
            } else {
//                logger.debug("Putting simple field: "+ row);
                generalFields.put(rowName, rowValue);
            }
        }
    }
        
    /**
     * @param whichItem
     * @param pattern
     * @param sdi
     * @param rows
     * @param curRow
     */
    protected int processMultiLineItem(String whichItem, Pattern pattern, List<SsRow> rows, int curRow) {
        int gotRow = curRow;
        String type = null;
        ForceNullHashMap<String, String> parts = new ForceNullHashMap<>();
        SsRow row = rows.get(gotRow);
        boolean isVarProcessing = whichItem.startsWith("Var");
        Matcher rowMatch;
        Matcher endMatch;
        Pattern p2 = Pattern.compile(whichItem+":?\\s+(.*)");
        boolean allDone = false;
        do {
            rowMatch = pattern.matcher(row.name()); // XXX This should be checking against 'whichOne'
            if ( !rowMatch.matches()) {
                String s = "Problem!  Row doesn't match " + whichItem + ". " + row;
                throw new IllegalStateException(s);
            }
            type = rowMatch.group(ITEM_GROUPS.TYPE.position);
            String remainder = especiallyClean(rowMatch.group(ITEM_GROUPS.REMAINDER.position)).trim();
            parts.put(remainder, row.value());
            allDone = ! (gotRow < rows.size()-1);
            if ( !allDone ) {
                row = rows.get(++gotRow);
            }
            endMatch = p2.matcher(row.name());
            isVarProcessing = isVarProcessing && row.name().startsWith("Var");
        } while ( ! allDone && // gotRow < (rows.size()) && 
                  ((isVarProcessing && 
                          ! (row.name().toLowerCase().contains(VAR_FIRST_LINE_OPENING_LC)) &&
                          ! StringUtils.emptyOrNull(row.name())) ||
                   (!isVarProcessing && endMatch.matches())));
        if ( allDone ) { // end of file.  Include last row.
            gotRow = rows.size()+1; // adjust for rollback on return to stop processing
        }
        addMetadataItem(elementForKey(type), parts);
        return gotRow - 1; // roll back from last checked row.
    }

    /**
     * @param type 
     * @param sdi
     * @param parts
     */
    private void addMetadataItem(ElementType type, Map<String, String> parts) {
//        logger.debug("Add " + type + " item from " + parts);
        if ( multiItems.containsKey(type)) {
            multiItems.get(type).add(parts);
        } else {
            Collection<Map<String,String>> collection = new ArrayList<>();
            collection.add(parts);
            multiItems.put(type, collection);
        }
    }


    @SuppressWarnings("unused")
    private void add_VAR(Document doc, Map<String, String> parts) {
        add_VAR(doc, parts, "0");
    }
    protected void add_VAR(Document doc, Map<String, String> parts, String internal) {
        Element root = doc.getRootElement();
        Element var = new Element("variable");
        fill_VAR(var, parts, internal);
        root.addContent(var);
    }
    protected void fill_VAR(Element var, Map<String, String> parts) {
        fill_VAR(var, parts, "0");
    }
    protected void fill_VAR(Element var, Map<String, String> parts, String internal) {
        if ( parts.get(_ssKeys.getKeyForName(_ssKeys.name_VarX_Variable_abbreviation_in_data_files)) == null) {
            logger.warn("No abbreviation for variable from: " + parts.toString());
        }
		maybeAdd(var,"abbrev",parts.get(_ssKeys.getKeyForName(_ssKeys.name_VarX_Variable_abbreviation_in_data_files)));
        if ( parts.get(_ssKeys.getKeyForName(_ssKeys.name_VarX_Full_variable_name)) == null) {
            logger.info("No full name for variable " + 
                        parts.get(_ssKeys.getKeyForName(_ssKeys.name_VarX_Variable_abbreviation_in_data_files)));
            parts.put(_ssKeys.getKeyForName(_ssKeys.name_VarX_Full_variable_name), "Depth");
        }
		maybeAdd(var,"fullname",parts.get(_ssKeys.getKeyForName(_ssKeys.name_VarX_Full_variable_name)));
		maybeAdd(var,"observationType",parts.get(_ssKeys.getKeyForName(_ssKeys.name_VarX_Observation_type)));
		maybeAdd(var,"insitu",parts.get(_ssKeys.getKeyForName(_ssKeys.name_VarX_In_situ_observation_X_manipulation_condition_X_response_variable)));
		maybeAdd(var,"unit",parts.get(_ssKeys.getKeyForName(_ssKeys.name_VarX_Variable_unit)));
		maybeAdd(var,"measured",parts.get(_ssKeys.getKeyForName(_ssKeys.name_VarX_Measured_or_calculated)));
		maybeAdd(var,"calcMethod",parts.get(_ssKeys.getKeyForName(_ssKeys.name_VarX_Calculation_method_and_parameters)));
		maybeAdd(var,"samplingInstrument",parts.get(_ssKeys.getKeyForName(_ssKeys.name_VarX_Sampling_instrument)));
		maybeAdd(var,"analyzingInstrument",parts.get(_ssKeys.getKeyForName(_ssKeys.name_VarX_Analyzing_instrument)));
		maybeAdd(var,"duration",parts.get(_ssKeys.getKeyForName(_ssKeys.name_VarX_Duration)));
		maybeAdd(var,"detailedInfo",parts.get(_ssKeys.getKeyForName(_ssKeys.name_VarX_Detailed_sampling_and_analyzing_information)));
		maybeAdd(var,"replicate",parts.get(_ssKeys.getKeyForName(_ssKeys.name_VarX_Field_replicate_information)));
		maybeAdd(var,"uncertainty",parts.get(_ssKeys.getKeyForName(_ssKeys.name_VarX_Uncertainty)));
		maybeAdd(var,"flag",parts.get(_ssKeys.getKeyForName(_ssKeys.name_VarX_Data_quality_flag_description)));
		maybeAdd(var,"methodReference",parts.get(_ssKeys.getKeyForName(_ssKeys.name_VarX_Method_reference)));
		maybeAdd(var,"biologicalSubject",parts.get(_ssKeys.getKeyForName(_ssKeys.name_VarX_Biological_subject)));
		maybeAdd(var,"speciesID",parts.get(_ssKeys.getKeyForName(_ssKeys.name_VarX_Species_Identification_code)));
		maybeAdd(var,"lifeStage",parts.get(_ssKeys.getKeyForName(_ssKeys.name_VarX_Life_Stage)));
		maybeAdd(var,"researcherName",parts.get(_ssKeys.getKeyForName(_ssKeys.name_VarX_Researcher_Name)));
		maybeAdd(var,"researcherInstitution",parts.get(_ssKeys.getKeyForName(_ssKeys.name_VarX_Researcher_Institution)));
        maybeAdd(var,"internal",internal);
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

    protected static String especiallyClean(String dirty) {
        if ( dirty.indexOf("SPECIAL USE ONLY") < 0 ) return dirty;
        return dirty.replaceAll(" *\\(SPECIAL USE ONLY\\) *", "");
    }
    
    protected static boolean addIfNotNull(Element var, String childName, String childContent) {
        if ( StringUtils.emptyOrNull(childContent)) { return false; }
        var.addContent(new Element(childName).addContent(childContent));
        return true;
    }
    
    protected boolean maybeAdd(Element var, String childName, String childContent) {
        if ( StringUtils.emptyOrNull(childContent) && _omitEmptyElements ) { return false; }
        var.addContent(new Element(childName).addContent(childContent));
        return true;
    }
    
    protected Map<String, String> getSingularItem(String keyName) {
        ElementType type = _ssKeys.getElementForKey(keyName);
        Collection<Map<String, String>> c = multiItems.get(type);
        if ( c == null || c.isEmpty()) {
            return null;
        }
        if ( c.size() > 1 ) {
            System.err.println("More than one item for getSingular " + type.name());
        }
        return c.iterator().next();
    }

    protected boolean isEmpty(Element elem) {
        if ( elem == null ) { return true; }
        List<Attribute> attrs = elem.getAttributes();
        List<Element> children = elem.getChildren();
                
        return ((children.size() == 0 && 
                 attrs.size() == 0 &&
                 StringUtils.emptyOrNull(elem.getTextTrim())) ||
                (isEmpty(children)));
    }
              
    protected boolean isEmpty(List<Element> elems) {
        for (Element elem : elems) {
            if (!isEmpty(elem)) { return false; }
        }
        return true;
    }

}
