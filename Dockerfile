FROM docker-registry.tools.expedia.com/stratus/primer-base-springboot:8-1

# Install application
COPY target/my-tiny-web-event-counter-service.war /app/bin/
