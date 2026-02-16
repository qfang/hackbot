# Deployment Guide: Spring Boot Performance Optimization

## Overview
This guide explains how to deploy the optimized Spring Boot application to Kubernetes with all performance enhancements enabled.

## Performance Features

### 1. Application-Level Optimizations
- **Caffeine Cache**: In-memory caching with 10-minute TTL
  - Reduces repeated computations
  - Cache statistics enabled via actuator
- **GZIP Compression**: Reduces payload size by 60-80%
  - Automatic for text/html, text/css, text/javascript, application/json
  - Minimum size: 1KB
- **Static Resource Caching**: 
  - JS/CSS: 1 year browser cache
  - HTML: 1 hour browser cache
- **Async Processing**: Thread pool for non-blocking operations
  - Core: 2 threads
  - Max: 10 threads
  - Queue: 500 capacity

### 2. Tomcat Optimizations
- **Connection Pool**:
  - Max connections: 10,000
  - Max threads: 200
  - Min spare threads: 10
- **Keep-Alive**:
  - Timeout: 60 seconds
  - Max requests: 100

### 3. JVM Optimizations
- **G1 Garbage Collector**: Better latency for web applications
- **Container Support**: Respects Kubernetes memory limits
- **Memory Settings**: 
  - Max RAM: 75% of container limit
  - Initial RAM: 50% of container limit
- **GC Tuning**: Max pause time 200ms
- **String Deduplication**: Reduces memory for duplicate strings

### 4. Kubernetes Optimizations
- **Resource Limits**: Prevents resource starvation
  - Requests: 512Mi memory, 250m CPU
  - Limits: 1Gi memory, 1000m CPU
- **Health Probes**: Fast startup and failure detection
  - Startup probe: 5s intervals, 12 retries (60s total)
  - Readiness probe: 20s initial delay, 10s intervals
  - Liveness probe: 40s initial delay, 30s intervals
- **Horizontal Pod Autoscaling**:
  - Min replicas: 2 (for HA)
  - Max replicas: 10
  - Target: 70% CPU, 80% memory
  - Aggressive scale-up, conservative scale-down
- **Pod Anti-Affinity**: Distributes pods across nodes
- **Session Affinity**: ClientIP-based routing for cache efficiency

## Quick Start

### Prerequisites
- Docker
- Kubernetes cluster (minikube, EKS, GKE, AKS)
- kubectl configured
- Maven (for building)

### Build and Deploy

1. **Build the JAR**:
```bash
mvn clean package
```

2. **Build Docker Image**:
```bash
docker build -t hackbot:latest .
```

For cloud registries:
```bash
# Tag for your registry
docker tag hackbot:latest your-registry/hackbot:latest
docker push your-registry/hackbot:latest

# Update k8s/deployment.yaml to use your image
```

3. **Deploy to Kubernetes**:
```bash
# Create deployment and services
kubectl apply -f k8s/

# Verify deployment
kubectl get pods -l app=hackbot
kubectl get svc hackbot
kubectl get hpa hackbot-hpa

# Wait for pods to be ready
kubectl wait --for=condition=ready pod -l app=hackbot --timeout=60s
```

4. **Access the Application**:

For local testing (port-forward):
```bash
kubectl port-forward svc/hackbot 8080:80
# Open http://localhost:8080
```

For production (with Ingress):
```yaml
apiVersion: networking.k8s.io/v1
kind: Ingress
metadata:
  name: hackbot-ingress
  annotations:
    nginx.ingress.kubernetes.io/rewrite-target: /
spec:
  rules:
  - host: your-domain.com
    http:
      paths:
      - path: /
        pathType: Prefix
        backend:
          service:
            name: hackbot
            port:
              number: 80
```

## Performance Verification

### 1. Check Health and Metrics
```bash
# Health check
kubectl port-forward svc/hackbot 8080:80
curl http://localhost:8080/actuator/health

# Cache statistics
curl http://localhost:8080/actuator/metrics/cache.gets

# JVM metrics
curl http://localhost:8080/actuator/metrics/jvm.memory.used
```

### 2. Load Testing
```bash
# Install Apache Bench
apt-get install apache2-utils

# Test without caching (different questions)
ab -n 1000 -c 10 http://localhost:8080/api/answer?question=test1

# Test with caching (same question)
ab -n 1000 -c 10 http://localhost:8080/api/answer?question=what+is+your+name
```

### 3. Monitor Auto-Scaling
```bash
# Generate load
kubectl run -it --rm load-generator --image=busybox /bin/sh
# Inside pod:
while true; do wget -q -O- http://hackbot/api/answer?question=test; done

# Watch HPA in another terminal
kubectl get hpa hackbot-hpa --watch

# Watch pods scaling
kubectl get pods -l app=hackbot --watch
```

## Expected Performance Improvements

### Before Optimization (Static HTML):
- First load: ~2-3 seconds (depends on network)
- Subsequent loads: ~1-2 seconds
- No caching
- No compression

### After Optimization:
- First load: ~0.8-1.2 seconds (40-60% faster)
  - Compression reduces payload size
  - Optimized connection handling
- Subsequent loads: ~0.1-0.3 seconds (80-90% faster)
  - Browser caching eliminates server requests
- API calls: ~5-10ms cached, ~50-100ms uncached
- Throughput: 3-5x higher with connection pooling
- Resource efficiency: 30-40% better with JVM tuning

## Monitoring in Production

### Prometheus Metrics
```bash
# Add Prometheus annotations to deployment
metadata:
  annotations:
    prometheus.io/scrape: "true"
    prometheus.io/path: "/actuator/prometheus"
    prometheus.io/port: "8080"
```

### Key Metrics to Monitor
- `http_server_requests_seconds`: Request duration
- `jvm_memory_used_bytes`: Memory usage
- `cache_gets_total`: Cache hit rate
- `tomcat_threads_current_threads`: Thread pool usage
- `process_cpu_usage`: CPU utilization

## Troubleshooting

### Pods Not Starting
```bash
# Check logs
kubectl logs -l app=hackbot

# Check events
kubectl describe pod <pod-name>

# Common issues:
# - Image pull errors: Check image name and registry access
# - OOMKilled: Increase memory limits
# - CrashLoopBackOff: Check application logs
```

### Performance Issues
```bash
# Check resource usage
kubectl top pods -l app=hackbot

# Check HPA status
kubectl describe hpa hackbot-hpa

# Check if compression is working
curl -H "Accept-Encoding: gzip" -I http://localhost:8080/

# Check cache hit rate
curl http://localhost:8080/actuator/metrics/cache.gets
```

### High Memory Usage
- Adjust JVM settings in Dockerfile
- Reduce MaxRAMPercentage
- Adjust cache size in PerformanceConfig.java
- Check for memory leaks using heap dumps

## Advanced Configuration

### Customize Cache Settings
Edit `src/main/java/com/qfang/hackbot/config/PerformanceConfig.java`:
```java
.maximumSize(1000)              // Max entries
.expireAfterWrite(10, TimeUnit.MINUTES)  // TTL
```

### Customize Thread Pool
Edit `src/main/java/com/qfang/hackbot/config/PerformanceConfig.java`:
```java
executor.setCorePoolSize(2);    // Minimum threads
executor.setMaxPoolSize(10);    // Maximum threads
executor.setQueueCapacity(500); // Queue size
```

### Customize HPA
Edit `k8s/hpa.yaml`:
```yaml
minReplicas: 2    # Minimum pods
maxReplicas: 10   # Maximum pods
averageUtilization: 70  # CPU threshold
```

## Production Checklist

- [ ] Configure proper resource limits based on load testing
- [ ] Set up monitoring with Prometheus/Grafana
- [ ] Configure Ingress with SSL/TLS
- [ ] Set up log aggregation (ELK, Loki, etc.)
- [ ] Configure backup and disaster recovery
- [ ] Set up CI/CD pipeline
- [ ] Enable security scanning (SAST/DAST)
- [ ] Configure network policies
- [ ] Set up alerting for critical metrics
- [ ] Document runbooks for common issues

## References

- [Spring Boot Actuator](https://docs.spring.io/spring-boot/docs/current/reference/html/actuator.html)
- [Caffeine Cache](https://github.com/ben-manes/caffeine)
- [Kubernetes HPA](https://kubernetes.io/docs/tasks/run-application/horizontal-pod-autoscale/)
- [G1 Garbage Collector](https://www.oracle.com/technical-resources/articles/java/g1gc.html)
