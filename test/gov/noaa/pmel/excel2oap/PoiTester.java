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
        boolean runTest = false;
        boolean testDates = false;
        try {
            if ( testDates ) {
                testDates();
                System.exit(0);
            }
            if ( runTest ) {
                String standardExcel = "/Users/kamb/workspace/oa_dashboard_test_data/WCOA/WCOA2011/WCOA11-01-06-2015_metadata-NCEI.xlsx";
                String fixedExcel = "/Users/kamb/workspace/oa_dashboard_test_data/WCOA/WCOA2011/WCOA11-01-06-2015_metadata-FIXED.xlsx";
                String strictExcel = "/Users/kamb/workspace/oa_dashboard_test_data/WOAC_metadata-fixed-strict.xlsx";
                String csvFile = "/Users/kamb/workspace/oa_dashboard_test_data/cosca/UploadTest.csv" ;
                String xlsFile = "/Users/kamb/workspace/oa_dashboard_test_data/cosca/UploadTest.xlsx" ;
                String julienOrig = "/Users/kamb/workspace/oa_dashboard_test_data/WOAC_metadata_example-orig.xlsx";
                String julienStrict = "/Users/kamb/workspace/oa_dashboard_test_data/WOAC_metadata-fixed-strict.xlsx";
                String daleStrict = "/Users/kamb/Downloads/WOAC_metadata-fixed-strict.xlsx";
                String[] testFiles = new String[] { 
                        standardExcel, 
                        fixedExcel, 
                        strictExcel, 
                        csvFile,
                        xlsFile,
                        julienOrig,
                        julienStrict,
                        daleStrict
                    };
                for (String file : testFiles) {
                    try {
                        System.out.println(file);
                        Excel2OAP.main(new String[] { file } );
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            } else {
                String[] debugArgs = new String[] { // "/Users/kamb/workspace/oap-git/OAPDashboard/UploadDashboard/test-data/NCEI/0123400-crab/SubmissionForm_OADS_Hemocytes.xlsx" };
//                                                "/Users/kamb/workspace/oa_dashboard_test_data/NCEI/WCOA16/WCOA2016_Hydro_metadata_12202017.xlsx" };
//                                                "/Users/kamb/workspace/oa_dashboard_test_data/33RO/WCOA2016_Hydro_metadata_12202017.xlsx" };
//                                                "/Users/kamb/workspace/oa_dashboard_test_data/people/julian/WOAC_metadata_example-orig.xlsx", "--", "ocads" };
//                                                "/Users/kamb/workspace/oa_dashboard_test_data/WCOA/WCOA2011/WCOA11-01-06-2015_metadata-FIXED.xlsx", "--", "ocads" }; // "WCOA2011.out", "ocads"};
//                                                "/Users/kamb/workspace/excel2oap/test-data/SubmissionForm_OADS_v6-poisonTest.xlsx" };
//                                                "/Users/kamb/workspace/excel2oap/test-data/WOAC metadata example for Linus_jh100918-fixed.xlsx" };
//                                                "/Users/kamb/workspace/excel2oap/test-data/1612019_SDG14_3_1_Metadata_submission_template_testing.xlsx", "oads_no_empties.xml", "oads" };
                                                "/Users/kamb/workspace/excel2oap/test-data/1612019_SDG14_3_1_Metadata_submission_template_FULL_TEST.xlsx", "--", "oads" };
//                                                "/Users/kamb/workspace/excel2oap/test-data/Metadata_template-example-for-SOCAT-FULL_TEST.xlsx", "--", "oads" };
//                                                "/Users/kamb/workspace/oa_dashboard_test_data/sophie/SubmissionForm_carbon_v1_prawler2017.xlsx" };
//                                                "/Users/kamb/workspace/excel2oap/test-data/cosca_UploadTest-test.csv" };
//                                                "/Users/kamb/workspace/oa_dashboard_test_data/WOAC_metadata-fixed-strict.xlsx" };
//                                                "/Users/kamb/workspace/oa_dashboard_test_data/aoml/33GG20170516-GU1701_GU1702-Metadata.xlsx", "--", "oads" };
//                                                "/Users/kamb/workspace/oa_dashboard_test_data/people/akozyr/740H20200119_Metadata-fixed_2.xlsx"};  // SOCAT / SDG 14.3.1
//                                                "/Users/kamb/workspace/oa_dashboard_test_data/people/akozyr/740H20200119_Metadata-fixed_2.xlsx", "--", "oads"};  // SOCAT / SDG 14.3.1
//                                                "/Users/kamb/workspace/oa_dashboard_test_data/people/akozyr/740H20200119_Metadata-testing.xlsx"};  // SOCAT / SDG 14.3.1
//                                                "/Users/kamb/workspace/oa_dashboard_test_data/metadata/UploadTest-salinityfieldsfilled.csv" };
//                                                "/Users/kamb/oxy-work/OMD/OAPMetadataEditor/test-data/1612019_SDG14_3_1_Metadata_submission_TA_Nuka_2017_2018_2019_2020.xlsx", "--", "oads" };
//                                                "/Users/kamb/Downloads/WOAC_metadata-fixed-strict.xlsx" };
                Excel2OAP.main(debugArgs);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            // TODO: handle exception
        }

    }

    private static void testDates() {
    /*
        String mmddyyyy = "09-22-2006";
        String mmddyy = "09-22-06";
        String ddmmyyyy = "22-09-2006";
        String ddmmyy = "22-09-06";
        String yyyymmdd = "2006-09-22";
        String inconclusive = "04-05-06";
        String alex = "2020-02-29";
        System.out.println(mmddyyyy+":"+PoiReader2.tryDatestamp(mmddyyyy));
        System.out.println(mmddyy+":"+PoiReader2.tryDatestamp(mmddyy));
        System.out.println(ddmmyyyy+":"+PoiReader2.tryDatestamp(ddmmyyyy));
        System.out.println(ddmmyy+":"+PoiReader2.tryDatestamp(ddmmyy));
        System.out.println(yyyymmdd+":"+PoiReader2.tryDatestamp(yyyymmdd));
        System.out.println(inconclusive+":"+PoiReader2.tryDatestamp(inconclusive));
        System.out.println(alex+":"+PoiReader2.tryDatestamp(alex));
     */
    }

}
