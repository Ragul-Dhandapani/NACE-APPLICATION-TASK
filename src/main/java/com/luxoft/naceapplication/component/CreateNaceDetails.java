package com.luxoft.naceapplication.component;

import com.luxoft.naceapplication.dao.entities.NaceDetailsEntity;
import com.luxoft.naceapplication.repositories.NaceDetailsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.concurrent.Callable;

public class CreateNaceDetails implements Callable<List<NaceDetailsEntity>> {

    private static final Logger LOG = LoggerFactory.getLogger(CreateNaceDetails.class);

    private final NaceDetailsRepository naceDetailsRepository;
    private final List<NaceDetailsEntity> naceDetails;

    @Autowired
    public CreateNaceDetails(List<NaceDetailsEntity> naceDetails , NaceDetailsRepository naceDetailsRepository) {
        this.naceDetails = naceDetails;
        this.naceDetailsRepository = naceDetailsRepository;
    }

    @Override
    public List<NaceDetailsEntity> call() throws Exception {
        List<NaceDetailsEntity> insertedDetails = this.naceDetailsRepository.saveAll(naceDetails);
        LOG.info("[{}] CreateNaceDetails Call() Method record count" , insertedDetails.size());
        return insertedDetails;
    }
}
