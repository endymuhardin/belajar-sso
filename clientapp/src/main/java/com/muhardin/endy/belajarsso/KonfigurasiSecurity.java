package com.muhardin.endy.belajarsso;

import java.util.ArrayList;
import java.util.List;
import javax.servlet.Filter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.oauth2.resource.ResourceServerProperties;
import org.springframework.boot.autoconfigure.security.oauth2.resource.UserInfoTokenServices;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.filter.OAuth2ClientAuthenticationProcessingFilter;
import org.springframework.security.oauth2.client.filter.OAuth2ClientContextFilter;
import org.springframework.security.oauth2.client.token.grant.code.AuthorizationCodeResourceDetails;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableOAuth2Client;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.filter.CompositeFilter;

@EnableWebSecurity(debug = true)
@EnableOAuth2Client
public class KonfigurasiSecurity extends WebSecurityConfigurerAdapter {

    @Autowired
    OAuth2ClientContext oauth2ClientContext;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/protected.html").authenticated()
                .anyRequest().permitAll()
                .and().addFilterBefore(ssoFilter(), BasicAuthenticationFilter.class);
    }

    @Bean
    public FilterRegistrationBean oauth2ClientFilterRegistration(
            OAuth2ClientContextFilter filter) {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(filter);
        registration.setOrder(-100);
        return registration;
    }

    private Filter ssoFilter() {
        CompositeFilter filter = new CompositeFilter();
        List<Filter> daftarFilter = new ArrayList<>();
        daftarFilter.add(ssoFilter(google(), "/login/google"));
        daftarFilter.add(ssoFilter(facebook(), "/login/facebook"));
        filter.setFilters(daftarFilter);
        return filter;
    }
    
    private Filter ssoFilter(ClientResources client, String path){
        OAuth2ClientAuthenticationProcessingFilter ssoFilter = new OAuth2ClientAuthenticationProcessingFilter(path);
        OAuth2RestTemplate ssoRestTemplate = new OAuth2RestTemplate(client.getClient(), oauth2ClientContext);
        ssoFilter.setRestTemplate(ssoRestTemplate);
        UserInfoTokenServices tokenServices = new UserInfoTokenServices(client.getResource().getUserInfoUri(), 
                client.getClient().getClientId());
        tokenServices.setRestTemplate(ssoRestTemplate);
        ssoFilter.setTokenServices(tokenServices);
        return ssoFilter;
    }

    @Bean
    @ConfigurationProperties("google")
    public ClientResources google() {
        return new ClientResources();
    }
    
    @Bean
    @ConfigurationProperties("facebook")
    public ClientResources facebook() {
        return new ClientResources();
    }

    class ClientResources {

        @NestedConfigurationProperty
        private final AuthorizationCodeResourceDetails client = new AuthorizationCodeResourceDetails();

        @NestedConfigurationProperty
        private final ResourceServerProperties resource = new ResourceServerProperties();

        public AuthorizationCodeResourceDetails getClient() {
            return client;
        }

        public ResourceServerProperties getResource() {
            return resource;
        }
    }
}
