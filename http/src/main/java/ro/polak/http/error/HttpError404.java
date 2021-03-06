/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2008-2016
 **************************************************/

package ro.polak.http.error;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import ro.polak.http.Statistics;
import ro.polak.http.servlet.HttpResponse;
import ro.polak.http.servlet.HttpResponseWrapper;

/**
 * 404 File Not Found HTTP error handler
 *
 * @author Piotr Polak piotr [at] polak [dot] ro
 * @since 201509
 */
public class HttpError404 implements HttpError {

    private String errorDocumentPath;

    /**
     * Default constructor.
     *
     * @param errorDocumentPath
     */
    public HttpError404(String errorDocumentPath) {
        this.errorDocumentPath = errorDocumentPath;
    }

    @Override
    public void serve(HttpResponse response) throws IOException {
        Statistics.addError404();

        response.setStatus(HttpResponse.STATUS_NOT_FOUND);
        response.setContentType("text/html");

        if (errorDocumentPath == null || errorDocumentPath.equals("")) {
            HtmlErrorDocument doc = new HtmlErrorDocument();
            doc.setTitle("Error 404 - File Not Found");
            doc.setMessage("<p>The server has not found anything matching the specified URL.</p>");

            String msg = doc.toString();
            response.getPrintWriter().write(msg);
            ((HttpResponseWrapper) response).flush();
        } else {
            File file = new File(errorDocumentPath);

            if (file.exists()) {
                response.setContentLength(file.length());
                ((HttpResponseWrapper) response).flushHeaders();
                InputStream inputStream = new FileInputStream(file);
                ((HttpResponseWrapper) response).serveStream(inputStream);
                ((HttpResponseWrapper) response).flush();
                try {
                    inputStream.close();
                } catch (IOException e) {
                }
            } else {
                // Serve 500
                HttpError500 error500 = new HttpError500();
                error500.setReason("404 error occurred, specified error handler (" + errorDocumentPath + ") was not found.");
                error500.serve(response);
            }
        }
    }
}
