package org.example;

import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
public class Main {
    public static void main(String[] args) {
        try {
            Bot bot = new Bot();
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(bot); //регистрируем бота
            System.out.println("Бот запущен");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}