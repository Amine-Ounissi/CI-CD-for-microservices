package com.value.buildingblocks.backend.api;

import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.util.StdDateFormat;
import java.text.DateFormat;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.format.datetime.DateFormatterRegistrar;
import org.springframework.format.support.FormattingConversionService;

@Configuration
@AutoConfigureBefore({JacksonAutoConfiguration.class})
@EnableConfigurationProperties({MvcApiProperties.class})
public class MvcConfiguration {
  @Bean
  @ConditionalOnBean({FormattingConversionService.class})
  @ConditionalOnProperty(name = {"value.api.register-formatters"}, havingValue = "true", matchIfMissing = true)
  public InitializingBean initDateFormatterRegistrar(ObjectProvider<FormattingConversionService> fcsProvider) {
    return () -> {
//        DateFormatterRegistrar dateFormatterRegistrar = new DateFormatterRegistrar();
//        fcsProvider.forEach(());
      };
  }
  
  @Bean
  @Order(-1)
  public Jackson2ObjectMapperBuilderCustomizer apiJackson2ObjectMapperBuilderCustomizer(MvcApiProperties properties) {
    return jacksonObjectMapperBuilder -> {
        jacksonObjectMapperBuilder.dateFormat((DateFormat)(new StdDateFormat()).withColonInTimeZone(false));
        if (properties.isZonedDateTimeDeserializerEnabled())
          jacksonObjectMapperBuilder.deserializers(new JsonDeserializer[] { (JsonDeserializer)new ISO8601ZonedDateTimeDeserializer() }); 
        if (properties.isOffsetDateTimeDeserializerEnabled())
          jacksonObjectMapperBuilder.deserializers(new JsonDeserializer[] { (JsonDeserializer)new ISO8601OffsetDateTimeDeserializer() }); 
      };
  }
}
