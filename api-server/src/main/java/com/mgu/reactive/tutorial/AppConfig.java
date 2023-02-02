package com.mgu.reactive.tutorial;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * This class allows to define if we must match or not the trailing '/' in
 * a URL. This is flagged as deprecated in v6.0.
 * see https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/web/util/pattern/PathPatternParser.html#setMatchOptionalTrailingSeparator(boolean)
 */
@Configuration
public class AppConfig implements WebMvcConfigurer {

    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        // this method is deprecated in 6.0
        // should be replaced by a proxy, a filter or a controller
        configurer.setUseTrailingSlashMatch(true);
    }
}
