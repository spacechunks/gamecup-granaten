package space.chunks.gamecup.dgr.passenger.task;

import org.jetbrains.annotations.NotNull;
import space.chunks.gamecup.dgr.map.object.impl.procedure.Procedure;
import space.chunks.gamecup.dgr.passenger.Passenger;

import java.util.ArrayDeque;
import java.util.Queue;


/**
 * @author Nico_ND1
 */
public final class PassengerTaskBuilder {
  @NotNull
  public Queue<PassengerTask> createTasks(@NotNull Passenger passenger) {
    Queue<PassengerTask> tasks = new ArrayDeque<>(100);
    switch (passenger.destination()) {
      case ARRIVING -> createTasksForArriving(tasks, passenger);
      case LEAVING -> createTasksForLeaving(tasks, passenger);
    }
    return tasks;
  }

  // Security Check
  // Ticket Control
  // Sit down (optional)
  private void createTasksForLeaving(Queue<PassengerTask> tasks, Passenger passenger) {
    tasks.add(new PassengerTask(passenger, Procedure.SECURITY_CHECK));
    tasks.add(new PassengerTask(passenger, Procedure.TICKET_CONTROL));
    if (Math.random() > 0.32322135325421D) {
      tasks.add(new PassengerTask(passenger, Procedure.SEAT));
    }
  }

  // Baggage Claim (optional)
  // Pass Control
  private void createTasksForArriving(Queue<PassengerTask> tasks, Passenger passenger) {
    if (passenger.baggage() != null) {
      tasks.add(new PassengerTask(passenger, Procedure.LUGGAGE_CLAIM));
    }
    //tasks.add(new PassengerTask(passenger, Procedure.PASS_CONTROL));
  }


}
