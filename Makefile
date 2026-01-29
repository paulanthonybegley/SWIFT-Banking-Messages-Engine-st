# Makefile for SWIFT Banking Messages
# Simplify common development tasks

# Variables
MVN = ./mvnw
APP_NAME = banking-swift-messages
VERSION = 0.0.0-SNAPSHOT
JAR_FILE = target/$(APP_NAME)-$(VERSION).jar

# Colors
BLUE = \033[36m
GREEN = \033[0;32m
YELLOW = \033[1;33m
RESET = \033[0m

.PHONY: all build run stop test clean help

all: build

help:
	@echo "$(BLUE)=== SWIFT Banking Messages - Command Menu ===$(RESET)"
	@echo "$(YELLOW)build$(RESET)   : Compile and package the application into a fat JAR"
	@echo "$(YELLOW)run$(RESET)     : Build and start the Spring Boot application"
	@echo "$(YELLOW)stop$(RESET)    : Stop any running instance of the application"
	@echo "$(YELLOW)test$(RESET)    : Run all unit and integration tests"
	@echo "$(YELLOW)clean$(RESET)   : Remove build artifacts and target directory"
	@echo "$(YELLOW)package$(RESET) : Package the application without running tests"

build:
	@echo "$(BLUE)Building application...$(RESET)"
	$(MVN) clean package

package:
	@echo "$(BLUE)Packaging application (skipping tests)...$(RESET)"
	$(MVN) clean package -DskipTests

run:
	@echo "$(BLUE)Starting application...$(RESET)"
	@if [ ! -f $(JAR_FILE) ]; then \
		echo "$(YELLOW)JAR not found, building first...$(RESET)"; \
		$(MVN) clean package -DskipTests; \
	fi
	java -jar $(JAR_FILE)

stop:
	@echo "$(YELLOW)Stopping running instance...$(RESET)"
	@pkill -f $(APP_NAME)-$(VERSION).jar || echo "No running instance found."
	@lsof -t -i :8080 | xargs kill -9 2>/dev/null || true
	@echo "$(GREEN)Done.$(RESET)"

test:
	@echo "$(BLUE)Running tests...$(RESET)"
	$(MVN) test

clean:
	@echo "$(BLUE)Cleaning project...$(RESET)"
	$(MVN) clean
	@echo "$(GREEN)Done!$(RESET)"