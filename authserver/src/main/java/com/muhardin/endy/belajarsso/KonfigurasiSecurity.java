package com.muhardin.endy.belajarsso;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;

@EnableWebSecurity(debug = true)
@Order(100)
public class KonfigurasiSecurity extends WebSecurityConfigurerAdapter {
    
    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth
                .inMemoryAuthentication()
                .withUser("endy").password("123").authorities("ADMIN").and()
                .withUser("anggi").password("123").authorities("CUSTOMER");
    }

    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .authorizeRequests().antMatchers("/**").authenticated()
                .and().formLogin().permitAll()
                .and().logout().permitAll();
    }
    
    
     
    @Configuration
    @EnableAuthorizationServer
    @Order(20)
    protected static class KonfigurasiAuthServer extends AuthorizationServerConfigurerAdapter {

        @Autowired
        @Qualifier("authenticationManagerBean")
        private AuthenticationManager authenticationManager;

        @Override
        public void configure(AuthorizationServerEndpointsConfigurer endpoints)
                throws Exception {
            endpoints.authenticationManager(authenticationManager);
        }

        @Override
        public void configure(AuthorizationServerSecurityConfigurer oauthServer) throws Exception {
            oauthServer.checkTokenAccess("hasAuthority('CLIENT')");
        }

        @Override
        public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
            clients
                    .inMemory()
                    .withClient("clientapp")
                    .secret("123456")
                    .authorities("CLIENT")
                    .authorizedGrantTypes("authorization_code", "refresh_token", "password")
                    .scopes("read", "write")
                    .autoApprove(true)
                    .resourceIds("belajarsso");
        }
    }
    
    @Configuration
    @EnableResourceServer
    @Order(30)
    protected static class ResourceServerConfiguration extends
            ResourceServerConfigurerAdapter {

        @Override
        public void configure(ResourceServerSecurityConfigurer resources) {
            resources
                    .resourceId("belajarsso");
        }

        @Override
        public void configure(HttpSecurity http) throws Exception {
            http
                    .requestMatchers()
                    .antMatchers("/api/**")
                    .and().authorizeRequests()
                    .antMatchers("/api/**")
                    .authenticated();
        }

    }
}
