package com.luxoft.naceapplication.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfiguration {

    @Bean
    public Docket productApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.luxoft.naceapplication.controllers"))
                .paths(PathSelectors.any()).build()
                .apiInfo(metaData())
                .useDefaultResponseMessages(true);
    }

    private ApiInfo metaData() {
        return new ApiInfoBuilder().title("NACE APPLICATION MICROSERVICES")
                .description("Available features:\n \n 1. Import CSV file which contains the NACE details into the application \n 2. Retrieve NACE records for a given order")
                .contact(new Contact("Ragul Dhandapani" , "https://github.com/ragul-dhandapani" , "abcde@gmail.com"))
                .license("Apache 2.0")
                .licenseUrl("http://www.apache.org/licenses/LICENSE-2.0.html")
                .version("0.0.1-SNAPSHOT")
                .build();
    }
}
