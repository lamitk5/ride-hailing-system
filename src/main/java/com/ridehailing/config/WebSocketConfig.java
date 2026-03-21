package com.ridehailing.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // "/topic": Dành cho các client đăng ký (subscribe) nhận dữ liệu từ Server (Ví dụ: Khách theo dõi xe)
        config.enableSimpleBroker("/topic");
        
        // "/app": Tiền tố cho các message gửi từ Client (Tài xế) lên Server để xử lý
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Cổng kết nối WebSocket ban đầu, hỗ trợ SockJS fallback
        registry.addEndpoint("/ws-ridehailing").setAllowedOriginPatterns("*").withSockJS();
    }
}
