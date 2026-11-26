SHELL := /bin/bash
.DEFAULT_GOAL := help

################################################################################
#  Microservice Makefile · Maven | Docker
################################################################################

POM_VALUE     = $(shell sed -n '/<\/parent>/,$$ s:.*<$(1)>\(.*\)</$(1)>.*:\1:p' pom.xml | head -n 1)
SERVICE_NAME ?= $(call POM_VALUE,artifactId)
SERVICE_TAG  ?= $(call POM_VALUE,version)
COMPOSE      := docker compose -f Docker/compose.yml
MVN          := mvn -B

CYAN  := \033[36m
BOLD  := \033[1m
RESET := \033[0m

define title
	@printf '$(BOLD)$(CYAN)==> %s$(RESET)\n' "$(1)"
endef

.PHONY: help run lint format test build \
        docker-build docker-up docker-down docker-logs rebuild clean

help: ## Show this help
	@printf '%s\n' '==========================================================='
	@printf '  %s %s · usage: make <target>\n' "$(SERVICE_NAME)" "$(SERVICE_TAG)"
	@printf '%s\n' '==========================================================='
	@grep -E '^[a-zA-Z_-]+:.*?## ' $(MAKEFILE_LIST) | \
		awk 'BEGIN {FS = ":.*?## "}; {printf "  $(CYAN)%-13s$(RESET) %s\n", $$1, $$2}'

# ---------------------------------------------------------------------------
# Development
# ---------------------------------------------------------------------------
run: ## Run the API on http://localhost:8082 with a throwaway Oracle
	$(MVN) spring-boot:test-run

lint: ## Check formatting with Spotless
	$(MVN) spotless:check

format: ## Apply formatting with Spotless
	$(MVN) spotless:apply

test: ## Run the tests (they start Oracle with Docker)
	$(call title,Testing $(SERVICE_NAME))
	$(MVN) test

build: ## Package the jar into target/ (tests run with make test)
	$(call title,Building $(SERVICE_NAME))
	$(MVN) -DskipTests package

# ---------------------------------------------------------------------------
# Docker (API and database)
# ---------------------------------------------------------------------------
docker-build: ## Build the image
	$(call title,Building the image of $(SERVICE_NAME))
	$(COMPOSE) build

docker-up: ## Build and start the API and Oracle
	$(call title,Starting $(SERVICE_NAME))
	$(COMPOSE) up -d --build
	$(COMPOSE) ps

docker-down: ## Stop and remove the containers (data is kept)
	$(COMPOSE) down

docker-logs: ## Follow the container logs
	$(COMPOSE) logs -f

rebuild: ## Recreate image and containers without cache
	$(call title,Recreating $(SERVICE_NAME))
	$(COMPOSE) down
	$(COMPOSE) build --no-cache
	$(COMPOSE) up -d

# ---------------------------------------------------------------------------
# Cleanup (this project only)
# ---------------------------------------------------------------------------
clean: ## Remove target/, the containers, the Oracle data and the API image
	$(call title,Cleaning $(SERVICE_NAME))
	-$(COMPOSE) down --volumes
	-docker image rm $(SERVICE_NAME):local
	rm -rf target
