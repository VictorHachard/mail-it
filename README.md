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

## Contribution

If you would like to contribute to this project, there are several ways you can help. Some ideas include:

-   Reporting bugs or suggesting new features by creating a new issue on the project's repository
-   Implementing existing issues or working on new features by submitting pull requests
-   Improving the project's documentation
-   Sharing the project with others and spreading the word

Any contribution, no matter how small, is valuable and appreciated. If you have any questions or need help getting started, don't hesitate to create a new issue on the project's repository and ask for help. Thank you for considering contributing to this project!
