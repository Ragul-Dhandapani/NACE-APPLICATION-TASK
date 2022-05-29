package com.luxoft.naceapplication.dao;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Builder
public class AddNaceInformationDto implements Serializable {

    private static final long serialVersionUID = -1L;

    private String responseStatus;
    private String responseDetails;
    private LocalDateTime responseTimestamp;
}
