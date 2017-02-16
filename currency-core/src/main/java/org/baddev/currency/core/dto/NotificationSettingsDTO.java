package org.baddev.currency.core.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data @AllArgsConstructor
public class NotificationSettingsDTO {
    private boolean mailOnExchangeTaskCompletion;
    private boolean uiNotifOnExchangeTaskCompletion;
}