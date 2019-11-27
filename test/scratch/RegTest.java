/**
 * 
 */
package scratch;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author kamb
 *
 */
public class RegTest {

    /**
     * @param args
     */
    public static void main(String[] args) {
        String str1 = "Investigator-1 name";
        String str2 = "Var1: Measured or calculated";
//        String str3 = "pCO2A: Frequency of standardization";
//        String str2 = "DIC: Variable abbreviation in data files";
        String str3 = "DIC Poisoning correction description";
        String matchPattern1 = "((Investigator|Platform|Var|DIC|TA|pH|pCO2A|pCO2D)(?:.)?(\\d))(?:\\:?)(.*)";
        String matchPattern2 = "((Investigator|Platform|Var|DIC|TA|pH|pCO2A|pCO2D))(?:.*?)( )(.*)";
        Matcher m11 = Pattern.compile(matchPattern1).matcher(str1);
        Matcher m21 = Pattern.compile(matchPattern2).matcher(str1);
        Matcher m12 = Pattern.compile(matchPattern1).matcher(str2);
        Matcher m22 = Pattern.compile(matchPattern2).matcher(str2);
        Matcher m13 = Pattern.compile(matchPattern1).matcher(str3);
        Matcher m23 = Pattern.compile(matchPattern2).matcher(str3);
        if ( m11.matches()) {
            for(int i=0; i<=m11.groupCount(); i++) { System.out.print(m11.group(i)+" || "); } System.out.println();
        } else {
            System.out.println("m11:"+m11.matches());
        }
        if ( m21.matches()) {
            for(int i=0; i<=m21.groupCount(); i++) { System.out.print(m21.group(i)+" || "); } System.out.println();
        } else {
            System.out.println("m21:"+m21.matches());
        }
        if ( m12.matches()) {
            for(int i=0; i<=m12.groupCount(); i++) { System.out.print(m12.group(i)+" || "); } System.out.println();
        } else {
            System.out.println("m12:"+m12.matches());
        }
        if ( m22.matches()) {
            for(int i=0; i<=m22.groupCount(); i++) { System.out.print(m22.group(i)+" || "); } System.out.println();
        } else {
            System.out.println("m22:"+m22.matches());
        }
        if ( m13.matches()) {
            for(int i=0; i<=m13.groupCount(); i++) { System.out.print(m13.group(i)+" || "); } System.out.println();
        } else {
            System.out.println("m13:"+m13.matches());
        }
        if ( m23.matches()) {
            for(int i=0; i<=m23.groupCount(); i++) { System.out.print(m23.group(i)+" || "); } System.out.println();
        } else {
            System.out.println("m23:"+m23.matches());
        }
        
    }

}
