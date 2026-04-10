[![GNU GPL 3.0](https://img.shields.io/badge/license-GNU_GPL_3.0-blue)](https://github.com/VictorHachard/mail-it/blob/main/LICENSE)
[![actions-build-mail-it](https://github.com/VictorHachard/mail-it/actions/workflows/actions-build-mail-it.yml/badge.svg)](https://github.com/VictorHachard/mail-it/actions/workflows/actions-build-mail-it.yml)

# mail-it

mail-it is a Spring Boot API that allows web applications to send emails to your mailbox via Gmail SMTP. It is designed to be called from frontend contact forms.

Features:
- **Domain whitelisting** — only allowed origins can POST emails
- **Alias system** — map short keys to real email addresses to protect privacy
- **Rate limiting** — nginx-based IP rate limiting (5 req/min)
- **Honeypot** — silent bot detection via a hidden form field

## Usage

```html
<form action="https://mail-it.example.com/contact" method="POST">
    <input type="text"  name="fromName"  placeholder="Your name">
    <input type="email" name="replyTo"   placeholder="Your email" required>
    <input type="text"  name="subject"   placeholder="Subject">
    <textarea          name="message"                             required></textarea>
    <!-- Honeypot — keep hidden, bots fill it, humans don't -->
    <input type="text" name="_hp" style="display:none" tabindex="-1" autocomplete="off">
    <button type="submit">Send</button>
</form>
```

`/contact` is either a real email address or an alias key defined in `ALIAS`.

### Request parameters

| Parameter | Required | Description |
|---|---|---|
| `replyTo` | Yes | Sender email (shown as Reply-To) |
| `message` | Yes | Email body (HTML or plain text) |
| `subject` | No | Email subject (default: `No Subject`) |
| `fromName` | No | Sender display name |
| `fromApplication` | No | Application name shown in brackets |
| `footer` | No | Custom footer HTML (replaces auto-generated footer) |
| `replaceMessageBreak` | No | Replace `\n` with `<br>` (default: `true`) |
| `files` | No | File attachments (multipart, max 20MB total) |
| `_hp` | No | Honeypot — must stay empty |

### Response

```json
{
  "result": "success",
  "email_to_send_count": 1,
  "email_sent_count": 1,
  "email_sent": ["contact"]
}
```

## Setup

### 1. Gmail App Password

Gmail requires an App Password — your regular password will not work.

1. Enable 2-Step Verification on your Google account
2. Go to **Google Account → Security → App Passwords**
3. Generate a password for "Mail"
4. Use the generated 16-character password as `EMAIL_PASSWORD`

### 2. Environment variables

Create a `.env` file in your deploy directory:

```env
EMAIL_USERNAME=your@gmail.com
EMAIL_PASSWORD=aaaa bbbb cccc dddd
EMAIL_FROM=noreply@your-site.com
CORS_ALLOWED_ORIGINS=https://your-site.com,https://other-site.com
ALIAS=contact=you@example.com,support=support@example.com
USER_AGENT_SIMPLIFIED=true
RUN_MODE=production
```

| Variable | Required | Description |
|---|---|---|
| `EMAIL_USERNAME` | Yes | Gmail address used to authenticate with SMTP |
| `EMAIL_PASSWORD` | Yes | Gmail App Password (spaces are ignored) |
| `EMAIL_FROM` | No | Display address shown to recipients (defaults to `EMAIL_USERNAME`) |
| `CORS_ALLOWED_ORIGINS` | Yes (production) | Comma-separated list of allowed origins |
| `ALIAS` | No | Comma-separated `key=email` pairs |
| `USER_AGENT_SIMPLIFIED` | No | Parse user-agent into browser/OS fields (default: `true`) |
| `RUN_MODE` | No | Set to `dev` to disable CORS restriction (default: production) |

### 3. Deploy with Docker Compose

```bash
# Pull and start
IMAGE_TAG=v1.0.0 docker compose pull
IMAGE_TAG=v1.0.0 docker compose up -d
```

The stack starts two containers:
- **nginx** — exposed on port `5686`, handles rate limiting and security headers
- **mail-it** — internal only, restarted automatically on crash (healthcheck on `/health`)

### 4. Deploy via GitHub Actions

Trigger the **Deploy to Oracle Server** workflow manually with the image tag to deploy. Required secrets:

| Secret | Description |
|---|---|
| `DEPLOY_HOST` | Server IP or hostname |
| `DEPLOY_USER` | SSH username |
| `DEPLOY_SSH_KEY` | SSH private key |
| `DEPLOY_PORT` | SSH port |
| `DEPLOY_DIR` | Path to the deploy directory on the server (e.g. `/opt/mail-it`) |

The GHCR image (`ghcr.io/victorhachard/mail-it`) is public — no registry authentication needed on the server.

### 5. Release a new version

```bash
git tag vX.Y.Z
git push origin main --tags
```

This triggers the build workflow which creates a multi-arch image (amd64 + arm64) on GHCR.

## Development

```bash
# Run with hot reload via Docker Compose
docker compose --profile dev up mail-it-dev

# Build
./mvnw clean package -DskipTests

# Run tests
./mvnw test
```

In dev mode (`RUN_MODE=dev`), CORS is disabled and all origins are allowed.

## Contribution

Bugs, feature requests and pull requests are welcome — open an issue on the repository.
