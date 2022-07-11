package com.luxoft.naceapplication;

import com.luxoft.naceapplication.component.NaceAddDetailsExecutor;
import com.luxoft.naceapplication.dao.entities.NaceDetailsEntity;
import com.luxoft.naceapplication.repositories.NaceDetailsRepository;
import com.luxoft.naceapplication.services.NaceService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.util.ReflectionTestUtils;

import javax.validation.ConstraintViolationException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@TestInstance(Lifecycle.PER_CLASS)
class NaceServiceLayerTests {

    static final String NACE_DATA_CSV = "NACE_DATA.csv";

    @Autowired
    private NaceService naceService;

    @Autowired
    private NaceDetailsRepository naceDetailsRepository;

    @Autowired
    private NaceAddDetailsExecutor naceAddDetailsExecutor;

    private List<NaceDetailsEntity> persistedNaceDetails;

    @BeforeAll
    void setUp() {
        naceService = new NaceService();
        naceDetailsRepository = mock(NaceDetailsRepository.class);
        naceAddDetailsExecutor = mock(NaceAddDetailsExecutor.class);
        ReflectionTestUtils.setField(naceService , "naceDetailsRepository" , naceDetailsRepository);
        ReflectionTestUtils.setField(naceService , "naceAddDetailsExecutor" , naceAddDetailsExecutor);
    }

    @Test
    void testPutNaceDetailsShouldBeSuccessful()
            throws NumberFormatException, ConstraintViolationException, IOException, InterruptedException {

        List<NaceDetailsEntity> addedNaceRecords = constructNaceDetailsEntityList();
        when(naceService.createNaceDetailsFromCSV(new FileReader(NACE_DATA_CSV))).thenReturn(addedNaceRecords);
        when(naceAddDetailsExecutor.execute(addedNaceRecords , naceDetailsRepository)).thenReturn(addedNaceRecords);

        persistedNaceDetails = naceService.createNaceDetailsFromCSV(new FileReader(NACE_DATA_CSV));
        assertEquals(2 , persistedNaceDetails.size());
    }

    @Test
    void testPutNaceDetailShouldFailWhenFilePathIsNull() {

        assertThrows(IllegalArgumentException.class , () -> naceService.createNaceDetailsFromCSV(null));
    }


    private List<NaceDetailsEntity> constructNaceDetailsEntityList() {
        List<NaceDetailsEntity> addedNaceRecords = new ArrayList<>();
        addedNaceRecords.add(NaceDetailsEntity.builder().code("11").description("description_1").entityId(1L)
                .itemAlsoIncludes("itemAlsoIncludes_1").itemIncludes("itemIncludes_1").itemExcludes("itemExcludes_1")
                .rulings("rulings_1").referencesIsic("1").order(1111L).build());
        addedNaceRecords.add(NaceDetailsEntity.builder().code("22").description("description_2").entityId(2L)
                .itemAlsoIncludes("itemAlsoIncludes_2").itemIncludes("itemIncludes_2").itemExcludes("itemExcludes_2")
                .rulings("rulings_2").referencesIsic("2").order(2222L).build());
        return addedNaceRecords;
    }
}
