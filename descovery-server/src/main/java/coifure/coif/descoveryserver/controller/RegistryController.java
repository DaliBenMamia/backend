package coifure.coif.descoveryserver.controller;

import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.shared.Application;
import com.netflix.eureka.EurekaServerContextHolder;
import com.netflix.eureka.registry.PeerAwareInstanceRegistry;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/registry")
public class RegistryController {

    @GetMapping("/services")
    public ResponseEntity<List<ServiceView>> listServices() {
        PeerAwareInstanceRegistry registry = EurekaServerContextHolder.getInstance()
                .getServerContext()
                .getRegistry();

        List<ServiceView> services = registry.getSortedApplications()
                .stream()
                .map(this::toServiceView)
                .toList();

        return ResponseEntity.ok(services);
    }

    private ServiceView toServiceView(Application application) {
        List<InstanceView> instances = application.getInstances().stream()
                .map(this::toInstanceView)
                .toList();

        return new ServiceView(application.getName(), application.size(), instances);
    }

    private InstanceView toInstanceView(InstanceInfo instance) {
        return new InstanceView(
                instance.getInstanceId(),
                instance.getHostName(),
                instance.getIPAddr(),
                instance.getPort(),
                instance.getStatus().name(),
                instance.getHomePageUrl()
        );
    }

    public record ServiceView(String name, int instanceCount, List<InstanceView> instances) {
    }

    public record InstanceView(
            String instanceId,
            String hostName,
            String ip,
            int port,
            String status,
            String homePageUrl
    ) {
    }
}
