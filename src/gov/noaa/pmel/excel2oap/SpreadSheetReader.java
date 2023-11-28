/**
 * 
 */
package gov.noaa.pmel.excel2oap;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
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
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.xmlbeans.impl.values.XmlValueOutOfRangeException;

import com.example.OoXmlStrictConverter;

import gov.noaa.pmel.excel2oap.ifc.SSReader;
import gov.noaa.pmel.tws.util.StringUtils;

/**
 * @author kamb
 *
 */
public class SpreadSheetReader implements SSReader {

    private static final Logger logger = LogManager.getLogger(SpreadSheetReader.class);

    private static final int MAX_ERRORS = 20;

    private Charset _charset;
    
    public SpreadSheetReader() {
        this(Charset.defaultCharset());
    }
    public SpreadSheetReader(Charset charset) {
        _charset = charset;
    }
    public List<SsRow> extractFileRows(InputStream inStream) throws Exception, IOException {
        List<SsRow> rows;
        ByteArrayOutputStream copyOut = new ByteArrayOutputStream();
        IOUtils.copy(inStream, copyOut);
        InputStream inCopy = new ByteArrayInputStream(copyOut.toByteArray());
        try ( InputStream bufIn = FileMagic.prepareToCheckMagic(inCopy); ) {
              FileMagic fm = FileMagic.valueOf(bufIn);
            switch (fm) {
                case OLE2:
                case OOXML:
                    rows = extractExcelRows(inCopy);
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
        
    // 
    // Strict format processing from OAPDashboard/ExcelFileReader
    // 
    final int MAX_PEEK = 8192 * 2;
    final String STRICT_NS_1 = "http://purl.oclc.org/ooxml/spreadsheetml/main";

    private List<SsRow> extractExcelRows(InputStream inStream) throws Exception, IOException {
        List<SsRow> rows = new ArrayList<>();
        DataFormatter df = new DataFormatter();
        InputStream useStream = inStream.markSupported() ? inStream : new BufferedInputStream(inStream);
        Workbook workbook;
//        try {
            // Strict format processing from OAPDashboard/ExcelFileReader
            int available = useStream.available() - 64;
            int maxMark = Math.min(available, MAX_PEEK);
            useStream.mark(maxMark);
            boolean strict = checkForStrict(useStream);
            useStream.reset();
            if ( ! strict ) {
                workbook = WorkbookFactory.create(useStream);
            } else {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                try {
                    OoXmlStrictConverter.Transform(useStream, baos);
//                    File looseFile = File.createTempFile("sdis_loose_", ".xlsx");
//                    OoXmlStrictConverter.Transform(strictFile.getAbsolutePath(), looseFile.getAbsolutePath());
                    byte[] bytes = baos.toByteArray();
//                    try ( FileOutputStream fos = new FileOutputStream("loose.xlsx")) {
//                    fos.write(bytes);
//                    fos.flush();
//                    }
                    ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
                    useStream = bais.markSupported() ? bais : new BufferedInputStream(bais);
                    workbook = WorkbookFactory.create(useStream);
                } catch (Exception e2) {
                    e2.printStackTrace();
                    throw new IllegalStateException("Failed to create ExcelFileReader:" + e2);
                } finally {
                    try { useStream.close(); }
                    catch (Throwable t) {
                        // ignore
                    }
                }
            }
            Sheet sheet = workbook.getSheetAt(0);
            int rowNum = 0;
            int numErrors = 0;
            for (Row row : sheet) {
                rowNum += 1;
                int itemNo = -999;
                Cell numCell = null;
                try {
                    numCell = row.getCell(0);
                    if ( numCell != null ) {
                        itemNo = (int)numCell.getNumericCellValue(); 
                    }
                } catch (IllegalStateException ex) {
                    String rawValue = numCell.getStringCellValue();
                    if ( !"SOCAT".equals(rawValue.toUpperCase())) {
                        logger.info("Not a valid metadata row at " + rowNum + " with cell 0 value: " + numCell);
                        if ( ++numErrors > MAX_ERRORS ) {
                            throw new IllegalStateException("Too many errors. Aborting file read.");
                        }
                        continue;
                    } else {
                        logger.info("Processing SOCAT row at " + rowNum);
                    }
                    itemNo = -1;
                } catch (NullPointerException npe) {
                    logger.info("NPE at " + rowNum + " with cell 0 value: " + numCell);
                    break;
                }
                Cell nameCell = row.getCell(1);
                if ( nameCell == null) {
                    logger.info("Null name cell at row: "+ rowNum + "[#"+itemNo+"]");
                    continue;
                }
                String rowName = nameCell.getStringCellValue();
                logger.trace(rowName + ":");
                Cell vcell = row.getCell(2);
                String rowValue = "";
//                vcell.setCellType(CellType.STRING);
                if ( vcell != null ) {
                    try {
                        if ( vcell.getCellType().equals(CellType.STRING)) {
                            rowValue = vcell.getStringCellValue();
                        } else if ( DateUtil.isCellDateFormatted(vcell) && 
                                    rowName.toLowerCase().indexOf("date") >= 0 ) {
                            Date d = vcell.getDateCellValue();
                            rowValue = d != null ? formatDate(d) : "";
                            String altVal = df.formatCellValue(vcell);
                            logger.trace("(alt:"+altVal+")");
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
                    // org.apache.xmlbeans.impl.values.XmlValueOutOfRangeException: 
                    // string value 'd' is not a valid enumeration value for ST_CellType in namespace 
                    // http://schemas.openxmlformats.org/spreadsheetml/2006/main
                    } catch (XmlValueOutOfRangeException ex) {
                        logger.info(ex);
                        XSSFCell xsc = (XSSFCell)vcell;
                        rowValue = xsc.getRawValue();
                        if ( ex.getMessage().contains("string value 'd' is not a valid enumeration value")) {
                            String[] parts = rowValue.split("[/ -]");
                            if ( parts.length == 3) {
                                rowValue = parts[1] + "/" + parts[2] + "/" + parts[0];
                            }
                        }
                    }
                    logger.trace(rowValue);
                    String encoded = new String(rowValue.getBytes(), "utf-8");
                    if ( ! rowValue.equals(encoded)) {
                        System.out.println(rowNum + "== " + rowValue);
                        System.out.println("++ " + encoded);
                    }
                }
                SsRow orow = new SsRow(itemNo,
                                           row.getCell(1).getStringCellValue(), 
                                           rowValue);
//                logger.debug(rowNum + ": " + orow);
                rows.add(orow);
            }
//        }
            workbook.close();
        return rows;
    }
    
    /**
     * @param inStream
     * @return
     * @throws IOException 
     * @throws XMLStreamException 
     */
    // Strict format processing from OAPDashboard/ExcelFileReader
    private boolean checkForStrict(InputStream inStream) throws IOException {
        boolean isStrict = false;
        boolean stop = false;
        XMLInputFactory XIF = XMLInputFactory.newInstance();
        ZipInputStream zis = new ZipInputStream(inStream);
        ZipEntry ze;
        while( !isStrict && !stop && (ze = zis.getNextEntry()) != null) {
            FilterInputStream filterIs = new FilterInputStream(zis) {
                @Override
                public void close() throws IOException {
                }
            };
            String zeName = ze.getName();
//            logger.debug("ZipEntry " + zeName);
            if ( "xl/workbook.xml".equals(zeName)) {
//                logger.info("Processing workbook.xml, then stopping.");
                stop = true;
            }
            if(isXml(ze.getName())) {
                try {
                    XMLEventReader xer = XIF.createXMLEventReader(filterIs);
                    while(xer.hasNext()) {
                        XMLEvent xe = xer.nextEvent();
                        if ( xe.isStartElement()) {
                            StartElement se = xe.asStartElement();
                            QName qn = se.getName();
                            String ns = qn.getNamespaceURI();
                            if ( STRICT_NS_1.equals(ns)) {
                                isStrict = true;
                                break;
                            }
                        }
                    }
                } catch (XMLStreamException xsx) {
                    throw new IOException("Exception parsing document XML:"+xsx.getMessage(), xsx);
                }
            }
        }
//        logger.info("Found strict: " + isStrict);
        return isStrict;
    }
    private boolean isXml(final String fileName) {
        if ( ! StringUtils.emptyOrNull(fileName)) {
            int pos = fileName.lastIndexOf(".");
            if(pos != -1) {
                String ext = fileName.substring(pos + 1).toLowerCase();
                return ext.equals("xml") || ext.equals("vml") || ext.equals("rels");
            }
        }
        return false;
    }

    private List<SsRow> tryDelimited(InputStream inStream) throws Exception, IOException {
        List<SsRow> rows = new ArrayList<>();
//        byte[] peak = IOUtils.peekFirstNBytes(inStream, 512);
        int available = inStream.available();
        int peakLength = available > 0 ? Math.min(available, 8192) : 8192;
            byte[] bytes = IOUtils.peekFirstNBytes(inStream, peakLength);
            String peak = new String(bytes, Charset.forName("UTF8"));
            char spacer = lookForDelimiter(peak);
            CSVFormat format = CSVFormat.EXCEL.withIgnoreSurroundingSpaces()
                    .withIgnoreEmptyLines()
                    .withQuote('"')
//                        .withTrailingDelimiter()
//                        .withCommentMarker('#')
                    .withDelimiter(spacer);
//            String charset = "CP1252";
    		try ( InputStreamReader isr = new InputStreamReader(inStream, _charset); 
    		        CSVParser dataParser = new CSVParser(isr, format); ) {
                int rowNum = 0;
                int numErrors = 0;
                for (CSVRecord record : dataParser) {
                    rowNum += 1;
                    int itemNo;
                    String numCell = record.get(0);
                    try {
                        itemNo = Integer.parseInt(numCell);
                    } catch (Exception ex) {
                        logger.debug("Not a valid metadata row at " + rowNum + " with cell 0 value: " + numCell);
                        if ( !"SOCAT".equals(numCell)) {
                            if ( ++numErrors > MAX_ERRORS ) {
                                throw new IllegalStateException("Too many errors. Aborting file read.");
                            }
                            continue;
                        }
                        itemNo = -1;
                    }
                    String rowName = record.get(1);
                    if ( rowName == null) {
                        logger.info("Null name cell at row: "+ rowNum + "[#"+itemNo+"]");
                        continue;
                    }
                    String vcell = record.get(2);
                    String rowValue = vcell;
                    String encoded = new String(rowValue.getBytes(), "utf-8");
                    if ( ! rowValue.equals(encoded)) {
                        System.out.println("== " + rowValue);
                        System.out.println("++ " + encoded);
                    }
                    SsRow orow = new SsRow(itemNo,
                                               rowName,
                                               rowValue);
//                    logger.debug(rowNum + ": " + orow);
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
    private char lookForDelimiter(String peak) {
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
    private Integer count(String peak, char c) {
        int count = 0;
        for (int i = 0; i < peak.length(); i++) {
            if ( peak.charAt(i) == c) {
                count += 1;
            }
        }
        return new Integer(count);
    }

    // default format month-day-year
    private static String formatDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        String dateStr = sdf.format(date);
        return dateStr;
    }
    
    private static void test(File inputFile, Charset charset) throws Exception {
        try ( InputStream in = new FileInputStream(inputFile); ) {
            SpreadSheetReader reader = new SpreadSheetReader(charset);
            List<SsRow> rows = reader.extractFileRows(in);
            System.out.println(rows.size());
        }
    }
    /**
     * @param args
     */
    public static void main(String[] args) {
        try {
            File macFile = new File("/Users/kamb/workspace/oa_dashboard_test_data/WCOA/WCOA2011/WCOA11-01-06-2015_metadata-FIXED_mac.csv");
            File winFile = new File("/Users/kamb/workspace/oa_dashboard_test_data/WCOA/WCOA2011/WCOA11-01-06-2015_metadata-FIXED_win.csv");
            Charset utf = Charset.defaultCharset();
            Charset win = Charset.forName("windows-1252");
         // Charset.forName("windows-1252")); // Charset.defaultCharset());           
            test(macFile, utf);
            test(macFile, win);
            test(winFile, utf);
            test(winFile, win);
        } catch (Exception ex) {
            ex.printStackTrace();
            // TODO: handle exception
        }

    }

}
