package com.luxoft.naceapplication.repositories;

import com.luxoft.naceapplication.dao.entities.NaceDetailsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NaceDetailsRepository extends JpaRepository<NaceDetailsEntity, Long> {

    List<NaceDetailsEntity> findByOrder(Long order);

    void deleteByOrder(Long order);
}
