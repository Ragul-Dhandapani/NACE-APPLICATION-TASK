package com.luxoft.naceapplication.constants;

public class NaceApplicationConstants {

    public static final String BASE_API_URL = "nace/api/v1/";
    public static final String FILE_PATH = "file";
    public static final String DATA_INSERTED_SUCCESS_MSG = "DATA IS SUCCESSFULLY ADDED INTO SYSTEM AND THE COUNT IS ";
    public static final String DATA_IMPORT_FAILURE_MSG = "CSV DATA IS NOT INSERTED, AND THE COUNT IS ";
    public static final String RECORD_MSG = " records";
    public static final String SUCCESS = "SUCCESS";
    public static final String FAILED = "FAILED";
    public static final String ZERO = "ZERO";

    //Error or Exceptions
    public static final String NON_FRACTION_ERROR_MSG = "Order value must follow non-fraction integer";
    public static final String ORDER_VALUE_CANNOT_BE_ZERO_ERR_MSG = "Order value must be greater than zero";
    public static final String ORDER_EMPTY_OR_BLANK_ERR_MSG = "Order value must not be empty/blank in the api request url";

    public static final String ISR_MSG = "Something gone wrong, please retry or try again later";
    public static final String DATA_NOT_FOUND_ERR_MSG = "Given Order Not available in the System";
    public static final String NACE_ORDER_DETAILS_CACHE = "nace_order_details_cache";
    public static final String FILE_CANNOT_BE_EMPTY_ERR_MSG = "Value of request header named 'file' cannot be empty";
    public static final String FILE_CANNOT_BE_BLANK_ERR_MSG = "Value of request header named 'file' cannot be blank";
    public static final String INVALID_CSV_EXTENSIONS_ERR_MSG = "File types other than CSV extensions are not allowed";
    public static final String NACE_APPLICATION_IS_UP_RUNNING_MSG = "Nace Application is UP & Running";
}
