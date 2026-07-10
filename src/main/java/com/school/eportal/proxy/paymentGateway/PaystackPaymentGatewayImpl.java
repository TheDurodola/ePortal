package com.school.eportal.proxy.paymentGateway;


import com.school.eportal.proxy.paymentGateway.dtos.requests.InitiatePaymentRequest;
import com.school.eportal.proxy.paymentGateway.dtos.responses.InitiatePaymentResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component

public class PaystackPaymentGatewayImpl implements PaymentGatewayClient {

    private final RestClient client;

    public PaystackPaymentGatewayImpl() {
         client = RestClient.builder()
                 .baseUrl("https://api.paystack.co")
                 .defaultHeader("Accept", "application/json")
                 .build();
    }

    @Override
    public InitiatePaymentResponse initiatePayment(InitiatePaymentRequest request) {

        RestClient.RequestBodySpec body = client
                .post()
                .uri("/initiatePayment")
                .body(request);
        return null;
    }


}
