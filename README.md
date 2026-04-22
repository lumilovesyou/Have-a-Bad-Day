<div align="center"><center>

<img alt="An eye against a blue background with bubbles floating upwards behind it" src="https://raw.githubusercontent.com/lumilovesyou/Have-a-Bad-Day/refs/heads/main/src/main/Images/icon-small.png">

# Have a Bad Day

*A mod with the sole purpose of making playing the game unbearable.*

Have a Bad Day adds blinking and breathing that the player must manually activate regularly.

</center></div>

## How to use

### Keybinds

Pressing `v` to breathe will refill your oxygen bar unless you're underwater or on fire. Pressing `b` to blink will remove the eye strain overlay and reset the amount of time until you next need to blink.

### Items

Eye drops will keep the player from needing to blink for three minutes by giving them the "Refreshed" potion effect. They can be applied by looking up and right-clicking while holding them.

### Recipes

Eye drops can be craft with an iron ingot, any type of dye, a water bottle, and an iron nugget.

<img alt="A crafting grid with an iron ingot, blue dye, water bottle, and iron nugget" src="https://raw.githubusercontent.com/lumilovesyou/Have-a-Bad-Day/refs/heads/main/Images/dropper-recipe.png">

### Gamerules

There's two gamerules. `require_blinking` and `require_breathing`. These can be changed in the world creation screen under the `PLAYER` category or by using the command `/gamerule have-a-bad-day:gamerule true/false`.

`require_blinking` when set to `false` disables the need to blink, though blinking is still possible.

`require_breathing` when set to `false` returns the oxygen bar back to the vanilla system. Breathing is not possible while this is disabled.
