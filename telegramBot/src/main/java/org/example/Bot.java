package org.example;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;


public class Bot extends TelegramLongPollingBot{
    private int activeThreads = 0;
    @Override
    public String getBotUsername() {
        return "@dezoleeBot";
    }

    @Override
    public String getBotToken() {
        return "7952337112:AAH6QXx4gQLqMH8usuyBu6dRxeyrMjTv7Lk";
    }

    //метод, вызывающийся, когда бот получает новое соо от юзера
    //создается поток
    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();
            if (messageText.equals("/start")) {
                sendMessage(chatId, "Привет, бро. Отправь мне число, и я вычислю его квадрат.");
                return; // Завершаем обработку, чтобы не проверять число
            }
            try {
                double num = Double.parseDouble(messageText);
                synchronized (this) {
                    activeThreads++;
                    System.out.println("Создан новый поток. Активных потоков:" + activeThreads);

                }
                Thread thread = new Thread(() -> processNumber(chatId, num),"MyThread-" + activeThreads);
                thread.start();
                System.out.println("Поток для юзера " + chatId + "(число: " + num + ") запущен");
            } catch (NumberFormatException e) {
                sendMessage(chatId, "Отправьте число");
            }
        }
    }
    //вычисление в отдельном потоке
    public void processNumber(long chatId, double num) {
        String threadName = Thread.currentThread().getName();
        System.out.println("Поток " + threadName + " обрабатывает число " + num + " для юзера " + chatId);
        try {
            Thread.sleep(3000);
            double res = num * num;
            sendMessage(chatId, "Квадрат числа " + num + " = " + res);
            System.out.println("Поток " + threadName + " закончил обрабатывать число " + num + " для юзера " + chatId);
        } catch (InterruptedException e) { //если поток был прерван
            sendMessage(chatId, "Ошибка при обработке числа");
        } finally {
            // Уменьшаем счётчик активных потоков
            synchronized (this) {
                activeThreads--;
                System.out.println("Поток завершён. Активных потоков: " + activeThreads);
            }
        }
    }

    public void sendMessage(long chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(text);
        try {
            execute(message);
        } catch (TelegramApiException e) { //если произошла ошибка при отправке
            e.printStackTrace();
        }
    }

}
