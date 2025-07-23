# Maven Installation Guide

This guide explains how to install Apache Maven, a build automation tool used for Java projects.

## Prerequisites

- Java JDK 11 or later installed

## Installation Instructions

### macOS

1. Using Homebrew (Recommended):
```bash
# Install Homebrew if not already installed
/bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)"

# Install Maven
brew install maven

# Verify installation
mvn -version
```

2. Manual Installation:
```bash
# Download Maven
curl -O https://dlcdn.apache.org/maven/maven-3/3.9.4/binaries/apache-maven-3.9.4-bin.tar.gz

# Extract the archive
tar xzvf apache-maven-3.9.4-bin.tar.gz

# Move to /usr/local
sudo mv apache-maven-3.9.4 /usr/local/

# Add to PATH
echo 'export PATH="/usr/local/apache-maven-3.9.4/bin:$PATH"' >> ~/.zshrc
# Or for bash:
# echo 'export PATH="/usr/local/apache-maven-3.9.4/bin:$PATH"' >> ~/.bashrc

# Reload shell configuration
source ~/.zshrc  # or source ~/.bashrc

# Verify installation
mvn -version
```

### Ubuntu/Debian

1. Using package manager (Recommended):
```bash
# Update package list
sudo apt update

# Install Maven
sudo apt install maven

# Verify installation
mvn -version
```

2. Manual Installation:
```bash
# Download Maven
wget https://dlcdn.apache.org/maven/maven-3/3.9.4/binaries/apache-maven-3.9.4-bin.tar.gz

# Extract the archive
tar xzvf apache-maven-3.9.4-bin.tar.gz

# Move to /opt
sudo mv apache-maven-3.9.4 /opt/

# Add to PATH
echo 'export PATH="/opt/apache-maven-3.9.4/bin:$PATH"' >> ~/.bashrc

# Reload shell configuration
source ~/.bashrc

# Verify installation
mvn -version
```

### Windows

1. Using Chocolatey (Recommended):
```powershell
# Install Chocolatey if not already installed
Set-ExecutionPolicy Bypass -Scope Process -Force; [System.Net.ServicePointManager]::SecurityProtocol = [System.Net.ServicePointManager]::SecurityProtocol -bor 3072; iex ((New-Object System.Net.WebClient).DownloadString('https://community.chocolatey.org/install.ps1'))

# Install Maven
choco install maven

# Verify installation
mvn -version
```

2. Manual Installation:
   1. Download Maven from https://maven.apache.org/download.cgi
   2. Extract the ZIP file (e.g., to `C:\Program Files\Apache\maven`)
   3. Add Maven to System Environment Variables:
      - Open System Properties (Win + Pause)
      - Click "Environment Variables"
      - Under System Variables, find PATH and click Edit
      - Add new entry: `C:\Program Files\Apache\maven\bin`
   4. Open new command prompt and verify:
      ```cmd
      mvn -version
      ```

## Verifying the Installation

After installation, verify Maven is correctly installed:
```bash
mvn -version
```

You should see output similar to:
```
Apache Maven 3.9.4 (...)
Maven home: /usr/local/Cellar/maven/3.9.4/libexec
Java version: 11.0.12, vendor: Oracle Corporation, runtime: /Library/Java/JavaVirtualMachines/jdk-11.0.12.jdk/Contents/Home
Default locale: en_US, platform encoding: UTF-8
OS name: "mac os x", version: "12.0", arch: "x86_64", family: "mac"
```

## Setting up Maven in your Project

1. Verify Maven works in your project:
```bash
cd aws-api-project
mvn clean install
```

2. If you see dependency download messages and a successful build, Maven is working correctly.

## Common Issues

1. **JAVA_HOME not set**
   ```bash
   # macOS/Linux
   export JAVA_HOME=$(/usr/libexec/java_home)
   
   # Windows (PowerShell)
   $env:JAVA_HOME = "C:\Program Files\Java\jdk-11"
   ```

2. **Permission denied**
   ```bash
   # Fix permissions
   sudo chown -R $USER ~/.m2
   ```

3. **Cannot resolve dependencies**
   - Check internet connection
   - Verify Maven settings in `~/.m2/settings.xml`
   - Try clearing Maven cache:
     ```bash
     rm -rf ~/.m2/repository
     ```

## Maven Commands Reference

Common Maven commands used in this project:
```bash
# Clean and install all modules
mvn clean install

# Build a specific module
cd service
mvn clean package

# Run tests
mvn test

# Skip tests
mvn clean install -DskipTests

# Show dependency tree
mvn dependency:tree
```
