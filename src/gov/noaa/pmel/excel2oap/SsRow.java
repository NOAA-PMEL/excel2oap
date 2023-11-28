/**
 * 
 */
package gov.noaa.pmel.excel2oap;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import lombok.Builder;
import lombok.Getter;

/**
 * @author kamb
 *
 */
@Getter
@Builder(toBuilder=true)
public class SsRow {
    
    public static final Logger logger = LogManager.getLogger(SsRow.class);
    
    private static String nbsp = String.valueOf((char) 160);
    
    private int _num;
    private final String _name;
    private final String _value;
    
    public SsRow(int rowNum, String rowName, String rowValue) {
        _num = rowNum;
        _name = rowName.trim();
        String t_value = rowValue;
        if ( rowValue != null ) {
            if ( rowValue.contains(nbsp)) {
                logger.debug("Found nbsp at row:" + rowNum);
                t_value = rowValue.replaceAll(nbsp, " ");
            }
            t_value = t_value.trim();
        }
        _value = t_value;
    }
    @Override
    public String toString() {
        return "row:"+_num+", name:"+_name+", value: " + _value;
    }
}
