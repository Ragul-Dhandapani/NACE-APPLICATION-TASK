package com.luxoft.naceapplication.services;

import com.luxoft.naceapplication.component.NaceAddDetailsExecutor;
import com.luxoft.naceapplication.constants.NaceApplicationConstants;
import com.luxoft.naceapplication.dao.entities.NaceDetailsEntity;
import com.luxoft.naceapplication.repositories.NaceDetailsRepository;
import com.opencsv.bean.CsvToBeanBuilder;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import javax.validation.ConstraintViolationException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

@Service
public class NaceService {

    private static final Logger LOG = LoggerFactory.getLogger(NaceService.class);

    @Autowired
    private NaceAddDetailsExecutor naceAddDetailsExecutor;

    @Autowired
    private NaceDetailsRepository naceDetailsRepository;


    public List<NaceDetailsEntity> createNaceDetails(String filePath) throws IOException, InterruptedException {
        List<NaceDetailsEntity> naceDetailsEntityList;

        if (!StringUtils.isEmpty(filePath)) {

            /** Converting the CSV to POJO */
            naceDetailsEntityList = new CsvToBeanBuilder(new FileReader(filePath))
                    .withType(NaceDetailsEntity.class)
                    .withIgnoreLeadingWhiteSpace(true)
                    .build()
                    .parse();

            /** Executor Service used which help for multi-threading */
            naceDetailsEntityList = naceAddDetailsExecutor.execute(naceDetailsEntityList , naceDetailsRepository);

            LOG.info("NaceService Imported CSV successfully");
        } else {
            throw new ConstraintViolationException("filePath cannot be empty in request header" , null);
        }
        return naceDetailsEntityList;
    }

    /**
     * Initially it checks the "order information" in spring cache(in-memory) if data isn't available
     * then it connects to database and retrieve the order information
     * <p>
     * Note: Key should be defined in same structure, so it's easy to identify in cache
     *
     * @param order
     * @return
     */
    @Cacheable(cacheNames = NaceApplicationConstants.NACE_ORDER_DETAILS_CACHE, key = "#order")
    public List<NaceDetailsEntity> fetchNaceDetailsByOrderId(Long order) {
        return naceDetailsRepository.findByOrder(order);
    }
}
