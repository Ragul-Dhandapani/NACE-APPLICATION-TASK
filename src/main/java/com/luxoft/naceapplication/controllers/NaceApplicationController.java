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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.ConstraintViolationException;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.luxoft.naceapplication.constants.NaceApplicationConstants.*;

@RestController
@RequestMapping(BASE_API_URL)
@Validated
@Api(value = "Nace Application Data Retrieval")
public class NaceApplicationController {

    private static final Logger LOG = LoggerFactory.getLogger(NaceApplicationController.class);


    private final NaceService naceService;

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
            @ApiResponse(code = 400, message = FILE_VALIDATION_ERROR_MSG)
    })
    @PostMapping(path = "importNaceDetails", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AddNaceInformationDto> importNaceDetailsCSVInformation(
            @RequestParam("file") @NotNull final MultipartFile file)
            throws IOException, ConstraintViolationException {

        AddNaceInformationDto resultDto = null;

        if (!file.isEmpty() && Objects.requireNonNull(file.getContentType()).endsWith("csv")) {
            LOG.info("Starts Import CSV Process");

            try (Reader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {

                List<NaceDetailsEntity> entityData = naceService.createNaceDetailsFromCSV(reader);
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
            } catch (InterruptedException | IOException fileReaderException) {
                throw new IOException("Unable to Import the CSV file due to technical issues" , fileReaderException);
            }
        } else {
            throw new ConstraintViolationException(FILE_VALIDATION_ERROR_MSG , null);
        }
        return new ResponseEntity<>(resultDto , HttpStatus.OK);
    }

    @ApiOperation(value = "Retrieve the Data from Database")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Data retrieve is successful") ,
            @ApiResponse(code = 400, message = NON_FRACTION_ERROR_MSG + "\n"
                    + ORDER_EMPTY_OR_BLANK_ERR_MSG + "\n"
                    + ORDER_VALUE_CANNOT_BE_ZERO_ERR_MSG)
    })
    @GetMapping(path = "order/{order}", produces = MediaType.APPLICATION_JSON_VALUE)
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