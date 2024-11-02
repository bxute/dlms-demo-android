package com.example.nativelib.interceptors;

import io.grpc.*;

public class LoggingClientInterceptor implements ClientInterceptor {
    @Override
    public <ReqT, RespT> ClientCall<ReqT, RespT> interceptCall(
            MethodDescriptor<ReqT, RespT> method,
            CallOptions callOptions,
            Channel next) {

        System.out.println("Making gRPC call to method: " + method.getFullMethodName());

        return new ForwardingClientCall.SimpleForwardingClientCall<ReqT, RespT>(next.newCall(method, callOptions)) {
            @Override
            public void start(Listener<RespT> responseListener, Metadata headers) {
                System.out.println("Starting gRPC call with headers: " + headers);
                super.start(responseListener, headers);
            }

            @Override
            public void request(int numMessages) {
                System.out.println("Requesting " + numMessages + " messages");
                super.request(numMessages);
            }

            @Override
            public void sendMessage(ReqT message) {
                System.out.println("Sending message: " + message);
                super.sendMessage(message);
            }
        };
    }
}

