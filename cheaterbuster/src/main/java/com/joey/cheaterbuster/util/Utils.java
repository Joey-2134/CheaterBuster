package com.joey.cheaterbuster.util;

import org.springframework.http.HttpHeaders;

/**
 * Utility class for common Leetify API operations.
 */
public class Utils {

    private Utils() {}

    /**
     * Creates HTTP headers with the Leetify API key.
     *
     * @param apiKey The Leetify API key to include in headers
     * @return HttpHeaders with the API key set
     */
    public static HttpHeaders createLeetifyHeaders(String apiKey) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("_leetify_key", apiKey);
        return headers;
    }
}
