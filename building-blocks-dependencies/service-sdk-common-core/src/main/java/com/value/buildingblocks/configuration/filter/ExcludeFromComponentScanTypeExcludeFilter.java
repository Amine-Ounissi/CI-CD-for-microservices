package com.value.buildingblocks.configuration.filter;

import java.io.IOException;
import org.springframework.boot.context.TypeExcludeFilter;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;

class ExcludeFromComponentScanTypeExcludeFilter extends TypeExcludeFilter {
  public boolean match(MetadataReader metadataReader, MetadataReaderFactory metadataReaderFactory) throws IOException {
    return isAnnotated(metadataReader);
  }
  
  public boolean equals(Object obj) {
    return super.equals(obj);
  }
  
  public int hashCode() {
    return super.hashCode();
  }
  
  private boolean isAnnotated(MetadataReader metadataReader) {
    return metadataReader.getAnnotationMetadata().isAnnotated(ExcludeFromComponentScan.class.getName());
  }
}
