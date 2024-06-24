package notifications_telegram_bot.repository;

import notifications_telegram_bot.entity.NotificationTask;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface NotificationTaskRepository extends JpaRepository<NotificationTask, Long> {
    List<NotificationTask> findNotificationTasksByDateTime(LocalDateTime localDateTime);
}
