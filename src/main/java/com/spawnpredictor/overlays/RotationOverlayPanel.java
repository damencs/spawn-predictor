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
import com.spawnpredictor.util.StartLocations;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;
import net.runelite.client.ui.overlay.components.LineComponent;

import javax.inject.Inject;
import java.awt.*;

public class RotationOverlayPanel extends OverlayPanel
{
	private final SpawnPredictorPlugin plugin;
	private final SpawnPredictorConfig config;

	@Inject
	private RotationOverlayPanel(SpawnPredictorPlugin plugin, SpawnPredictorConfig config)
	{
		this.plugin = plugin;
		this.config = config;

		setPriority(OverlayPriority.HIGH);
		setPosition(OverlayPosition.ABOVE_CHATBOX_RIGHT);
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		if (!plugin.isInTzhaarArea())
		{
			return null;
		}

		int rotation = plugin.getRotationCol();

		boolean activeSafetyNet = plugin.isActiveSafetyNet();

		if (plugin.isServerUTCTimeSecondSet() && rotation != -1)
		{
			panelComponent.setPreferredSize(new Dimension(135, 0));

			int serverTimeSeconds = plugin.getServerUTCTime().getSecond();

			panelComponent.getChildren().add(LineComponent.builder()
					.left("Current Rotation:")
					.leftColor(Color.WHITE)
					.right(StartLocations.translateRotation(rotation) + ((activeSafetyNet) ? "*" : ""))
					.rightColor(activeSafetyNet ? Color.ORANGE : Color.GREEN)
					.build());

			panelComponent.getChildren().add(LineComponent.builder()
					.left("Next:")
					.leftColor(Color.WHITE)
					.right((activeSafetyNet ? "" : "T - " + (60 - serverTimeSeconds) + "s, ")
							+ "Rot: " + ((rotation + 1) > 15 ? "4" : Integer.toString(StartLocations.translateRotation(rotation + 1))))
					.rightColor(Color.YELLOW)
					.build());

			if (config.displayDesiredRotation())
			{
				int currentRotationCol = plugin.getRotationCol();
				int desiredRotation = StartLocations.translateRotation(plugin.getDesiredRotation(), true);

				panelComponent.getChildren().add(LineComponent.builder()
						.left("Desired:")
						.leftColor(Color.WHITE)
						.right(config.desiredRotation() + " - ETA: "
								+ (plugin.getRotationCol() <= desiredRotation ? desiredRotation - currentRotationCol : ((15 - currentRotationCol) + desiredRotation))
								+ "min")
						.rightColor(Color.PINK)
						.build());
			}
		}
		else
		{
			panelComponent.setPreferredSize(new Dimension(85, 0));

			panelComponent.getChildren().add(LineComponent.builder()
					.right("Determining...")
					.rightColor(Color.YELLOW)
					.build());
		}

		return super.render(graphics);
	}
}
