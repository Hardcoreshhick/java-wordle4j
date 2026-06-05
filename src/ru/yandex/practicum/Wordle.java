package ru.yandex.practicum;

import ru.yandex.practicum.exceptions.GameInitializationException;
import ru.yandex.practicum.exceptions.GameOverException;
import ru.yandex.practicum.exceptions.WordNotFoundException;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class Wordle {

    public static void main(String[] args) {
        PrintWriter logger = null;
        try {
             logger = new PrintWriter(new FileWriter("game.log", StandardCharsets.UTF_8));
            WordleDictionaryLoader loader = new WordleDictionaryLoader();
            WordleDictionary dictionary;
            try {
                dictionary = loader.load("words_ru.txt");
                if (dictionary.getWords().isEmpty()) {
                    throw new GameInitializationException("Словарь пуст, игра невозможна");
                }
            } catch (GameInitializationException | IOException e) {
                logger.println("Ошибка загрузки словаря: " + e.getMessage());
                System.out.println("Не удалось загрузить словарь. Попробуйте позже.");
                return;
            }

            WordleGame game = new WordleGame(dictionary, logger);
            Scanner scanner = new Scanner(System.in);

            logger.println("Игра начата. Загадано слово: " + game.getAnswer());

            printGreeting();

            while (!game.isGameOver()) {
                System.out.println("Осталось попыток: " + game.getStepsLeft());
                System.out.println("Введите слово (или нажмите Enter для подсказки): ");
                String input = scanner.nextLine();

                if (input.isBlank()) {
                    String hintWord = game.suggestWord();
                    System.out.println("Подсказка: " + hintWord);
                    logger.println("Игрок запросил подсказку: " + hintWord);
                    continue;
                }

                try {
                    String result = game.guess(input);
                    System.out.println(result);
                    logger.println("Ход: " + input + " -> " + result);
                } catch (WordNotFoundException | GameOverException e) {
                    System.out.println(e.getMessage());
                    logger.println("Ошибка: " + e.getMessage());
                }
            }

            System.out.println("Игра окончена. Загаданное слово: " + game.getAnswer());

        } catch (Exception e) {
            if (logger != null) {
                logger.println("Критическая ошибка: " + e.getMessage());
                e.printStackTrace(logger);
            }
            System.out.println("Произошла внутренняя ошибка. Игра завершена.");
        } finally {
            if (logger != null) {
                logger.close();
            }
        }
    }

    private static void printGreeting() {
        System.out.println("Добро пожаловать в игру Wordle!");
        System.out.println("Нужно угадать слово из 5 букв.");
        System.out.println("+ — буква на месте, ^ — буква есть, но не на месте, - — буквы нет.");
        System.out.println("Если хотите подсказку — просто нажмите Enter.\n");
    }
}
