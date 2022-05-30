package com.luxoft.naceapplication;

import com.luxoft.naceapplication.controllers.NaceApplicationController;
import com.luxoft.naceapplication.dao.entities.NaceDetailsEntity;
import com.luxoft.naceapplication.services.NaceService;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.ArrayList;
import java.util.List;

import static com.luxoft.naceapplication.constants.NaceApplicationConstants.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = NaceApplicationController.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class NaceControllerLayerTests {

    public static final String CREATE_NACE_RECORD_ENDPOINT = "/" + BASE_API_URL + "create";
    public static final long ORDER_ID = 1234567;
    public static final String GET_ORDER_ENDPOINT = "/" + BASE_API_URL + "order/";
    private static final String VALID_CSV_FILE_PATH = "NACE_DATA.csv";
    private static final String INVALID_FILE_PATH = "NACE_APPLICATION_DATA.csv";
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private NaceService naceService;
    private MvcResult mvcResult;


    /**
     * Successful Scenario : Import Nace Details CSV Api Test
     * To be tested
     *
     * @throws Exception
     */
    @Test
    public void testImportNaceDetailsApiSuccessful() throws Exception {
        List<NaceDetailsEntity> addedNaceRecords = prepareEntityList();
        when(naceService.createNaceDetails(Mockito.anyString())).thenReturn(addedNaceRecords);
        mockMvc.perform(post(CREATE_NACE_RECORD_ENDPOINT).headers(getHttpHeaders(VALID_CSV_FILE_PATH)))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.responseStatus").value(SUCCESS))
                .andExpect(MockMvcResultMatchers.jsonPath("$.responseDetails").value(DATA_INSERTED_SUCCESS_MSG + addedNaceRecords.size() + RECORD_MSG));
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
        when(naceService.fetchNaceDetailsByOrderId(Mockito.anyLong())).thenReturn(fetchedEntities);
        mvcResult = mockMvc.perform(get(GET_ORDER_ENDPOINT + ORDER_ID)).andExpect(status().isOk()).andReturn();
        JSONArray responseObject = new JSONArray(mvcResult.getResponse().getContentAsString());
        assertEquals(fetchedEntities.get(0).getOrder() , Long.valueOf(responseObject.getJSONObject(0).getString("order")));
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
     * Successful Scenario : Test fetchNaceDetailsByOrderId method in NaceService
     *
     * @throws Exception
     */
    @Test
    public void testFetchNaceDetailsByOrderIdMethodSuccessful() throws Exception {
        List<NaceDetailsEntity> fetchedEntities = prepareEntityList();
        when(naceService.fetchNaceDetailsByOrderId(Mockito.anyLong())).thenReturn(fetchedEntities);
        assertEquals(fetchedEntities , naceService.fetchNaceDetailsByOrderId(ORDER_ID));
    }

    /**
     * Negative Scenario : Calling the POST Nace Service API with invalid file name in header
     *
     * @throws Exception
     */
    @Test
    public void testInvalidCsvFileName() throws Exception {
        mockMvc.perform(post(CREATE_NACE_RECORD_ENDPOINT).headers(getHttpHeaders(INVALID_FILE_PATH)))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.responseStatus").value(FAILED))
                .andExpect(MockMvcResultMatchers.jsonPath("$.responseDetails").value(DATA_IMPORT_FAILURE_MSG + "ZERO" + RECORD_MSG));
    }

    /**
     * Negative Scenario : Test the post endpoint with headers as empty
     * @throws Exception
     */
    @Test
    public void testPutNaceDetailsShouldFailForEmptyCsvFileName() throws Exception {
        mockMvc.perform(post(CREATE_NACE_RECORD_ENDPOINT).headers(getHttpHeaders(StringUtils.EMPTY)))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.responseStatus").value(FAILED));
    }

    /**
     * Negative Scenario : Get Nace Details CSV Api Test with Order Id as "space"
     *
     * @throws Exception
     */
    @Test
    public void testGetNaceInformationApiOrderIdAsSpace() throws Exception {
        mockMvc.perform(get(GET_ORDER_ENDPOINT + StringUtils.SPACE))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.responseStatus").value(FAILED));
    }

    /**
     * Negative Scenario : Get Nace Details CSV Api Test with Order Id as decimal digit
     *
     * @throws Exception
     */
    @Test
    public void testGetNaceInformationApiOrderIdAsInvalidFractionNumber() throws Exception {
        mockMvc.perform(get(GET_ORDER_ENDPOINT + "9028.02"))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.responseStatus").value(FAILED));

    }

    /**
     * Negative Scenario : Get Nace Details CSV Api Test with Invalid Order Id
     *
     * @throws Exception
     */
    @Test
    public void testGetNaceInformationApiOrderIdAsBigIntegerInvalidOrderId() throws Exception {
        mockMvc.perform(get(GET_ORDER_ENDPOINT + String.valueOf(Integer.MAX_VALUE)))
                .andExpect(status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.responseStatus").value(FAILED))
                .andExpect(MockMvcResultMatchers.jsonPath("$.responseDetails").value(DATA_NOT_FOUND_ERR_MSG));
    }

    /**
     * Negative Scenario : Get Nace Details CSV Api Test with Order Id as Zero('0')
     *
     * @throws Exception
     */
    @Test
    public void testGetNaceInformationApiOrderIdAsZero() throws Exception {
        mockMvc.perform(get(GET_ORDER_ENDPOINT + "0"))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.responseStatus").value(FAILED));
    }

    private HttpHeaders getHttpHeaders(String scenario) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("file" , scenario);
        return httpHeaders;
    }

    private List<NaceDetailsEntity> prepareEntityList() {

        List<NaceDetailsEntity> addedNaceRecords = new ArrayList<>();
        addedNaceRecords.add(NaceDetailsEntity.builder().entityId(1L).code("92929")
                .description("CSV FILE CREATION").level(2L)
                .itemAlsoIncludes("item also includes").itemIncludes("").itemExcludes("exclude this item")
                .order(ORDER_ID).rulings("rule no 1").referencesIsic("data is available").build());

        return addedNaceRecords;
    }
}
