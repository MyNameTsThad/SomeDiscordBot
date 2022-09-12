FROM openjdk:17.0.2-jdk
ARG VERSION=2.0.1
ARG TOKEN
CMD ./gradlew build
COPY ./build/libs/SomeDiscordBot-${VERSION}-all.jar SomeDiscordBot.jar
ENV SDB_TOKEN=${TOKEN}
ENTRYPOINT ["java","-jar","/SomeDiscordBot.jar"]