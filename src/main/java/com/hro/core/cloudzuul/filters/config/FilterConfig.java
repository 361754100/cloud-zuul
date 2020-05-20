package com.hro.core.cloudzuul.filters.config;

import com.hro.core.cloudzuul.filters.IpFilter;
import org.apache.catalina.filters.RemoteIpFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {

//    @Bean
//    public RemoteIpFilter remoteIpFilter(){
//        return new RemoteIpFilter();
//    }

    @Bean
    public IpFilter ipFilter() {
        return new IpFilter();
    }
}
