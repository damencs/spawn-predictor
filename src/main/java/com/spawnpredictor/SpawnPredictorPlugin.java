/*
 * Copyright (c) 2022, Damen <gh: damencs>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:

 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.

 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.

 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.spawnpredictor;

import com.google.gson.Gson;
import com.google.inject.Provides;
import com.spawnpredictor.overlays.DebugOverlayPanel;
import com.spawnpredictor.overlays.DisplayModeOverlay;
import com.spawnpredictor.overlays.RotationOverlayPanel;
import com.spawnpredictor.util.AccountMemory;
import com.spawnpredictor.util.FightCavesNpc;
import com.spawnpredictor.util.FightCavesNpcSpawn;
import com.spawnpredictor.util.StartLocations;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.input.KeyListener;
import net.runelite.client.input.KeyManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.task.Schedule;
import net.runelite.client.ui.overlay.OverlayManager;
import org.apache.commons.lang3.ArrayUtils;

import javax.inject.Inject;
import java.awt.event.KeyEvent;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@PluginDescriptor(
		name = "FC Spawn Predictor",
		description = "Determine the spawn rotations for Fight Caves",
		tags = {"fight", "caves", "jad", "spawn", "predictor", "waves", "time", "timer", "rotation", "damen"}
)
@Slf4j
public class SpawnPredictorPlugin extends Plugin implements KeyListener
{
	@Inject
	private Client client;

	@Inject
	private KeyManager keyManager;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private RotationOverlayPanel rotationOverlayPanel;

	@Inject
	private DisplayModeOverlay displayModeOverlay;

	@Inject
	private DebugOverlayPanel debugOverlayPanel;

	@Inject
	private SpawnPredictorConfig config;

	@Inject
	private ConfigManager configManager;

	@Inject
	private Gson gson;

	@Provides SpawnPredictorConfig providesConfig(ConfigManager configManager)
	{
		return configManager.getConfig(SpawnPredictorConfig.class);
	}

	@Getter
	private static List<List<FightCavesNpcSpawn>> waveData = new ArrayList<>();

	@Getter
	private LocalTime serverUTCTime;

	private int serverTime = -1;
	private int oldServerTime = -1;
	private int serverTimeSecondOffset = 1;

	@Getter
	private boolean serverUTCTimeSecondSet = false;

	@Getter
	private int currentUTCTime;

	@Getter
	private int rotationCol = -1;

	@Getter
	private int currentWave = -1;

	@Getter
	private int currentRotation = -1;

	@Getter
	private static int rsVal = -1;

	@Getter
	private boolean hotkeyEnabled = false;

	private boolean active = false; // This boolean is required because of loading lines

	private boolean confirmedReset = false;

	private final Pattern WAVE_PATTERN = Pattern.compile(".*Wave: (\\d+).*");

	public boolean isFightCavesActive()
	{
		return ArrayUtils.contains(client.getMapRegions(), 9551) && client.isInInstancedRegion();
	}

	public boolean isInTzhaarArea()
	{
		return ArrayUtils.contains(client.getMapRegions(), 9808) && !client.isInInstancedRegion();
	}

	@Override
	protected void startUp()
	{
		overlayManager.add(rotationOverlayPanel);
		overlayManager.add(displayModeOverlay);
		overlayManager.add(debugOverlayPanel);

		keyManager.registerKeyListener(this);
	}

	@Override
	protected void shutDown()
	{
		overlayManager.remove(rotationOverlayPanel);
		overlayManager.remove(displayModeOverlay);
		overlayManager.remove(debugOverlayPanel);

		keyManager.unregisterKeyListener(this);

		reset();
	}

	private void reset()
	{
		currentUTCTime = -1;
		rotationCol = -1;
		currentWave = -1;
		currentRotation = -1;
		rsVal = -1;
		active = false;
		hotkeyEnabled = false;
	}

	@Subscribe
	private void onConfigChanged(ConfigChanged event)
	{
		if (event.getGroup().equalsIgnoreCase("spawnpredictor"))
		{
			switch (event.getKey())
			{
				case "displayMode":
				case "displayCurrentWaveToggle":
				case "displayCurrentWaveKey":
				{
					hotkeyEnabled = false;
					break;
				}
			}
		}
	}

	@Subscribe
	private void onGameTick(GameTick event)
	{
		if (!isFightCavesActive() && !confirmedReset && (isInTzhaarArea() || !client.isInInstancedRegion()))
		{
			if (client.getLocalPlayer() != null)
			{
				if (getRotationConfig(client.getLocalPlayer().getName()) != null)
				{
					removeRotationConfig(client.getLocalPlayer().getName());
				}

				confirmedReset = true;
			}
		}
	}

	@Subscribe
	private void onGameStateChanged(GameStateChanged event)
	{
		if (event.getGameState() != GameState.LOGGED_IN)
		{
			if (event.getGameState() == GameState.HOPPING || event.getGameState() == GameState.LOGIN_SCREEN)
			{
				reset();
				confirmedReset = false;
			}
			return;
		}

		if (isFightCavesActive() && !active)
		{
			AccountMemory memory = getRotationConfig(client.getLocalPlayer().getName());
			if (memory != null)
			{
				rotationCol = memory.getRotation();
				currentRotation = StartLocations.translateRotation(rotationCol);
				currentWave = memory.getWave();
			}
			else
			{
				currentRotation = StartLocations.translateRotation(rotationCol);
				currentWave = 1; // Should fix not displaying 'Wave 1' before seeing the 1st wave chat message
			}

			if (rotationCol == -1 || currentRotation == -1)
			{
				return;
			}

			rsVal = StartLocations.getLookupMap().get(currentRotation);
			updateWaveData(rsVal);
			active = true;
		}
		else if (!isFightCavesActive())
		{
			reset();
			removeRotationConfig(client.getLocalPlayer().getName());
		}
	}

	@Subscribe
	private void onChatMessage(ChatMessage event)
	{
		if (event.getType() != ChatMessageType.GAMEMESSAGE)
		{
			return;
		}

		if (event.getMessage().contains("Your TzTok-Jad kill count"))
		{
			removeRotationConfig(client.getLocalPlayer().getName());
			return;
		}

		if (isFightCavesActive() && getCurrentRotation() != -1)
		{
			if (event.getMessage().contains("The Fight Cave has been paused."))
			{
				currentWave++;
				saveRotation(client.getLocalPlayer().getName(), rotationCol, currentWave);
				return;
			}

			final Matcher waveMatcher = WAVE_PATTERN.matcher(event.getMessage());

			if (!waveMatcher.matches())
			{
				return;
			}

			currentWave = Integer.parseInt(waveMatcher.group(1));

			if (currentWave != 0)
			{
				saveRotation(client.getLocalPlayer().getName(), rotationCol, currentWave);
			}

			if (currentRotation == 7 && currentWave == 3)
			{
				rsVal = 11; // Different spawn points on the spawn wheel for Wave 4+
				updateWaveData(rsVal);
			}
		}
	}

	private void updateWaveData(int rsVal)
	{
		waveData = calculateSpawns(rsVal);
	}

	private static List<List<FightCavesNpcSpawn>> calculateSpawns(int rsVal)
	{
		ArrayList spawns = new ArrayList<List<FightCavesNpcSpawn>>();

		int currentCycle = rsVal;

		for (FightCavesNpc npc : FightCavesNpc.values())
		{
			if (npc == FightCavesNpc.JAD)
			{
				continue;
			}

			List<List<FightCavesNpcSpawn>> subSpawns = generateSubSpawns((currentCycle + 1) % 15, npc, spawns);

			ArrayList initialSpawn = new ArrayList<FightCavesNpcSpawn>();
			initialSpawn.add(new FightCavesNpcSpawn(npc, currentCycle));
			spawns.add(initialSpawn);
			currentCycle = (currentCycle + 1) % 15;

			spawns.addAll(subSpawns);
			currentCycle = (currentCycle + subSpawns.size()) % 15;

			ArrayList postSpawns = new ArrayList<FightCavesNpcSpawn>();
			postSpawns.add(new FightCavesNpcSpawn(npc, currentCycle));
			postSpawns.add(new FightCavesNpcSpawn(npc, (currentCycle + 1) % 15));
			spawns.add(postSpawns);
			currentCycle = (currentCycle + 1) % 15;
		}

		ArrayList jadSpawn = new ArrayList<FightCavesNpcSpawn>();
		jadSpawn.add(new FightCavesNpcSpawn(FightCavesNpc.JAD, currentCycle));
		spawns.add(jadSpawn);

		return spawns;
	}

	private static List<List<FightCavesNpcSpawn>> generateSubSpawns(int currentCycle, FightCavesNpc npc, List<List<FightCavesNpcSpawn>> existing)
	{
		ArrayList subSpawns = new ArrayList<List<FightCavesNpcSpawn>>();

		for (List<FightCavesNpcSpawn> existingWave : existing)
		{
			ArrayList newSpawn = new ArrayList<FightCavesNpcSpawn>();
			newSpawn.add(new FightCavesNpcSpawn(npc, currentCycle));

			for (int i = 0; i < existingWave.size(); i++)
			{
				FightCavesNpcSpawn existingSpawn = existingWave.get(i);
				newSpawn.add(new FightCavesNpcSpawn(existingSpawn.getNpc(), (currentCycle + i + 1) % 15));
			}

			subSpawns.add(newSpawn);
			currentCycle = (currentCycle + 1) % 15;
		}

		return subSpawns;
	}

	public final LocalTime getUTCTime()
	{
		return LocalTime.now(ZoneId.of("UTC"));
	}

	public final String getUTCFormatted()
	{
		if (serverTime == -1)
		{
			return "";
		}

		return serverUTCTime.format(DateTimeFormatter.ofPattern("HH:mm:ss"));
	}

	@Schedule(period = 500, unit = ChronoUnit.MILLIS)
	public void updateSchedule()
	{
		// Server UTC Time: 2208
		// Hour Calculation: 1328 / 60 = 22
		// Minuted Calculation: 1328 % 60 = 8
		if (client.getGameState() != GameState.LOGGED_IN || !isInTzhaarArea() || client.isInInstancedRegion())
		{
			serverUTCTime = null;
			serverUTCTimeSecondSet = false;
			serverTime = -1;
			oldServerTime = -1;
			serverTimeSecondOffset = -1;
			return;
		}

		int currentLocalSecond = LocalTime.now().getSecond();
		serverTime = client.getVarbitValue(8354);

		if (!serverUTCTimeSecondSet)
		{
			if (oldServerTime > 0 && serverTime != oldServerTime)
			{
				serverUTCTime = LocalTime.of(serverTime / 60, serverTime % 60, 0);
				serverUTCTimeSecondSet = true;
				serverTimeSecondOffset = LocalTime.now().getSecond();
			}
			else
			{
				serverUTCTime = LocalTime.of(serverTime / 60, serverTime % 60, currentLocalSecond);
			}

			oldServerTime = serverTime;
		}
		else if (serverUTCTimeSecondSet && serverTimeSecondOffset != -1)
		{
			oldServerTime = serverTime;

			if (serverTime != oldServerTime)
			{
				serverUTCTime = LocalTime.of(serverTime / 60, serverTime % 60, 0);
				serverTimeSecondOffset = LocalTime.now().getSecond();
			}
			else
			{
				serverUTCTime = LocalTime.of(serverTime / 60, serverTime % 60, OffsetDateTime.now().plusSeconds(-serverTimeSecondOffset).getSecond());
			}
		}

		if (!active && serverUTCTimeSecondSet)
		{
			rotationCol = serverTime % 16;

			int minute = serverUTCTime.getMinute();

			if ((rotationCol == 15 && (minute % 2) != 0) || (rotationCol == 0 && (minute % 2) == 0))
			{
				// Needed in-order to make Rotation 4 (Column 1 and 16) repeat itself
				rotationCol = 1;
			}
			else
			{
				// Because of the modulo above, everything is 1 value lower than it should be ... so +1 to every rotation
				rotationCol++;
			}
		}
	}

	@Override
	public void keyTyped(KeyEvent e)
	{
	}

	@Override
	public void keyPressed(KeyEvent e)
	{
		if (config.displayCurrentWave() != SpawnPredictorConfig.CurrentWaveDisplayMode.OFF && config.displayCurrentWaveKey().matches(e))
		{
			switch (config.displayCurrentWave())
			{
				case FLASH:
				{
					hotkeyEnabled = true;
					break;
				}

				case TOGGLE:
				{
					hotkeyEnabled = !hotkeyEnabled;
					break;
				}
			}
		}
	}

	@Override
	public void keyReleased(KeyEvent e)
	{
		if (config.displayCurrentWave() != SpawnPredictorConfig.CurrentWaveDisplayMode.OFF
				&& config.displayCurrentWave() == SpawnPredictorConfig.CurrentWaveDisplayMode.FLASH
				&& config.displayCurrentWaveKey().matches(e))
		{
			hotkeyEnabled = false;
		}
	}

	private void saveRotation(String name, int rotation, int wave)
	{
		AccountMemory memory = new AccountMemory(rotation, wave);
		setRotationConfig(name, memory);
	}

	AccountMemory getRotationConfig(String name)
	{
		String json = configManager.getConfiguration(SpawnPredictorConfig.GROUP, "spawnpredictor_" + name);

		if (json == null)
		{
			return null;
		}

		return gson.fromJson(json, AccountMemory.class);
	}

	void setRotationConfig(String name, AccountMemory memory)
	{
		String json = gson.toJson(memory);
		configManager.setConfiguration(SpawnPredictorConfig.GROUP, "spawnpredictor_" + name, json);
		log.info("spawnpredictor: set rotation config for {} with rotation: {} and wave: {}", name, memory.getRotation(), memory.getWave());
	}

	void removeRotationConfig(String name)
	{
		if (name == null || getRotationConfig(name) == null)
		{
			return;
		}

		configManager.unsetConfiguration(SpawnPredictorConfig.GROUP, "spawnpredictor_" + name);
		log.info("spawnpredictor: removed rotation config for {}", name);
	}
}