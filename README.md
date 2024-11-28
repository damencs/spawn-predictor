# Fight Caves Spawn Predictor
Serves as a Spawn Predictor based on preset rotations in OldSchool RuneScape. This can display your current and next wave spawns, or utilize a key bind to toggle on demand.

![](https://github.com/damencs/spawn-predictor/raw/master/readme-wavedisplay.png)

### Lobby Information
When you are in the TzHaar area, the plugin will become active and display the Lobby Information related to the Fight Caves Rotation. The Lobby Information will outline the state of the plugin, current and next rotations.
- 'Determining...' - This awaits the next server minute to begin to 'calibrate' the server time cycles. Do not enter when this is displayed.
  - ![](https://github.com/damencs/spawn-predictor/raw/master/readme-determining.png)
- 'Current Rotation: {#}*' - The current rotation in Fight Caves. When there is an '*' after the Rotation, this means that it is at risk of being slightly off due to inconsistencies with Jagex server times.
  - ![](https://github.com/damencs/spawn-predictor/raw/master/readme-currentrotation.png)
- 'Next: [T - {#}s, Rot: {#}] - This is the amount of time until the current rotation is cycled through and the next rotation in the cycle.
  - ![](https://github.com/damencs/spawn-predictor/raw/master/readme-nextrotation.png)

### Entrance Label
The Fight Caves entrace will have labeled text to assist in aiding players to not enter until it is determined/confirmed to reduce inquiries around the plugin not predicting appropriately.
- "Please wait for the plugin to determine the rotation." - This syncing server times and creating a baseline to track rotations.
  - ![](https://github.com/damencs/spawn-predictor/raw/master/readme-determinerotation.png)
- "Please wait for the plugin to confirm the rotation." - This is the 'safety net' that protects the first and last 5 seconds of a rotation being enabled. This will appear when the '*' is present for the Current Rotation.
  - ![](https://github.com/damencs/spawn-predictor/raw/master/readme-confirmrotation.png)
- "You may proceed into the Fight Caves." - This is when you should enter the Fight Caves instance and should have the best reduced risk chance of the server cycle being out of sync with the plugin.
  - ![](https://github.com/damencs/spawn-predictor/raw/master/readme-proceedintocaves.png)

### Set Rotation Command
This outlines the usage of the "::setrotation {#}" command which will forcefully set the plugin's rotation IF it is not calibrated, confirmed, or potentially incorrect in its prediction.

If you enter into the caves when a rotation is not determined OR you are already in the caves upon installing the plugin, you will be prompted with the following messages to assist in setting the rotation (without having to restart the Fight Caves).
- When entering the plugin is not calibrated or running when entering the caves:
  - ![](https://github.com/damencs/spawn-predictor/raw/master/readme-assistancecalibrated.png)
- When entering the Fight Caves and the rotation is not 'confirmed':
  - ![](https://github.com/damencs/spawn-predictor/raw/master/readme-assistanceconfirmed.png)

### Additional Information
More information on how the Fight Caves Spawn Predictions work: https://youtu.be/LG5hy9bfb_g and https://oldschool.runescape.wiki/w/TzHaar_Fight_Cave/Rotations

If you experience any issues or have any concerns, please reach out to Damen via a GitHub Issue or by Discord Messaging directly (Damen#9999) OR via the Runelite Discord by mentioning @Damen.

You can also receive assistance for any plugin that Damen manages on the plugin hub in the following **Discord**: https://discord.gg/F2mfSvcnaj

### Change Log
#### v1.2.0 - 11/28/24
- Removes: Config Item for Displaying Lobby Information (this was causing confusion as to why people did not know what they were getting or when to enter)
- Removes: UTC Server Time Display as this is no longer relevant with assisted measures
- Adjusts: Width for Lobby Information overlay has been extended from 125 to 135 to accommodate specific times on a single line
- Adjusts: Default Display Mode to "BOTH" for new players
- Adds: Command '::setrotation {#1-15}' to serve as an override if a player is in the caves without a predicted rotation (for various reasons)
- Adds: Assistance Messages if it is not a confirmed rotation OR if they were in the caves when the plugin first started on that account
- Adds: First and Last 5 seconds of a minute is protected and serves as a "cushion" for Jagex server time not always being precise to real-time
- Adds: Fight Caves Entrance label to assist with when a player should *wait* to enter for less risk to predicting a rotation
#### v1.1.0 - 3/5/23
- Adds: Jagex Server UTC Time instead of Local Time Reference for Rotation Determination
- Removes: the ability to see the determined rotation UNTIL the plugin has cycled (requires the UTC time to change a MINUTE value on server before committing to a rotation)
- Adds: Account Memory for storing the rotation and wave you were on so you can log out and back in while maintaining your wave rotation predictions (even after closing client)
#### v1.0.1 - 8/13/22
- Adjusts: Minor Plugin Hub Cosmetic Items
- Adjusts: Logging Level Lowered for a Debug Item
#### v1.0.0 - 8/9/22
- Plugin Released