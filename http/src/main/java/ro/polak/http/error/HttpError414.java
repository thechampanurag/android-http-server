/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2008-2016
 **************************************************/

package ro.polak.http.error;

import java.io.IOException;

import ro.polak.http.servlet.HttpResponse;
import ro.polak.http.servlet.HttpResponseWrapper;

/**
 * 414 URI Too Long
 *
 * @author Piotr Polak piotr [at] polak [dot] ro
 * @since 201509
 */
public class HttpError414 implements HttpError {

    @Override
    public void serve(HttpResponse response) throws IOException {
        String msg = "Error 414 - URI Too Long";
        response.setStatus(HttpResponse.STATUS_URI_TOO_LONG);
        response.setContentType("text/plain");
        response.getPrintWriter().write(msg);
        ((HttpResponseWrapper) response).flush();
    }
}
