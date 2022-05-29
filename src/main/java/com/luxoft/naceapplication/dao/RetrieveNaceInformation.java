package com.luxoft.naceapplication.dao;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class RetrieveNaceInformation implements Serializable {
    private static final long serialVersionUID = 8746518191002067892L;

    private Long order;
    private Long level;
    private String code;
    private String parent;
    private String description;
    private String itemIncludes;
    private String itemAlsoIncludes;
    private String rulings;
    private String referencesIsic;
}
