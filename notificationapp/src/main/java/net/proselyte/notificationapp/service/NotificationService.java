package net.proselyte.notificationapp.service;

import lombok.extern.slf4j.Slf4j;
import net.proselyte.notificationapp.dto.NotificationDto;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.UUID;

@Slf4j
@Service
public class NotificationService {

    private final Random random = new Random();

    public NotificationDto save(NotificationDto dto) {
        log.info("IN save - received event with message {}", dto.message());
        return new NotificationDto(UUID.randomUUID().toString(), System.currentTimeMillis(), System.currentTimeMillis(), dto.message());
    }

    public NotificationDto getByUid(UUID uid) {
        return generateNotification(uid);
    }

    private NotificationDto generateNotification(UUID uuid) {
        return new NotificationDto(UUID.randomUUID().toString(), System.currentTimeMillis() - random.nextInt(1_000_000), System.currentTimeMillis(), "Notification: " + uuid);
    }
}
