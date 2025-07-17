package com.tracker.job_ts.auth.config;

import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;
/**
 * Decorator for ServerHttpResponse to capture the response body.
 * It joins all DataBuffers into a single buffer, reads its content,
 * passes it to a consumer, and then creates a new DataBuffer to
 * ensure the original response stream is not corrupted.
 */
public class ServerHttpResponseDecoratorWithBody extends ServerHttpResponseDecorator {

    private final Consumer<String> bodyConsumer;

    /**
     * Constructs a new ServerHttpResponseDecoratorWithBody.
     * @param delegate The original ServerHttpResponse to decorate.
     * @param bodyConsumer A consumer to which the captured response body will be passed.
     */
    public ServerHttpResponseDecoratorWithBody(ServerHttpResponse delegate, Consumer<String> bodyConsumer) {
        super(delegate);
        this.bodyConsumer = bodyConsumer;
    }

    /**
     * Overrides the writeWith method to intercept and capture the response body.
     * @param body The publisher of DataBuffers representing the response body.
     * @return A Mono<Void> indicating when the writing is complete.
     */
    @Override
    public Mono<Void> writeWith(org.reactivestreams.Publisher<? extends DataBuffer> body) {
        // Join all data buffers into a single buffer.
        // This ensures we get the complete response body, even if it's chunked.
        return DataBufferUtils.join(Flux.from(body))
                .flatMap(dataBuffer -> {
                    // Read the content of the joined buffer into a byte array.
                    byte[] bytes = new byte[dataBuffer.readableByteCount()];
                    dataBuffer.read(bytes);
                    // Release the original joined buffer to prevent memory leaks.
                    DataBufferUtils.release(dataBuffer);

                    // Convert the byte array to a String using UTF-8 encoding.
                    String responseBody = new String(bytes, StandardCharsets.UTF_8);
                    // Pass the captured full body to the provided consumer.
                    bodyConsumer.accept(responseBody);

                    // Create a new buffer from the captured bytes.
                    // This is crucial to ensure the actual response stream receives the data
                    // and is not empty or corrupted after our interception.
                    DataBuffer newBuffer = getDelegate().bufferFactory().wrap(bytes);
                    // Continue the original write operation with the new buffer.
                    return super.writeWith(Mono.just(newBuffer));
                })
                // Handle cases where the response body might be empty (e.g., 204 No Content).
                // In such cases, DataBufferUtils.join might return an empty Mono,
                // so we switch to writing an empty Mono to the delegate.
                .switchIfEmpty(super.writeWith(Mono.empty()));
    }
}