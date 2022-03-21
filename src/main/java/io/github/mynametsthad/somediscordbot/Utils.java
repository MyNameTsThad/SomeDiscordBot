package io.github.mynametsthad.somediscordbot;

public class Utils {
    public static String formatTime(long time) {
        StringBuilder sb = new StringBuilder();
        long seconds = time / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;
        long months = days / 30;
        long years = months / 12;

        if (years > 0) sb.append(years).append(years > 1 ? " years, " : " year, ");
        if (months > 0) sb.append(months).append(months > 1 ? " months " : " month, ");
        if (days > 0) sb.append(days).append(days > 1 ? " days, " : " day, ");
        if (hours > 0) sb.append(hours).append(hours > 1 ? " hours, " : " hour, ");
        if (minutes > 0) sb.append(minutes).append(minutes > 1 ? " minutes, " : " minute, ");
        if (seconds > 0) sb.append(seconds).append(seconds > 1 ? " seconds" : " second");
        return sb.toString();
    }

    public static String formatBold(String text) {
        return "**" + text + "**";
    }

    public static String formatItalic(String text) {
        return "*" + text + "*";
    }

    public static String formatUnderline(String text) {
        return "~~" + text + "~~";
    }

    public static String formatSpoiler(String text) {
        return "||" + text + "||";
    }

    public static String formatCode(String text) {
        return "`" + text + "`";
    }

    public static String formatBlockCode(String text, String language) {
        return "```" + language + "\n" + text + "\n```";
    }

    public static String formatQuote(String text) {
        return "> " + text;
    }

    public static String formatMentionUser(String id) {
        return "<@" + id + ">";
    }

    public static String formatMentionChannel(String id) {
        return "<#" + id + ">";
    }

    public static String formatMentionRole(String id) {
        return "<@&" + id + ">";
    }
}
