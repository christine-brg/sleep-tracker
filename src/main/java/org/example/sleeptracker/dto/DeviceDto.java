package org.example.sleeptracker.dto;

import lombok.Builder;
import lombok.Data;
import org.example.sleeptracker.models.Device;
import org.example.sleeptracker.models.User;

import java.time.LocalDateTime;

@Data
@Builder
public class DeviceDto {

    private String deviceName;
    private String model;
    private String firmwareVersion;
    private LocalDateTime registeredAt;
    private String username;

    public static DeviceDto fromDevice(Device device, User user) {
        return DeviceDto.builder()
                .deviceName(device.getDeviceName())
                .model(device.getModel())
                .firmwareVersion(device.getFirmwareVersion())
                .registeredAt(device.getRegisteredAt())
                .username(user.getUsername())
                .build();
    }

}
