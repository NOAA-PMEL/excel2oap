/**
 * 
 */
package scratch;

import java.util.ArrayList;
import java.util.List;

/**
 * @author kamb
 *
 */
public class Splitter {

    private static String[] split(String listString, char separator) {
        List<String> pieces = new ArrayList<>();
        if ( listString != null && ! listString.isEmpty()) {
            do {
                String piece = "";
                int idx = listString.indexOf(separator);
                if ( idx < 0 ) {
                    piece = listString;
                    listString = "";
                } else if ( idx < listString.length()) {
                    piece = listString.substring(0, idx);
                    listString = idx < listString.length() - 1 ?
                                    listString.substring(idx+1) :
                                    "";
                }
                if ( !piece.isEmpty()) {
                    pieces.add(piece);
                }
            } while ( ! listString.isEmpty());
        }
        return pieces.toArray(new String[pieces.size()]);
    }
    
    private static void dump(String s, String[] pieces) {
        System.out.print("<" + s + "["+pieces.length+"]>");
        String space = "";
        for (String piece : pieces) {
            System.out.print(space + piece );
            space = " ";
        }
        System.out.println("<");
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        String s0 = "one;;two;";
        String[] zeros = s0.split(";" );
        System.out.println(zeros);
        String s1 = "";
        String s2 = "one";
        String s3 = "one;two";
        String s4 = "one;two;three";
        
        String o1 = ";";
        String o2 = ";one";
        String o3 = "one;two;";
        String o4 = "one;two;three;4";
        
        dump(s1, split(s1, ';'));
        dump(s2, split(s2, ';'));
        dump(s3, split(s3, ';'));
        dump(s4, split(s4, ';'));
        dump(o1, split(o1, ';'));
        dump(o2, split(o2, ';'));
        dump(o3, split(o3, ';'));
        dump(o4, split(o4, ';'));

    }

}
