package notifications_telegram_bot.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import jakarta.annotation.PostConstruct;
import notifications_telegram_bot.entity.NotificationTask;
import notifications_telegram_bot.repository.NotificationTaskRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.regex.Pattern;

@Service
public class TelegramBotUpdatesListener implements UpdatesListener {

    private Logger logger = LoggerFactory.getLogger(TelegramBotUpdatesListener.class);

    private final TelegramBot telegramBot;

    private final NotificationTaskRepository notificationTaskRepository;

    public TelegramBotUpdatesListener(TelegramBot telegramBot, NotificationTaskRepository notificationTaskRepository) {
        this.telegramBot = telegramBot;
        this.notificationTaskRepository = notificationTaskRepository;
    }

    @PostConstruct
    public void init() {
        telegramBot.setUpdatesListener(this);
    }

    @Override
    public int process(List<Update> updates) {
        updates.forEach(update -> {
            logger.info("Processing update: {}", update);
            String updatesMessageText = update.message().text();
            Long chatId = update.message().chat().id();

            if (updatesMessageText == null) {
                logger.warn("Message or editedMessage is null in update: {}", update);
                return;
            }

            String messageText = "Добро пожаловать!\n" +
                    "Здесь вы можете создать напоминание.\n" +
                    "Для этого отправьте сообщение в формате:\n" +
                    "дд.мм.гггг чч:мм Текст напоминания\n" +
                    "(прим. 01.01.2022 20:00 Сделать домашнюю работу)";

            if (update.message() != null && updatesMessageText.equals("/start")) {
                sendMessage(chatId, messageText);
            } else {
                Pattern pattern = Pattern.compile("([0-9\\.\\:\\s]{16})(\\s)([\\W+]+)");
                var matcher = pattern.matcher(updatesMessageText);
                if (matcher.matches()) {
                    String date = matcher.group(1);
                    String text = matcher.group(3);
                    try {
                        NotificationTask notificationTask = new NotificationTask();
                        notificationTask.setChatId(chatId);
                        notificationTask.setText(text);
                        notificationTask.setDateTime(LocalDateTime.parse(date, DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")));
                        notificationTaskRepository.save(notificationTask);
                        sendMessage(chatId, "Напоминание успешно создано!\n" + date + " я напомню вам " + text);
                    } catch (DateTimeParseException e) {
                        logger.warn("Error");
                        throw new RuntimeException(e);
                    }
                } else {
                    logger.warn("Неправильный формат сообщения");
                    telegramBot.execute(new SendMessage(chatId, "Неправильный формат сообщения"));
                }
            }
        });
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }

    private void sendMessage(long chatId, String messageText) {
        SendMessage message = new SendMessage(chatId, messageText);
        try {
            telegramBot.execute(message);
        } catch (Exception e) {
            logger.error("Error sending message", e);
        }
    }
}
