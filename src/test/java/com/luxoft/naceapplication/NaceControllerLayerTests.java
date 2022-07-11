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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
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
class NaceControllerLayerTests {

    static final String CREATE_NACE_RECORD_ENDPOINT = "/" + BASE_API_URL + "importNaceDetails";
    static final long ORDER_ID = 1234567;
    static final String GET_ORDER_ENDPOINT = "/" + BASE_API_URL + "order/";

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private NaceService naceService;


    /**
     * Successful Scenario : Import Nace Details CSV Api Test
     * To be tested
     */
    @Test
    void testImportNaceDetailsApiSuccessful() throws Exception {
        MockMultipartFile firstFile = new MockMultipartFile("file" , "filename.csv" , "text/csv" , "any csv".getBytes());

        List<NaceDetailsEntity> addedNaceRecords = prepareEntityList();
        when(naceService.createNaceDetailsFromCSV(Mockito.any())).thenReturn(addedNaceRecords);

        mockMvc.perform(MockMvcRequestBuilders.multipart(CREATE_NACE_RECORD_ENDPOINT).file(firstFile))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.responseStatus").value(SUCCESS))
                .andExpect(MockMvcResultMatchers.jsonPath("$.responseDetails").value(DATA_INSERTED_SUCCESS_MSG + addedNaceRecords.size() + RECORD_MSG));
    }

    /**
     * Successful Scenario : Get Nace Details CSV Api Test
     * To be tested
     */
    @Test
    void testGetNaceInformationApiSuccessful() throws Exception {
        List<NaceDetailsEntity> fetchedEntities = prepareEntityList();
        when(naceService.fetchNaceDetailsByOrderId(Mockito.anyLong())).thenReturn(fetchedEntities);
        MvcResult mvcResult = mockMvc.perform(get(GET_ORDER_ENDPOINT + ORDER_ID)).andExpect(status().isOk()).andReturn();
        JSONArray responseObject = new JSONArray(mvcResult.getResponse().getContentAsString());
        assertEquals(fetchedEntities.get(0).getOrder() , Long.valueOf(responseObject.getJSONObject(0).getString("order")));
    }

    /**
     * Successful Scenario : Test the HealthCheck endpoint
     */
    @Test
    void testHealthCheck() throws Exception {
        mockMvc.perform(get("/" + BASE_API_URL + "healthCheck")).andExpect(status().isOk()).
                andExpect(content().string(NACE_APPLICATION_IS_UP_RUNNING_MSG));
    }

    /**
     * Successful Scenario : Test fetchNaceDetailsByOrderId method in NaceService
     */
    @Test
    void testFetchNaceDetailsByOrderIdMethodSuccessful() {
        List<NaceDetailsEntity> fetchedEntities = prepareEntityList();
        when(naceService.fetchNaceDetailsByOrderId(Mockito.anyLong())).thenReturn(fetchedEntities);
        assertEquals(fetchedEntities , naceService.fetchNaceDetailsByOrderId(ORDER_ID));
    }

    /**
     * Negative Scenario : Test the post endpoint with "txt" file type
     */
    @Test
    void testPutNaceDetailsShouldFailForOtherFileTypes() throws Exception {
        MockMultipartFile multipartFile = new MockMultipartFile("file" , "filename.txt" , "text/plain" , "any text".getBytes());

        mockMvc.perform(MockMvcRequestBuilders.multipart(CREATE_NACE_RECORD_ENDPOINT).file(multipartFile))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.responseStatus").value(FAILED))
                .andExpect(MockMvcResultMatchers.jsonPath("$.responseDetails").value(FILE_VALIDATION_ERROR_MSG));
    }

    /**
     * Negative Scenario : Test the post endpoint without file
     */
    @Test
    void testPutNaceDetailsShouldFailUnSupportedMediaType() throws Exception {
        mockMvc.perform(post(CREATE_NACE_RECORD_ENDPOINT))
                .andExpect(status().isUnsupportedMediaType());
    }


    /**
     * Negative Scenario : Test the post endpoint without file name
     */
    @Test
    void testPutNaceDetailsShouldFailForEmptyCsvFileName() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.multipart(CREATE_NACE_RECORD_ENDPOINT).file("file" , null))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.responseStatus").value(FAILED))
                .andExpect(MockMvcResultMatchers.jsonPath("$.responseDetails").value(FILE_VALIDATION_ERROR_MSG));
    }

    /**
     * Negative Scenario : Get Nace Details CSV Api Test with Order I'd as "space"
     */
    @Test
    void testGetNaceInformationApiOrderIdAsSpace() throws Exception {
        mockMvc.perform(get(GET_ORDER_ENDPOINT + StringUtils.SPACE))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.responseStatus").value(FAILED));
    }

    /**
     * Negative Scenario : Get Nace Details CSV Api Test with Order I'd as decimal digit
     */
    @Test
    void testGetNaceInformationApiOrderIdAsInvalidFractionNumber() throws Exception {
        mockMvc.perform(get(GET_ORDER_ENDPOINT + "9028.02"))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.responseStatus").value(FAILED));

    }

    /**
     * Negative Scenario : Get Nace Details CSV Api Test with Invalid Order I'd
     */
    @Test
    void testGetNaceInformationApiOrderIdAsBigIntegerInvalidOrderId() throws Exception {
        mockMvc.perform(get(GET_ORDER_ENDPOINT + Integer.MAX_VALUE))
                .andExpect(status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.responseStatus").value(FAILED))
                .andExpect(MockMvcResultMatchers.jsonPath("$.responseDetails").value(DATA_NOT_FOUND_ERR_MSG));
    }

    /**
     * Negative Scenario : Get Nace Details CSV Api Test with Order I'd as Zero('0')
     */
    @Test
    void testGetNaceInformationApiOrderIdAsZero() throws Exception {
        mockMvc.perform(get(GET_ORDER_ENDPOINT + "0"))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.responseStatus").value(FAILED));
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
