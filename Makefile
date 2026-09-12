.PHONY: up down test verify lint deps-check

up:
	docker compose up -d

down:
	docker compose down

lint:
	mvn -B checkstyle:check

test:
	mvn -B test

deps-check:
	mvn -B org.owasp:dependency-check-maven:check -DfailBuildOnCVSS=9

verify: lint deps-check test
