package xyz.raymoore.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import xyz.raymoore.auth.ApiSecretInterceptor;

@Configuration
public class WebMvcConfiguration implements WebMvcConfigurer {

    private final ApiSecretInterceptor apiSecretInterceptor;

    public WebMvcConfiguration(ApiSecretInterceptor apiSecretInterceptor) {
        this.apiSecretInterceptor = apiSecretInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(apiSecretInterceptor);
    }
}
