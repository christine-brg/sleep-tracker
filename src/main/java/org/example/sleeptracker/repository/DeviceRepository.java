package org.example.sleeptracker.repository;

import org.example.sleeptracker.models.Device;
import org.example.sleeptracker.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DeviceRepository extends JpaRepository<Device, Long> {
    Optional<Device> findByUser(User user);
}
