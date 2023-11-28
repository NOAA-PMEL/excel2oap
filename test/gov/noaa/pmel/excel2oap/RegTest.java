/**
 * 
 */
package gov.noaa.pmel.excel2oap;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * @author kamb
 *
 */
public class RegTest {

    static void checkMulti() {
        try {
            String regex = "((PI|Investigator|Platform|Var|Data submitter|Cruise ID|fCO2/pCO2/xCO2|fCO2|pCO2|xCO2|Temperature|Salinity|Depth))(?:.*?)( )(.*)";
            String line = "fCO2/pCO2/xCO2: Analyzing information with citation (SOP etc)";
            Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(line);
            if (matcher.matches()) {
                System.out.println("Yay!");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    static void checkPlatform() {
        try {
            String regex = "((Platform)(?:.)?(\\d))(.*)";
            String line1 = "Platform name";
            String line2 = "Platform-1 owner";
            Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(line1);
            System.out.print("line1: ");
            if (matcher.matches()) {
                System.out.println("Yay!");
            } else { System.out.println(); }
            matcher = pattern.matcher(line2);
            System.out.print("line2: ");
            if (matcher.matches()) {
                System.out.println("Yay!");
                System.out.println("group 0:" + matcher.group(0));
                System.out.println("group 1:" + matcher.group(1));
                System.out.println("group 2:" + matcher.group(2));
                System.out.println("group 3:" + matcher.group(3));
                System.out.println("group 4:" + matcher.group(4));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            // TODO: handle exception
        }
    }
    
    /**
     * @param args
     */
    public static void main(String[] args) {
        try {
            checkPlatform();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

}
