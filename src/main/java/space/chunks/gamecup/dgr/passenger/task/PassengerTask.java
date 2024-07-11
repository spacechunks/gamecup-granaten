package space.chunks.gamecup.dgr.passenger.task;

/**
 * @author Nico_ND1
 */
public interface PassengerTask {
  /*
  Passengers have a list of target machines that they have to visit before going to their destination and leave.
  When they start visiting a machine they walk to the start of the PassengerQueue and queue up. If there is no one in queue they go to the work pos of the machine.
  There they may deposit their baggage and wait for a process. After that they leave the machine and go to the next one.
  If it was their last machine they go to their destination and leave.

  Example:

  - Baggage Claim
     they queue up in front of the line spread out randomly and their luggage will roll to them and pick it up
  -
   */
}
