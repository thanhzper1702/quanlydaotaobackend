# ====================================================================
# STAGE 1: Build application with Maven & JDK 17
# ====================================================================
FROM maven:3.9.6-eclipse-temurin-17-alpine AS builder

WORKDIR /build

# Pre-fetch dependencies to leverage Docker layer caching
COPY pom.xml .
RUN mvn dependency:go-offline -B || true

# Copy source code and package WAR
COPY src ./src
RUN mvn clean package -DskipTests

# ====================================================================
# STAGE 2: Run application on Apache Tomcat 10.1 (Jakarta EE 10 / Servlet 6.0)
# ====================================================================
FROM tomcat:10.1-jdk17-temurin

WORKDIR /usr/local/tomcat

# Remove default Tomcat webapps (docs, examples, ROOT, etc.)
RUN rm -rf webapps/*

# Copy packaged WAR to ROOT.war so it serves at context path /
COPY --from=builder /build/target/academic-backend.war webapps/ROOT.war

# Copy entrypoint script for dynamic Render $PORT binding
COPY entrypoint.sh /usr/local/bin/entrypoint.sh
RUN chmod +x /usr/local/bin/entrypoint.sh

# Render sets PORT dynamically (defaults to 8080 if running locally)
ENV PORT=8080
EXPOSE 8080

ENTRYPOINT ["/usr/local/bin/entrypoint.sh"]
