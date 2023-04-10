[![Alpha](https://raster.shields.io/badge/maturity-Alpha-red.png)]()
[![GNU GPL 3.0](https://img.shields.io/badge/license-GNU_GPL_3.0-blue)](https://github.com/VictorHachard/mail-it/blob/main/LICENSE)
[![Codacy Badge](https://app.codacy.com/project/badge/Grade/2f86fd10ba9e41a39e4c268e7f0b65e7)](https://www.codacy.com/gh/VictorHachard/mail-it/dashboard?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=VictorHachard/mail-it&amp;utm_campaign=Badge_Grade)
[![actions-build-mail-it](https://github.com/VictorHachard/mail-it/actions/workflows/actions-build-mail-it.yml/badge.svg)](https://github.com/VictorHachard/mail-it/actions/workflows/actions-build-mail-it.yml)

# mail-it

mail-it is a Spring API that allows web applications to easily send email to your mailbox using Google's email service.

mail-it offers features such as domain whitelisting and an alias system.

The domain whitelisting feature allows which domains are allowed to send emails through the mail-it API, providing an additional level of security and control.

The alias system allows sending emails using aliases instead of actual email address to protect privacy.

## Code Example

```html
<form action="https://mail-it.example.com/you_email@example.com" method="POST">
    <input type="text" name="fromName" placeholder="Your name">
    <input type="email" name="replyTo" placeholder="Your email">
    <input type="text" name="subject" placeholder="Your subject">
    <input type="text" name="message" placeholder="Your message">
    <input type="submit" value="Send Email">
</form>
```

## Installation

### Setup for production

Install Java JDK 11:

```bash
sudo apt install default-jdk -y
```

Create the `/opt/mail-it/` folder to place the Jar file:

```bash
sudo mkdir /opt/mail-it
```

Download the latest of mail-it:

```bash
wget https://github.com/VictorHachard/mail-it/releases/latest/download/mail-it.jar
```

Create the `setting.conf` file in the `/opt/mail-it/` directory:

```bash
sudo echo '{
    "access_control_allow_origin_url": [
      "https://example.com"
    ],
    "email_username": "gmail_sender@gmail.com",
    "email_password": "aaaa bbbb cccc dddd",
    "alias": {
        "you_email": "you_email@example.com"
    }
}' > /opt/mail-it/setting.conf
```

Run the mail-it:

```bash
java -jar /opt/mail-it/mail-it.jar -file /opt/mail-it/setting.conf
```

#### Options

   - Port: `-port <port_number>`

## Contribution

If you would like to contribute to this project, there are several ways you can help. Some ideas include:

-   Reporting bugs or suggesting new features by creating a new issue on the project's repository
-   Implementing existing issues or working on new features by submitting pull requests
-   Improving the project's documentation
-   Sharing the project with others and spreading the word

Any contribution, no matter how small, is valuable and appreciated. If you have any questions or need help getting started, don't hesitate to create a new issue on the project's repository and ask for help. Thank you for considering contributing to this project!
