package com.example.demo.service.impl;

import com.example.demo.common.VNPayConstant;
import com.example.demo.common.VNPayHelper;
import com.example.demo.dto.request.payment.CreateTransaction;
import com.example.demo.dto.request.vnpay.VNPayOrderRequest;
import com.example.demo.model.Order;
import com.example.demo.repository.OrderRepository;
import com.example.demo.service.PaymentIntegrationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class VNPayServiceImpl implements PaymentIntegrationService {

    private final OrderRepository orderRepository;

    public VNPayServiceImpl(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    @Transactional
    public Map<String, Object> createOrder(HttpServletRequest request, CreateTransaction createTransaction) throws UnsupportedEncodingException {
        try {
//            Account account = (Account) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

//            if (!RedisServiceImpl.exists("pickseat:" + orderRequestDTO.getPerformId() + ":" + account.getId())) {
//                throw new RuntimeException("You have not picked any seat yet");
//            }


//            Optional<Payment> paymentTemp = paymentRepository.existsPaymentWithPendingStatusOfMyOwn(
//                    account.getId(),
//                    orderRequestDTO.getPerformId()
//            );

//            if (paymentTemp.isPresent()) {
//                throw new RuntimeException("You have a pending payment");
//            }

//            Perform perform = performRepository.findById(orderRequestDTO.getPerformId()).orElseThrow(() -> new RuntimeException("Perform not found"));
//            BankCinema bankCinema = perform.getCinemaRoom().getCinema().getBankCinemas().stream().filter(bank -> bank.getBankType() == BankType.VNPAY).findFirst().orElseThrow(() -> new RuntimeException("Bank not found"));
//            BusinessBank businessBank = bankCinema.getBusinessBank();
//

//            String vnp_TmnCode = businessBank.getData().get("vnp_TmnCode").toString();
//            String vnp_SecureHash = businessBank.getData().get("vnp_SecureHash").toString();

//            System.out.println(vnp_SecureHash + " " + vnp_TmnCode);

//            if (vnp_TmnCode == null || vnp_SecureHash == null) {
//                throw new RuntimeException("Error: Bank not found with VNPAY");
//            }

            Optional<Order> order = orderRepository.findById(createTransaction.getOrderId());
            if (order.isEmpty()) {
                throw new RuntimeException("Order not found");
            }

            Map<String, Object> payload = this.generateVNPayPayload(VNPayOrderRequest.builder()
                    .orderId(createTransaction.getOrderId())
                    .orderInfo("Mua san pham - Ma don hang: " + createTransaction.getOrderId())
                    .ipAddr(VNPayHelper.getIpAddress(request))
                    .amount(order.get().getFinalPrice().multiply(BigDecimal.valueOf(100000)).longValue()) // Convert to VND
                    .build());

            String queryUrl = this.generateQueryURL(payload);
            String paymentUrl = this.generatePaymentURL(VNPayConstant.VNP_PAY_URL, queryUrl);
            payload.put("redirect_url", paymentUrl);

            return payload;
        } catch (Exception ex) {
            throw new RuntimeException("Error: " + ex.getMessage());
        }
    }


    private Map<String, Object> generateVNPayPayload(VNPayOrderRequest orderRequest) {
        String createDate = VNPayHelper.generateDate(false);
        String expireDate = VNPayHelper.generateDate(true);

        return new HashMap<String, Object>() {
            {
                put("vnp_Version", VNPayConstant.VNP_VERSION);
                put("vnp_Command", VNPayConstant.VNP_COMMAND_ORDER);
                put("vnp_TmnCode", VNPayConstant.VNP_TMNCODE);
                put("vnp_Amount", String.valueOf(orderRequest.getAmount()));
                put("vnp_CurrCode", VNPayConstant.VNP_CURRENCY_CODE);
                put("vnp_TxnRef", orderRequest.getOrderId().toString());
                put("vnp_OrderInfo", orderRequest.getOrderInfo());
                put("vnp_OrderType", VNPayConstant.ORDER_TYPE);
                put("vnp_Locale", VNPayConstant.VNP_LOCALE);
                put("vnp_ReturnUrl", VNPayConstant.VNP_RETURN_URL);
                put("vnp_IpAddr", orderRequest.getIpAddr());
                put("vnp_CreateDate", createDate);
                put("vnp_ExpireDate", expireDate);
            }
        };
    }

    private String generateQueryURL(Map<String, Object> payload) throws UnsupportedEncodingException {
        String secretHash = VNPayConstant.VNP_SECRETKEY;
        try {
            String queryUrl = this.getQueryUrl(payload).get("queryUrl") + "&vnp_SecureHash=" + VNPayHelper.hmacSHA512(secretHash, getQueryUrl(payload).get("hashData"));
            return queryUrl;
        } catch (Exception e) {
            throw new RuntimeException("Error: " + e.getMessage());
        }
    }

    private String generatePaymentURL(String VNPayURL, String queryURL) {
        return VNPayURL + "?" + queryURL;
    }

    //    @Transactional
    public void handlePayment(Map<String, String> params) {
        String vnpResponseCode = params.get("vnp_ResponseCode");
        String vnpTxnRef = params.get("vnp_TxnRef");
        String vnpAmount = params.get("vnp_Amount");
        String vnpOrderInfo = params.get("vnp_OrderInfo");
        String vnpTransactionNo = params.get("vnp_TransactionNo");
        String vnpTransactionStatus = params.get("vnp_TransactionStatus");
        String vnpPayDate = params.get("vnp_PayDate");
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");

        try {
            switch (vnpResponseCode) {
                case "00":
                    break;
                case "01":
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error: " + e.getMessage());
        }
    }


    private Map<String, String> getQueryUrl(Map<String, Object> payload) throws UnsupportedEncodingException {
        List<String> fieldNames = new ArrayList<>(payload.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();
        Iterator itr = fieldNames.iterator();
        while (itr.hasNext()) {
            String fieldName = (String) itr.next();
            String fieldValue = (String) payload.get(fieldName);
            if ((fieldValue != null) && (!fieldValue.isEmpty())) {
                // Build hash data
                hashData.append(fieldName);
                hashData.append('=');
                hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));

                // Build query
                query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII));
                query.append('=');
                query.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));
                if (itr.hasNext()) {

                    query.append('&');
                    hashData.append('&');
                }
            }
        }
        return new HashMap<>() {
            {
                put("queryUrl", query.toString());
                put("hashData", hashData.toString());
            }
        };
    }
}
