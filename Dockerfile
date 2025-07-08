FROM eclipse-temurin:17-jdk-jammy
WORKDIR /app
COPY . .
RUN ./mill mill.scalalib.GenIdea/idea
RUN ./mill burbarik.compile
CMD ["./mill", "burbarik.run"]
