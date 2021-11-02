/**
 * 
 */
package gov.noaa.pmel.excel2oap.ifc;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import gov.noaa.pmel.excel2oap.SsRow;

/**
 * @author kamb
 *
 */
public interface SSReader {
    public List<SsRow> extractFileRows(InputStream inStream) throws Exception, IOException ;
}
