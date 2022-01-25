FROM gradle:7.3.3-jdk17 AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle build --no-daemon shadowJar

FROM openjdk:17-jdk

EXPOSE 27772

RUN mkdir /app

COPY --from=build /home/gradle/src/build/libs/bedrock-dragon-1.0-all.jar /app/bedrock-dragon.jar
WORKDIR /app


#ENTRYPOINT ["java", "-XX:+UnlockExperimentalVMOptions", "-Djava.security.egd=file:/dev/./urandom","-jar","/app/bedrock-dragon.jar"]
CMD ["java","-jar","bedrock-dragon.jar"]
