package com.muhardin.endy.belajarsso;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.RemoteTokenServices;

@EnableWebSecurity(debug = true)
public class KonfigurasiSecurity {

    @Configuration
    @EnableResourceServer
    protected static class ResourceServerConfiguration extends
            ResourceServerConfigurerAdapter {

        @Override
        public void configure(ResourceServerSecurityConfigurer resources) {
            RemoteTokenServices tokenService = new RemoteTokenServices();
            tokenService.setClientId("clientapp");
            tokenService.setClientSecret("123456");
            tokenService.setCheckTokenEndpointUrl("http://localhost:10000/oauth/check_token");
            
            resources
                    .resourceId("belajarsso")
                    .tokenServices(tokenService);
        }

        @Override
        public void configure(HttpSecurity http) throws Exception {
            http.authorizeRequests()
                    .antMatchers("/me").authenticated();
        }

    }
}
