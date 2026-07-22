package divar.aut.frontend.util;

import javafx.scene.control.TextField;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

/**
 * Formats prices for display (grouping digits by three with "," and never
 * using scientific notation) and helps price {@link TextField}s stay
 * formatted while the user types.
 */
public final class PriceFormatter {

    private PriceFormatter() {}

    /**
     * Formats a price for display, e.g. {@code 20000000} becomes
     * {@code "20,000,000"}. Never falls back to scientific notation,
     * regardless of magnitude. Returns {@code "0"} for {@code null}.
     */
    public static String format(Double price) {
        return price == null ? "0" : format(price.doubleValue());
    }

    /**
     * Formats a price for display, e.g. {@code 20000000} becomes
     * {@code "20,000,000"}. Never falls back to scientific notation,
     * regardless of magnitude.
     */
    public static String format(double price) {
        DecimalFormat format = new DecimalFormat("#,##0.##", DecimalFormatSymbols.getInstance(Locale.US));
        format.setRoundingMode(RoundingMode.HALF_UP);
        return format.format(price);
    }

    /**
     * Removes grouping separators so the text can be parsed back into a
     * number, e.g. {@code "20,000,000"} becomes {@code "20000000"}.
     */
    public static String stripGrouping(String text) {
        return text == null ? "" : text.replace(",", "").trim();
    }

    /**
     * Parses text that may contain "," grouping separators into a
     * {@code double}. Delegates to {@link Double#parseDouble(String)} after
     * stripping the separators, so it throws {@link NumberFormatException}
     * on invalid input the same way.
     */
    public static double parse(String text) {
        return Double.parseDouble(stripGrouping(text));
    }

    /**
     * Makes a numeric {@link TextField} reformat itself with "," grouping
     * separators as the user types (digits and at most one decimal point
     * are kept; everything else is stripped).
     */
    public static void attachLiveGrouping(TextField field) {
        field.textProperty().addListener((obs, oldText, newText) -> {
            String reformatted = reformatWhileTyping(newText);
            if (!reformatted.equals(newText)) {
                field.setText(reformatted);
                field.positionCaret(reformatted.length());
            }
        });
    }

    private static String reformatWhileTyping(String text) {
        if (text == null || text.isEmpty()) return "";

        StringBuilder digitsOnly = new StringBuilder();
        boolean seenDot = false;
        String decimals = "";
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (Character.isDigit(c)) {
                if (seenDot) decimals += c; else digitsOnly.append(c);
            } else if (c == '.' && !seenDot) {
                seenDot = true;
            }
        }

        if (digitsOnly.isEmpty() && !seenDot) return "";

        String intPart = digitsOnly.isEmpty() ? "0" : digitsOnly.toString();
        String grouped = format(Double.parseDouble(intPart));
        return seenDot ? grouped + "." + decimals : grouped;
    }
}
