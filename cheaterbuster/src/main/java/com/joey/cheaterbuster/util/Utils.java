package com.joey.cheaterbuster.util;

import org.springframework.http.HttpHeaders;

/**
 * Utility class for common Leetify API operations.
 */
public class Utils {

    private Utils() {}

    /**
     * Creates HTTP headers with the Leetify API key and User-Agent.
     *
     * @param apiKey The Leetify API key to include in headers
     * @param userAgent The User-Agent string to identify the client
     * @return HttpHeaders with the API key and User-Agent set
     */
    public static HttpHeaders createLeetifyHeaders(String apiKey, String userAgent) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("_leetify_key", apiKey);
        headers.set("User-Agent", userAgent);
        return headers;
    }

    /**
     * Creates HTTP headers with User-Agent for general API requests.
     *
     * @param userAgent The User-Agent string to identify the client
     * @return HttpHeaders with the User-Agent set
     */
    public static HttpHeaders createHeadersWithUserAgent(String userAgent) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("User-Agent", userAgent);
        return headers;
    }
}
