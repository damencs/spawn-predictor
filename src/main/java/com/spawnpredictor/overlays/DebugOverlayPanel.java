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

import com.google.inject.Inject;
import com.spawnpredictor.SpawnPredictorConfig;
import com.spawnpredictor.SpawnPredictorPlugin;
import com.spawnpredictor.util.StartLocations;
import net.runelite.api.Client;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;
import net.runelite.client.ui.overlay.components.LineComponent;

import java.awt.*;

public class DebugOverlayPanel extends OverlayPanel
{
	private final SpawnPredictorPlugin plugin;
	private final SpawnPredictorConfig config;

	@Inject
	private DebugOverlayPanel(SpawnPredictorPlugin plugin, SpawnPredictorConfig config)
	{
		this.plugin = plugin;
		this.config = config;

		setPriority(OverlayPriority.HIGH);
		setPosition(OverlayPosition.ABOVE_CHATBOX_RIGHT);
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		if (!config.debug())
		{
			return null;
		}

		panelComponent.setPreferredSize(new Dimension(75, 0));

		if (!plugin.isFightCavesActive() && plugin.isInTzhaarArea())
		{
			int rotationCol = plugin.getRotationCol();

			panelComponent.getChildren().add(LineComponent.builder()
					.left("RCol:")
					.leftColor(Color.WHITE)
					.right(Integer.toString(rotationCol))
					.rightColor(Color.GREEN)
					.build());

			panelComponent.getChildren().add(LineComponent.builder()
					.left("RTrans:")
					.leftColor(Color.WHITE)
					.right(Integer.toString(StartLocations.translateRotation(rotationCol)))
					.rightColor(Color.GREEN)
					.build());
		}

		if (plugin.isFightCavesActive() && !plugin.isInTzhaarArea())
		{
			int rotation = plugin.getCurrentRotation();

			panelComponent.getChildren().add(LineComponent.builder()
					.left("Wave:")
					.leftColor(Color.WHITE)
					.right(Integer.toString(SpawnPredictorPlugin.getCurrentWave()))
					.rightColor(Color.GREEN)
					.build());

			panelComponent.getChildren().add(LineComponent.builder()
					.left("Rotation:")
					.leftColor(Color.WHITE)
					.right(Integer.toString(rotation))
					.rightColor(Color.GREEN)
					.build());

			panelComponent.getChildren().add(LineComponent.builder()
					.left("rsV:")
					.leftColor(Color.WHITE)
					.right(Integer.toString(SpawnPredictorPlugin.getRsVal()))
					.rightColor(Color.ORANGE)
					.build());

			panelComponent.getChildren().add(LineComponent.builder()
					.left("rsF:")
					.leftColor(Color.WHITE)
					.right(Integer.toString(StartLocations.getLookupMap().get(rotation)))
					.rightColor(Color.ORANGE)
					.build());
		}

		return super.render(graphics);
	}
}
