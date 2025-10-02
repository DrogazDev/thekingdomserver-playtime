package nl.drogaz.thekingdomserver_assignment.helper;

public class TimeFormatHelper {

    public static String getTime(long totalSeconds) {
        long seconds = totalSeconds;

        long years = seconds / (365 * 24 * 60 * 60);
        seconds %= 365 * 24 * 60 * 60;

        long months = seconds / (30 * 24 * 60 * 60);
        seconds %= 30 * 24 * 60 * 60;

        long weeks = seconds / (7 * 24 * 60 * 60);
        seconds %= 7 * 24 * 60 * 60;

        long days = seconds / (24 * 60 * 60);
        seconds %= 24 * 60 * 60;

        long hours = seconds / (60 * 60);
        seconds %= 60 * 60;

        long minutes = seconds / 60;
        seconds %= 60;

        StringBuilder sb = new StringBuilder();
        if (years > 0) sb.append(years).append(" ").append(years == 1 ? "Jaar" : "Jaren").append(", ");
        if (months > 0) sb.append(months).append(" ").append(months == 1 ? "Maand" : "Maanden").append(", ");
        if (weeks > 0) sb.append(weeks).append(" ").append(weeks == 1 ? "Week" : "Weken").append(", ");
        if (days > 0) sb.append(days).append(" ").append(days == 1 ? "Dag" : "Dagen").append(", ");
        if (hours > 0) sb.append(hours).append(" ").append(hours == 1 ? "Uur" : "Uren").append(", ");
        if (minutes > 0) sb.append(minutes).append(" ").append(minutes == 1 ? "Minuut" : "Minuten").append(" en ");
        sb.append(seconds).append(" ").append(seconds == 1 ? "Seconde" : "Seconden");

        return sb.toString();
    }
}
