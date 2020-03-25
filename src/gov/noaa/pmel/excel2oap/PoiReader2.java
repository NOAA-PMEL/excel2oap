/**
 * 
 */
package gov.noaa.pmel.excel2oap;

import static gov.noaa.pmel.excel2oap.OadsSpreadSheetKeys.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.DOMBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import gov.noaa.pmel.excel2oap.OadsSpreadSheetKeys.ElementType;
import gov.noaa.pmel.sdimetadata.Coverage;
import gov.noaa.pmel.sdimetadata.MiscInfo;
import gov.noaa.pmel.sdimetadata.SDIMetadata;
import gov.noaa.pmel.sdimetadata.instrument.Instrument;
import gov.noaa.pmel.sdimetadata.person.Investigator;
import gov.noaa.pmel.sdimetadata.person.Person;
import gov.noaa.pmel.sdimetadata.person.Submitter;
import gov.noaa.pmel.sdimetadata.platform.Platform;
import gov.noaa.pmel.sdimetadata.util.Datestamp;
import gov.noaa.pmel.sdimetadata.util.NumericString;
import gov.noaa.pmel.sdimetadata.variable.DataVar;
import gov.noaa.pmel.sdimetadata.variable.Variable;
import gov.noaa.pmel.sdimetadata.xml.OcadsWriter;
import gov.noaa.pmel.tws.util.StringUtils;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.poi.poifs.filesystem.FileMagic;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.util.IOUtils;

/**
 * @author kamb
 *
 */
public class PoiReader2 {
    

    static class OadsRow {
        int num;
        String name;
        String value;
        OadsRow(int rowNum, String rowName, String rowValue) {
            num = rowNum;
            name = rowName.trim();
            value = rowValue != null ? rowValue.trim() : rowValue;
        }
        @Override
        public String toString() {
            return "row:"+num+", name:"+name+", value: " + value;
        }
    }
    
    private static final String[] multiItemFields = { 
            ElementType.INVESTIGATOR.key(), 
            ElementType.PI.key(), 
            ElementType.PLATFORM.key(), 
            ElementType.VAR.key() 
    };
    private static final String[] multiLineFields = { 
            ElementType.DATA_SUBMITTER.key(), 
//            ElementType.FUNDING.key(), 
            ElementType.DIC.key(), 
            ElementType.TA.key(), 
            ElementType.PH.key(), 
            ElementType.PCO2A.key(), 
            ElementType.PCO2D.key()
    };
    
    private final String multiItemDetectorRegex = buildLineStartDetector(multiItemFields, true);
    private Pattern multiItemPattern = Pattern.compile(multiItemDetectorRegex);
    
    @SuppressWarnings("serial")
    private static List<String> all = new ArrayList<String>() 
    {{ 
        addAll(Arrays.asList(multiItemFields)); 
        addAll(Arrays.asList(multiLineFields));
    }};
    private final String multiLineDetectorRegex = buildLineStartDetector((String[]) all.toArray(new String[all.size()]), false);
    private Pattern multiLinePattern = Pattern.compile(multiLineDetectorRegex);
    
    private enum ITEM_GROUPS {
        ALL(0),
        WHICH(1),
        TYPE(2),
        NUM(3),
        REMAINDER(4);
        
        int position;
        private ITEM_GROUPS(int pos) { position = pos; }
    }
    
    private boolean _omitEmptyElements = false;
    
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
    
    public static void ConvertExcelToOADS(InputStream excelInStream, OutputStream outputXmlStream) throws Exception {
        new PoiReader2().convert(excelInStream, outputXmlStream);
    }
    
//    public void convert(File excelFile, File outputXmlFile, boolean omitEmptyElements) throws Exception {
//        try ( InputStream fIn = new FileInputStream(excelFile);
//              OutputStream fout = new FileOutputStream(outputXmlFile); ) {
//            convert(fIn, fout, omitEmptyElements);
//        }
//    }
    
    public void convert(InputStream excelInStream, OutputStream outputXmlStream) throws Exception {
        List<OadsRow> rows = extractFileRows(excelInStream);
        SDIMetadata sdi = processRows(rows);
        Document doc = writeSdiMetadata(sdi);
        addOtherStuff(doc);
        XMLOutputter xout = new XMLOutputter(Format.getPrettyFormat());
        xout.output(doc, outputXmlStream);
    }
    
    public PoiReader2() {}
    public PoiReader2(boolean omitEmptyElements) {
        _omitEmptyElements = omitEmptyElements;
    }
    
    private static String especiallyClean(String dirty) {
        if ( dirty.indexOf("SPECIAL USE ONLY") < 0 ) return dirty;
        return dirty.replaceAll(" *\\(SPECIAL USE ONLY\\) *", "");
    }
    
    private static List<OadsRow> extractFileRows(InputStream inStream) throws Exception, IOException {
        List<OadsRow> rows;
        ByteArrayOutputStream copyOut = new ByteArrayOutputStream();
        IOUtils.copy(inStream, copyOut);
        InputStream inCopy = new ByteArrayInputStream(copyOut.toByteArray());
        try ( InputStream bufIn = FileMagic.prepareToCheckMagic(inCopy); ) {
              FileMagic fm = FileMagic.valueOf(bufIn);
            switch (fm) {
                case OLE2:
                case OOXML:
                    rows = extractExcelRows(bufIn);
                    break;
                case UNKNOWN:
                    rows = tryDelimited(inCopy);
                    break;
                default:
                    throw new IOException("Cannot parse input stream of type: "+fm);
            }
        }
        return rows;
    }
        
    private static List<OadsRow> extractExcelRows(InputStream inStream) throws Exception, IOException {
        List<OadsRow> rows = new ArrayList<>();
        DataFormatter df = new DataFormatter();
        try ( Workbook wb = WorkbookFactory.create(inStream); ) {
            Sheet sheet = wb.getSheetAt(0);
            int rowNum = 0;
            for (Row row : sheet) {
                rowNum += 1;
                int itemNo;
                Cell numCell = null;
                try {
                    numCell = row.getCell(0);
                    itemNo = (int)numCell.getNumericCellValue(); 
                } catch (Exception ex) {
                    System.err.println("Not a valid metadata row at " + rowNum + " with cell 0 value: " + numCell);
                    continue;
                }
                Cell nameCell = row.getCell(1);
                if ( nameCell == null) {
                    System.err.println("Null name cell at row: "+ rowNum + "[#"+itemNo+"]");
                    continue;
                }
                String rowName = nameCell.getStringCellValue();
                Cell vcell = row.getCell(2);
                String rowValue = "";
//                vcell.setCellType(CellType.STRING);
                if ( vcell != null ) {
                    if ( vcell.getCellType().equals(CellType.STRING)) {
                        rowValue = vcell.getStringCellValue();
                    } else if ( DateUtil.isCellDateFormatted(vcell) && 
                                rowName.toLowerCase().indexOf("date") >= 0 ) {
                        Date d = vcell.getDateCellValue();
                        rowValue = d != null ? formatDate(d) : "";
                    } else if ( vcell.getCellType().equals(CellType.NUMERIC)) {
                        rowValue = df.formatCellValue(vcell);
//                        BigDecimal bd = new BigDecimal(vcell.getNumericCellValue());
//                        double d = bd.doubleValue();
//                        long l = (long)d;
//                        if ( l != d )
//                        rowValue = String.valueOf(d);
                    } else if ( vcell.getCellType().equals(CellType.BOOLEAN)) {
                        rowValue = String.valueOf(vcell.getBooleanCellValue());
                    }
                }
                OadsRow orow = new OadsRow(itemNo,
                                           row.getCell(1).getStringCellValue(), 
                                           rowValue);
//                System.out.println(rowNum + ": " + orow);
                rows.add(orow);
            }
        }
        return rows;
    }
    
    private static List<OadsRow> tryDelimited(InputStream inStream) throws Exception, IOException {
        List<OadsRow> rows = new ArrayList<>();
//        byte[] peak = IOUtils.peekFirstNBytes(inStream, 512);
            byte[] bytes = IOUtils.peekFirstNBytes(inStream, 4096);
            String peak = new String(bytes, Charset.forName("UTF8"));
            char spacer = lookForSpacer(peak);
            CSVFormat format = CSVFormat.EXCEL.withIgnoreSurroundingSpaces()
                    .withIgnoreEmptyLines()
                    .withQuote('"')
//                        .withTrailingDelimiter()
//                        .withCommentMarker('#')
                    .withDelimiter(spacer);
    		try ( InputStreamReader isr = new InputStreamReader(inStream);
    		        CSVParser dataParser = new CSVParser(isr, format); ) {
                int rowNum = 0;
                for (CSVRecord record : dataParser) {
                    rowNum += 1;
                    int itemNo;
                    String numCell = record.get(0);
                    try {
                        itemNo = Integer.parseInt(numCell);
                    } catch (Exception ex) {
                        System.err.println("Not a valid metadata row at " + rowNum + " with cell 0 value: " + numCell);
                        continue;
                    }
                    String rowName = record.get(1);
                    if ( rowName == null) {
                        System.err.println("Null name cell at row: "+ rowNum + "[#"+itemNo+"]");
                        continue;
                    }
                    String vcell = record.get(2);
                    String rowValue = vcell;
                    OadsRow orow = new OadsRow(itemNo,
                                               rowName,
                                               rowValue);
//                    System.out.println(rowNum + ": " + orow);
                    rows.add(orow);
                }
    		} catch (Exception ex) {
    		    ex.printStackTrace();
                throw ex;
    		}
        return rows;
    }
    /**
     * @param peak
     * @return
     */
    private static char lookForSpacer(String peak) {
        SortedMap<Integer, Character> sort = new TreeMap<>(new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                return o2.intValue() - o1.intValue();
            }
        });
        sort.put(count(peak, ','), new Character(','));
        sort.put(count(peak, ';'), new Character(';'));
        sort.put(count(peak, '\t'), new Character('\t'));
        sort.put(count(peak, '|'), new Character('|'));
        return sort.values().iterator().next().charValue();
    }

    /**
     * @param peak
     * @param c
     * @return
     */
    private static Integer count(String peak, char c) {
        int count = 0;
        for (int i = 0; i < peak.length(); i++) {
            if ( peak.charAt(i) == c) {
                count += 1;
            }
        }
        return new Integer(count);
    }

    private static String formatDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd/");
        String dateStr = sdf.format(date);
        return dateStr;
    }
    private SDIMetadata processRows(List<OadsRow> rows) {
        SDIMetadata sdi = new SDIMetadata();
        NonNullHashMap<String, String> generalFields = new NonNullHashMap<>();
        for (int curRow = 0; curRow < rows.size(); curRow++) {
            OadsRow row = rows.get(curRow);
            String rowName = row.name.trim();
            String rowValue = row.value.trim();
            if ( StringUtils.emptyOrNull(rowValue)) { 
//                System.out.println("Skipping empty value for " + row.name);
                continue; 
            }
            Matcher multiLineMatcher = multiLinePattern.matcher(rowName);
            if ( multiLineMatcher.matches()) {
                Pattern matchedPattern = multiLinePattern;
                String which = multiLineMatcher.group(ITEM_GROUPS.WHICH.position);
//                System.out.println("multiLineMatch: "+ multiLineMatcher);
//                for (ITEM_GROUPS groups : ITEM_GROUPS.values()) { System.out.print(groups+":"+multiLineMatcher.group(groups.position)+" "); } System.out.println();
                Matcher multiItemMatcher = multiItemPattern.matcher(rowName);
                if ( multiItemMatcher.matches()) {
//                    for (ITEM_GROUPS groups : ITEM_GROUPS.values()) { System.out.print(groups+":"+multiItemMatcher.group(groups.position)+" "); } System.out.println();
                    which = multiItemMatcher.group(ITEM_GROUPS.WHICH.position);
                    matchedPattern = multiItemPattern;
                }
                curRow = processMultiLineItem(which, matchedPattern, sdi, rows, curRow);
            } else {
//                System.out.println("Putting simple field: "+ row);
                generalFields.put(rowName, rowValue);
            }
        }
        putGeneralFields(sdi, generalFields);
        return sdi;
    }
        
    /**
     * @param whichItem
     * @param pattern
     * @param sdi
     * @param rows
     * @param curRow
     */
    private int processMultiLineItem(String whichItem, Pattern pattern, SDIMetadata sdi, List<OadsRow> rows, int curRow) {
        int gotRow = curRow;
        String type = null;
        NonNullHashMap<String, String> parts = new NonNullHashMap<>();
        OadsRow row = rows.get(gotRow);
        do {
            Matcher rowMatch = pattern.matcher(row.name); // XXX This should be checking against 'whichOne'
            if ( !rowMatch.matches()) {
                String s = "Problem!  Row doesn't match " + whichItem + ". " + row;
                throw new IllegalStateException(s);
            }
            type = rowMatch.group(ITEM_GROUPS.TYPE.position);
            String remainder = especiallyClean(rowMatch.group(ITEM_GROUPS.REMAINDER.position)).trim();
            parts.put(remainder, row.value);
        } while ( ++gotRow < rows.size() && (row=rows.get(gotRow)).name.startsWith(whichItem));
        
        addMetadataItem(ElementType.fromSsRowName(type), sdi, parts);
        return gotRow - 1; // roll back from last checked row.
    }

    /**
     * @param type 
     * @param sdi
     * @param parts
     */
    private Map<ElementType, Collection<Map<String, String>>> metaItems = 
                        new HashMap<ElementType, Collection<Map<String,String>>>();
    private void addMetadataItem(ElementType type, SDIMetadata sdi, NonNullHashMap<String, String> parts) {
//        System.out.println("Add " + type + " item from " + parts);
        try {
            Method m = PoiReader2.class.getDeclaredMethod("add_"+type.name(), SDIMetadata.class, Map.class);
            m.invoke(null, sdi, parts);
        } catch (NoSuchMethodException ex) {
            if ( metaItems.containsKey(type)) {
                metaItems.get(type).add(parts);
            } else {
                Collection<Map<String,String>> collection = new ArrayList<>();
                collection.add(parts);
                metaItems.put(type, collection);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * @param generalFields
     * @param sdi
     */
    private static void putGeneralFields(SDIMetadata sdi, NonNullHashMap<String, String> generalFields) {
        addMiscInfo(sdi, generalFields);
        addCoverage(sdi, generalFields);
    }
    private static void addMiscInfo(SDIMetadata sdi, NonNullHashMap<String, String> generalFields) {
        Datestamp startDatestamp = tryDatestamp(generalFields.get(r_Start_date));
        Datestamp endDatestamp = tryDatestamp(generalFields.get(r_End_date));
        MiscInfo mi = new MiscInfo().toBuilder()
                .datasetId(generalFields.get(r_EXPOCODE))
                .datasetName(generalFields.get(r_Cruise_ID))
                .sectionName(generalFields.get(r_Section))
                .fundingAgency(generalFields.get(r_Funding_agency_name))
                .fundingId(generalFields.get(r_Funding_project_ID))
                .fundingTitle(generalFields.get(r_Funding_project_title))
                .researchProject(generalFields.get(r_Research_projects))
//                .datasetDoi(generalFields.get(nothing))
                .accessId(generalFields.get(r_Accession_no_of_related_data_sets))
                .citation(generalFields.get(r_Author_list_for_citation))
                .synopsis(generalFields.get(r_Abstract))
                .purpose(generalFields.get(r_Purpose))
                .title(generalFields.get(r_Title))
                .addReference(generalFields.get(r_References))
                .addInfo(generalFields.get(r_Supplemental_information))
                .startDatestamp(startDatestamp)
                .endDatestamp(endDatestamp)
                .build();
        sdi.setMiscInfo(mi);
    }

    private static void addCoverage(SDIMetadata sdi, NonNullHashMap<String, String> generalFields) {
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
                .westernLongitude(new NumericString(generalFields.get(r_Westbd_longitude), Coverage.LONGITUDE_UNITS))
                .easternLongitude(new NumericString(generalFields.get(r_Eastbd_longitude), Coverage.LONGITUDE_UNITS))
                .northernLatitude(new NumericString(generalFields.get(r_Northbd_latitude), Coverage.LATITUDE_UNITS))
                .southernLatitude(new NumericString(generalFields.get(r_Southbd_latitude), Coverage.LATITUDE_UNITS))
                .earliestDataTime(cStart)
                .latestDataTime(cEnd)
                .spatialReference(generalFields.get(r_Spatial_reference_system))
                .addGeographicName(generalFields.get(r_Geographic_names))
                .build();
        sdi.setCoverage(coverage);
    }
        
    /**
     * @param string
     * @return
     */
    private static Datestamp tryDatestamp(String string) {
        Datestamp ds = new Datestamp();
        if ( ! StringUtils.emptyOrNull(string)) {
            String[] parts = string.split("[/ -]");
            if ( parts.length == 3 ) {
//                String p0 = parts[0];
//                String p1 = parts[1];
//                String p2 = parts[2];
//                int i0 = Integer.parseInt(p0);
//                int i1 = Integer.parseInt(p1);
//                int i2 = Integer.parseInt(p2);
                ds.setYear(Integer.valueOf(parts[0]));
                ds.setMonth(Integer.valueOf(parts[1]));
                ds.setDay(Integer.valueOf(parts[2]));
            } else {
                System.err.println("Excel2Oap: Cannot parse date string:" + string);
            }
        }
        return ds;
    }

    /**
     * @param sdi
     * @return
     */
    @SuppressWarnings("unused")
    private static void add_PI(SDIMetadata sdi, Map<String, String> parts) {
        add_INVESTIGATOR(sdi, parts);
    }
    /**
     * @param sdi
     * @return
     */
    @SuppressWarnings("unused") // used by reflection
    private static void add_INVESTIGATOR(SDIMetadata sdi, Map<String, String> parts) {
        Person person = Person.personBuilder()
                .firstName(parts.get(OadsSpreadSheetKeys.PersonX_name))
                .organization(parts.get(OadsSpreadSheetKeys.PersonX_institution))
                .id(parts.get(OadsSpreadSheetKeys.PersonX_researcher_ID))
                .idType(parts.get(OadsSpreadSheetKeys.PersonX_ID_type))
                .build();
        Investigator investigator = new Investigator(person);
        investigator.setEmail(parts.get(OadsSpreadSheetKeys.PersonX_email));
        investigator.setPhone(parts.get(OadsSpreadSheetKeys.PersonX_phone));
        investigator.addStreet(parts.get(OadsSpreadSheetKeys.PersonX_address));
        sdi.addInvestigator(investigator);
    }

    @SuppressWarnings("unused") // used by reflection
    private static void add_DATA_SUBMITTER(SDIMetadata sdi, Map<String, String> parts) {
        Person person = Person.personBuilder()
                .firstName(parts.get(OadsSpreadSheetKeys.PersonX_name))
                .organization(parts.get(OadsSpreadSheetKeys.PersonX_institution))
                .id(parts.get(OadsSpreadSheetKeys.PersonX_researcher_ID))
                .idType(parts.get(OadsSpreadSheetKeys.PersonX_ID_type))
                .build();
        Submitter submitter = new Submitter(person);
        submitter.setEmail(parts.get(OadsSpreadSheetKeys.PersonX_email));
        submitter.setPhone(parts.get(OadsSpreadSheetKeys.PersonX_phone));
        submitter.addStreet(parts.get(OadsSpreadSheetKeys.PersonX_address));
        sdi.setSubmitter(submitter);
    }
    @SuppressWarnings("unused") // used by reflection
    private static void add_PLATFORM(SDIMetadata sdi, Map<String, String> parts) {
        String platformName = parts.get(OadsSpreadSheetKeys.PlatformX_name);
        String platformId = parts.get(OadsSpreadSheetKeys.PlatformX_ID);
        if ( (StringUtils.emptyOrNull(platformName) || "none".equalsIgnoreCase(platformName)) && 
             (StringUtils.emptyOrNull(platformId) || "none".equalsIgnoreCase(platformId))) {
            return;
        }
        Platform platform = new Platform();
        platform.setPlatformName(platformName);
        platform.setPlatformId(platformId);
        platform.setPlatformTypeStr(parts.get(OadsSpreadSheetKeys.PlatformX_type));
        platform.setPlatformOwner(parts.get(OadsSpreadSheetKeys.PlatformX_owner));
        platform.setPlatformCountry(parts.get(OadsSpreadSheetKeys.PlatformX_country));
        sdi.setPlatform(platform);
    }
    private static Variable buildVariable(Map<String, String> parts) {
        Variable v = new Variable();
        v.setColName(parts.get(VarX_Variable_abbreviation_in_data_files));
        v.setFullName(parts.get(VarX_Full_variable_name));
        v.setVarUnit(parts.get(VarX_Variable_unit));
        return v;
    }
//    private static DataVar buildDataVar(Variable baseVar, Map<String, String> parts) {
//        DataVar dataVar = new DataVar(baseVar).toBuilder()
//            .observeType(parts.get(VarX_Observation_type))
//            // .measureMethod(parts.get(VarX_Measured_or_calculated)) // XXX needs in-situs
//            .methodDescription(parts.get(VarX_Detailed_sampling_and_analyzing_information))
//            .methodReference(parts.get(VarX_Method_reference))
//            .replication(parts.get(VarX_Field_replicate_information))
//            .researcher(Person.personBuilder()
//                            .firstName(parts.get(VarX_Researcher_Name))
//                            .organization(parts.get(VarX_Researcher_Institution))
//                            .build())
//            .build();
//        return dataVar;
//    }
    private Map<String, String> getSingularItem(ElementType type) {
        Collection<Map<String, String>> c = metaItems.get(type);
        if ( c == null || c.isEmpty()) {
            return null;
        }
        return c.iterator().next();
    }

    private void maybeAdd(Element var, String childName, String childContent) {
        if ( StringUtils.emptyOrNull(childContent) && _omitEmptyElements ) { return ; }
        var.addContent(new Element(childName).addContent(childContent));
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
    private void add_DIC(Document doc) {
        Map<String, String> parts = getSingularItem(ElementType.DIC);
        if ( parts == null ) {
            return;
        }
        Element root = doc.getRootElement();
        
        Element var = new Element("variable");
        maybeAdd(var,"fullname","Dissolved inorganic carbon");
        maybeAdd(var,"abbrev",parts.get(DICX_Variable_abbreviation_in_data_files));
        maybeAdd(var,"observationType",parts.get(DICX_Observation_type));
        maybeAdd(var,"insitu",parts.get(DICX_In_situ_observation_X_manipulation_condition_X_response_variable));
        maybeAdd(var,"manipulationMethod",parts.get(DICX_Manipulation_method));
        maybeAdd(var,"unit",parts.get(DICX_Variable_unit));
        maybeAdd(var,"measured",parts.get(DICX_Measured_or_calculated));
        maybeAdd(var,"calcMethod",parts.get(DICX_Calculation_method_and_parameters));
        maybeAdd(var,"samplingInstrument",parts.get(DICX_Sampling_instrument));
        maybeAdd(var,"analyzingInstrument",parts.get(DICX_Analyzing_instrument));
        maybeAdd(var,"detailedInfo",parts.get(DICX_Detailed_sampling_and_analyzing_information));
        maybeAdd(var,"replicate",parts.get(DICX_Field_replicate_information));
        Element standard = new Element("standard");
        maybeAdd(standard,"description",parts.get(DICX_Standardization_technique_description));
        maybeAdd(standard,"frequency",parts.get(DICX_Frequency_of_standardization));
        Element crm = new Element("crm");
        maybeAdd(crm,"manufacturer",parts.get(DICX_CRM_manufacturer));
        maybeAdd(crm,"batch",parts.get(DICX_Batch_number));
        standard.addContent(crm);
        var.addContent(standard);
        Element poison = new Element("poison");
        maybeAdd(poison,"poisonName",parts.get(DICX_Poison_used_to_kill_the_sample));
        maybeAdd(poison,"volume",parts.get(DICX_Poison_volume));
        maybeAdd(poison,"correction",parts.get(DICX_Poisoning_correction_description));
        var.addContent(poison);
        maybeAdd(var,"uncertainty",parts.get(DICX_Uncertainty));
        maybeAdd(var,"flag",parts.get(DICX_Data_quality_flag_description));
        maybeAdd(var,"methodReference",parts.get(DICX_Method_reference));
        maybeAdd(var,"researcherName",parts.get(DICX_Researcher_Name));
        maybeAdd(var,"researcherInstitution",parts.get(DICX_Researcher_Institution));
        var.addContent(new Element("internal").addContent("1"));
                                 
        root.addContent(var);
    }
    private void add_TA(Document doc) {
        Map<String, String> parts = getSingularItem(ElementType.TA);
        if ( parts == null ) {
            return;
        }
        Element root = doc.getRootElement();
        
        Element var = new Element("variable");
        var.addContent(new Element("fullname").addContent("Total alkalinity"));
        maybeAdd(var,"abbrev",parts.get(TAX_Variable_abbreviation_in_data_files));
        maybeAdd(var,"observationType",parts.get(TAX_Observation_type));
        maybeAdd(var,"insitu",parts.get(TAX_In_situ_observation_X_manipulation_condition_X_response_variable));
        maybeAdd(var,"manipulationMethod",parts.get(TAX_Manipulation_method));
        maybeAdd(var,"unit",parts.get(TAX_Variable_unit));
        maybeAdd(var,"measured",parts.get(TAX_Measured_or_calculated));
        maybeAdd(var,"calcMethod",parts.get(TAX_Calculation_method_and_parameters));
        maybeAdd(var,"samplingInstrument",parts.get(TAX_Sampling_instrument));
        maybeAdd(var,"analyzingInstrument",parts.get(TAX_Analyzing_instrument));
        maybeAdd(var,"titrationType",parts.get(TAX_Type_of_titration));
        maybeAdd(var,"cellType",parts.get(TAX_Cell_type));
        maybeAdd(var,"curveFitting",parts.get(TAX_Curve_fitting_method));
        maybeAdd(var,"detailedInfo",parts.get(TAX_Detailed_sampling_and_analyzing_information));
        maybeAdd(var,"replicate",parts.get(TAX_Field_replicate_information));
        Element standard = new Element("standard");
        maybeAdd(standard,"description",parts.get(TAX_Standardization_technique_description));
        maybeAdd(standard,"frequency",parts.get(TAX_Frequency_of_standardization));
        Element crm = new Element("crm");
        maybeAdd(crm,"manufacturer",parts.get(TAX_CRM_manufacturer));
        maybeAdd(crm,"batch",parts.get(TAX_Batch_number));
        standard.addContent(crm);
        var.addContent(standard);
        Element poison = new Element("poison");
        maybeAdd(poison,"poisonName",parts.get(TAX_Poison_used_to_kill_the_sample));
        maybeAdd(poison,"volume",parts.get(TAX_Poison_volume));
        maybeAdd(poison,"correction",parts.get(TAX_Poisoning_correction_description));
        var.addContent(poison);
        maybeAdd(var,"uncertainty",parts.get(TAX_Uncertainty));
        maybeAdd(var,"flag",parts.get(TAX_Data_quality_flag_description));
        maybeAdd(var,"methodReference",parts.get(TAX_Method_reference));
        maybeAdd(var,"researcherName",parts.get(TAX_Researcher_Name));
        maybeAdd(var,"researcherInstitution",parts.get(TAX_Researcher_Institution));
        var.addContent(new Element("internal").addContent("2"));
                                 
        root.addContent(var);
    }
    private void add_PH(Document doc) {
        Map<String, String> parts = getSingularItem(ElementType.PH);
        if ( parts == null ) {
            return;
        }
        Element root = doc.getRootElement();
        
        Element var = new Element("variable");
        var.addContent(new Element("fullname").addContent("pH"));
        maybeAdd(var,"abbrev",parts.get(PHX_Variable_abbreviation_in_data_files));
        maybeAdd(var,"observationType",parts.get(PHX_Observation_type));
        maybeAdd(var,"insitu",parts.get(PHX_In_situ_observation_X_manipulation_condition_X_response_variable));
        maybeAdd(var,"manipulationMethod",parts.get(PHX_Manipulation_method));
        maybeAdd(var,"measured",parts.get(PHX_Measured_or_calculated));
        maybeAdd(var,"calcMethod",parts.get(PHX_Calculation_method_and_parameters));
        maybeAdd(var,"samplingInstrument",parts.get(PHX_Sampling_instrument));
        maybeAdd(var,"analyzingInstrument",parts.get(PHX_Analyzing_instrument));
        maybeAdd(var,"phscale",parts.get(PHX_pH_scale));
        maybeAdd(var,"temperatureMeasure",parts.get(PHX_Temperature_of_measurement));
        maybeAdd(var,"detailedInfo",parts.get(PHX_Detailed_sampling_and_analyzing_information));
        maybeAdd(var,"replicate",parts.get(PHX_Field_replicate_information));
        Element standard = new Element("standard");
        maybeAdd(standard,"description",parts.get(PHX_Standardization_technique_description));
        maybeAdd(standard,"frequency",parts.get(PHX_Frequency_of_standardization));
        maybeAdd(standard,"standardphvalues",parts.get(PHX_pH_values_of_the_standards));
        maybeAdd(standard,"temperatureStandardization",parts.get(PHX_Temperature_of_standardization));
        var.addContent(standard);
        maybeAdd(var,"temperatureCorrectionMethod",parts.get(PHX_Temperature_correction_method));
        maybeAdd(var,"phReportTemperature",parts.get(PHX_at_what_temperature_was_pH_reported));
        maybeAdd(var,"uncertainty",parts.get(PHX_Uncertainty));
        maybeAdd(var,"flag",parts.get(PHX_Data_quality_flag_description));
        maybeAdd(var,"methodReference",parts.get(PHX_Method_reference));
        maybeAdd(var,"researcherName",parts.get(PHX_Researcher_Name));
        maybeAdd(var,"researcherInstitution",parts.get(PHX_Researcher_Institution));
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
    private void add_PCO2A(Document doc) {
        Map<String, String> parts = getSingularItem(ElementType.PCO2A);
        if ( parts == null ) {
            return;
        }
        Element root = doc.getRootElement();
        
        Element var = new Element("variable");
        var.addContent(new Element("fullname").addContent("pCO2 (fCO2) autonomous"));
        maybeAdd(var,"abbrev",parts.get(pCO2AX_Variable_abbreviation_in_data_files));
        maybeAdd(var,"observationType",parts.get(pCO2AX_Observation_type));
        maybeAdd(var,"insitu",parts.get(pCO2AX_In_situ_observation_X_manipulation_condition_X_response_variable));
        maybeAdd(var,"manipulationMethod",parts.get(pCO2AX_Manipulation_method));
        maybeAdd(var,"unit",parts.get(pCO2AX_Variable_unit));
        maybeAdd(var,"measured",parts.get(pCO2AX_Measured_or_calculated));
        maybeAdd(var,"calcMethod",parts.get(pCO2AX_Calculation_method_and_parameters));
        maybeAdd(var,"samplingInstrument",parts.get(pCO2AX_Sampling_instrument));
        maybeAdd(var,"locationSeawaterIntake",parts.get(pCO2AX_Location_of_seawater_intake));
        maybeAdd(var,"DepthSeawaterIntake",parts.get(pCO2AX_Depth_of_seawater_intake));
        maybeAdd(var,"analyzingInstrument",parts.get(pCO2AX_Analyzing_instrument));
        maybeAdd(var,"detailedInfo",parts.get(pCO2AX_Detailed_sampling_and_analyzing_information));
        Element equilibrator = new Element("equilibrator");
        maybeAdd(equilibrator,"type",parts.get(pCO2AX_Equilibrator_type));
        maybeAdd(equilibrator,"volume",parts.get(pCO2AX_Equilibrator_volume));
        maybeAdd(equilibrator,"vented",parts.get(pCO2AX_Vented_or_not));
        maybeAdd(equilibrator,"waterFlowRate",parts.get(pCO2AX_Water_flow_rate));
        maybeAdd(equilibrator,"gasFlowRate",parts.get(pCO2AX_Headspace_gas_flow_rate));
        maybeAdd(equilibrator,"temperatureEquilibratorMethod",parts.get(pCO2AX_How_was_temperature_inside_the_equilibrator_measured));
        maybeAdd(equilibrator,"pressureEquilibratorMethod",parts.get(pCO2AX_How_was_pressure_inside_the_equilibrator_measured));
        maybeAdd(equilibrator,"dryMethod",parts.get(pCO2AX_Drying_method_for_CO2_gas));
        var.addContent(equilibrator);
        Element gasDetector = new Element("gasDetector");
         maybeAdd(gasDetector,"manufacturer",parts.get(pCO2AX_Manufacturer_of_standard_gas));
         maybeAdd(gasDetector,"model",parts.get(pCO2AX_Model_of_the_gas_detector));
         maybeAdd(gasDetector,"resolution",parts.get(pCO2AX_Resolution_of_the_gas_detector));
         maybeAdd(gasDetector,"uncertainty",parts.get(pCO2AX_Uncertainty_of_the_gas_detector));
        var.addContent(gasDetector);
        Element standard = new Element("standard");
        maybeAdd(standard,"description",parts.get(pCO2AX_Standardization_technique_description));
        maybeAdd(standard,"frequency",parts.get(pCO2AX_Frequency_of_standardization));
        Element standardGas = new Element("standardgas");
            maybeAdd(standardGas,"manufacturer",parts.get(pCO2AX_Manufacturer_of_standard_gas));
            maybeAdd(standardGas,"concentration",parts.get(pCO2AX_Concentrations_of_standard_gas));
            maybeAdd(standardGas,"uncertainty",parts.get(pCO2AX_Uncertainties_of_standard_gas));
        standard.addContent(standardGas);
        var.addContent(standard);
        maybeAdd(var,"waterVaporCorrection",parts.get(pCO2AX_Water_vapor_correction_method));
        maybeAdd(var,"temperatureCorrection",parts.get(pCO2AX_Temperature_correction_method));
        maybeAdd(var,"co2ReportTemperature",parts.get(pCO2AX_at_what_temperature_was_pCO2_reported));
        maybeAdd(var,"uncertainty",parts.get(pCO2AX_Uncertainty));
        maybeAdd(var,"flag",parts.get(pCO2AX_Data_quality_flag_description));
        maybeAdd(var,"methodReference",parts.get(pCO2AX_Method_reference));
        maybeAdd(var,"researcherName",parts.get(pCO2AX_Researcher_Name));
        maybeAdd(var,"researcherInstitution",parts.get(pCO2AX_Researcher_Institution));
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
    private void add_PCO2D(Document doc) {
        Map<String, String> parts = getSingularItem(ElementType.PCO2D);
        if ( parts == null ) {
            return;
        }
        Element root = doc.getRootElement();
        
        Element var = new Element("variable");
        var.addContent(new Element("fullname").addContent("pCO2 (fCO2) discrete"));
        maybeAdd(var,"abbrev",parts.get(pCO2DX_Variable_abbreviation_in_data_files));
        maybeAdd(var,"observationType",parts.get(pCO2DX_Observation_type));
        maybeAdd(var,"insitu",parts.get(pCO2DX_In_situ_observation_X_manipulation_condition_X_response_variable));
        maybeAdd(var,"manipulationMethod",parts.get(pCO2DX_Manipulation_method));
        maybeAdd(var,"unit",parts.get(pCO2DX_Variable_unit));
        maybeAdd(var,"measured",parts.get(pCO2DX_Measured_or_calculated));
        maybeAdd(var,"calcMethod",parts.get(pCO2DX_Calculation_method_and_parameters));
        maybeAdd(var,"samplingInstrument",parts.get(pCO2DX_Sampling_instrument));
        maybeAdd(var,"analyzingInstrument",parts.get(pCO2DX_Analyzing_instrument));
        maybeAdd(var,"storageMethod",parts.get(pCO2DX_Storage_method));
        maybeAdd(var,"seawatervol",parts.get(pCO2DX_Seawater_volume));
        maybeAdd(var,"headspacevol",parts.get(pCO2DX_Headspace_volume));
        maybeAdd(var,"temperatureMeasure",parts.get(pCO2DX_Temperature_of_measurement));
        maybeAdd(var,"detailedInfo",parts.get(pCO2DX_Detailed_sampling_and_analyzing_information));
        Element gasDetector = new Element("gasDetector");
         maybeAdd(gasDetector,"manufacturer",parts.get(pCO2DX_Manufacturer_of_standard_gas));
         maybeAdd(gasDetector,"model",parts.get(pCO2DX_Model_of_the_gas_detector));
         maybeAdd(gasDetector,"resolution",parts.get(pCO2DX_Resolution_of_the_gas_detector));
         maybeAdd(gasDetector,"uncertainty",parts.get(pCO2DX_Uncertainty_of_the_gas_detector));
        var.addContent(gasDetector);
        Element standard = new Element("standard");
        maybeAdd(standard,"description",parts.get(pCO2DX_Standardization_technique_description));
        maybeAdd(standard,"frequency",parts.get(pCO2DX_Frequency_of_standardization));
        Element standardGas = new Element("standardGas");
            maybeAdd(standardGas,"manufacturer",parts.get(pCO2DX_Manufacturer_of_standard_gas));
            maybeAdd(standardGas,"concentration",parts.get(pCO2DX_Concentrations_of_standard_gas));
            maybeAdd(standardGas,"uncertainty",parts.get(pCO2DX_Uncertainties_of_standard_gas));
        standard.addContent(standardGas);
        var.addContent(standard);
        maybeAdd(var,"waterVaporCorrection",parts.get(pCO2DX_Water_vapor_correction_method));
        maybeAdd(var,"temperatureCorrection",parts.get(pCO2DX_Temperature_correction_method));
        maybeAdd(var,"co2ReportTemperature",parts.get(pCO2DX_at_what_temperature_was_pCO2_reported));
        maybeAdd(var,"uncertainty",parts.get(pCO2DX_Uncertainty));
        maybeAdd(var,"flag",parts.get(pCO2DX_Data_quality_flag_description));
        maybeAdd(var,"methodReference",parts.get(pCO2DX_Method_reference));
        maybeAdd(var,"researcherName",parts.get(pCO2DX_Researcher_Name));
        maybeAdd(var,"researcherInstitution",parts.get(pCO2DX_Researcher_Institution));
        var.addContent(new Element("internal").addContent("5"));
                                 
        root.addContent(var);
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
 */

    private void add_VARs(Document doc) {
        Collection<Map<String, String>> vars = metaItems.get(ElementType.VAR);
        if ( vars == null || vars.isEmpty()) {
            return;
        }
        Element root = doc.getRootElement();
        for (Map<String, String> parts : vars) {
            Element var = new Element("variable");
    		maybeAdd(var,"abbrev",parts.get(VarX_Variable_abbreviation_in_data_files));
    		maybeAdd(var,"fullname",parts.get(VarX_Full_variable_name));
    		maybeAdd(var,"observationType",parts.get(VarX_Observation_type));
    		maybeAdd(var,"insitu",parts.get(VarX_In_situ_observation_X_manipulation_condition_X_response_variable));
    		maybeAdd(var,"unit",parts.get(VarX_Variable_unit));
    		maybeAdd(var,"measured",parts.get(VarX_Measured_or_calculated));
    		maybeAdd(var,"calcMethod",parts.get(VarX_Calculation_method_and_parameters));
    		maybeAdd(var,"samplingInstrument",parts.get(VarX_Sampling_instrument));
    		maybeAdd(var,"analyzingInstrument",parts.get(VarX_Analyzing_instrument));
    		maybeAdd(var,"duration",parts.get(VarX_Duration));
    		maybeAdd(var,"detailedInfo",parts.get(VarX_Detailed_sampling_and_analyzing_information));
    		maybeAdd(var,"replicate",parts.get(VarX_Field_replicate_information));
    		maybeAdd(var,"uncertainty",parts.get(VarX_Uncertainty));
    		maybeAdd(var,"flag",parts.get(VarX_Data_quality_flag_description));
    		maybeAdd(var,"methodReference",parts.get(VarX_Method_reference));
    		maybeAdd(var,"biologicalSubject",parts.get(VarX_Biological_subject));
    		maybeAdd(var,"speciesID",parts.get(VarX_Species_Identification_code));
    		maybeAdd(var,"lifeStage",parts.get(VarX_Life_Stage));
    		maybeAdd(var,"researcherName",parts.get(VarX_Researcher_Name));
    		maybeAdd(var,"researcherInstitution",parts.get(VarX_Researcher_Institution));
            var.addContent(new Element("internal").addContent("0"));
          
            root.addContent(var);
        }
    }
    private static Instrument buildInstrument(String string, Map<String, String> parts) {
        // TODO Auto-generated method stub
        return null;
    }

    @SuppressWarnings("unused")
    private static void add_DIC(Document doc, Map<String, String> parts) {
        Variable var = buildVariable(parts);
        var.setFullName("Dissolved inorganic carbon");
    }
    @SuppressWarnings("unused")
    private static void add_TA(Document doc, Map<String, String> parts) {
        Variable var = buildVariable(parts);
        var.setFullName("Total alkalinity");
    }
    @SuppressWarnings("unused")
    private static void add_PH(Document doc, Map<String, String> parts) {
        Variable var = buildVariable(parts);
        var.setFullName("pH");
    }
    @SuppressWarnings("unused")
    private static void add_PCO2A(Document doc, Map<String, String> parts) {
        Variable var = buildVariable(parts);
        var.setFullName("pCO2 (fCO2) autonomous");
    }
    @SuppressWarnings("unused")
    private static void add_PCO2D(Document doc, Map<String, String> parts) {
        Variable var = buildVariable(parts);
        var.setFullName("pCO2 (fCO2) discrete");
    }
    @SuppressWarnings("unused")
    private static void add_VAR(Document doc, Map<String, String> parts) {
    }
//    @SuppressWarnings("unused")
//    private static void add_FUNDING(SDIMetadata sdi, Map<String, String> parts) {
//    }
            
    public static Document buildJDocument(InputStream is) throws Exception {
        // create the w3c DOM document from which JDOM is to be created
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        // we are interested in making it namespace aware.
        factory.setNamespaceAware(true);
        DocumentBuilder dombuilder = factory.newDocumentBuilder();
 
        org.w3c.dom.Document w3cDocument = dombuilder.parse(is);
 
        // the DOMBuilder uses the DefaultJDOMFactory to create the JDOM2 objects.
        DOMBuilder jdomBuilder = new DOMBuilder();
 
        // jdomDocument is the JDOM2 Object
        Document jdomDocument = jdomBuilder.build(w3cDocument);
 
        return jdomDocument;
    }
    
        // "test-data/WOAC metadata example for Linus_jh100918-fixed.xlsx"); // "test-data/SubmissionForm_OADS_v6.modified.xlsx"); // "test-data/SubmissionForm_OADS_v6.20181018.xlsx");

    /**
     * @param string
     * @return
     */
    private static boolean getBoolean(String string) {
        return string != null && string.length() > 0 &&
                string.toLowerCase().matches("y(es)?|t(rue)?");
    }

    /**
     * 
     */
    private static void usage() {
        System.out.println("Translate OADS Metadata Submission Excel Spreadsheet to OAP XML v0.");
        System.out.println("usage: excel2oap [excel_file] [output_file]");
        System.out.println("       If not specified, input and output will be from/to Standard.in/out.");
        System.out.println("       To read from Standard.in and output to a file, use '-' as input file name.");
    }

    private void addOtherStuff(Document doc) {
        add_DIC(doc);
        add_TA(doc);
        add_PH(doc);
        add_PCO2A(doc);
        add_PCO2D(doc);
        add_VARs(doc);
    }

    /**
     * @param sdi
     * @throws IOException 
     * @throws JDOMException 
     */
    private Document writeSdiMetadata(SDIMetadata sdi) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        OutputStreamWriter os = new OutputStreamWriter(baos);
        OcadsWriter oc = new OcadsWriter(_omitEmptyElements);
        oc.writeSDIMetadata(sdi, os);
        InputStream is = new ByteArrayInputStream(baos.toByteArray());
        Document doc = buildJDocument(is);
        return doc;
    }
    
    /**
     * @param args
     */
    public static void main(String[] args) {
        if ( args.length == 1 &&
             ( args[0].contains("-h") || args[0].equals("-?"))) {
            usage();
            System.exit(1);
        }
        File inputFile = null;
        String outFileName = null;
        File outputFile = null;
        
        if ( args.length >= 2 ) {
            outFileName = args[1];
        }
        if ( args.length >= 1 && ! "-".equals(args[0])) {
            inputFile = new File(args[0]);
        }
        if ( inputFile != null && outFileName == null ) {
            String filename = inputFile.getName();
            String shortname = filename.substring(0, filename.lastIndexOf('.'));
            String outfileName = shortname+".xml";
            File inParent = inputFile.getParentFile();
            outputFile = new File(inParent, outfileName);
        }
        boolean preserveEmpty = args.length >= 3 ? getBoolean(args[2]) : true;
        try ( InputStream in = inputFile != null ? new FileInputStream(inputFile) : System.in;
              OutputStream out = outputFile != null ? new FileOutputStream(outputFile) : System.out; ) {
            new PoiReader2( ! preserveEmpty).convert(in, out);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
