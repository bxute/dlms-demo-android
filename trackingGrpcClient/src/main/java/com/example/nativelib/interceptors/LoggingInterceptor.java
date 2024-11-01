package com.example.nativelib.interceptors;

import io.grpc.*;

import java.util.logging.Level;
import java.util.logging.Logger;

public class LoggingInterceptor implements ClientInterceptor {
    private static final Logger logger = Logger.getLogger(LoggingInterceptor.class.getName());

    @Override
    public <ReqT, RespT> ClientCall<ReqT, RespT> interceptCall(MethodDescriptor<ReqT, RespT> method, CallOptions callOptions, Channel next) {
        return new ForwardingClientCall.SimpleForwardingClientCall<ReqT, RespT>(next.newCall(method, callOptions)) {
            private int attemptCount = 0;
            @Override
            public void start(Listener<RespT> responseListener, Metadata headers) {
                responseListener = new Listener<RespT>() {
                    @Override
                    public void onClose(Status status, Metadata trailers) {
                        if (status.isOk()) {
                            logger.log(Level.INFO, "RPC call successful");
                        } else {
                            attemptCount++;
                            logger.log(Level.WARNING, "RPC call failed: {0}. Attempt: {1}", new Object[]{status, attemptCount});
                        }
                        super.onClose(status, trailers);
                    }
                };
                super.start(responseListener, headers);
            }
        };
    }
}

