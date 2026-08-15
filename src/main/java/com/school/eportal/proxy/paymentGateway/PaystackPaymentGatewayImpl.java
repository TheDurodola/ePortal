package com.school.eportal.proxy.paymentGateway;


import com.school.eportal.proxy.paymentGateway.dtos.requests.InitiatePaymentRequest;
import com.school.eportal.proxy.paymentGateway.dtos.responses.InitiatePaymentResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HexFormat;

import static com.school.eportal.configs.PaymentGatewayConfig.getSecretKey;


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

    public static boolean isValidSignature(String payload, String signature) {
        try {
            Mac hmac = Mac.getInstance("HmacSHA512");
            hmac.init(new SecretKeySpec(getSecretKey().getBytes(StandardCharsets.UTF_8), "HmacSHA512"));
            byte[] hash = hmac.doFinal(payload.getBytes(StandardCharsets.UTF_8));
            String computed = HexFormat.of().formatHex(hash);
            return MessageDigest.isEqual(
                    computed.getBytes(StandardCharsets.UTF_8),
                    signature.getBytes(StandardCharsets.UTF_8)
            );
        } catch (Exception e) {
            return false;
        }
    }
}
