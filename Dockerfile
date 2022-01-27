FROM gradle:7.3.3-jdk17 AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle build --no-daemon shadowJar

FROM openjdk:17-jdk

EXPOSE 19132/udp

RUN mkdir /app

COPY --from=build /home/gradle/src/build/libs/bedrock-dragon-1.0-all.jar /app/bedrock-dragon.jar
WORKDIR /app


CMD ["java","-jar","bedrock-dragon.jar"]
