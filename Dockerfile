FROM amazoncorretto:21-alpine AS builder

WORKDIR /src
COPY . .
RUN ./gradlew build --no-daemon

WORKDIR /build
RUN tar -xvf /src/app/build/distributions/app.tar

FROM builder AS runner

WORKDIR /
COPY --from=builder /build .

WORKDIR /app
ENTRYPOINT ["bin/app"]
