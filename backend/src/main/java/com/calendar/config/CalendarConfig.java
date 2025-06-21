package com.calendar.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProvider;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProviderBuilder;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServletOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@ConfigurationProperties(prefix = "calendar")
public class CalendarConfig {

    private Google google;
    private Outlook outlook;

    @Bean
    @ConditionalOnProperty(name = "spring.security.enabled", havingValue = "true", matchIfMissing = true)
    public OAuth2AuthorizedClientManager authorizedClientManager(
            ClientRegistrationRepository clientRegistrationRepository,
            OAuth2AuthorizedClientRepository authorizedClientRepository) {

        OAuth2AuthorizedClientProvider authorizedClientProvider =
                OAuth2AuthorizedClientProviderBuilder.builder()
                        .authorizationCode()
                        .refreshToken()
                        .build();

        DefaultOAuth2AuthorizedClientManager authorizedClientManager =
                new DefaultOAuth2AuthorizedClientManager(
                        clientRegistrationRepository, authorizedClientRepository);
        authorizedClientManager.setAuthorizedClientProvider(authorizedClientProvider);

        return authorizedClientManager;
    }

    @Bean
    @ConditionalOnProperty(name = "spring.security.enabled", havingValue = "true", matchIfMissing = true)
    public WebClient webClient(OAuth2AuthorizedClientManager authorizedClientManager) {
        ServletOAuth2AuthorizedClientExchangeFilterFunction oauth2Client =
                new ServletOAuth2AuthorizedClientExchangeFilterFunction(authorizedClientManager);
        return WebClient.builder()
                .apply(oauth2Client.oauth2Configuration())
                .build();
    }

    public static class Google {
        private String clientId;
        private String clientSecret;
        private String redirectUri;
        private String applicationName;

        // Getters and setters
        public String getClientId() { 
            return clientId; 
        }

        public void setClientId(String clientId) {
             this.clientId = clientId;
        }

        public String getClientSecret() {
             return clientSecret; 
        }

        public void setClientSecret(String clientSecret) { 
            this.clientSecret = clientSecret; 
        }

        public String getRedirectUri() {
             return redirectUri; 
        }

        public void setRedirectUri(String redirectUri) { 
            this.redirectUri = redirectUri; 
        }

        public String getApplicationName() { 
            return applicationName; 
        }

        public void setApplicationName(String applicationName) { 
            this.applicationName = applicationName; 
        }
    }

    public static class Outlook {
        private String clientId;
        private String clientSecret;
        private String redirectUri;
        private String tenantId;

        // Getters and setters
        public String getClientId() { 
            return clientId; 
        }

        public void setClientId(String clientId) { 
            this.clientId = clientId; 
        }

        public String getClientSecret() { 
            return clientSecret; 
        }

        public void setClientSecret(String clientSecret) { 
            this.clientSecret = clientSecret; 
        }

        public String getRedirectUri() { 
            return redirectUri; 
        }

        public void setRedirectUri(String redirectUri) { 
            this.redirectUri = redirectUri; 
        }

        public String getTenantId() { 
            return tenantId; 
        }

        public void setTenantId(String tenantId) { 
            this.tenantId = tenantId; 
        }
    }

    // Getters and setters for main properties
    public Google getGoogle() { 
        return google; 
    }

    public void setGoogle(Google google) { 
        this.google = google; 
    }

    public Outlook getOutlook() { 
        return outlook; 
    }

    public void setOutlook(Outlook outlook) { 
        this.outlook = outlook; 
    }
} 