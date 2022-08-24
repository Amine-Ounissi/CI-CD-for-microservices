package com.value.platform.registry;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;
import org.springframework.web.WebApplicationInitializer;

@SpringBootApplication
@EnableEurekaServer
public class Application extends SpringBootServletInitializer implements WebApplicationInitializer {
  public static void main(String... args) {
    SpringApplicationBuilder springApplicationBuilder = new SpringApplicationBuilder(
      Application.class);
    springApplicationBuilder.sources(Application.class).run(args);
  }
  
  protected SpringApplicationBuilder configure(SpringApplicationBuilder springApplicationBuilder) {
    return springApplicationBuilder
      .sources(Application.class);
  }
}
