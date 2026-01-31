# SWIFT Banking Messages Engine [![starline](https://starlines.qoo.monster/assets/qoomon/banking-swift-messages-java)](https://github.com/qoomon/starline)

Professional SWIFT Banking Messages Toolkit with a modern Spring Boot + Thymeleaf web interface.

[![License: MIT](https://img.shields.io/badge/License-MIT-blue.svg)](https://opensource.org/licenses/MIT)

## 🌟 Features

- **Modern Web Interface**: Professional Spring Boot + Thymeleaf-based UI with a sleek dark theme.
- **Message Parser**: Parse MT101, MT103, MT940, and MT942 messages with precise field extraction.
- **Message Composer**: Interactive forms to generate standard-compliant SWIFT messages.
- **Message Validator**: Structural and mandatory field validation for various MT types.
- **Interactive Documentation**: Integrated guide to SWIFT message formats and specifications.
- **Developer Friendly**: Includes a `Makefile` for streamlined build, run, and process management.

### Supported Message Types
- **MT101** - General Financial Institution Transfer
- **MT103** - Single Customer Credit Transfer
- **MT940** - Customer Statement Message
- **MT942** - Interim Transaction Report

### Deployment (Docker & Render.com)

This project is configured for easy deployment to **Render.com** (or any Docker-capable host) using a multi-stage `Dockerfile`.

1. **GitHub Integration**: Push your code to a GitHub repository.
2. **New Web Service**: In Render, select **New > Web Service**.
3. **Select Repository**: Pick your repository.
4. **Environment**: Select **Docker** as the runtime.
5. **Deploy**: Render will build the image using the `Dockerfile` and start the service on port 8080.

### Local Development
... existing Quick Start ...

### Commands

| Command | Description |
|---------|-------------|
| `make run` | Builds the application (if needed) and starts the server on port 8080 |
| `make stop` | Reliably stops any running instances of the application |
| `make build` | Compiles and packages the application into a fat JAR |
| `make package` | Packages the application skipping tests |
| `make clean` | Removes build artifacts and the `target` directory |
| `make help` | Displays the command menu |

### Access the Application
Open your browser and navigate to `http://localhost:8080`

## 📚 Component Overview

### Parser
Paste raw SWIFT message content (including headers) to extract metadata and field values. The parser now supports robust message type identification using Block 2 headers.

### Composer
Generate SWIFT messages by filling out structured forms. Supports generating full messages including Block 1, 2, and 3 headers.

### Validator
Performs mandatory field checks and structural validation for supported MT types, providing clear error reporting for missing or malformed fields.

### Documentation
An interactive specification guide with field formats and message examples for all supported MT types.

## 🛠 Tech Stack

- **Backend**: Java 17, Spring Boot 3.3.1
- **Templating**: Thymeleaf 3
- **Build System**: Maven (via `mvnw`)
- **Process Management**: Makefile
- **Frontend**: Vanilla CSS (Custom Design System), FontAwesome

---
Developed with precision for modern banking compliance.
