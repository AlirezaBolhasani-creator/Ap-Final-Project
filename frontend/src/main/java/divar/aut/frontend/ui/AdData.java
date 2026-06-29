package divar.aut.frontend.ui;

import java.util.List;

/**
 * Immutable data holder for a single advertisement.
 * Replace with your real model/DTO class.
 */
public record AdData(
        String title,
        String price,
        String location,
        String time,
        String condition,
        String imageUrl,
        int    photoCount
) {
    /** Generates a page of sample ads for development/demo purposes. */
    public static List<AdData> samplePage(int page) {
        int offset = page * 6;
        return List.of(
            new AdData("فروش پراید ۱۳۱ مدل ۹۵", "۵۵۰٬۰۰۰٬۰۰۰", "نیروگاه", "لحظاتی پیش", "کارکرده", null, 2),
            new AdData("تابلو رزین نقره‌ای مادر و کودک", "۵۰۰٬۰۰۰", "دانش", "۱۰ دقیقه پیش", "در حد نو", null, 2),
            new AdData("یک جفت مرغ عشق", "۵۰۰٬۰۰۰", "زاویه", "۲ ساعت پیش", "نو", null, 7),
            new AdData("قلموی سری ۱۱۱۱ پارس آرت", "۲٬۷۶۵٬۰۰۰", "یزدان‌شهر", "۳۰ دقیقه پیش", "کارکرده", null, 2),
            new AdData("برنج هندی جشنواره ۱۰ کیلویی", "۲٬۵۰۰٬۰۰۰", "امام", "۱ ساعت پیش", "نو", null, 2),
            new AdData("مبل خاص مدرنا کاملاً نو", "۱۷۹٬۰۰۰٬۰۰۰", "کلهر", "همین الان", "نو", null, 6)
        );
    }
}
