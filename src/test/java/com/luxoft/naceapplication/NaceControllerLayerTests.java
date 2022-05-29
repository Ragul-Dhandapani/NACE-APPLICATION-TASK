package com.luxoft.naceapplication;

import com.luxoft.naceapplication.controllers.NaceApplicationController;
import com.luxoft.naceapplication.dao.AddNaceInformationDto;
import com.luxoft.naceapplication.dao.RetrieveNaceInformation;
import com.luxoft.naceapplication.dao.entities.NaceDetailsEntity;
import com.luxoft.naceapplication.services.NaceService;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.luxoft.naceapplication.constants.NaceApplicationConstants.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class NaceControllerLayerTests {

    public static final String CREATE_NACE_RECORD_ENDPOINT = "/" + BASE_API_URL + "create";
    public static final long ORDER_ID = 1234567;
    public static final String GET_ORDER_ENDPOINT = "/" + BASE_API_URL + "order/";
    private static final String VALID_CSV_FILE_PATH = "NACE_DATA.csv";
    private static final String INVALID_FILE_PATH = "NACE_APPLICATION_DATA.csv";

    @Autowired
    MockMvc mockMvc;

    @Autowired
    NaceApplicationController naceApplicationController;

    @MockBean
    NaceService mockNaceService;

    private MvcResult mvcResult;

    @BeforeAll
    public void setUp() {

        naceApplicationController = new NaceApplicationController();
        mockNaceService = mock(NaceService.class);
        ReflectionTestUtils.setField(naceApplicationController , "naceService" , mockNaceService);
    }

    /**
     * Successful Scenario : Import Nace Details CSV Api Test
     * To be tested
     *
     * @throws Exception
     */
    @Test
    public void testImportNaceDetailsApiSuccessful() throws Exception {
        List<NaceDetailsEntity> addedNaceRecords = prepareEntityList();
        when(mockNaceService.createNaceDetails("NACE_DATA.csv")).thenReturn(addedNaceRecords);
        mvcResult = mockMvc.perform(post(CREATE_NACE_RECORD_ENDPOINT).headers(getHttpHeaders(VALID_CSV_FILE_PATH)))
                .andExpect(status().isOk()).andReturn();
        assertEquals(SUCCESS , new JSONObject(mvcResult.getResponse().getContentAsString()).get("responseStatus"));

    }

    /**
     * Successful Scenario : Get Nace Details CSV Api Test
     * To be tested
     *
     * @throws Exception
     */
    @Test
    public void testGetNaceInformationApiSuccessful() throws Exception {
        List<NaceDetailsEntity> fetchedEntities = prepareEntityList();
        when(mockNaceService.fetchNaceDetailsByOrderId(Mockito.anyLong())).thenReturn(fetchedEntities);
        mockMvc.perform(get(GET_ORDER_ENDPOINT + ORDER_ID)).andExpect(status().isOk());

    }

    /**
     * Successful Scenario : Test the HealthCheck endpoint
     *
     * @throws Exception
     */
    @Test
    public void testHealthCheck() throws Exception {
        mockMvc.perform(get("/" + BASE_API_URL + "healthCheck")).andExpect(status().isOk()).
                andExpect(content().string(NACE_APPLICATION_IS_UP_RUNNING_MSG));
    }

    /**
     * Negative Scenario : Calling the POST Nace Service API with invalid file name in header
     *
     * @throws Exception
     */
    @Test
    public void testInvalidCsvFileName() throws Exception {
        mvcResult = mockMvc.perform(post(CREATE_NACE_RECORD_ENDPOINT).headers(getHttpHeaders(INVALID_FILE_PATH)))
                .andExpect(status().isOk()).andReturn();
        validateResponseStatus();
    }

    @Test
    public void testPutNaceDetailsShouldFailForEmptyCsvFileName() throws Exception {
        mvcResult = mockMvc.perform(post(CREATE_NACE_RECORD_ENDPOINT).headers(getHttpHeaders(StringUtils.EMPTY)))
                .andExpect(status().isBadRequest()).andReturn();
        validateResponseStatus();
    }

    private void validateResponseStatus() throws JSONException, UnsupportedEncodingException {
        assertEquals(FAILED , new JSONObject(mvcResult.getResponse().getContentAsString()).get("responseStatus"));
    }

    /**
     * Successful Scenario : Test fetchNaceDetailsByOrderId method in NaceService
     *
     * @throws Exception
     */
    @Test
    public void testFetchNaceDetailsByOrderIdMethodSuccessful() throws Exception {
        List<NaceDetailsEntity> fetchedEntities = prepareEntityList();
        when(mockNaceService.fetchNaceDetailsByOrderId(Mockito.anyLong())).thenReturn(fetchedEntities);
        assertEquals(fetchedEntities , mockNaceService.fetchNaceDetailsByOrderId(ORDER_ID));
    }


    /**
     * Negative Scenario : Get Nace Details CSV Api Test with Order Id as "space"
     *
     * @throws Exception
     */
    @Test
    public void testGetNaceInformationApiOrderIdAsSpace() throws Exception {
        mvcResult = this.mockMvc.perform(get(GET_ORDER_ENDPOINT + StringUtils.SPACE))
                .andExpect(status().isBadRequest()).andReturn();
        validateResponseStatus();
    }

    /**
     * Negative Scenario : Get Nace Details CSV Api Test with Order Id as decimal digit
     *
     * @throws Exception
     */
    @Test
    public void testGetNaceInformationApiOrderIdAsInvalidFractionNumber() throws Exception {
        mvcResult = mockMvc.perform(get(GET_ORDER_ENDPOINT + "9028.02"))
                .andExpect(status().isBadRequest()).andReturn();
        validateResponseStatus();

    }

    /**
     * Negative Scenario : Get Nace Details CSV Api Test with Invalid Order Id
     *
     * @throws Exception
     */
    @Test
    public void testGetNaceInformationApiOrderIdAsBigIntegerInvalidOrderId() throws Exception {
        mvcResult = mockMvc.perform(get(GET_ORDER_ENDPOINT + String.valueOf(Integer.MAX_VALUE)))
                .andExpect(status().isNotFound()).andReturn();
        validateResponseStatus();
        assertEquals(DATA_NOT_FOUND_ERR_MSG , new JSONObject(mvcResult.getResponse().getContentAsString()).get("responseDetails"));
    }

    /**
     * Negative Scenario : Get Nace Details CSV Api Test with Order Id as Zero('0')
     *
     * @throws Exception
     */
    @Test
    public void testGetNaceInformationApiOrderIdAsZero() throws Exception {
        mvcResult = this.mockMvc.perform(get(GET_ORDER_ENDPOINT + "0"))
                .andExpect(status().isBadRequest()).andReturn();
        validateResponseStatus();
    }

    private ResponseEntity<List<RetrieveNaceInformation>> formGetResponse(List<NaceDetailsEntity> naceDetailsEntityList) {

        List<RetrieveNaceInformation> orderIdInformation = new ArrayList<>();
        naceDetailsEntityList.parallelStream().forEach(list -> {
            orderIdInformation.add(RetrieveNaceInformation.builder().order(list.getOrder())
                    .description(list.getDescription())
                    .level(list.getLevel())
                    .code(list.getCode())
                    .parent(list.getParent())
                    .itemIncludes(list.getItemIncludes())
                    .itemAlsoIncludes(list.getItemAlsoIncludes())
                    .referencesIsic(list.getReferencesIsic())
                    .rulings(list.getRulings()).build());
        });

        return new ResponseEntity<>(orderIdInformation , HttpStatus.OK);
    }

    /**
     * Successful Scenario : Create Nace Details Method test in NaceService
     *
     * @throws Exception
     */

    void testCreateNaceDetailsSuccess() throws Exception {

        List<NaceDetailsEntity> addedNaceRecords = prepareEntityList();
        when(mockNaceService.createNaceDetails(Mockito.anyString())).thenReturn(addedNaceRecords);
        //DATA IS SUCCESSFULLY ADDED INTO SYSTEM AND THE AggregationFunction.COUNT IS 1 records
        ResponseEntity<AddNaceInformationDto> postSuccessResponse = formPostResponse(addedNaceRecords);
        when(naceApplicationController.importNaceDetails(String.valueOf(addedNaceRecords))).thenReturn(postSuccessResponse);
    }

    private List<NaceDetailsEntity> prepareEntityList() {

        List<NaceDetailsEntity> addedNaceRecords = new ArrayList<>();
        addedNaceRecords.add(NaceDetailsEntity.builder().entityId(1L).code("92929")
                .description("CSV FILE CREATION").level(2L)
                .itemAlsoIncludes("item also includes").itemIncludes("").itemExcludes("exclude this item")
                .order(ORDER_ID).rulings("rule no 1").referencesIsic("data is available").build());

        return addedNaceRecords;
    }

    private ResponseEntity<AddNaceInformationDto> formPostResponse(List<NaceDetailsEntity> entityData) {

        AddNaceInformationDto resultDto;
        String entityDataSize = (entityData.isEmpty()) ? ZERO : String.valueOf(Math.max(entityData.size() , 0));

        if (!ZERO.equals(entityDataSize)) {
            String successMsg = DATA_INSERTED_SUCCESS_MSG.concat(entityDataSize).concat(RECORD_MSG);
            resultDto = AddNaceInformationDto.builder().responseStatus(SUCCESS).responseDetails(successMsg).
                    responseTimestamp(LocalDateTime.now()).build();
        } else {
            String failureDetailMsg = DATA_IMPORT_FAILURE_MSG.concat(entityDataSize).concat(RECORD_MSG);
            resultDto = AddNaceInformationDto.builder().responseStatus(FAILED).responseDetails(failureDetailMsg).
                    responseTimestamp(LocalDateTime.now()).build();
        }

        return new ResponseEntity<>(resultDto , HttpStatus.OK);
    }

    private HttpHeaders getHttpHeaders(String scenario) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("file" , scenario);
        return httpHeaders;
    }

}
