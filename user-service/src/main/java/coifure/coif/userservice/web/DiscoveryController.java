package coifure.coif.userservice.web;

import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/discovery")
public class DiscoveryController {

    private final DiscoveryClient discoveryClient;

    public DiscoveryController(DiscoveryClient discoveryClient) {
        this.discoveryClient = discoveryClient;
    }

    @GetMapping("/services")
    public Map<String, Object> services() {
        List<String> services = discoveryClient.getServices();
        Map<String, List<String>> instances = services.stream()
                .collect(java.util.stream.Collectors.toMap(
                        serviceId -> serviceId,
                        serviceId -> discoveryClient.getInstances(serviceId).stream()
                                .map(ServiceInstance::getUri)
                                .map(Object::toString)
                                .toList()
                ));

        return Map.of(
                "serviceCount", services.size(),
                "services", services,
                "instances", instances
        );
    }
}
