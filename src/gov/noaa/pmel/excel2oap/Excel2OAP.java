/**
 * 
 */
package gov.noaa.pmel.excel2oap;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import gov.noaa.ncei.oads.xml.v_a0_2_2s.OadsMetadataDocumentType;
import gov.noaa.pmel.excel2oap.ifc.SSParser;
import gov.noaa.pmel.excel2oap.ifc.SSReader;
import gov.noaa.pmel.excel2oap.ifc.XmlBuilder;
import gov.noaa.pmel.excel2oap.ocads.OcadsHandler;
import gov.noaa.pmel.excel2oap.ocads.OcadsKeys;
import gov.noaa.pmel.excel2oap.sdg.SDG_14_3_Keys;
import gov.noaa.pmel.excel2oap.sdg.SdgHandler;
import gov.noaa.pmel.excel2oap.sdg.socat.SocatHandler;
import gov.noaa.pmel.excel2oap.sdg.socat.SocatKeys;
import gov.noaa.pmel.excel2oap.xml.OadsXmlBuilder;
import gov.noaa.pmel.excel2oap.xml.OcadsXmlBuilder;
import gov.noaa.pmel.excel2oap.xml.XmlFlavor;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

/**
 * @author kamb
 *
 */
public class Excel2OAP {
    
    private static final Logger logger = LogManager.getLogger(Excel2OAP.class);

    private boolean _omitEmptyElements = true;
    
    private SpreadSheetFlavor _ssFlavor;

    public Excel2OAP() {
    }
    public Excel2OAP(boolean omitEmptyElements) {
        this();
        _omitEmptyElements = omitEmptyElements;
    }
    @SuppressWarnings("serial")
    public Excel2OAP(SpreadSheetFlavor ssType) {
        this();
        _ssFlavor = ssType;
    }
    
    public static void ConvertExcelToOADS_xml(InputStream excelInStream, OutputStream outputXmlStream) throws Exception {
        new Excel2OAP().convert(excelInStream, outputXmlStream, XmlFlavor.OADS);
    }
    
    public static OadsMetadataDocumentType ConvertExcelToOADS_doc(InputStream excelInputStream) throws Exception {
        return new Excel2OAP().convertToOADS_doc(excelInputStream);
    }
    
    @Deprecated
    public static void ConvertExcelToOCADS_xml(InputStream excelInStream, OutputStream outputXmlStream) throws Exception {
        new Excel2OAP().convert(excelInStream, outputXmlStream, XmlFlavor.OCADS);
    }
    
    public OadsMetadataDocumentType convertToOADS_doc(InputStream excelInStream) throws Exception {
        SSReader reader = new SpreadSheetReader();
        List<SsRow> rows = reader.extractFileRows(excelInStream);
        SpreadSheetType ssType = guessSStype(rows);
        SSParser parser = getSpreadSheetProcessor(ssType, _omitEmptyElements);
        parser.processRows(rows);
        OadsXmlBuilder xmlBuilder = (OadsXmlBuilder)getXmlBuilder(XmlFlavor.OADS, parser);
        xmlBuilder.buildDocument(parser);
        OadsMetadataDocumentType doc =  xmlBuilder.getDocuemnt();
        return doc;
    }
    
    public void convert(InputStream excelInStream, OutputStream outputXmlStream, XmlFlavor xmlFlavor) throws Exception {
        SSReader reader = new SpreadSheetReader();
        List<SsRow> rows = reader.extractFileRows(excelInStream);
        SpreadSheetType ssType = guessSStype(rows);
        SSParser parser = getSpreadSheetProcessor(ssType, _omitEmptyElements);
        parser.processRows(rows);
        XmlBuilder xmlBuilder = getXmlBuilder(xmlFlavor, parser);
        xmlBuilder.buildDocument(parser);
        xmlBuilder.outputXml(outputXmlStream);
    }
        
    private static SSParser getSpreadSheetProcessor(SpreadSheetType ssType, boolean omitEmpty) {
        switch (ssType) {
            case OCADS:
                return new OcadsHandler(omitEmpty, new OcadsKeys());
            case SDG_14_3_1:
                return new SdgHandler(omitEmpty, new SDG_14_3_Keys());
            case SOCAT:
                return new SocatHandler(omitEmpty, new SocatKeys());
            default:
                throw new RuntimeException("Unknown SpreadSheetType:" + ssType);
        }
    }
    
    /**
     * @param xmlFlavor
     * @return
     */
    private XmlBuilder getXmlBuilder(XmlFlavor xmlFlavor, SSParser parser) {
        switch (xmlFlavor) {
            case OADS:
                return OadsXmlBuilder.GetOadsXmlBuilder(parser, _omitEmptyElements);
            case OCADS:
                return getOcadsXmlBuilder(parser);
            default:
                throw new RuntimeException("Unknown XmlFlavor: " + xmlFlavor);
        }
    }
    /**
     * @param parser
     * @param _omitEmptyElements2
     * @return
     */
    private XmlBuilder getOcadsXmlBuilder(SSParser parser) {
        return new OcadsXmlBuilder(parser.getMultiItemFields(),
                                   parser.getSingleFields(),
                                   parser.getSpreadSheetKeys(),
                                   _omitEmptyElements);
    }
    /**
     * @param rows
     * @return
     */
    private static SpreadSheetType guessSStype(List<SsRow> rows) {
        SpreadSheetType sst = SpreadSheetType.fromSheet(rows);
        return sst;
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
        System.out.println("usage: excel2oap [excel_file] [output_file] [xml_flavor]");
        System.out.println("       If not specified, input and output will be from/to Standard.in/out.");
        System.out.println("       To read from Standard.in and output to a file, use '-' as input file name.");
        System.out.println("       If input file is specified, but output file is not, output will be [input_file_base].xml");
        System.out.println("       Use '--' as output file name if you want the above behavior but want to specify the xml flavor.");
        System.out.println("       XML flavors are: oads (default) and ocads.");
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
        String xml = args.length >= 3 ? args[2] : "OADS";
        XmlFlavor xFlavor = XmlFlavor.valueOf(xml.toUpperCase());
        File inputFile = null;
        String outFileName = null;
        File outputFile = null;
        
        if ( args.length >= 2 && ! args[1].equals("--")) {
            outFileName = args[1];
        }
        if ( args.length >= 1 && ! "-".equals(args[0])) {
            inputFile = new File(args[0]);
        }
        if ( inputFile != null && outFileName == null ) {
            String filename = inputFile.getName();
            String shortname = filename.substring(0, filename.lastIndexOf('.'));
            String outfileName = shortname+"."+xFlavor.name()+".xml";
            File inParent = inputFile.getParentFile();
            outputFile = new File(inParent, outfileName);
        } else if ( outFileName != null ) {
            outputFile = new File(outFileName);
        }
        boolean preserveEmpty = args.length >= 4 ? getBoolean(args[3]) : false;
        try ( InputStream in = inputFile != null ? new FileInputStream(inputFile) : System.in;
              OutputStream out = outputFile != null && ! "-".equals(outFileName)? 
                      new FileOutputStream(outputFile) : 
                      System.out; ) {
            new Excel2OAP( ! preserveEmpty).convert(in, out, xFlavor);
//            OadsMetadataDocumentType doc = Excel2OAP.ConvertExcelToOADS_doc(in);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
