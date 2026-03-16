package coifure.coif.apigateway.audit;

import java.time.Instant;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("gateway_request_logs")
public class GatewayRequestLog {

    @Id
    private Long id;
    private String method;
    private String path;

    @Column("status_code")
    private Integer statusCode;

    @Column("duration_ms")
    private Long durationMs;

    @Column("route_id")
    private String routeId;

    @Column("created_at")
    private Instant createdAt;

    public GatewayRequestLog() {}

    public GatewayRequestLog(
            String method,
            String path,
            Integer statusCode,
            Long durationMs,
            String routeId,
            Instant createdAt
    ) {
        this.method = method;
        this.path = path;
        this.statusCode = statusCode;
        this.durationMs = durationMs;
        this.routeId = routeId;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Integer getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(Integer statusCode) {
        this.statusCode = statusCode;
    }

    public Long getDurationMs() {
        return durationMs;
    }

    public void setDurationMs(Long durationMs) {
        this.durationMs = durationMs;
    }

    public String getRouteId() {
        return routeId;
    }

    public void setRouteId(String routeId) {
        this.routeId = routeId;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
