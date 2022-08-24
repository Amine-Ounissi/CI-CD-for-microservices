package org.springframework.cloud.kubernetes.loadbalancer;

import io.fabric8.kubernetes.api.model.ObjectMeta;
import io.fabric8.kubernetes.api.model.Service;
import io.fabric8.kubernetes.api.model.ServicePort;
import io.fabric8.kubernetes.client.utils.Utils;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.cloud.kubernetes.discovery.KubernetesDiscoveryProperties;
import org.springframework.cloud.kubernetes.discovery.KubernetesDiscoveryProperties.Metadata;
import org.springframework.cloud.kubernetes.discovery.KubernetesServiceInstance;
import org.springframework.util.StringUtils;

public class KubernetesServiceInstanceMapper {

  private final KubernetesLoadBalancerProperties properties;
  private final KubernetesDiscoveryProperties discoveryProperties;

  KubernetesServiceInstanceMapper(KubernetesLoadBalancerProperties properties,
    KubernetesDiscoveryProperties discoveryProperties) {
    this.properties = properties;
    this.discoveryProperties = discoveryProperties;
  }

  public KubernetesServiceInstance map(Service service) {
    ObjectMeta meta = service.getMetadata();
    List<ServicePort> ports = service.getSpec().getPorts();
    ServicePort port = null;
    if (ports.size() == 1) {
      port = ports.get(0);
    } else if (ports.size() > 1 && Utils.isNotNullOrEmpty(this.properties.getPortName())) {
      Optional<ServicePort> optPort = ports.stream().filter((it) -> {
        return this.properties.getPortName().endsWith(it.getName());
      }).findAny();
      if (optPort.isPresent()) {
        port = optPort.get();
      }
    }

    if (port == null) {
      return null;
    } else {
      String host = this.createHost(service);
      boolean secure = this.isSecure(service, port);
      return new KubernetesServiceInstance(meta.getUid(), meta.getName(), host, port.getPort(),
        this.getServiceMetadata(service), secure);
    }
  }

  private Map<String, String> getServiceMetadata(Service service) {
    Map<String, String> serviceMetadata = new HashMap();
    Metadata metadataProps = this.discoveryProperties.getMetadata();
    Map annotationMetadata;
    if (metadataProps.isAddLabels()) {
      annotationMetadata = this
        .getMapWithPrefixedKeys(service.getMetadata().getLabels(), metadataProps.getLabelsPrefix());
      serviceMetadata.putAll(annotationMetadata);
    }

    if (metadataProps.isAddAnnotations()) {
      annotationMetadata = this.getMapWithPrefixedKeys(service.getMetadata().getAnnotations(),
        metadataProps.getAnnotationsPrefix());
      serviceMetadata.putAll(annotationMetadata);
    }

    return serviceMetadata;
  }

  private Map<String, String> getMapWithPrefixedKeys(Map<String, String> map, String prefix) {
    if (map == null) {
      return new HashMap();
    } else if (!StringUtils.hasText(prefix)) {
      return map;
    } else {
      Map<String, String> result = new HashMap();
      map.forEach((k, v) -> {
        String var10000 = (String) result.put(prefix + k, v);
      });
      return result;
    }
  }

  private boolean isSecure(Service service, ServicePort port) {
    String securedLabelValue = service.getMetadata().getLabels().getOrDefault("secured", "false");
    if (securedLabelValue.equals("true")) {
      return true;
    } else {
      Map<String, String> annotations = service.getMetadata().getAnnotations();
      String securedAnnotationValue =
        annotations != null ? (String) annotations.getOrDefault("secured", "false") : "false";
      if (securedAnnotationValue.equals("true")) {
        return true;
      } else {
        return port.getName() != null && port.getName().endsWith("https") || port.getPort()
          .toString().endsWith("443");
      }
    }
  }

  private String createHost(Service service) {
    return String.format("%s.%s.svc.%s", service.getMetadata().getName(),
      org.apache.commons.lang.StringUtils.isNotBlank(service.getMetadata().getNamespace()) ? service
        .getMetadata().getNamespace() : "default", this.properties.getClusterDomain());
  }
}
