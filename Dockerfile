# syntax = docker/dockerfile:1.2
FROM eclipse-temurin:21.0.8_9-jre-noble

VOLUME /tmp
ARG JAR_FILE
COPY ${JAR_FILE} spring-boot.jar
ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -jar /spring-boot.jar ${0} ${@}"]
EXPOSE 8080

#EXPOSE 8080
#VOLUME /tmp
#ARG GRAFANA_AGENT_FILE
#ARG JAR_FILE
#COPY ${GRAFANA_AGENT_FILE} opentelemetry-javaagent.jar
#COPY ${JAR_FILE} spring-boot.jar
#CMD ["java", \
#     "-javaagent:/opentelemetry-javaagent.jar", \
#     "-Dotel.logs.exporter=otlp", \
#     "-Dotel.semconv-stability.opt-in=http", \
#     "-Dotel.instrumentation.micrometer.base-time-unit=s", \
#     "-Dotel.instrumentation.log4j-appender.experimental-log-attributes=true", \
#     "-Dotel.instrumentation.logback-appender.experimental-log-attributes=true", \
#     "-Dotel.exporter.otlp.protocol=grpc", \
#     "-Dotel.service.name=tcla-web-service", \
#     "-Dotel.resource.attributes=deployment.environment=production,service.namespace=alexandria", \
#     "-jar", \
#     "/spring-boot.jar"]