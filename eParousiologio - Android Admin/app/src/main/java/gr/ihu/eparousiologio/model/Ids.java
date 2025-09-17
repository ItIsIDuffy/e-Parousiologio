package gr.ihu.eparousiologio.model;

import android.annotation.SuppressLint;

import java.text.Normalizer;
import java.util.Locale;

public class Ids {
    private Ids() {
    }

    public static String sanitizeCourseId(String s) {
        if (s == null) return "course";
        String n = Normalizer.normalize(s, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");
        n = n.replaceAll("[^\\p{L}\\p{Nd}\\s-]", "")
                .trim()
                .replaceAll("[\\s-]+", "_")
                .toLowerCase(Locale.ROOT);
        if (n.isEmpty()) n = "course";
        return n;
    }

    public static String sanitizeLabId(String greekDay, String hourFrom, String explicitCode) {
        if (explicitCode != null && !explicitCode.trim().isEmpty()) {
            return explicitCode.trim()
                    .replaceAll("[^\\p{L}\\p{Nd}-_]", "")
                    .toLowerCase(Locale.ROOT);
        }
        String day = daySlugFromGreek(greekDay);
        String hhmm = normalizeHour(hourFrom);
        return day + "-" + hhmm;
    }

    public static String daySlugFromGreek(String day) {
        if (day == null) return "day";
        switch (day.trim()) {
            case "Δευτέρα":
                return "mon";
            case "Τρίτη":
                return "tue";
            case "Τετάρτη":
                return "wed";
            case "Πέμπτη":
                return "thu";
            case "Παρασκευή":
                return "fri";
            case "Σάββατο":
                return "sat";
            case "Κυριακή":
                return "sun";
            default:
                return "day";
        }
    }

    @SuppressLint("DefaultLocale")
    private static String normalizeHour(String from) {
        try {
            int h = Integer.parseInt(from);
            return String.format("%02d00", h);
        } catch (Exception e) {
            return "0000";
        }
    }
}
