/**
 * 
 */
package gov.noaa.pmel.excel2oap.xml;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBException;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
//import org.jdom2.Document;
//import org.jdom2.Element;

import gov.noaa.ncei.oads.xml.v_a0_2_2s.OadsMetadataDocumentType;
import gov.noaa.ncei.oads.xml.v_a0_2_2s.OadsMetadataDocumentType.OadsMetadataDocumentTypeBuilder;
import gov.noaa.ncei.oads.xml.v_a0_2_2s.OrderedStringElementType;
import gov.noaa.ncei.oads.xml.v_a0_2_2s.PersonContactInfoType;
import gov.noaa.ncei.oads.xml.v_a0_2_2s.PersonNameType;
import gov.noaa.ncei.oads.xml.v_a0_2_2s.PersonReferenceType;
import gov.noaa.ncei.oads.xml.v_a0_2_2s.PersonType;
import gov.noaa.ncei.oads.xml.v_a0_2_2s.PhVariableType;
import gov.noaa.ncei.oads.xml.v_a0_2_2s.PhVariableType.PhVariableTypeBuilder;
import gov.noaa.ncei.oads.xml.v_a0_2_2s.PlatformType;
import gov.noaa.ncei.oads.xml.v_a0_2_2s.PoisonType;
import gov.noaa.ncei.oads.xml.v_a0_2_2s.QcFlagInfoType;
import gov.noaa.ncei.oads.xml.v_a0_2_2s.SpatialExtentsType;
import gov.noaa.ncei.oads.xml.v_a0_2_2s.SpatialLocationType;
import gov.noaa.ncei.oads.xml.v_a0_2_2s.StandardGasType;
import gov.noaa.ncei.oads.xml.v_a0_2_2s.StandardizationType;
import gov.noaa.ncei.oads.xml.v_a0_2_2s.TaVariableType;
import gov.noaa.ncei.oads.xml.v_a0_2_2s.TaVariableType.TaVariableTypeBuilder;
import gov.noaa.ncei.oads.xml.v_a0_2_2s.TemporalExtentsType;
import gov.noaa.ncei.oads.xml.v_a0_2_2s.TypedIdentifierType;
import gov.noaa.ncei.oads.xml.v_a0_2_2s.AddressType;
import gov.noaa.ncei.oads.xml.v_a0_2_2s.FundingSourceType;
import gov.noaa.ncei.oads.xml.v_a0_2_2s.GasDetectorType;
import gov.noaa.ncei.oads.xml.v_a0_2_2s.GeospatialExtentsType;
import gov.noaa.ncei.oads.xml.v_a0_2_2s.BaseVariableType;
import gov.noaa.ncei.oads.xml.v_a0_2_2s.BaseVariableType.BaseVariableTypeBuilder;
import gov.noaa.ncei.oads.xml.v_a0_2_2s.Co2Autonomous;
import gov.noaa.ncei.oads.xml.v_a0_2_2s.Co2Autonomous.Co2AutonomousBuilder;
import gov.noaa.ncei.oads.xml.v_a0_2_2s.Co2Base.Co2BaseBuilder;
import gov.noaa.ncei.oads.xml.v_a0_2_2s.Co2Discrete;
import gov.noaa.ncei.oads.xml.v_a0_2_2s.Co2Discrete.Co2DiscreteBuilder;
import gov.noaa.ncei.oads.xml.v_a0_2_2s.CrmType;
import gov.noaa.ncei.oads.xml.v_a0_2_2s.DicVariableType;
import gov.noaa.ncei.oads.xml.v_a0_2_2s.EquilibratorType;
import gov.noaa.ncei.oads.xml.v_a0_2_2s.DicVariableType.DicVariableTypeBuilder;
import gov.noaa.ncei.oads.xml.v_a0_2_2s.EquilibratorMeasurementType;
import gov.noaa.pmel.excel2oap.ElementType;
import gov.noaa.pmel.excel2oap.NonNullHashMap;
import gov.noaa.pmel.excel2oap.SpreadSheetKeys;
import gov.noaa.pmel.excel2oap.ifc.SSParser;
import gov.noaa.pmel.excel2oap.ifc.XmlBuilder;
import gov.noaa.pmel.excel2oap.ocads.OcadsElementType;
import gov.noaa.pmel.excel2oap.sdg.SDG_14_3_Keys;
import gov.noaa.pmel.oads.xml.a0_2_2.OadsXmlWriter;
import gov.noaa.pmel.tws.util.StringUtils;


/**
 * @author kamb
 *
 */
public class OadsXmlBuilder extends XmlBuilderBase implements XmlBuilder {
    
    private static final Logger logger = LogManager.getLogger(OadsXmlBuilder.class);
    
    protected OadsMetadataDocumentType metadataDoc;
    
    public static XmlBuilder GetOadsXmlBuilder(SSParser parser, boolean omitEmptyElements) {
        switch (parser.getSpreadSheetType()) {
            case OCADS:
                return new OadsXmlBuilder(parser.getMultiItemFields(),
                                          parser.getSingleFields(),
                                          parser.getSpreadSheetKeys(),
                                          omitEmptyElements);
            case SDG_14_3_1:
                return new SdgXmlBuilder(parser.getMultiItemFields(),
                                          parser.getSingleFields(),
                                          parser.getSpreadSheetKeys(),
                                          omitEmptyElements);
            case SOCAT:
                return new SocatXmlBuilder(parser.getMultiItemFields(),
                                          parser.getSingleFields(),
                                          parser.getSpreadSheetKeys(),
                                          omitEmptyElements);
            default:
                throw new RuntimeException("Invalid SpreadSheet type: " + parser.getSpreadSheetType());
            
        }
    }

    public OadsXmlBuilder(Map<ElementType, Collection<Map<String, String>>> multiItems,
                             Map<String, String> simpleItems,
                             SpreadSheetKeys keys, boolean omitEmptyElements) {
        super(multiItems, simpleItems, keys);
        // omitEmpty not working with JAXB
//        this.multiItems = multiItems;
//        this.simpleItems = simpleItems;
//        ssKeys = keys;
//        _omitEmptyElements = omitEmptyElements;
    }
        
    public OadsMetadataDocumentType processDocument() {
        OadsMetadataDocumentTypeBuilder doc = OadsMetadataDocumentType.builder();
        addGeneralFields(doc);
        addVariables(doc);
        return doc.build();
    }
    
    @Override
    public void buildDocument(SSParser spreadSheet) {
        metadataDoc = processDocument();
    }
    public OadsMetadataDocumentType getDocuemnt() {
        return metadataDoc;
    }
    public void outputXml(OutputStream out) throws IOException {
        try {
            OadsXmlWriter.outputXml(metadataDoc, out);
        } catch (JAXBException ex) {
            throw new IOException("JAXBException processing metadata document.", ex);
        }
    }

        
    /**
     * @param generalFields
     * @param doc
     */
    public void addGeneralFields(OadsMetadataDocumentTypeBuilder doc) {
        Date startDatestamp = tryDatestamp(simpleItems.get(ssKeys.getKeyForName(ssKeys.name_r_Start_date)));
        Date endDatestamp = tryDatestamp(simpleItems.get(ssKeys.getKeyForName(ssKeys.name_r_End_date)));
        add_PLATFORM(doc); // , simpleItems); // getSingularItem("Platform")); // XXX TODO:
        add_FUNDING(doc); // , simpleItems);
        doc.addExpocode(simpleItems.get(ssKeys.getKeyForName(ssKeys.name_r_EXPOCODE)))
           .addCruiseId(TypedIdentifierType.builder()
                        .value(simpleItems.get(ssKeys.getKeyForName(ssKeys.name_r_Cruise_ID)))
                        .type(simpleItems.get(ssKeys.r_Cruise_ID_type))
                        .build())
           .addSection(simpleItems.get(ssKeys.getKeyForName(ssKeys.name_r_Section)))
           .addResearchProject(simpleItems.get(ssKeys.getKeyForName(ssKeys.name_r_Research_projects)))
//                .datasetDoi(simpleItems.get(nothing))
//                .accessId(simpleItems.get(ssKeys.getKey(ssKeys.name_r_Accession_no_of_related_data_sets)))
//           .citation(simpleItems.get(ssKeys.getKeyForName(ssKeys.name_r_Author_list_for_citation)))
           ._abstract(simpleItems.get(ssKeys.getKeyForName(ssKeys.name_r_Abstract)))
           .purpose(simpleItems.get(ssKeys.getKeyForName(ssKeys.name_r_Purpose)))
           .title(simpleItems.get(ssKeys.getKeyForName(ssKeys.name_r_Title)))
           .addReference(simpleItems.get(ssKeys.getKeyForName(ssKeys.name_r_References)))
           .supplementalInfo(simpleItems.get(ssKeys.getKeyForName(ssKeys.name_r_Supplemental_information)))
           .temporalExtents(TemporalExtentsType.builder()
                            .startDate(startDatestamp)
                            .endDate(endDatestamp)
                            .build());
        if ( !StringUtils.emptyOrNull(simpleItems.get(ssKeys.getKeyForName(ssKeys.name_r_Site_longitude)))) {
            try {
                addSiteLocation(doc, simpleItems);
            } catch (NumberFormatException nfe) {
                logger.debug("NumberFormatException: " + nfe.toString());
                addSpatialBounds(doc, simpleItems);
            }
        } else {
            addSpatialBounds(doc, simpleItems);
        }
        String submissionDateStr = simpleItems.get(ssKeys.getKeyForName(ssKeys.name_r_Submission_Date));
        if ( !StringUtils.emptyOrNull(submissionDateStr)) {
            Date submissionDate = tryDatestamp(submissionDateStr);
            doc.submissionDate(submissionDate);
        }
        addPeople(doc);
    }

    /**
     * @param doc
     */
    private void addPeople(OadsMetadataDocumentTypeBuilder doc) {
        addDataSubmitter(doc);
        addInvestigators(doc);
        addAuthorList(doc);
    }

    /**
     * @param doc
     */
    private void addAuthorList(OadsMetadataDocumentTypeBuilder doc) {
//           .citation(simpleItems.get(ssKeys.getKeyForName(ssKeys.name_r_Author_list_for_citation)))
        String authorList = simpleItems.get(ssKeys.getKeyForName(ssKeys.name_r_Author_list_for_citation));
        doc.addAuthor(authorList);
    }

    /**
     * @param doc
     */
    private void addDataSubmitter(OadsMetadataDocumentTypeBuilder doc) {
        ElementType dsKey = ssKeys.getElementForKey("Data submitter");
        if ( ! multiItems.containsKey(dsKey)) {
            logger.info("No Data submitter element found.");
            return;
        }
        Map<String,String> dsParts = multiItems.get(dsKey).iterator().next();
        PersonType dataSubmitter = buildPerson(dsParts);
        doc.dataSubmitter(dataSubmitter);
    }

    /**
     * @param doc
     */
    private void addInvestigators(OadsMetadataDocumentTypeBuilder doc) {
        ElementType investigatorKey = ssKeys.getElementForKey("Investigator");
        if ( ! multiItems.containsKey(investigatorKey)) {
            logger.info("no Investigator elements found.");
            investigatorKey = ssKeys.getElementForKey("PI");
            if ( ! multiItems.containsKey(investigatorKey)) {
                logger.info("No investigator or PI elements found!");
                return;
            }
        }
        for (Map<String, String>parts : multiItems.get(investigatorKey)) {
            String oneName = parts.get(ssKeys.getKeyForName(ssKeys.name_r_Investigator1_name));
            PersonType investigator = buildPerson(parts);
            doc.addInvestigator(investigator);
        }
        
    }

    /**
     * @param doc
     * @param generalFields
     */
    protected void addSiteLocation(OadsMetadataDocumentTypeBuilder doc,
                                   Map<String, String> generalFields) {
        doc.spatialExtents(GeospatialExtentsType.builder()
                           .location(SpatialLocationType.builder()
                                     .latitude(new BigDecimal(generalFields.get(ssKeys.getKeyForName(ssKeys.name_r_Site_latitude))))
                                     .longitude(new BigDecimal(generalFields.get(ssKeys.getKeyForName(ssKeys.name_r_Site_longitude))))
                                     .build()
                                     )
                           .build());
    }

    /**
     * @param doc
     * @param generalFields
     */
    private void addSpatialBounds(OadsMetadataDocumentTypeBuilder doc, Map<String, String> generalFields) {
        try {
            doc.spatialExtents(GeospatialExtentsType.builder()
                               .bounds(SpatialExtentsType.builder()
                                         .northernBounds(new BigDecimal(generalFields.get(ssKeys.getKeyForName(ssKeys.name_r_Northbd_latitude))))
                                         .southernBounds(new BigDecimal(generalFields.get(ssKeys.getKeyForName(ssKeys.name_r_Southbd_latitude))))
                                         .westernBounds(new BigDecimal(generalFields.get(ssKeys.getKeyForName(ssKeys.name_r_Westbd_longitude))))
                                         .easternBounds(new BigDecimal(generalFields.get(ssKeys.getKeyForName(ssKeys.name_r_Eastbd_longitude))))
                                         .build()
                                         )
                               .build());
        } catch (NumberFormatException nfe) {
            logger.warn(nfe);
        }
    }

    /**
     * @param doc
     * @return
     */
    public void add_PI(OadsMetadataDocumentTypeBuilder doc, Map<String, String> parts) {
        add_INVESTIGATOR(doc, parts);
    }
    private PersonType buildPerson(Map<String, String> parts) {
        String name = parts.get(ssKeys.getKeyForName(ssKeys.name_PersonX_name));
        if ( name == null ) {
            logger.info("No name for person from " + parts);
            return null;
        }
        
        String[] nameParts = name != null ? name.split("\\s") : new String[0];
        int n = nameParts.length;
        String lname = nameParts[n-1];
        StringBuilder fname = new StringBuilder();
        String space = "";
        for (int i = 0; i < n-1; i++) {
            fname.append(space).append(nameParts[i]);
            space = " ";
        }
        PersonType person = PersonType.builder()
                .name(PersonNameType.builder()
                      .first(fname.toString().trim())
                      .last(lname)
                      .build()
                      )
                .contactInfo(PersonContactInfoType.builder()
                             .address(AddressType.builder()
                                      .addDeliveryPoint(new OrderedStringElementType(
                                           parts.get(ssKeys.getKeyForName(ssKeys.name_PersonX_address)), 0))
                                      .build()
                                     )
                             .email(parts.get(ssKeys.getKeyForName(ssKeys.name_PersonX_email)))
                             .phone(parts.get(ssKeys.getKeyForName(ssKeys.name_PersonX_phone)))
                             .build()
                             )
                .organization(parts.get(ssKeys.getKeyForName(ssKeys.name_PersonX_institution)))
                .addIdentifier(TypedIdentifierType.builder()
                               .value(parts.get(ssKeys.getKeyForName(ssKeys.name_PersonX_researcher_ID)))
                               .type(parts.get(ssKeys.getKeyForName(ssKeys.name_PersonX_ID_type)))
                               .build())
                .build();
         return person;
    }
    
    /**
     * @param doc
     * @return
     */
    public void add_INVESTIGATOR(OadsMetadataDocumentTypeBuilder doc, Map<String, String> parts) {
        PersonType investigator = buildPerson(parts);
        doc.addInvestigator(investigator);
    }

    public void add_DATA_SUBMITTER(OadsMetadataDocumentTypeBuilder doc, Map<String, String> parts) {
        PersonType submitter = buildPerson(parts);
        doc.dataSubmitter(submitter);
    }
    public void add_PLATFORM(OadsMetadataDocumentTypeBuilder doc) {
        if ( multiItems.containsKey(ssKeys.getElementForKey("Platform"))) {
            Collection<Map<String, String>> platforms = 
                    multiItems.get(ssKeys.getElementForKey("Platform"));
            add_MultiPLATFORM(doc, platforms);
        } else {
            add_SingularPLATFORM(doc);
        }
    }
    private void add_SingularPLATFORM(OadsMetadataDocumentTypeBuilder doc) {
        Map<String, String> parts = getPlatformParts();
        String platformName = parts.get(ssKeys.getKeyForName(ssKeys.name_r_Platform1_name)); // XXX TODO: Mulitple platforms (And fundings)
        String platformId = parts.get(ssKeys.getKeyForName(ssKeys.name_r_Platform1_ID));
        if ( (StringUtils.emptyOrNull(platformName) || "none".equalsIgnoreCase(platformName)) && 
             (StringUtils.emptyOrNull(platformId) || "none".equalsIgnoreCase(platformId))) {
            return;
        }
        String idTypeKey = ( ssKeys instanceof SDG_14_3_Keys ) ?
                            ssKeys.getKeyForName(((SDG_14_3_Keys)ssKeys).r_Platform1_ID_type) :
                             null;
        String idType =  idTypeKey != null ?
                            parts.get(idTypeKey) :
                            null;
        PlatformType platform = PlatformType.builder()
                .name(platformName)
                .identifier(TypedIdentifierType.builder()
                            .type(idType)
                            .value(platformId)
                            .build())
                .type(parts.get(ssKeys.getKeyForName(ssKeys.name_r_Platform1_type)))
                .owner(parts.get(ssKeys.getKeyForName(ssKeys.name_r_Platform1_owner)))
                .country(parts.get(ssKeys.getKeyForName(ssKeys.name_r_Platform1_country)))
                .build();
        doc.addPlatform(platform);
    }
    private void add_MultiPLATFORM(OadsMetadataDocumentTypeBuilder doc, Collection<Map<String, String>> platforms) {
        for (Map<String, String> parts : platforms) {
            String platformName = parts.get(ssKeys.getKeyForName(ssKeys.name_PlatformX_name)); // XXX TODO: Mulitple platforms (And fundings)
            String platformId = parts.get(ssKeys.getKeyForName(ssKeys.name_PlatformX_ID));
            if ( (StringUtils.emptyOrNull(platformName) || "none".equalsIgnoreCase(platformName)) && 
                 (StringUtils.emptyOrNull(platformId) || "none".equalsIgnoreCase(platformId))) {
                return;
            }
            String idTypeKey = ( ssKeys instanceof SDG_14_3_Keys ) ?
                                ((SDG_14_3_Keys)ssKeys).PlatformX_ID_type :
                                 null;
            String idType =  idTypeKey != null ?
                                parts.get(idTypeKey) :
                                null;
            PlatformType platform = PlatformType.builder()
                    .name(platformName)
                    .identifier(TypedIdentifierType.builder()
                                .type(idType)
                                .value(platformId)
                                .build())
                    .type(parts.get(ssKeys.getKeyForName(ssKeys.name_PlatformX_type)))
                    .owner(parts.get(ssKeys.getKeyForName(ssKeys.name_PlatformX_owner)))
                    .country(parts.get(ssKeys.getKeyForName(ssKeys.name_PlatformX_country)))
                    .build();
            doc.addPlatform(platform);
        }
    }

    /**
     * @return
     */
    protected Map<String, String> getPlatformParts() {
        if ( multiItems.containsKey(ssKeys.getElementForKey("Platform"))) {
            return getSingularItem("Platform");
        } else {
            return simpleItems;
        }
        
    }

    /*
    protected void add_VAR(Document doc, Map<String, String> parts) {
        add_VAR(doc, parts, "0");
    }
    protected void add_VAR(Document doc, Map<String, String> parts, String internal) {
        Element root = doc.getRootElement();
        Element var = new Element("variable");
        fill_VAR(var, parts, internal);
        root.addContent(var);
    }
    protected void fill_VAR(Element var, Map<String, String> parts) {
        fill_VAR(var, parts, "0");
    }
    protected void fill_VAR(Element var, Map<String, String> parts, String internal) {
		maybeAdd(var,"abbrev",parts.get(ssKeys.getKey(ssKeys.name_VarX_Variable_abbreviation_in_data_files)));
		maybeAdd(var,"fullname",parts.get(ssKeys.getKey(ssKeys.name_VarX_Full_variable_name)));
		maybeAdd(var,"observationType",parts.get(ssKeys.getKey(ssKeys.name_VarX_Observation_type)));
		maybeAdd(var,"insitu",parts.get(ssKeys.getKey(ssKeys.name_VarX_In_situ_observation_X_manipulation_condition_X_response_variable)));
		maybeAdd(var,"unit",parts.get(ssKeys.getKey(ssKeys.name_VarX_Variable_unit)));
		maybeAdd(var,"measured",parts.get(ssKeys.getKey(ssKeys.name_VarX_Measured_or_calculated)));
		maybeAdd(var,"calcMethod",parts.get(ssKeys.getKey(ssKeys.name_VarX_Calculation_method_and_parameters)));
		maybeAdd(var,"samplingInstrument",parts.get(ssKeys.getKey(ssKeys.name_VarX_Sampling_instrument)));
		maybeAdd(var,"analyzingInstrument",parts.get(ssKeys.getKey(ssKeys.name_VarX_Analyzing_instrument)));
		maybeAdd(var,"duration",parts.get(ssKeys.getKey(ssKeys.name_VarX_Duration)));
		maybeAdd(var,"detailedInfo",parts.get(ssKeys.getKey(ssKeys.name_VarX_Detailed_sampling_and_analyzing_information)));
		maybeAdd(var,"replicate",parts.get(ssKeys.getKey(ssKeys.name_VarX_Field_replicate_information)));
		maybeAdd(var,"uncertainty",parts.get(ssKeys.getKey(ssKeys.name_VarX_Uncertainty)));
		maybeAdd(var,"flag",parts.get(ssKeys.getKey(ssKeys.name_VarX_Data_quality_flag_description)));
		maybeAdd(var,"methodReference",parts.get(ssKeys.getKey(ssKeys.name_VarX_Method_reference)));
		maybeAdd(var,"biologicalSubject",parts.get(ssKeys.getKey(ssKeys.name_VarX_Biological_subject)));
		maybeAdd(var,"speciesID",parts.get(ssKeys.getKey(ssKeys.name_VarX_Species_Identification_code)));
		maybeAdd(var,"lifeStage",parts.get(ssKeys.getKey(ssKeys.name_VarX_Life_Stage)));
		maybeAdd(var,"researcherName",parts.get(ssKeys.getKey(ssKeys.name_VarX_Researcher_Name)));
		maybeAdd(var,"researcherInstitution",parts.get(ssKeys.getKey(ssKeys.name_VarX_Researcher_Institution)));
        maybeAdd(var,"internal",internal);
    }
    */
    
    public void add_FUNDING(OadsMetadataDocumentTypeBuilder doc) {
        if ( multiItems.containsKey(ssKeys.getElementForKey("Funding"))) {
            _add_FUNDING_multiItem(doc, getSingularItem("Funding"));
        } else {
            _add_FUNDING_simple(doc, simpleItems);
        }
    }
    private void _add_FUNDING_multiItem(OadsMetadataDocumentTypeBuilder doc, Map<String, String> parts) {
        String fundingAgencyName = // parts.get(ssKeys.getKeyForName(ssKeys.name_FundingX_agency_name)) != "" ?
                                   parts.get(ssKeys.getKeyForName(ssKeys.name_FundingX_agency_name)) ; // :
                                   // parts.get(ssKeys.getKeyForName(ssKeys.name_FundingX_agency_name_ALT));
        String fundingProjectId = // parts.get(ssKeys.getKeyForName(ssKeys.name_FundingX_project_ID)) != "" ?
                                  parts.get(ssKeys.getKeyForName(ssKeys.name_FundingX_project_ID)) ; // :
                                  // parts.get(ssKeys.getKeyForName(ssKeys.name_FundingX_project_ID_ALT));
       doc.addFunding(FundingSourceType.builder()
                       .agency(fundingAgencyName)
                       .title(parts.get(ssKeys.getKeyForName(ssKeys.name_FundingX_project_title)))
                       .identifier(TypedIdentifierType.builder().value(fundingProjectId).build())
                       .build());
    }
    private void _add_FUNDING_simple(OadsMetadataDocumentTypeBuilder doc, Map<String, String> parts) {
        String fundingAgencyName = parts.get(ssKeys.getKeyForName(ssKeys.name_r_Funding_agency_name)) != "" ?
                                   parts.get(ssKeys.getKeyForName(ssKeys.name_r_Funding_agency_name)) :
                                   parts.get(ssKeys.getKeyForName(ssKeys.name_r_Funding_agency_name_ALT));
        String fundingProjectId = parts.get(ssKeys.getKeyForName(ssKeys.name_r_Funding_project_ID)) != "" ?
                                  parts.get(ssKeys.getKeyForName(ssKeys.name_r_Funding_project_ID)) :
                                  parts.get(ssKeys.getKeyForName(ssKeys.name_r_Funding_project_ID_ALT));
       doc.addFunding(FundingSourceType.builder()
                       .agency(fundingAgencyName)
                       .title(parts.get(ssKeys.getKeyForName(ssKeys.name_r_Funding_project_title)))
                       .identifier(TypedIdentifierType.builder().value(fundingProjectId).build())
                       .build());
    }


    public void addVariables(OadsMetadataDocumentTypeBuilder doc) {
        addDIC(doc);
        addTA(doc);
        addPH(doc);
        addPCO2a(doc);
        addPCO2d(doc);
        addVARs(doc);
    }
    
    /**
     * @param doc
     * @param collection 
     */
    protected void addDIC(OadsMetadataDocumentTypeBuilder doc) {
        Map<String, String> parts = getSingularItem(OcadsElementType.DIC.key());
        if ( parts == null || parts.isEmpty()) { return; }
        DicVariableTypeBuilder<?, ?> var = DicVariableType.builder();
        fillBaseVariable(var, parts);
        fillDIC(var, parts);
        DicVariableType dic = var.build();
        doc.addVariable(dic);
    }

    /**
     * @param var
     * @param parts
     */
    private void fillDIC(DicVariableTypeBuilder<?, ?> var, Map<String, String> parts) {
        var.standardization(StandardizationType.builder()
                            .description(parts.get(ssKeys.getKeyForName(ssKeys.name_pCO2AX_Standardization_technique_description)))
                            .frequency(parts.get(ssKeys.getKeyForName(ssKeys.name_pCO2AX_Frequency_of_standardization)))
                            .crm(CrmType.builder()
                                            .manufacturer(parts.get(ssKeys.getKeyForName(ssKeys.name_DICX_CRM_manufacturer)))
                                            .batch(parts.get(ssKeys.getKeyForName(ssKeys.name_DICX_Batch_number)))
                                            .build())
                            .build());
        // poison
        String name = parts.containsKey(ssKeys.getKeyForName(ssKeys.name_DICX_Poison_used_to_kill_the_sample)) ?
                      parts.get(ssKeys.getKeyForName(ssKeys.name_DICX_Poison_used_to_kill_the_sample)) :
                      parts.get(ssKeys.getKeyForName(ssKeys.name_DICX_Poison_used_to_kill_the_sample_ALT));
        String correction = parts.containsKey(ssKeys.getKeyForName(ssKeys.name_DICX_Poisoning_correction_description)) ?
                            parts.get(ssKeys.getKeyForName(ssKeys.name_DICX_Poisoning_correction_description)) :
                            parts.get(ssKeys.getKeyForName(ssKeys.name_DICX_Poisoning_correction_description_ALT));
        String volume = parts.containsKey(ssKeys.getKeyForName(ssKeys.name_DICX_Poison_volume)) ?
                             parts.get(ssKeys.getKeyForName(ssKeys.name_DICX_Poison_volume)) :
                             parts.get(ssKeys.getKeyForName(ssKeys.name_DICX_Poison_volume_ALT));
        var.poison(PoisonType.builder()
                   .name(name)
                   .correction(correction)
                   .volume(volume)
                   .build());
        // crm is under standardization
    }

    /**
     * @param doc
     */
    protected void addTA(OadsMetadataDocumentTypeBuilder doc) {
        Map<String, String> parts = getSingularItem(OcadsElementType.TA.key());
        if ( parts == null || parts.isEmpty()) { return; }
        TaVariableTypeBuilder<?, ?> var = TaVariableType.builder();
        fillBaseVariable(var, parts);
        fillTA(var, parts);
        TaVariableType ta = var.build();
        doc.addVariable(ta);
    }

    /**
     * @param var
     * @param parts
     */
    private void fillTA(TaVariableTypeBuilder<?, ?> var, Map<String, String> parts) {
        fillDIC(var, parts);
        var.titrationType(parts.get(ssKeys.getKeyForName(ssKeys.name_TAX_Type_of_titration)))
           .cellType(parts.get(ssKeys.getKeyForName(ssKeys.name_TAX_Cell_type)))
           .curveFitting(parts.get(ssKeys.getKeyForName(ssKeys.name_TAX_Curve_fitting_method)))
           .blankCorrection(parts.get(ssKeys.getKeyForName(ssKeys.name_TAX_Magnitude_of_blank_correction)));
    }

    /**
     * @param doc
     */
    protected void addPH(OadsMetadataDocumentTypeBuilder doc) {
        Map<String, String> parts = getSingularItem(OcadsElementType.PH.key());
        if ( parts == null || parts.isEmpty()) { return; }
        PhVariableTypeBuilder<?, ?> var = PhVariableType.builder();
        fillBaseVariable(var, parts);
        PhVariableType ph = fillPH(var, parts);
        doc.addVariable(ph);
    }

    /**
     * @param var
     * @param parts
     */
    private PhVariableType fillPH(PhVariableTypeBuilder<?, ?> var, Map<String, String> parts) {
        // ph of standards - in standards
        // ph scale
        // temp of measurement
        // temp correction method
        // temp of ph reporting
        var.phScale(parts.get(ssKeys.getKeyForName(ssKeys.name_PHX_pH_scale)))
           .measurementTemperature(parts.get(ssKeys.getKeyForName(ssKeys.name_PHX_Temperature_of_measurement)))
           .temperatureCorrectionMethod(parts.get(ssKeys.getKeyForName(ssKeys.name_PHX_Temperature_correction_method)))
           .phReportTemperature(parts.get(ssKeys.getKeyForName(ssKeys.name_PHX_at_what_temperature_was_pH_reported)));
        PhVariableType ph = var.build();
        StandardizationType std = ph.getStandardization();
        std.setTemperature(parts.get(ssKeys.getKeyForName(ssKeys.name_PHX_Temperature_of_standardization)));
        std.setPhOfStandards(parts.get(ssKeys.PHX_pH_values_of_the_standards));
        return ph;
    }

    /**
     * @param doc
     */
    protected void addPCO2a(OadsMetadataDocumentTypeBuilder doc) {
        Map<String, String> parts = getSingularItem(OcadsElementType.PCO2A.key());
        if ( parts == null || parts.isEmpty()) { return; }
        Co2AutonomousBuilder<?,?> var = Co2Autonomous.builder();
        fillCO2aSections(var,parts);
        Co2Autonomous co2a = var.build();
        doc.addVariable(co2a);
    }

	/**
     * @param var
     * @param parts
     */
    protected void fillCO2aSections(Co2AutonomousBuilder<?, ?> var, Map<String, String> parts) {
        fillBaseVariable(var, parts);
        fillCO2common(var, parts);
        fillCO2autonomous(var, parts);
    }

    /*
        var.addContent(new Element("fullname").addContent("pCO2 (fCO2) autonomous"));
        maybeAdd(var,"abbrev",parts.get(pCO2AX_Variable_abbreviation_in_data_files));
        maybeAdd(var,"observationType",parts.get(pCO2AX_Observation_type));
        maybeAdd(var,"insitu",parts.get(pCO2AX_In_situ_observation_X_manipulation_condition_X_response_variable));
        maybeAdd(var,"manipulationMethod",parts.get(pCO2AX_Manipulation_method));
        maybeAdd(var,"unit",parts.get(pCO2AX_Variable_unit));
        maybeAdd(var,"measured",parts.get(pCO2AX_Measured_or_calculated));
        maybeAdd(var,"calcMethod",parts.get(pCO2AX_Calculation_method_and_parameters));
        maybeAdd(var,"samplingInstrument",parts.get(pCO2AX_Sampling_instrument));
        maybeAdd(var,"locationSeawaterIntake",parts.get(pCO2AX_Location_of_seawater_intake));
        maybeAdd(var,"DepthSeawaterIntake",parts.get(pCO2AX_Depth_of_seawater_intake));
        maybeAdd(var,"analyzingInstrument",parts.get(pCO2AX_Analyzing_instrument));
        maybeAdd(var,"detailedInfo",parts.get(pCO2AX_Detailed_sampling_and_analyzing_information));
        Element equilibrator = new Element("equilibrator");
        maybeAdd(equilibrator,"type",parts.get(pCO2AX_Equilibrator_type));
        maybeAdd(equilibrator,"volume",parts.get(pCO2AX_Equilibrator_volume));
        maybeAdd(equilibrator,"vented",parts.get(pCO2AX_Vented_or_not));
        maybeAdd(equilibrator,"waterFlowRate",parts.get(pCO2AX_Water_flow_rate));
        maybeAdd(equilibrator,"gasFlowRate",parts.get(pCO2AX_Headspace_gas_flow_rate));
        maybeAdd(equilibrator,"temperatureEquilibratorMethod",parts.get(pCO2AX_How_was_temperature_inside_the_equilibrator_measured));
        maybeAdd(equilibrator,"pressureEquilibratorMethod",parts.get(pCO2AX_How_was_pressure_inside_the_equilibrator_measured));
        maybeAdd(equilibrator,"dryMethod",parts.get(pCO2AX_Drying_method_for_CO2_gas));
        var.addContent(equilibrator);
        Element gasDetector = new Element("gasDetector");
         maybeAdd(gasDetector,"manufacturer",parts.get(pCO2AX_Manufacturer_of_standard_gas));
         maybeAdd(gasDetector,"model",parts.get(pCO2AX_Model_of_the_gas_detector));
         maybeAdd(gasDetector,"resolution",parts.get(pCO2AX_Resolution_of_the_gas_detector));
         maybeAdd(gasDetector,"uncertainty",parts.get(pCO2AX_Uncertainty_of_the_gas_detector));
        var.addContent(gasDetector);
        Element standard = new Element("standard");
        maybeAdd(standard,"description",parts.get(pCO2AX_Standardization_technique_description));
        maybeAdd(standard,"frequency",parts.get(pCO2AX_Frequency_of_standardization));
        Element standardGas = new Element("standardgas");
            maybeAdd(standardGas,"manufacturer",parts.get(pCO2AX_Manufacturer_of_standard_gas));
            maybeAdd(standardGas,"concentration",parts.get(pCO2AX_Concentrations_of_standard_gas));
            maybeAdd(standardGas,"uncertainty",parts.get(pCO2AX_Uncertainties_of_standard_gas));
        standard.addContent(standardGas);
        var.addContent(standard);
        maybeAdd(var,"waterVaporCorrection",parts.get(pCO2AX_Water_vapor_correction_method));
        maybeAdd(var,"temperatureCorrection",parts.get(pCO2AX_Temperature_correction_method));
        maybeAdd(var,"co2ReportTemperature",parts.get(pCO2AX_at_what_temperature_was_pCO2_reported));
        maybeAdd(var,"uncertainty",parts.get(pCO2AX_Uncertainty));
        maybeAdd(var,"flag",parts.get(pCO2AX_Data_quality_flag_description));
        maybeAdd(var,"methodReference",parts.get(pCO2AX_Method_reference));
        maybeAdd(var,"researcherName",parts.get(pCO2AX_Researcher_Name));
        maybeAdd(var,"researcherInstitution",parts.get(pCO2AX_Researcher_Institution));
        var.addContent(new Element("internal").addContent("4"));
		*/
    /**
     * @param var
     * @param parts
     */
    protected void fillCO2common(Co2BaseBuilder<?, ?> var, Map<String, String> parts) {
        // manufacture of std gas
        // concentration of std gas
        // uncertainties of std gas
        var.standardization(StandardizationType.builder()
                            .description(parts.get(ssKeys.getKeyForName(ssKeys.name_pCO2AX_Standardization_technique_description)))
                            .frequency(parts.get(ssKeys.getKeyForName(ssKeys.name_pCO2AX_Frequency_of_standardization)))
                            .addStandardGas(StandardGasType.builder()
                                            .manufacturer(parts.get(ssKeys.getKeyForName(ssKeys.name_pCO2AX_Manufacturer_of_standard_gas)))
                                            .concentration(parts.get(ssKeys.getKeyForName(ssKeys.name_pCO2AX_Concentrations_of_standard_gas)))
                                            .uncertainty(parts.get(ssKeys.getKeyForName(ssKeys.name_pCO2AX_Uncertainties_of_standard_gas)))
                                            .build())
                            .build())
        // manuf gas detector
        // model gas detector
        // reso gas detector
        // uncertainty gas detector
            .gasDetector(GasDetectorType.builder()
                         .manufacturer(parts.get(ssKeys.getKeyForName(ssKeys.name_pCO2AX_Manufacturer_of_the_gas_detector)))
                         .model(parts.get(ssKeys.getKeyForName(ssKeys.name_pCO2AX_Model_of_the_gas_detector)))
                         .resolution(parts.get(ssKeys.getKeyForName(ssKeys.name_pCO2AX_Resolution_of_the_gas_detector)))
                         .uncertainty(parts.get(ssKeys.getKeyForName(ssKeys.name_pCO2AX_Uncertainty_of_the_gas_detector)))
                         .build())
        // water vapor correction
        // temperature correction
        // co2 reported temp
            .waterVaporCorrection(parts.get(ssKeys.getKeyForName(ssKeys.name_pCO2AX_Water_vapor_correction_method)))
            .temperatureCorrectionMethod(parts.get(ssKeys.getKeyForName(ssKeys.name_pCO2AX_Temperature_correction_method)))
            .co2ReportTemperature(parts.get(ssKeys.getKeyForName(ssKeys.name_pCO2AX_at_what_temperature_was_pCO2_reported)))
        ;
    }

    /**
     * @param var
     * @param parts
     */
    protected void fillCO2autonomous(Co2AutonomousBuilder<?, ?> var, Map<String, String> parts) {
        // CO2 autonomous-specific fields
        // location of intake
        var.locationSeawaterIntake(parts.get(ssKeys.getKeyForName(ssKeys.name_pCO2AX_Location_of_seawater_intake)))
        // depth of intake
            .depthSeawaterIntake(parts.get(ssKeys.getKeyForName(ssKeys.name_pCO2AX_Depth_of_seawater_intake)))
        // eq type
        // eq vol (L)
        // vented
        // water flow rate
        // headspace gas flow rate
        // how was temp measured
        // how was pressure measured
            .equilibrator(EquilibratorType.builder()
                          .type(parts.get(ssKeys.getKeyForName(ssKeys.name_pCO2AX_Equilibrator_type)))
                          .volume(parts.get(ssKeys.getKeyForName(ssKeys.name_pCO2AX_Equilibrator_volume)))
                          .vented(parts.get(ssKeys.getKeyForName(ssKeys.name_pCO2AX_Vented_or_not)))
                          .gasFlowRate(parts.get(ssKeys.getKeyForName(ssKeys.name_pCO2AX_Headspace_gas_flow_rate)))
                          .waterFlowRate(parts.get(ssKeys.getKeyForName(ssKeys.name_pCO2AX_Headspace_gas_flow_rate)))
                          .temperatureMeasurement(EquilibratorMeasurementType.builder()
                                                  .method(parts.get(ssKeys.getKeyForName(ssKeys.name_pCO2AX_How_was_temperature_inside_the_equilibrator_measured)))
                                                  .build())
                          .pressureMeasurement(EquilibratorMeasurementType.builder()
                                                  .method(parts.get(ssKeys.getKeyForName(ssKeys.name_pCO2AX_How_was_pressure_inside_the_equilibrator_measured)))
                                                  .build())
                          .build())
            .co2GasDryingMethod(parts.get(ssKeys.getKeyForName(ssKeys.name_pCO2AX_Drying_method_for_CO2_gas)))
        ;
    }

    /**
     * @param doc
     */
    protected void addPCO2d(OadsMetadataDocumentTypeBuilder doc) {
        Map<String, String> parts = getSingularItem(OcadsElementType.PCO2D.key());
        if ( parts == null || parts.isEmpty()) { return; }
        Co2DiscreteBuilder<?,?> var = Co2Discrete.builder();
        fillBaseVariable(var, parts);
        fillCO2common(var, parts);
        Co2Discrete co2d = fillCO2discrete(var, parts);
        doc.addVariable(co2d);
    }

    /**
     * @param var
     * @param parts
     */
    private Co2Discrete fillCO2discrete(Co2DiscreteBuilder<?, ?> var, Map<String, String> parts) {
        // storage method
        // seawater volume
        // headspace volume
        // measurement temperature
        var.storageMethod(parts.get(ssKeys.getKeyForName(ssKeys.name_pCO2DX_Storage_method)))
           .seawaterVolume(parts.get(ssKeys.getKeyForName(ssKeys.name_pCO2DX_Seawater_volume)))
           .headspaceVolume(parts.get(ssKeys.getKeyForName(ssKeys.name_pCO2DX_Headspace_volume)))
           .measurementTemperature(parts.get(ssKeys.getKeyForName(ssKeys.name_pCO2DX_at_what_temperature_was_pCO2_reported)))
        ;
        Co2Discrete co2d = var.build();
        co2d.getStandardization().setTemperature(parts.get(ssKeys.pCO2DX_Temperature_of_standardization));
        return co2d;
    }

    /**
     * @param doc
     */
    protected void addVARs(OadsMetadataDocumentTypeBuilder doc) {
        Collection<Map<String, String>> vars = multiItems.get(OcadsElementType.fromSsRowName("Var")); // XXX String Constant! Subclass!
        if ( vars == null || vars.isEmpty()) {
            return;
        }
        for (Map<String, String> parts : vars) {
            addVar(doc, parts);
        }
    }

    /**
     * @param doc
     * @param parts
     */
    protected void addVar(OadsMetadataDocumentTypeBuilder doc, Map<String, String> parts) {
        if (parts == null) {
            logger.debug("null variable parts");
            return;
        }
        BaseVariableType var = fillBaseVariable(BaseVariableType.builder(), parts).build();
        doc.addVariable(var);
    }

    public BaseVariableTypeBuilder<?,?> fillBaseVariable(BaseVariableTypeBuilder<?, ?> var, 
                                                         Map<String, String> parts) {
        if (parts == null) {
            logger.debug("null variable parts");
            return var;
        }
        var.datasetVarName(parts.get(ssKeys.getKeyForName(ssKeys.name_VarX_Variable_abbreviation_in_data_files)))
           .fullName(parts.get(ssKeys.getKeyForName(ssKeys.name_VarX_Full_variable_name)))
           .units(parts.get(ssKeys.getKeyForName(ssKeys.name_VarX_Variable_unit)))
           .observationType(parts.get(ssKeys.getKeyForName(ssKeys.name_VarX_Observation_type)))
           .variableType(parts.get(ssKeys.getKeyForName(ssKeys.name_VarX_In_situ_observation_X_manipulation_condition_X_response_variable)))
           .samplingInstrument(parts.get(ssKeys.getKeyForName(ssKeys.name_VarX_Sampling_instrument)))
           .analyzingInstrument(parts.get(ssKeys.getKeyForName(ssKeys.name_VarX_Analyzing_instrument)))
           .detailedAnalyzingInfo(parts.get(ssKeys.getKeyForName(ssKeys.name_VarX_Detailed_sampling_and_analyzing_information)))
//           .qcFlag(buildQcInfo(parts)) // XXX TODO:
           .uncertainty(parts.get(ssKeys.getKeyForName(ssKeys.name_VarX_Uncertainty)))
           .fieldReplicateHandling(parts.get(ssKeys.getKeyForName(ssKeys.name_VarX_Field_replicate_information)))
           .methodReference(parts.get(ssKeys.getKeyForName(ssKeys.name_VarX_Method_reference)))
           .variationsFromMethod(parts.get(ssKeys.getKeyForName(ssKeys.name_VarX_SOP_Changes)))
           .standardization(StandardizationType.builder()
                               .description(parts.get(ssKeys.getKeyForName(ssKeys.name_pCO2AX_Standardization_technique_description)))
                               .frequency(parts.get(ssKeys.getKeyForName(ssKeys.name_pCO2AX_Frequency_of_standardization)))
                               .build())
           .researcher(PersonReferenceType.builder()
                       .name(parts.get(ssKeys.getKeyForName(ssKeys.name_VarX_Researcher_Name)))
                       .organization(parts.get(ssKeys.getKeyForName(ssKeys.name_VarX_Researcher_Institution)))
                       .build())
           .qcFlag(QcFlagInfoType.builder()
                   .description(parts.get(ssKeys.getKeyForName(ssKeys.name_VarX_Data_quality_flag_description)))
                   .build())
           ;
        return var;
    }

    /**
     * @param parts
     * @return
    protected QcFlagInfoType buildQcInfo(Map<String, String> parts) {
        String varname = parts.get(ssKeys.Var)
        QcFlagInfoType.builder()
            .
        return null;
    }
     */

    // Not necessary, since we use NonNullHashMap
//    protected String getIfNotNull(String key, Map<String, String> parts) {
//        String value = parts.get(ssKeys.getKey(key));
//        return value != null ? value : "";
//    }
    
    protected static String especiallyClean(String dirty) {
        if ( dirty.indexOf("SPECIAL USE ONLY") < 0 ) return dirty;
        return dirty.replaceAll(" *\\(SPECIAL USE ONLY\\) *", "");
    }
    
    /*
    protected static boolean addIfNotNull(Element var, String childName, String childContent) {
        if ( StringUtils.emptyOrNull(childContent)) { return false; }
        var.addContent(new Element(childName).addContent(childContent));
        return true;
    }
    
    protected boolean maybeAdd(Element var, String childName, String childContent) {
        if ( StringUtils.emptyOrNull(childContent) && _omitEmptyElements ) { return false; }
        var.addContent(new Element(childName).addContent(childContent));
        return true;
    }
    */
    
    protected Map<String, String> getSingularItem(String rowName) {
        return getSingularItem(ssKeys.getElementForKey(rowName));
    }
    private Map<String, String> getSingularItem(ElementType type) {
        Collection<Map<String, String>> c = multiItems.get(type);
        if ( c == null || c.isEmpty()) {
            return null;
        }
        if ( c.size() > 1 ) {
            System.err.println("More than one item for getSingular " + type.name());
        }
        return c.iterator().next();
    }

    /**
     * @param string
     * @return
     */
    protected static Date tryDatestamp(String string) {
        logger.info("Trying datastamp for " + string);
        if ( StringUtils.emptyOrNull(string)) {
            return null;
        }
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, -1);
            String[] parts = string.split("[/ -]");
            if ( parts.length == 3 ) {
                String p0 = parts[0];
                String p1 = parts[1];
                String p2 = parts[2];
                try {
                    int i0 = Integer.parseInt(p0);
                    int i1 = Integer.parseInt(p1);
                    int i2 = Integer.parseInt(p2);
                    int month, day, year = -1;
                    month = day = year;
                    if ( i0 > 12 ) { // assume not month
                        if ( i0 > 31 ) { // assume year
                            year = i0;   // then assume yyyy-mm-dd
                            month = i1;
                            day = i2;
                        } else {
                            day = i0;  // else assume dd-mm-yyyy
                            month = i1;
                            year = i2;
                        }
                    } else { // assume mm-dd-yyyy
                        month = i0;
                        day = i1;
                        year = i2;
                    } 
                    
                    if ( year < 1000 ) {
                        if ( year < 42 ) { // because that's the answer
                            year += 2000;
                        } else {
                            year += 1900;
                        }
                    }
                    cal.set(Calendar.MONTH, month-1);
                    cal.set(Calendar.DAY_OF_MONTH, day);
                    cal.set(Calendar.YEAR, year);
                }
                catch (Exception ex) {
                    logger.info("Excel2Oap Exception parsing date string:"+ string + ":"+ex.toString());
                    if ( ex instanceof NumberFormatException ) {
                        SimpleDateFormat sdf = new SimpleDateFormat("MMMMM d y");
                        try {
                            Date d = sdf.parse(string);
                            cal = Calendar.getInstance();
                            cal.setTime(d);
                        } catch (Exception e2) {
                            logger.info("Failed again to parse date string " + string + ": " + e2.toString());
                            sdf = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss");
                            try {
                                Date d = sdf.parse(string);
                                cal = Calendar.getInstance();
                                cal.setTime(d);
                                logger.debug("Successfully parsed ISO8601 datetime");
                            } catch (Exception e3) {
                                logger.info("Final failure to parse date string " + string + ": " + e2.toString());
                            }
                        }
                    }
                }
            } else {
                logger.info("Excel2Oap: Cannot parse date string:" + string);
            }
//        }
        if ( cal.get(Calendar.YEAR) <= 0 ) {
            logger.info("Unable to parse date string: "+ string);
            return null;
        }
        cal.set(Calendar.HOUR, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        return cal.getTime();
    }


}
