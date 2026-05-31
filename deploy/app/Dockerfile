FROM eclipse-temurin:21-jre-alpine

LABEL maintainer="zyn"

ARG JAR_FILE

RUN apk add --no-cache \
      tzdata \
      curl \
      wget \
      bind-tools \
      busybox-extras \
      iproute2 \
      tcpdump && \
    cp /usr/share/zoneinfo/Asia/Shanghai /etc/localtime && \
    echo "Asia/Shanghai" > /etc/timezone && \
    addgroup -g 1000 app && \
    adduser -u 1000 -G app -s /bin/sh -D app

WORKDIR /app

COPY start.sh /app/start.sh
COPY ${JAR_FILE} /app/app.jar
RUN chmod +x /app/start.sh && chown -R app:app /app

USER app

EXPOSE 8080

ENTRYPOINT ["/app/start.sh"]
