# TODO

## Gameplay

- [x] Add a pause/resume control.
- [ ] Increase the game speed gradually as the score increases.
- [ ] Add a win state when the snake fills the board.
- [ ] Decide whether moving through screen edges should wrap around or remain game over.

## User experience

- [x] Display controls and a short start/restart instruction.
- [x] Improve the visual distinction between the snake head, body, and food.
- [ ] Add a high-score display.

## Code quality

- [x] Replace magic character directions with an enum or named constants.
- [x] Make game state initialization reusable for restarting.
- [x] Move the frame setup into a Swing event-dispatch-thread startup block.
