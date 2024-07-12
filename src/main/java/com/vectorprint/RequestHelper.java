/*
 * Copyright 2023 E. Drenth Software.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.vectorprint;

/*-
 * #%L
 * VectorPrintCommon
 * %%
 * Copyright (C) 2011 - 2023 E. Drenth Software
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 *
 * @author eduard
 */
public class RequestHelper implements Closeable, AutoCloseable {
    private static final Logger LOGGER = LoggerFactory.getLogger(RequestHelper.class);
    
    private final HttpClient httpClient;

    private final Executor executor;

    /**
     * provide your own httpclient that will be (re)used by this helper;
     * @param httpClient 
     */
    public RequestHelper(HttpClient httpClient) {
        this.httpClient = httpClient;
        executor = httpClient.executor().orElse(null);
    }

    /**
     * Initialize this helper with a default httpclient.
     */
    public RequestHelper() {
        this(HttpClient.newBuilder().executor(Executors.newCachedThreadPool()).build());
    }
   

    /**
     * Calls {@link #request(int, java.net.http.HttpRequest...) }
     * @param request
     * @param timeoutSeconds
     * @return
     * @throws TimeoutException
     * @throws ExecutionException
     * @throws InterruptedException 
     */
    public String request(HttpRequest request, int timeoutSeconds) throws TimeoutException, ExecutionException, InterruptedException {
        List<String> r = request(timeoutSeconds, request);
        return r.isEmpty() ? "" : r.get(0);
    }
    
    /**
     * Executes all requests in parallel, waits timeoutSeconds for all to finish. Returns a list equal in size to the number of requests, holding the response bodies.
     * When a response code is not 200 a warning is logged and an empty String is added to the list.
     * @param timeoutSeconds
     * @param requests
     * @return
     * @throws TimeoutException
     * @throws ExecutionException
     * @throws InterruptedException 
     */
    public List<String> request(int timeoutSeconds, HttpRequest... requests) throws TimeoutException, ExecutionException, InterruptedException {
        List<String> rv = new ArrayList<>(2);
        List<CompletableFuture<HttpResponse<String>>> responses = new ArrayList<>(2);
        for (HttpRequest request : requests) {
            responses.add(httpClient
                .sendAsync(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8)));
        }
        CompletableFuture<Void> allOf = CompletableFuture.allOf(responses.toArray(new CompletableFuture[0]));

        allOf.get(timeoutSeconds, TimeUnit.SECONDS);
        short i = -1;
        for (CompletableFuture<HttpResponse<String>> cr : responses) {
            i++;
            HttpResponse<String> response = cr.get();
            if (response.statusCode() == HttpURLConnection.HTTP_OK) {
                if (!"null".equals(response.body())) rv.add(response.body()); else rv.add("");
            } else {
                rv.add("");
                LOGGER.warn(String.format("request to %s failed with %d", requests[i].uri().toString(), response.statusCode()));
            }                
        }

        return rv;
    }

    /**
     * Calls {@link ExecutorService#shutdown()} if applicable
     * @throws IOException
     */
    @Override
    public void close() throws IOException {
        if (executor != null && executor instanceof ExecutorService service) {service.shutdown();}
    }
}
