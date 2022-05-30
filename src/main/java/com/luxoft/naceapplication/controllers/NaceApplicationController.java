package com.luxoft.naceapplication.controllers;

import com.luxoft.naceapplication.dao.AddNaceInformationDto;
import com.luxoft.naceapplication.dao.RetrieveNaceInformation;
import com.luxoft.naceapplication.dao.entities.NaceDetailsEntity;
import com.luxoft.naceapplication.exceptions.DataNotFoundException;
import com.luxoft.naceapplication.services.NaceService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.ConstraintViolationException;
import javax.validation.constraints.*;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.luxoft.naceapplication.constants.NaceApplicationConstants.*;

@RestController
@RequestMapping(BASE_API_URL)
@Validated
@Api(value = "Nace Application Data Retrieval")
public class NaceApplicationController {

    private static final Logger LOG = LoggerFactory.getLogger(NaceApplicationController.class);

    private NaceService naceService;

    @Autowired
    public NaceApplicationController(NaceService naceService) {
        this.naceService = naceService;
    }

    @ApiOperation(value = "Health Check API to verify the application is up and running")
    @GetMapping(path = "healthCheck")
    public ResponseEntity<String> healthCheck() {
        return new ResponseEntity<>(NACE_APPLICATION_IS_UP_RUNNING_MSG , HttpStatus.OK);
    }

    @ApiOperation(value = "Import the CSV, create the data and store into Database")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Data is imported successfully") ,
            @ApiResponse(code = 400, message = INVALID_CSV_EXTENSIONS_ERR_MSG + "\n" + FILE_CANNOT_BE_EMPTY_ERR_MSG
                    + " \n" + FILE_CANNOT_BE_BLANK_ERR_MSG)
    })
    @PostMapping(path = "create")
    public ResponseEntity<AddNaceInformationDto> importNaceDetails(
            @RequestHeader(name = FILE_PATH)
            @NotEmpty(message = FILE_CANNOT_BE_EMPTY_ERR_MSG)
            @NotBlank(message = FILE_CANNOT_BE_BLANK_ERR_MSG)
            @Pattern(regexp = ".+(\\.csv)$", message = INVALID_CSV_EXTENSIONS_ERR_MSG) final String filePath)
            throws IOException, ConstraintViolationException, InterruptedException {

        AddNaceInformationDto resultDto;

        LOG.info("Starts Import CSV Process");
        List<NaceDetailsEntity> entityData = naceService.createNaceDetails(filePath.trim());
        String entityDataSize = (entityData.isEmpty()) ? ZERO : String.valueOf(Math.max(entityData.size() , 0));

        if (!ZERO.equals(entityDataSize)) {
            final String successMsg = DATA_INSERTED_SUCCESS_MSG.concat(entityDataSize).concat(RECORD_MSG);
            resultDto = AddNaceInformationDto.builder().responseStatus(SUCCESS).responseDetails(successMsg).
                    responseTimestamp(LocalDateTime.now()).build();
        } else {
            final String failureDetailMsg = DATA_IMPORT_FAILURE_MSG.concat(entityDataSize).concat(RECORD_MSG);
            resultDto = AddNaceInformationDto.builder().responseStatus(FAILED).responseDetails(failureDetailMsg).
                    responseTimestamp(LocalDateTime.now()).build();
        }
        LOG.info("Ends Import CSV Process");

        return new ResponseEntity<>(resultDto , HttpStatus.OK);
    }

    @ApiOperation(value = "Retrieve the Data from Database")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Data retrieve is successful") ,
            @ApiResponse(code = 400, message = NON_FRACTION_ERROR_MSG + "\n"
                    + ORDER_EMPTY_OR_BLANK_ERR_MSG + "\n"
                    + ORDER_VALUE_CANNOT_BE_ZERO_ERR_MSG)
    })
    @GetMapping(path = "order/{order}")
    public ResponseEntity<List<RetrieveNaceInformation>> getNaceInformation(@PathVariable(name = "order", required = true)
                                                                            @NotEmpty(message = ORDER_EMPTY_OR_BLANK_ERR_MSG)
                                                                            @Digits(message = NON_FRACTION_ERROR_MSG, fraction = 0, integer = 10)
                                                                            @Min(value = 1, message = ORDER_VALUE_CANNOT_BE_ZERO_ERR_MSG) final String order) {

        List<RetrieveNaceInformation> orderInformation = new ArrayList<>();
        List<NaceDetailsEntity> entityDataList = naceService.fetchNaceDetailsByOrderId(Long.valueOf(order));

        if (entityDataList.isEmpty())
            throw new DataNotFoundException();

        entityDataList.parallelStream().forEach(list ->
            orderInformation.add(RetrieveNaceInformation.builder().order(list.getOrder())
                    .description(list.getDescription())
                    .level(list.getLevel())
                    .code(list.getCode())
                    .parent(list.getParent())
                    .itemIncludes(list.getItemIncludes())
                    .itemAlsoIncludes(list.getItemAlsoIncludes())
                    .referencesIsic(list.getReferencesIsic())
                    .rulings(list.getRulings()).build())
        );

        return new ResponseEntity<>(orderInformation , HttpStatus.OK);

    }

}