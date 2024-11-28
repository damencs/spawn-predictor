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
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Point;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;
import net.runelite.client.ui.overlay.OverlayUtil;

import javax.inject.Inject;
import java.awt.*;

@Slf4j
public class SpawnPredictorOverlay extends Overlay
{
	private final SpawnPredictorPlugin plugin;
	private final SpawnPredictorConfig config;

	@Inject
	private SpawnPredictorOverlay(SpawnPredictorPlugin plugin, SpawnPredictorConfig config)
	{
		this.plugin = plugin;
		this.config = config;

		setPriority(OverlayPriority.HIGH);
		setPosition(OverlayPosition.DYNAMIC);
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		if (!plugin.isInTzhaarArea() || plugin.getEntrance() == null)
		{
			return null;
		}

		if (config.displayEntranceLabel())
		{
			String text;
			Color textColor;

			if (!plugin.isServerUTCTimeSecondSet() || plugin.isActiveSafetyNet())
			{
				text = "Please wait for the plugin to " + (plugin.isActiveSafetyNet() ? "confirm" : "determine") + " the rotation.";
				textColor = Color.YELLOW;
			}
			else
			{
				text = "You may proceed into the Fight Caves.";
				textColor = Color.GREEN;
			}

			Point textLocation = plugin.getEntrance().getCanvasTextLocation(graphics, text, 0);

			if (textLocation != null)
			{
				OverlayUtil.renderTextLocation(graphics, new Point(textLocation.getX(), textLocation.getY()), text, textColor);
			}
		}

		return null;
	}
}
