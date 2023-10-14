# Use an official OpenJDK runtime as a parent image
FROM openjdk:11

# Set the working directory
WORKDIR /app

# Copy your Java source code and any necessary resources to the container
COPY . /app

# Compile your Java code
RUN javac javaFiles/StandardNode.java

# Run your Java application
CMD ["java", "javaFiles.StandardNode"]
