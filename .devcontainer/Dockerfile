# Use the official Node.js & TypeScript image as a base
FROM mcr.microsoft.com/devcontainers/typescript-node:1-20-bookworm

ENV SDKMAN_DIR=/usr/local/sdkman
# Install Java versions and Maven using SDKMAN
RUN curl -s "https://get.sdkman.io" | bash && \
    bash -c "source ${SDKMAN_DIR}/bin/sdkman-init.sh && \
    sdk install java 11.0.20-tem && \
    sdk install java 17.0.8-tem && \
    sdk install java 21.0.4-tem && \
    sdk install java 8.0.382-tem && \
    sdk install maven"

# Set environment variables for Java versions
ENV JAVA_HOME=/usr/local/sdkman/candidates/java/current
ENV JAVA8HOME=/usr/local/sdkman/candidates/java/8.0.382-tem
ENV JAVA11HOME=/usr/local/sdkman/candidates/java/11.0.20-tem
ENV JAVA17HOME=/usr/local/sdkman/candidates/java/17.0.8-tem
ENV JAVA21HOME=/usr/local/sdkman/candidates/java/21.0.4-tem

# Add JAVA 8 and Maven to PATH
ENV PATH="${JAVA8HOME}/bin:${SDKMAN_DIR}/candidates/maven/current/bin:${PATH}"

# Ensure SDKMAN is sourced for all users
RUN echo "source ${SDKMAN_DIR}/bin/sdkman-init.sh" >> /etc/bash.bashrc

# Set the default shell to bash
SHELL ["/bin/bash", "-c"]

# Copy the microsoft-autodev-0.5.0.tgz file into the image
COPY microsoft-autodev-0.7.0.tgz /workspace/

# Unpack the file
RUN tar -xzf /workspace/microsoft-autodev-0.7.0.tgz -C /workspace/

# Set the working directory
WORKDIR /workspace/package
EXPOSE 8080

# Set the command to run the server
CMD ["node", "./dist/src/java-upgrade/webServer/server.js"]
