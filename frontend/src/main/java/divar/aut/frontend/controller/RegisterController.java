package divar.aut.frontend.controller;

import divar.aut.frontend.net.AuthService;
import divar.aut.frontend.ui.ViewManager;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.util.regex.Pattern;

public class RegisterController {
    @FXML private TextField nameField;
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private TextField emailField;
    @FXML private TextField phoneField;
    @FXML private Label statusLabel;

    private ViewManager viewManager;

    // Must be letters (Persian or Latin) only, with at least a first and last name.
    private static final Pattern FULL_NAME_PATTERN = Pattern.compile("^[\\p{L}]+(\\s[\\p{L}]+)+$");
    // Standard email shape: local@domain.tld
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[\\w.+-]+@[\\w-]+\\.[a-zA-Z]{2,}$");
    // Iranian mobile numbers: 11 digits starting with 09
    private static final Pattern PHONE_PATTERN = Pattern.compile("^09\\d{9}$");

    public void setViewManager(ViewManager viewManager) {
        this.viewManager = viewManager;
    }

    @FXML
    private void handleRegister() {
        statusLabel.getStyleClass().removeAll("status-success", "status-danger");
        statusLabel.getStyleClass().add("status-danger");

        String name = nameField.getText() == null ? "" : nameField.getText().trim();
        String email = emailField.getText() == null ? "" : emailField.getText().trim();
        String phone = phoneField.getText() == null ? "" : phoneField.getText().trim();

        String validationError = validate(name, email, phone);
        if (validationError != null) {
            statusLabel.setText(validationError);
            return;
        }

        AuthService.sendRegisterRequest(
                name,
                usernameField.getText(),
                passwordField.getText(),
                email,
                phone,
                (token, role) -> {
                    viewManager.setUserToken(token);
                    viewManager.setUserRole(role);
                    statusLabel.getStyleClass().remove("status-danger");
                    statusLabel.getStyleClass().add("status-success");
                    statusLabel.setText("ثبت نام موفقیت‌آمیز بود");
                    viewManager.toMain();
                },
                error -> statusLabel.setText("خطا: " + error));
    }

    /**
     * Returns a Persian error message for the first invalid field, or null if
     * everything looks correct. Email and phone are optional — they're only
     * validated when the person actually typed something in.
     */
    private String validate(String name, String email, String phone) {
        if (name.isEmpty()) {
            return "لطفاً نام و نام خانوادگی را وارد کنید";
        }
        if (!FULL_NAME_PATTERN.matcher(name).matches()) {
            return "نام و نام خانوادگی باید فقط شامل حروف باشد و نام و نام خانوادگی را با فاصله وارد کنید";
        }
        if (!email.isEmpty() && !EMAIL_PATTERN.matcher(email).matches()) {
            return "ایمیل وارد شده معتبر نیست (مثلاً: example@mail.com)";
        }
        if (!phone.isEmpty() && !PHONE_PATTERN.matcher(phone).matches()) {
            return "شماره موبایل باید ۱۱ رقم باشد و با ۰۹ شروع شود (مثلاً: 09123456789)";
        }
        return null;
    }

    @FXML private void goWelcome() { if (viewManager != null) viewManager.toWelcome(); }
}
