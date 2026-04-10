# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Commands

```bash
# Build
./mvnw clean package -DskipTests

# Run tests
./mvnw test

# Run locally (requires .env values as env vars or set in environment)
./mvnw spring-boot:run

# Dev mode with hot reload (via Docker Compose)
docker compose --profile dev up mail-it-dev

# Build and run production container
docker compose up --build
```

## Architecture

mail-it is a Spring Boot 3.4.1 / Java 21 relay service: it receives HTTP POST requests and forwards them as emails via Gmail SMTP (port 465, SSL). It is intended to be called from frontend contact forms.

**Request flow:**
1. `CORSFilter` — checks `Origin` header against `CORS_ALLOWED_ORIGINS` whitelist (production) or allows `*` (dev mode)
2. `HtmlEmailController.sendSimpleMessage()` — handles `POST /{email}` where `{email}` can be a real address or an alias key
3. Aliases are resolved via `Environment.ALIAS` (e.g. `vha` → `victor.hachard@hotmail.fr`)
4. Email is built as HTML with an auto-generated footer (sender, date, origin, user-agent) and sent via `JavaMailSender`

**Key classes:**
- `MailItApplication` — entry point; reads `RUN_MODE` env var, initializes `Environment` as a static singleton
- `Environment` — reads all config from env vars at startup (no Spring `@Value`)
- `MailConfig` — configures `JavaMailSenderImpl` with Gmail SMTP (smtp.gmail.com:465, SSL)
- `CORSFilter` — servlet filter handling CORS; production mode enforces origin whitelist
- `EmailValidator` — Lombok model for form parameters (`replyTo`, `message`, `subject`, `fromName`, `fromApplication`, `footer`, `files`, `replaceMessageBreak`)

## Environment variables

| Variable | Description |
|---|---|
| `EMAIL_USERNAME` | Gmail address used to send emails |
| `EMAIL_PASSWORD` | Gmail App Password (16 chars, spaces optional) |
| `CORS_ALLOWED_ORIGINS` | Comma-separated list of allowed origins (e.g. `https://foo.fr,https://bar.fr`) |
| `ALIAS` | Comma-separated `key=email` pairs (e.g. `vha=victor@hotmail.fr,fev=fred@hotmail.fr`) |
| `USER_AGENT_SIMPLIFIED` | `true` to parse user-agent into browser/OS fields, `false` for raw string |
| `RUN_MODE` | `dev` disables CORS restriction; anything else = production |

## Deployment

Images are built and pushed to `ghcr.io/victorhachard/mail-it` via `.github/workflows/actions-build-mail-it.yml` (triggered on `v*` tag push, multi-arch amd64+arm64).

Deployment to Oracle Cloud is via `.github/workflows/deploy-oracle.yml` (manual trigger with tag input). Requires secrets: `DEPLOY_HOST`, `DEPLOY_USER`, `DEPLOY_SSH_KEY`, `DEPLOY_PORT`, `DEPLOY_DIR`. The GHCR package is public so no registry auth is needed on the server.

To release a new version:
```bash
git tag vX.Y.Z
git push origin main --tags
```
