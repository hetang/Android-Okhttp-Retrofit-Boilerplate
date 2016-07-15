package com.infra.managers.responses;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Generic wrapper for BFF responses containing both the BFF data and connection information.
 *
 */
@Data
@NoArgsConstructor
public class ServiceResponse<T> {
    /**
     * Generic markers for response states derived from the HTTP error code ranges.
     */
    public static enum ResponseState {
        OK, UNAUTHORIZED, SERVER_ERROR, NOT_FOUND, OTHER_ERROR;

        /**
         * Transform a raw HTTP response code into a response state.
         *
         * @param code the HTTP response code
         * @return the matching response state
         */
        public static ResponseState parseHttpErrorCode(int code) {
            if(code == 200) {
                return OK;
            }

            if(code == 401) {
                return UNAUTHORIZED;
            }

            if(code >= 500) {
                return SERVER_ERROR;
            }

            if(code == 404) {
                return NOT_FOUND;
            }

            return OTHER_ERROR;
        }
    }

    /** The BFF data. Might be null for response states other than {@code ResponseState.OK} */
    T data;

    /** The response state derived from the {@code httpResponseCode} */
    ResponseState responseState;

    /** An optional message passed along the HTTP response */
    String responseMessage;

    /** The HTTP response code */
    int httpResponseCode;
}
