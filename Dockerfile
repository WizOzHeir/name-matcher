FROM alpine/git as clone
ARG url
WORKDIR /app
RUN git clone ${url}
FROM maven:3.8.4-jdk-11 as build
ARG project
WORKDIR /app
COPY --from=clone /app/${project} /app
RUN mvn install
FROM openjdk:11-jdk
ARG artifactid
ARG version
ENV artifact ${artifactid}-${version}.jar
WORKDIR /app
COPY --from=build /app/target/${artifact} /app
COPY --from=build /app/target/classes/basic.txt /app
EXPOSE 8083
ENTRYPOINT ["sh", "-c"]
CMD ["java -jar ${artifact}"]