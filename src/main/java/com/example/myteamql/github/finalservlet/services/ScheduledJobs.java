package com.example.myteamql.github.finalservlet.services;

import com.example.myteamql.github.finalservlet.entities.Room;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Log4j2
@Configuration
@EnableScheduling
public class ScheduledJobs {
  private static final long INTERVAL = 30000L;

  private RoomService roomService;
  private ReservationService reservationService;

  @Autowired
  public ScheduledJobs(RoomService roomService, ReservationService reservationService) {
    this.roomService = roomService;
    this.reservationService = reservationService;
  }

  @Scheduled(fixedDelay = INTERVAL)
  private void updateAvailability() {
    log.info("Updating availabilities.");
    roomService.getAllRooms().stream()
        .map(Room::getRoomNumber)
        .forEach(reservationService::changeNextAvailable);
  }
}
