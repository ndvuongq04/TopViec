package com.topviec.topviec_be.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

import io.github.cdimascio.dotenv.Dotenv;

@Configuration
public class DotenvConfig {

    @Bean
    public static PropertySourcesPlaceholderConfigurer dotenvConfigurer() {

        // Đọc biến môi trường OS xem đang chạy profile nào
        String profile = System.getenv("SPRING_PROFILE");
        if (profile == null)
            profile = "dev"; // Mặc định dev

        // Chọn đúng file .env theo môi trường
        String envFile = profile.equals("prod") ? ".env.production" : ".env";

        Dotenv dotenv = Dotenv.configure()
                .filename(envFile)
                .ignoreIfMissing() // Không crash nếu không có file (prod dùng biến OS)
                .load();

        // Đẩy vào System properties để Spring đọc được qua ${BIEN}
        dotenv.entries().forEach(entry -> System.setProperty(entry.getKey(), entry.getValue()));

        return new PropertySourcesPlaceholderConfigurer();
    }
}