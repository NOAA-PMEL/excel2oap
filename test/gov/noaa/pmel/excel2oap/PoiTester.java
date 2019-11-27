/**
 * 
 */
package gov.noaa.pmel.excel2oap;

/**
 * @author kamb
 *
 */
public class PoiTester {

    /**
     * @param args
     */
    public static void main(String[] args) {
        try {
            String[] debugArgs = new String[] { // "/Users/kamb/workspace/oap-git/OAPDashboard/UploadDashboard/test-data/NCEI/0123400-crab/SubmissionForm_OADS_Hemocytes.xlsx" };
                                                "/Users/kamb/workspace/oa_dashboard_test_data/NCEI/WCOA16/WCOA2016_Hydro_metadata_12202017.xlsx" };
            PoiReader2.main(debugArgs);
        } catch (Exception ex) {
            ex.printStackTrace();
            // TODO: handle exception
        }

    }

}
