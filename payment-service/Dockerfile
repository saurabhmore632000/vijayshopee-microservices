FROM eclipse-temurin:17-jre-alpine AS extractor
WORKDIR /application
COPY target/*.jar app.jar
RUN java -Djarmode=layertools -jar app.jar extract

FROM eclipse-temurin:17-jre-alpine
WORKDIR /application
COPY --from=extractor /application/dependencies/ ./
COPY --from=extractor /application/spring-boot-loader/ ./
COPY --from=extractor /application/snapshot-dependencies/ ./
COPY --from=extractor /application/application/ ./
EXPOSE 8085
ENTRYPOINT ["java", "org.springframework.boot.loader.launch.JarLauncher"]