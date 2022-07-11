package com.luxoft.naceapplication;

import com.luxoft.naceapplication.dao.entities.NaceDetailsEntity;
import com.luxoft.naceapplication.repositories.NaceDetailsRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@TestInstance(Lifecycle.PER_CLASS)
class NaceRepositoryLayerTests {

    static final long ORDER = 11111L;
    static final long ORDER_2 = 2222L;
    static final String DESCRIPTION_1 = "description_1";

    @Autowired
    private NaceDetailsRepository naceDetailsRepository;

    private List<NaceDetailsEntity> naceRecordInformation;

    @BeforeAll
    void setUp() {
        naceRecordInformation = new ArrayList<>();
        naceRecordInformation.add(NaceDetailsEntity.builder().code("11").description(DESCRIPTION_1).entityId(1L)
                .itemAlsoIncludes("itemAlsoIncludes_1").itemIncludes("itemIncludes_1").itemExcludes("itemExcludes_1").rulings("rulings_1").referencesIsic("1")
                .order(ORDER).build());
        naceRecordInformation.add(NaceDetailsEntity.builder().code("22").description("description_2").entityId(2L)
                .itemAlsoIncludes("itemAlsoIncludes_2").itemIncludes("itemIncludes_2").itemExcludes(" ").rulings("rulings_2").referencesIsic("2")
                .order(ORDER_2).build());
    }

    /**
     * Successful Scenario :  Test Repository to add all the above orders
     */
    @Test
    void testSaveAllNaceDetails() {

        naceDetailsRepository.saveAll(naceRecordInformation);
        List<NaceDetailsEntity> result = naceDetailsRepository.findByOrder(ORDER);
        assertEquals(result.get(0).getDescription() , DESCRIPTION_1);
    }

    /**
     * Successful Scenario :  Test Repository to delete the second order and check the size of it.
     */
    @Test
    void testDeleteByOrder() {

        naceDetailsRepository.saveAll(naceRecordInformation);

        naceDetailsRepository.deleteByOrder(ORDER_2);
        List<NaceDetailsEntity> findById = naceDetailsRepository.findByOrder(ORDER_2);
        assertEquals(findById.size() , 0);
    }
}
