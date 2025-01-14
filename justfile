default:
  @just --list

# Run a complete development environment
run-dev:
  fama-dev

fmt:
  ./gradlew ktlintCheck
