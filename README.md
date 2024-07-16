# GameCup 2024

## Getting started
We created a Java example to help you get started with the GameCup 2024. You can change and customize everything you want.

If you need a database or other services, please contact us in your Team specific Discord channel.

## Testing
To test your game locally, you can just run your main class, make sure you put all files that your server creates in the `.gitignore` file.

## Deployment
We automatically deploy your game to our servers when you create a new GitHub release using a custom Docker image. You can edit the `Dockerfile` to your needs.

If you want to add files, like a custom world, a plugin, etc., put them into the `template` folder. If you have any issues, please contact us or check the `Dockerfile`!

# Game

## Principle
The game is about managing the flow of passengers at an airport. The players must keep the passengers happy and make
sure they reach their destination on time. However, they will lose their patience and leave if they have to wait too
long.
The players can upgrade their airport over time and unlock more terminals.

For the GameCup the game will be a competitive minigame where multiple players can compete for reaching a goal of
successfully managing the airport and keeping the passenger flow going.

## Game Mechanics
Players are divided into teams which compete against each other.
The game is seperated into phases which are time based. For the actual game the ActiveGamePhase is used, which
registers their own listeners and tasks to manage the game. Periodically the ShoppingPhase starts, where the game
freezes and the teams must select upgrades for their airport. After every team (or X seconds) selected an upgrade
the ActiveGamePhase continues. The game ends after a certain amount of time or when a team reaches a certain amount
of passengers transported.

## Map
Each team has their own map which is loaded at the launch of the game. The map contains an Instance where players
will play on and MapObjects. The MapObjects are used for game mechanics like Passengers, Machines, Incidents or
more. They can be registered at runtime and may spawn anything. If wanted the implementations can implement the
Ticking interface to automatically get called every tick.

## Procedures & Incidents


## Passengers
Passengers are generated for each map in random intervals. They may be arriving or leaving and will complete work on
a specific list of machines. The list may vary as some passengers have luggage and others might not.
When they select the next machine to work on they walk to the start of the PassengerQueue of the machine and queue
up. If there is no one in queue they go to the work position of the machine. Their they play an animation, f.e.
giving their luggage or going through a security scan. After they are done they walk to the exit of the machine and
proceed with the next. If it was their last machine they go to their destination and leave.

## Commands
