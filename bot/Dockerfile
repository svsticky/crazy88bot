FROM eclipse-temurin:21 AS app-builder
RUN mkdir /opt/app

COPY gradle/ /opt/app/gradle/
COPY app/ /opt/app/app/
COPY gradlew /opt/app/
COPY settings.gradle /opt/app/

WORKDIR /opt/app
RUN /opt/app/gradlew build

FROM eclipse-temurin:21
#RUN apk add --no-cache ca-certificates
RUN apt install ca-certificates -y

RUN mkdir /opt/app
COPY --from=app-builder /opt/app/app/build/libs/*.jar /opt/app/crazy88.jar

EXPOSE 9001
ENTRYPOINT ["java", "-jar", "/opt/app/crazy88.jar"]