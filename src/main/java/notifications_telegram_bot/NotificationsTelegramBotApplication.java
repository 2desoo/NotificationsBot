package notifications_telegram_bot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class NotificationsTelegramBotApplication {

	public static void main(String[] args) {
		SpringApplication.run(NotificationsTelegramBotApplication.class, args);
	}

}
