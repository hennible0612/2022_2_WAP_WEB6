FROM openjdk:8
EXPOSE 8080
#VOLUME /tmp
ADD backend/build/libs/toast.jar toast.jar
ENTRYPOINT ["java","-jar","/toast.jar"]
