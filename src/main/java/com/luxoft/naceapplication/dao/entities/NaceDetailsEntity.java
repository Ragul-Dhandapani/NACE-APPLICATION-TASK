package com.luxoft.naceapplication.dao.entities;

import com.opencsv.bean.CsvBindByName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Builder
@Entity
@Table(name = "NACE_INFORMATION")
@NoArgsConstructor
@AllArgsConstructor
public class NaceDetailsEntity implements Serializable {
    private static final long serialVersionUID = -2L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long entityId;

    @Column(name = "ORDERS_INFO")
    @CsvBindByName(column = "Order")
    private Long order;

    @Column(name = "LEVEL")
    @CsvBindByName(column = "Level")
    private Long level;

    @Column(name = "CODE")
    @CsvBindByName(column = "Code")
    private String code;

    @Column(name = "PARENT")
    @CsvBindByName(column = "Parent")
    private String parent;

    @Column(name = "DESCRIPTION")
    @CsvBindByName(column = "Description")
    private String description;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(name = "ITEM_INCLUDES")
    @CsvBindByName(column = "This item includes")
    private String itemIncludes;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(name = "ITEM_ALSO_INCLUDES")
    @CsvBindByName(column = "This item also includes")
    private String itemAlsoIncludes;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(name = "RULINGS")
    @CsvBindByName(column = "Rulings")
    private String rulings;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(name = "ITEM_EXCLUDES")
    @CsvBindByName(column = "This item excludes")
    private String itemExcludes;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(name = "REFERENCES_ISIC")
    @CsvBindByName(column = "Reference to ISIC Rev. 4")
    private String referencesIsic;


}
