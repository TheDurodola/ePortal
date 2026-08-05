package com.school.eportal.proxy.paymentGateway;


import com.school.eportal.proxy.paymentGateway.dtos.requests.InitiatePaymentRequest;
import com.school.eportal.proxy.paymentGateway.dtos.responses.InitiatePaymentResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class PaystackPaymentGatewayImpl implements PaymentGatewayClient {

    private final RestClient paystackRestClient;

    public PaystackPaymentGatewayImpl(RestClient paystackRestClient) {
        this.paystackRestClient = paystackRestClient;
    }

    @Override
    public InitiatePaymentResponse initiatePayment(InitiatePaymentRequest request) {
        return paystackRestClient
                .post()
                .uri("/transaction/initialize")
                .body(request)
                .retrieve()
                .body(InitiatePaymentResponse.class);
    }
}
