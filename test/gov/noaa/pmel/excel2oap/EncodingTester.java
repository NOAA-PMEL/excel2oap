/**
 * 
 */
package gov.noaa.pmel.excel2oap;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;

/**
 * @author kamb
 *
 */
public class EncodingTester {

    /**
     * @param args
     */
    private static void fileTest() {
        try {
            File inputFile = new File("/Users/kamb/workspace/oa_dashboard_test_data/people/julian/WOAC_metadata-fixed-strict.xlsx");
            File isoOutputFile = new File("ISO_8859_1");
            File winOutputFile = new File("windows_1252");
            File utf8OutputFile = new File("utf-8");
            byte[] inputBytes = readFile(inputFile);
            writeFile(isoOutputFile, "ISO_8859_1", inputBytes);
            writeFile(winOutputFile, "windows-1252", inputBytes);
            writeFile(utf8OutputFile, "utf-8", inputBytes);
        } catch (Exception ex) {
            ex.printStackTrace();
            // TODO: handle exception
        }
    }

    /**
     * @param winOutputFile
     * @param string
     * @param inputBytes 
     */
    private static void writeFile(File outputFile, String charsetName, byte[] contentBytes) throws Exception {
        FileOutputStream fos = new FileOutputStream(outputFile);
        OutputStreamWriter out = new OutputStreamWriter(fos, Charset.forName(charsetName));
        out.write(new String(contentBytes));
    }

    /**
     * @param inputFile
     * @return
     * @throws Exception 
     */
    private static byte[] readFile(File inputFile) throws Exception {
        byte[] buffer = new byte[8192];
        try ( FileInputStream fis = new FileInputStream(inputFile);
              ByteArrayOutputStream baos = new ByteArrayOutputStream((int)inputFile.length()); ) {
            int read = 0;
            while ((read = fis.read(buffer)) > 0) {
                baos.write(buffer, 0, read);
            }
            buffer = baos.toByteArray();
        }
        return buffer;
    }

    private static void stringTest() {
        String germanStr = "Entwickeln Sie mit Vergnügen";
        String winStr = "±0.1%";
        String csvStr = "76,DIC: Uncertainty,±2 umol/kg,22.14";
        String oadsStr = ">�2 umol/kg</";
    }
    public static void main(String[] args) {
        try {
            
        } catch (Exception ex) {
            ex.printStackTrace();
            // TODO: handle exception
        }
    }
}
