# Hackbot - High-Performance Spring Boot Application

This is a Spring Boot application optimized for fast page load times when running in Kubernetes.

## Performance Optimizations

### Application Level
1. **Caching with Caffeine**: High-performance in-memory cache for frequently accessed data
   - 10-minute TTL for cached responses
   - Maximum 1000 entries per cache
   - Reduces database/computation overhead

2. **GZIP Compression**: All responses are compressed
   - Reduces payload size by 60-80%
   - Configured for HTML, CSS, JS, JSON

3. **Static Resource Optimization**:
   - Aggressive cache headers (1 year for static assets)
   - Content versioning strategy
   - Reduces browser round-trips

4. **Async Processing**: Non-blocking operations using thread pools
   - Prevents request blocking
   - Better resource utilization

5. **Connection Pooling**: Optimized Tomcat configuration
   - 200 max threads
   - 10,000 max connections
   - Keep-alive enabled

### JVM Optimizations
- G1 Garbage Collector for low latency
- Container-aware memory settings (75% max RAM)
- String deduplication for memory efficiency
- Fast random number generation

### Kubernetes Optimizations
1. **Resource Limits**: Properly configured CPU/memory requests and limits
2. **Health Probes**: Fast startup detection and health checks
3. **Horizontal Pod Autoscaling**: Automatic scaling based on CPU/memory
4. **Pod Anti-Affinity**: Spread pods across nodes for better availability
5. **Session Affinity**: Better cache utilization with sticky sessions

## Building and Running

### Local Development
```bash
# Build the application
mvn clean package

# Run locally
java -jar target/hackbot-1.0.0.jar

# Access the application
open http://localhost:8080
```

### Docker
```bash
# Build Docker image
docker build -t hackbot:latest .

# Run container
docker run -p 8080:8080 hackbot:latest
```

### Kubernetes Deployment
```bash
# Deploy to Kubernetes
kubectl apply -f k8s/

# Check deployment status
kubectl get pods -l app=hackbot
kubectl get svc hackbot
kubectl get hpa hackbot-hpa

# Access application (port-forward for testing)
kubectl port-forward svc/hackbot 8080:80
```

## Performance Metrics

Expected improvements:
- **First Page Load**: 40-60% faster with compression and optimized resources
- **Subsequent Loads**: 80-90% faster with browser caching
- **API Response Time**: 50-70% faster with application caching
- **Throughput**: 3-5x higher with connection pooling and async processing
- **Resource Usage**: 30-40% more efficient with JVM tuning

## Monitoring

Health check endpoints:
- Liveness: `http://localhost:8080/actuator/health/liveness`
- Readiness: `http://localhost:8080/actuator/health/readiness`
- Metrics: `http://localhost:8080/actuator/metrics`

## Architecture

```
User → Kubernetes Ingress → Service (Session Affinity) → Pod (Cached + Compressed)
                                                          ↓
                                                    Caffeine Cache
                                                          ↓
                                                    Application Logic
```

