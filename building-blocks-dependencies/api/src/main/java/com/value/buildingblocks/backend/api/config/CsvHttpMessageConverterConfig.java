package com.value.buildingblocks.backend.api.config;

import com.value.buildingblocks.backend.api.csv.CsvClassProperties;
import com.value.buildingblocks.backend.api.csv.CsvHttpMessageConverter;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@ConditionalOnProperty(prefix = "value.api.mvc.csv", name = {
  "autoconfig"}, havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties({ApiProperties.class})
public class CsvHttpMessageConverterConfig implements WebMvcConfigurer {

  @Autowired
  private ApiProperties apiConfig;

  public void extendMessageConverters(List<HttpMessageConverter<?>> messageConverters) {
    messageConverters.add(getCsvMessageConverter());
  }

  public CsvHttpMessageConverter getCsvMessageConverter() {
    CsvHttpMessageConverter csvMessageConverter = new CsvHttpMessageConverter();
    Map<Class<?>, CsvClassProperties> csvClassPropertiesMap = getConfiguredCsvClassPropertiesMap();
    if (csvClassPropertiesMap != null) {
      csvMessageConverter.setCsvClassPropertiesMap(getConfiguredCsvClassPropertiesMap());
    }
    return csvMessageConverter;
  }

  private Map<Class<?>, CsvClassProperties> getConfiguredCsvClassPropertiesMap() {
    return this.apiConfig.getMvc().getCsv().getCsvClassProperties();
  }
}
