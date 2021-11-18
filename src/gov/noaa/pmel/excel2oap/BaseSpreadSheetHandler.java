/**
 * 
 */
package gov.noaa.pmel.excel2oap;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
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
import gov.noaa.pmel.sdimetadata.Coverage;
import gov.noaa.pmel.sdimetadata.MiscInfo;
import gov.noaa.pmel.sdimetadata.MiscInfo.MiscInfoBuilder;
import gov.noaa.pmel.sdimetadata.SDIMetadata;
import gov.noaa.pmel.sdimetadata.instrument.Instrument;
import gov.noaa.pmel.sdimetadata.person.Investigator;
import gov.noaa.pmel.sdimetadata.person.Person;
import gov.noaa.pmel.sdimetadata.person.Submitter;
import gov.noaa.pmel.sdimetadata.platform.Platform;
import gov.noaa.pmel.sdimetadata.util.Datestamp;
import gov.noaa.pmel.sdimetadata.util.NumericString;
import gov.noaa.pmel.sdimetadata.variable.Variable;
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
        generalFields = omitEmptyItems ? new ForceNullHashMap<>() : new NonNullHashMap<>();
        
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
            sb.append("(?:.)?(\\d))(?:\\:?)");  // "(?:.*)?(\\d| ?)");
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
//        try {
//            Method m = BaseSpreadSheetHandler.class.getDeclaredMethod("add_"+type.name(), SDIMetadata.class, Map.class);
//            m.invoke(this, sdi, parts);
//        } catch (NoSuchMethodException ex) {
//            logger.debug("No add method for " + type.name());
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
    }

    /**
     * @param generalFields
     * @param sdi
     */
    private void putGeneralFields(SDIMetadata sdi, Map<String, String> generalFields) {
        addMiscInfo(sdi, generalFields);
        addCoverage(sdi, generalFields);
    }
    private void addMiscInfo(SDIMetadata sdi, Map<String, String> generalFields) {
        Datestamp startDatestamp = tryDatestamp(generalFields.get(_ssKeys.getKeyForName(_ssKeys.name_r_Start_date)));
        Datestamp endDatestamp = tryDatestamp(generalFields.get(_ssKeys.getKeyForName(_ssKeys.name_r_End_date)));
        String fundingAgency = generalFields.get(_ssKeys.getKeyForName(_ssKeys.name_r_Funding_agency_name)) != "" ?
                                generalFields.get(_ssKeys.getKeyForName(_ssKeys.name_r_Funding_agency_name)) :
                                generalFields.get(_ssKeys.getKeyForName(_ssKeys.name_r_Funding_agency_name_ALT));
        String fundingProjectId = generalFields.get(_ssKeys.getKeyForName(_ssKeys.name_r_Funding_project_ID)) != "" ?
                                   generalFields.get(_ssKeys.getKeyForName(_ssKeys.name_r_Funding_project_ID)) :
                                   generalFields.get(_ssKeys.getKeyForName(_ssKeys.name_r_Funding_project_ID_ALT));
        MiscInfoBuilder mib = new MiscInfo().toBuilder()
                .datasetId(generalFields.get(_ssKeys.getKeyForName(_ssKeys.name_r_EXPOCODE)))
                .datasetName(generalFields.get(_ssKeys.getKeyForName(_ssKeys.name_r_Cruise_ID)))
                .sectionName(generalFields.get(_ssKeys.getKeyForName(_ssKeys.name_r_Section)))
                .fundingAgency(fundingAgency)
                .fundingId(fundingProjectId)
                .fundingTitle(generalFields.get(_ssKeys.getKeyForName(_ssKeys.name_r_Funding_project_title)))
                .researchProject(generalFields.get(_ssKeys.getKeyForName(_ssKeys.name_r_Research_projects)))
//                .datasetDoi(generalFields.get(nothing))
                .accessId(generalFields.get(_ssKeys.getKeyForName(_ssKeys.name_r_Accession_no_of_related_data_sets)))
                .citation(generalFields.get(_ssKeys.getKeyForName(_ssKeys.name_r_Author_list_for_citation)))
                .synopsis(generalFields.get(_ssKeys.getKeyForName(_ssKeys.name_r_Abstract)))
                .purpose(generalFields.get(_ssKeys.getKeyForName(_ssKeys.name_r_Purpose)))
                .title(generalFields.get(_ssKeys.getKeyForName(_ssKeys.name_r_Title)))
                .addReference(generalFields.get(_ssKeys.getKeyForName(_ssKeys.name_r_References)))
                .addInfo(generalFields.get(_ssKeys.getKeyForName(_ssKeys.name_r_Supplemental_information)))
                .startDatestamp(startDatestamp)
                .endDatestamp(endDatestamp);
        String submissionDateStr = generalFields.get(_ssKeys.getKeyForName(_ssKeys.name_r_Submission_Date));
        if ( !StringUtils.emptyOrNull(submissionDateStr)) {
            Datestamp submissionDate = tryDatestamp(submissionDateStr);
            mib.history(new ArrayList<Datestamp>() {{ add(submissionDate); }});
        }
        MiscInfo mi = mib.build();
        sdi.setMiscInfo(mi);
    }

    private void addCoverage(SDIMetadata sdi, Map<String, String> generalFields) {
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
                .westernLongitude(new NumericString(generalFields.get(_ssKeys.getKeyForName(_ssKeys.name_r_Westbd_longitude)), Coverage.LONGITUDE_UNITS))
                .easternLongitude(new NumericString(generalFields.get(_ssKeys.getKeyForName(_ssKeys.name_r_Eastbd_longitude)), Coverage.LONGITUDE_UNITS))
                .northernLatitude(new NumericString(generalFields.get(_ssKeys.getKeyForName(_ssKeys.name_r_Northbd_latitude)), Coverage.LATITUDE_UNITS))
                .southernLatitude(new NumericString(generalFields.get(_ssKeys.getKeyForName(_ssKeys.name_r_Southbd_latitude)), Coverage.LATITUDE_UNITS))
                .earliestDataTime(cStart)
                .latestDataTime(cEnd)
                .spatialReference(generalFields.get(_ssKeys.getKeyForName(_ssKeys.name_r_Spatial_reference_system)))
                .addGeographicName(generalFields.get(_ssKeys.getKeyForName(_ssKeys.name_r_Geographic_names)))
                .build();
        sdi.setCoverage(coverage);
    }
        
    /**
     * @param sdi
     * @return
     */
    @SuppressWarnings("unused")
    public void add_PI(SDIMetadata sdi, Map<String, String> parts) {
        add_INVESTIGATOR(sdi, parts);
    }
    /**
     * @param sdi
     * @return
     */
    @SuppressWarnings("unused") // used by reflection
    public void add_INVESTIGATOR(SDIMetadata sdi, Map<String, String> parts) {
        Person person = Person.personBuilder()
                .firstName(parts.get(_ssKeys.getKeyForName(_ssKeys.name_PersonX_name)))
                .organization(parts.get(_ssKeys.getKeyForName(_ssKeys.name_PersonX_institution)))
                .id(parts.get(_ssKeys.getKeyForName(_ssKeys.name_PersonX_researcher_ID)))
                .idType(parts.get(_ssKeys.getKeyForName(_ssKeys.name_PersonX_ID_type)))
                .build();
        Investigator investigator = new Investigator(person);
        investigator.setEmail(parts.get(_ssKeys.getKeyForName(_ssKeys.name_PersonX_email)));
        investigator.setPhone(parts.get(_ssKeys.getKeyForName(_ssKeys.name_PersonX_phone)));
        investigator.addStreet(parts.get(_ssKeys.getKeyForName(_ssKeys.name_PersonX_address)));
        sdi.addInvestigator(investigator);
    }

    @SuppressWarnings("unused") // used by reflection
    public void add_DATA_SUBMITTER(SDIMetadata sdi, Map<String, String> parts) {
        Person person = Person.personBuilder()
                .firstName(parts.get(_ssKeys.getKeyForName(_ssKeys.name_PersonX_name)))
                .organization(parts.get(_ssKeys.getKeyForName(_ssKeys.name_PersonX_institution)))
                .id(parts.get(_ssKeys.getKeyForName(_ssKeys.name_PersonX_researcher_ID)))
                .idType(parts.get(_ssKeys.getKeyForName(_ssKeys.name_PersonX_ID_type)))
                .build();
        Submitter submitter = new Submitter(person);
        submitter.setEmail(parts.get(_ssKeys.getKeyForName(_ssKeys.name_PersonX_email)));
        submitter.setPhone(parts.get(_ssKeys.getKeyForName(_ssKeys.name_PersonX_phone)));
        submitter.addStreet(parts.get(_ssKeys.getKeyForName(_ssKeys.name_PersonX_address)));
        sdi.setSubmitter(submitter);
    }
    @SuppressWarnings("unused") // used by reflection
    public void add_PLATFORM(SDIMetadata sdi, Map<String, String> parts) {
        String platformName = parts.get(_ssKeys.getKeyForName(_ssKeys.name_PlatformX_name));
        String platformId = parts.get(_ssKeys.getKeyForName(_ssKeys.name_PlatformX_ID));
        if ( (StringUtils.emptyOrNull(platformName) || "none".equalsIgnoreCase(platformName)) && 
             (StringUtils.emptyOrNull(platformId) || "none".equalsIgnoreCase(platformId))) {
            return;
        }
        Platform platform = new Platform();
        platform.setPlatformName(platformName);
        platform.setPlatformId(platformId);
        platform.setPlatformTypeStr(parts.get(_ssKeys.getKeyForName(_ssKeys.name_PlatformX_type)));
        platform.setPlatformOwner(parts.get(_ssKeys.getKeyForName(_ssKeys.name_PlatformX_owner)));
        platform.setPlatformCountry(parts.get(_ssKeys.getKeyForName(_ssKeys.name_PlatformX_country)));
        sdi.setPlatform(platform);
    }

    @SuppressWarnings("unused")
    private Instrument buildInstrument(String string, Map<String, String> parts) {
        // unused
        // TODO Auto-generated method stub
        return null;
    }
    @SuppressWarnings("unused")
    protected void add_VAR(Document doc, Map<String, String> parts) {
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
    @SuppressWarnings("unused")
    public void add_FUNDING(SDIMetadata sdi, Map<String, String> parts) {
        // unused
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
    public Variable buildSDIvariable(Map<String, String> parts) {
        Variable v = new Variable();
        v.setColName(parts.get(_ssKeys.getKeyForName(_ssKeys.name_VarX_Variable_abbreviation_in_data_files)));
        v.setFullName(parts.get(_ssKeys.getKeyForName(_ssKeys.name_VarX_Full_variable_name)));
        v.setVarUnit(parts.get(_ssKeys.getKeyForName(_ssKeys.name_VarX_Variable_unit)));
        return v;
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

    /**
     * @param string
     * @return
     */
    protected static Datestamp tryDatestamp(String string) {
        logger.info("Trying datastamp for " + string);
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
