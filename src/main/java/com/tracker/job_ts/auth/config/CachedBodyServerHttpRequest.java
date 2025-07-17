package com.tracker.job_ts.auth.config;

import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import reactor.core.publisher.Flux;

public class CachedBodyServerHttpRequest extends ServerHttpRequestDecorator {
    private final byte[] cachedBody;
    private final DataBufferFactory bufferFactory;

    public CachedBodyServerHttpRequest(ServerHttpRequest delegate, byte[] cachedBody, DataBufferFactory bufferFactory) {
        super(delegate);
        this.cachedBody = cachedBody;
        this.bufferFactory = bufferFactory;
    }

    @Override
    public Flux<DataBuffer> getBody() {
        return Flux.just(bufferFactory.wrap(cachedBody));
    }
}