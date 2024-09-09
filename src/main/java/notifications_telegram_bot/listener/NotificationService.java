package notifications_telegram_bot.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import notifications_telegram_bot.repository.NotificationTaskRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Service
public class NotificationService {
    private final TelegramBot telegramBot;
    private final NotificationTaskRepository notificationTaskRepository;

    public NotificationService(TelegramBot telegramBot, NotificationTaskRepository notificationTaskRepository) {
        this.telegramBot = telegramBot;
        this.notificationTaskRepository = notificationTaskRepository;
    }

    @Scheduled(cron = "0 0/1 * * * *")
    public void checkNotification() {
        notificationTaskRepository.findNotificationTasksByDateTime(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES)).
                forEach(notificationTask ->
                {
                    telegramBot.execute(new SendMessage(notificationTask.getChatId(), notificationTask.getText()));
                });
    }
}
