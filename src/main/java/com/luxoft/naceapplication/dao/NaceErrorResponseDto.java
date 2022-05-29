package com.luxoft.naceapplication.dao;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class NaceErrorResponseDto implements Serializable {

    private static final long serialVersionUID = -4762892889298267276L;

    private String responseStatus;
    private String responseDetails;
    private LocalDateTime responseTimestamp;
}
