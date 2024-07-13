package space.chunks.gamecup.dgr.passenger;

/**
 * @author Nico_ND1
 */
public record PassengerGeneratorConfig(

) {

  public record Flight(
      int minPassengers,
      int maxPassengers
  ) {
  }
}
