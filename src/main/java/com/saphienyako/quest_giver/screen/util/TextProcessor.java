package com.saphienyako.quest_giver.screen.util;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class TextProcessor {

    public static final TextProcessor INSTANCE = new TextProcessor();

    private static final Pattern CONTROL_PATTERN =
            Pattern.compile("\\$\\(((?:(?:\\w+|#[0-9A-Fa-f]{6})(?:;(?:\\w+|#[0-9A-Fa-f]{6}))*)?)\\)");

    private TextProcessor() {}

    public List<Component> process(Component text) {
        return process(text.getString());
    }

    public List<Component> process(String text) {
        return Arrays.stream(text.split("\\$\\((?:n|newline)\\)"))
                .map(String::trim)
                .map(this::processLine)
                .toList();
    }

    public Component processLine(Component line) {
        return processLine(line.getString());
    }

    public Component processLine(String line) {

        if (line.isEmpty()) return Component.empty();

        Matcher matcher = CONTROL_PATTERN.matcher(line);

        MutableComponent result = Component.empty();
        int index = 0;
        Style style = Style.EMPTY;

        while (matcher.find()) {

            if (index < matcher.start()) {
                result.append(Component.literal(line.substring(index, matcher.start())).withStyle(style));
            }

            index = matcher.end();

            String commandBlock = matcher.group(1).trim();

            if (commandBlock.isEmpty()) {
                style = Style.EMPTY;
                continue;
            }

            for (String part : commandBlock.split(";")) {

                part = part.trim().toLowerCase(Locale.ROOT);

                /* HEX COLORS */

                if (part.startsWith("#")) {
                    try {
                        int color = Integer.parseInt(part.substring(1), 16);
                        style = style.withColor(TextColor.fromRgb(color));
                    } catch (NumberFormatException ignored) {}
                    continue;
                }

                /* BASIC FORMAT */

                switch (part) {
                    case "b", "bold" -> style = style.withBold(true);
                    case "i", "italic" -> style = style.withItalic(true);
                    case "u", "underline" -> style = style.withUnderlined(true);
                    case "s", "strikethrough" -> style = style.withStrikethrough(true);
                    default -> {

                        /* Vanilla formatting */

                        ChatFormatting formatting = ChatFormatting.getByName(part);
                        if (formatting != null) {
                            style = style.applyFormat(formatting);
                        }
                    }
                }
            }
        }

        if (index < line.length()) {
            result.append(Component.literal(line.substring(index)).withStyle(style));
        }

        return result;
    }
}
