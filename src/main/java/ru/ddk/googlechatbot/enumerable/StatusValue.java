package ru.ddk.googlechatbot.enumerable;

public enum StatusValue {
    F("задание упало с ошибкой"),
    C("задание выполнено"),
    W("задание выполняется"),
    B("задание выполняется другим пользователем");

    private String text;

    StatusValue(String text) {
        this.text = text;
    }

    public String getText() {
        return this.text;
    }

    public static StatusValue fromString(String text) {
        for (StatusValue b : StatusValue.values()) {
            if (b.text.equalsIgnoreCase(text)) {
                return b;
            }
        }
        return null;
    }
}
