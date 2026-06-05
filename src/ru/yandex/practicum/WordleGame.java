package ru.yandex.practicum;

import java.io.PrintWriter;
import java.util.*;

public class WordleGame {

    private static final int WORD_LENGTH = 5;
    private final String answer;
    private int stepsLeft;
    private final WordleDictionary dictionary;
    private boolean won;
    private final List<String> previousGuesses;
    private final PrintWriter logger;
    private final List<String> previousHints = new ArrayList<>();
    private final Set<Character> correctLetters = new HashSet<>();
    private final Set<Character> wrongLetters = new HashSet<>();
    private final Map<Integer, Character> exactPositions = new HashMap<>();
    private final Map<Integer, Set<Character>> wrongPositions = new HashMap<>();

    public WordleGame(WordleDictionary dictionary, PrintWriter logger) {
        this.dictionary = dictionary;
        this.logger = logger;
        this.answer = selectRandomWord();
        this.stepsLeft = 6;
        this.won = false;
        this.previousGuesses = new ArrayList<>();
    }

    private String selectRandomWord() {
        List<String> words = dictionary.getWords();
        Random random = new Random();
        return words.get(random.nextInt(words.size()));
    }

    public String guess(String userWord) throws GameOverException, WordNotFoundException {
        if (won || stepsLeft <= 0) {
            throw new GameOverException("Игра уже закончена.");
        }

        if (userWord == null || userWord.isBlank()) {
            throw new WordNotFoundException("Введите слово.");
        }

        userWord = userWord.trim().toLowerCase().replace('ё', 'е');

        if (userWord.length() != WORD_LENGTH) {
            throw new WordNotFoundException("Слово должно состоять из " + WORD_LENGTH + " букв.");
        }

        if (previousGuesses.contains(userWord)) {
            throw new WordNotFoundException("Вы уже пробовали это слово");
        }

        if (!dictionary.getWords().contains(userWord)) {
            throw new WordNotFoundException("Слова нет в словаре.");
        }

        previousGuesses.add(userWord);
        stepsLeft--;

        if (userWord.equals(answer)) {
            won = true;
            logger.println("Игрок угадал слово: " + userWord);
            return "Поздравляю! Вы угадали слово!";
        }

        if (stepsLeft == 0) {
            logger.println("Игрок проиграл. Загаданное слово: " + answer);
            return "Вы проиграли. Загаданное слово: " + answer;
        }

        String hint = buildHint(userWord);
        logger.println("Ход: " + userWord + " -> " + hint);
        return hint;
    }

    private String buildHint(String userWord) {
        StringBuilder hint = new StringBuilder();
        char[] answerChars = answer.toCharArray();
        char[] guessChars = userWord.toCharArray();
        boolean[] used = new boolean[answerChars.length];

        for (int i = 0; i < guessChars.length; i++) {
            if (guessChars[i] == answerChars[i]) {
                hint.append('+');
                used[i] = true;
                exactPositions.put(i, guessChars[i]);
                correctLetters.add(guessChars[i]);
            } else {
                hint.append('?');
            }
        }

        for (int i = 0; i < guessChars.length; i++) {
            if (hint.charAt(i) == '+') {
                continue;
            }

            boolean found = false;
            for (int j = 0; j < answerChars.length; j++) {
                if (!used[j] && guessChars[i] == answerChars[j]) {
                    found = true;
                    used[j] = true;
                    correctLetters.add(guessChars[i]);
                    wrongPositions.computeIfAbsent(i, k -> new HashSet<>()).add(guessChars[i]);
                    break;
                }
            }
            hint.setCharAt(i, found ? '^' : '-');
            if (!found) {
                wrongLetters.add(guessChars[i]);
            }
        }
        return hint.toString();
    }

    private boolean isWordValid(String word) {
        for (Map.Entry<Integer, Character> entry : exactPositions.entrySet()) {
            if (word.charAt(entry.getKey()) != entry.getValue()) return false;
        }

        for (char ch : correctLetters) {
            if (word.indexOf(ch) == -1) return false;
        }

        for (char ch : wrongLetters) {
            if (word.indexOf(ch) != -1) return false;
        }

        for (Map.Entry<Integer, Set<Character>> entry : wrongPositions.entrySet()) {
            int pos = entry.getKey();
            for (char ch : entry.getValue()) {
                if (word.charAt(pos) == ch) return false;
            }
        }
        return true;
    }

    public String suggestWord() {
        List<String> candidates = new ArrayList<>();

        for (String word : dictionary.getWords()) {
            if (!previousGuesses.contains(word) && !previousHints.contains(word) && isWordValid(word)) {
             candidates.add(word);
            }
        }

        if (candidates.isEmpty()) {
            return "Нет подходящих слов для подсказки";
        }

        Random random = new Random();
        String word = candidates.get(random.nextInt(candidates.size()));
        previousHints.add(word);
        logger.println("Подсказка: " + word);
        return word;
    }

    public boolean isGameOver() {
        return won || stepsLeft <= 0;
    }

    public boolean isWon() {
        return won;
    }

    public int getStepsLeft() {
        return stepsLeft;
    }

    public String getAnswer() {
        return answer;
    }
}
