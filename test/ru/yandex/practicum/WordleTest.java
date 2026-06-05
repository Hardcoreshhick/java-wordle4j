package ru.yandex.practicum;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.PrintWriter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class WordleTest {
    private WordleGame game;

    @BeforeEach
    void setUp() {
        WordleDictionary dictionary = new WordleDictionary(List.of("герой", "гонец", "слово", "котёл", "мотор"));
        game = new WordleGame(dictionary, new PrintWriter(System.out));
    }

    @Test
    @DisplayName("Слово не из 5 букв")
    void testWrongLength() {
        assertThrows(WordNotFoundException.class, () -> game.guess("кот"));
        assertEquals(6, game.getStepsLeft());
    }

    @Test
    @DisplayName("Слова нет в словаре")
    void testWordNotInDictionary() {
        assertThrows(WordNotFoundException.class, () -> game.guess("абвгд"));
        assertEquals(6, game.getStepsLeft());
    }

    @Test
    @DisplayName("Подсказка работает")
    void testSuggestWord() {
        String word = game.suggestWord();
        assertNotNull(word);
        assertEquals(5, word.length());
    }

    @Test
    @DisplayName("Победа")
    void testWin() throws Exception {
        java.lang.reflect.Field field = WordleGame.class.getDeclaredField("answer");
        field.setAccessible(true);
        field.set(game, "герой");

        String result = game.guess("герой");
        assertTrue(game.isWon());
        assertEquals("Поздравляю! Вы угадали слово!", result);
    }

    @Test
    @DisplayName("Игра начинается с 6 шагов")
    void testStepsAtStart() {
        assertEquals(6, game.getStepsLeft());
        assertFalse(game.isGameOver());
    }
}
