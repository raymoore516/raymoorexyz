package xyz.raymoore.app.connections.category;

public enum Color {
    yellow("\uD83D\uDFE8"),  // 🟨
    green("\uD83D\uDFE9"),  // 🟩
    blue("\uD83D\uDFE6"),  // 🟦
    purple("\uD83D\uDFEA");  // 🟪

    private String emoji;

    Color (String emoji) {
        this.emoji = emoji;
    }

    public String getEmoji() {
        return emoji;
    }
}
