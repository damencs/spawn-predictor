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
package com.spawnpredictor.overlays;

import com.spawnpredictor.SpawnPredictorConfig;
import com.spawnpredictor.SpawnPredictorPlugin;
import com.spawnpredictor.util.FightCavesNpc;
import com.spawnpredictor.util.FightCavesNpcSpawn;
import com.spawnpredictor.util.SpawnLocations;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.Perspective;
import net.runelite.api.Point;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.ui.overlay.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import java.awt.*;
import java.util.List;

@Slf4j
public class DisplayModeOverlay extends Overlay
{
	private final Client client;
	private final SpawnPredictorPlugin plugin;
	private final SpawnPredictorConfig config;

	@Inject
	private DisplayModeOverlay(Client client, SpawnPredictorPlugin plugin, SpawnPredictorConfig config)
	{
		this.client = client;
		this.plugin = plugin;
		this.config = config;

		setPriority(OverlayPriority.HIGH);
		setPosition(OverlayPosition.DYNAMIC);
		setLayer(OverlayLayer.ABOVE_SCENE);
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		if (!plugin.isFightCavesActive() || config.displayMode() == SpawnPredictorConfig.DisplayMode.OFF || SpawnPredictorPlugin.getCurrentWave() <= 0)
		{
			return null;
		}

		final int wave = SpawnPredictorPlugin.getCurrentWave() - 1;
		List<FightCavesNpcSpawn> currentWaveContents = SpawnPredictorPlugin.getWaveData().get(wave);

		switch(config.displayMode())
		{
			case CURRENT_WAVE:
			{
				renderWaveContents(graphics, currentWaveContents, config.currentWaveColor());
				break;
			}

			case NEXT_WAVE:
			{
				if (wave == 0 || (config.displayCurrentWave() != SpawnPredictorConfig.CurrentWaveDisplayMode.OFF && plugin.isHotkeyEnabled()))
				{
					renderWaveContents(graphics, currentWaveContents, config.currentWaveColor());
				}

				if (wave != 63)
				{
					renderWaveContents(graphics, SpawnPredictorPlugin.getWaveData().get(wave + 1), config.nextWaveColor());
				}
				break;
			}

			case BOTH:
			{
				renderWaveContents(graphics, currentWaveContents, config.currentWaveColor());

				if (wave != 63)
				{
					renderWaveContents(graphics, SpawnPredictorPlugin.getWaveData().get(wave + 1), config.nextWaveColor());
				}
				break;
			}

			default:
			{
				throw new IllegalStateException("Illegal 'Display Mode' config state... How did this happen? Who knows");
			}
		}

		return null;
	}

	private void renderWaveContents(Graphics2D graphics, List<FightCavesNpcSpawn> waveContents, Color color)
	{
		waveContents.forEach(fcNpc ->
		{
			FightCavesNpc npc = fcNpc.getNpc();
			String name = npc.getName();
			int size = npc.getSize();

			SpawnLocations spawnLoc = SpawnLocations.lookup(fcNpc.getSpawnLocation());

			if (spawnLoc == null)
			{
				return;
			}

			LocalPoint localPoint = getCenterLocalPoint(spawnLoc.getRegionX(), spawnLoc.getRegionY(), size);

			if (localPoint == null)
			{
				return;
			}

			Polygon poly = Perspective.getCanvasTileAreaPoly(client, localPoint, size);
			renderPolygon(graphics, poly, color);

			Point textLocation = Perspective.getCanvasTextLocation(client, graphics, localPoint, name, 0);

			if (textLocation != null)
			{
				OverlayUtil.renderTextLocation(graphics, textLocation, name, config.multicolorNames() ? color : Color.WHITE);
			}
		});
	}

	private LocalPoint getCenterLocalPoint(int regionX, int regionY, int size)
	{
		// Region Coords are treated as the 'SW' tile
		LocalPoint lp = LocalPoint.fromWorld(client,
				WorldPoint.fromRegion(
						client.getLocalPlayer().getWorldLocation().getRegionID(),
						regionX, regionY, client.getPlane())
		);

		if (lp == null)
		{
			return null;
		}

		// Builds outwards from the SW tile and adjusts for NPC size and returns the center-most local point
		return new LocalPoint(lp.getX() + ((size - 1) * Perspective.LOCAL_HALF_TILE_SIZE), lp.getY() + ((size - 1) * Perspective.LOCAL_HALF_TILE_SIZE));
	}

	private void renderPolygon(Graphics2D graphics, @Nullable Shape poly, @Nonnull Color color)
	{
		if (poly == null)
		{
			return;
		}

		graphics.setColor(color);
		graphics.setStroke(new BasicStroke(config.overlayStrokeSize()));
		graphics.draw(poly);
		graphics.setColor(new Color(0, 0, 0, 50));
		graphics.fill(poly);
	}
}
