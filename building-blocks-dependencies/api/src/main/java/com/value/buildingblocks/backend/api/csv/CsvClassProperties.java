package com.value.buildingblocks.backend.api.csv;

import java.util.ArrayList;
import java.util.List;

public class CsvClassProperties {

  private List<Class<?>> mixIns = new ArrayList<>();

  private List<String> ignoredProperties = new ArrayList<>();

  private String collectionPropertyName;

  public List<Class<?>> getMixIns() {
    return this.mixIns;
  }

  public void setMixIns(List<Class<?>> mixIns) {
    this.mixIns = mixIns;
  }

  public List<String> getIgnoredProperties() {
    return this.ignoredProperties;
  }

  public void setIgnoredProperties(List<String> ignoredProperties) {
    this.ignoredProperties = ignoredProperties;
  }

  public String getCollectionPropertyName() {
    return this.collectionPropertyName;
  }

  public void setCollectionPropertyName(String collectionPropertyName) {
    this.collectionPropertyName = collectionPropertyName;
  }
}
