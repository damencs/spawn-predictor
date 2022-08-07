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

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.runelite.client.config.*;

import java.awt.*;

@ConfigGroup("spawnpredictor")
public interface SpawnPredictorConfig extends Config
{
	@ConfigItem(
			name = "Debug",
			keyName = "debugOverlay",
			description = "",
			position = 0,
			hidden = true
	)
	default boolean debug()
	{
		return false;
	}

	@ConfigItem(
			name = "Lobby Rotation Info",
			keyName = "lobbyRotationInfoOverlay",
			description = "- Displays information about the current/upcoming rotation in the lobby area",
			position = 1
	)
	default boolean lobbyRotationInfoOverlay()
	{
		return false;
	}

	@ConfigItem(
			name = "Include UTC Time",
			keyName = "includeUTCTime",
			description = "- Display the UTC Time within the overlay",
			position = 2
	)
	default boolean includeUTCTime()
	{
		return false;
	}

	@ConfigItem(
			name = "Wave Display Mode",
			keyName = "displayMode",
			description = "- Choose the display mode of the overlays",
			position = 3
	)
	default DisplayMode displayMode()
	{
		return DisplayMode.OFF;
	}

	@ConfigItem(
			name = "Display Current Wave with Key",
			keyName = "displayCurrentWaveToggle",
			description = "- Only works with the 'Next Wave' display mode",
			position = 4
	)
	default CurrentWaveDisplayMode displayCurrentWave()
	{
		return CurrentWaveDisplayMode.OFF;
	}

	@ConfigItem(
			name = "Display Key",
			keyName = "displayCurrentWaveKey",
			description = "- Set a key to display the current wave with when held/pressed to toggle",
			position = 5
	)
	default Keybind displayCurrentWaveKey()
	{
		return Keybind.NOT_SET;
	}

	@ConfigItem(
			name = "Overlay Stroke Size",
			keyName = "overlayStrokeSize",
			description = "Sets the stroke size of the tile overlay",
			position = 6
	)
	@Range(max = 3, min = 1)
	@Units(Units.PIXELS)
	default int overlayStrokeSize()
	{
		return 2;
	}

	@ConfigItem(
			name = "Multicolor Names",
			keyName = "multicolorNames",
			description = "- Color the overlay names inside the Fight Caves to it's respective color.<br>" +
						  "Example: Current Wave Color = White -> Name = White<br>" +
					      "Next Wave Color = Green -> Name = Green",
			position = 7
	)
	default boolean multicolorNames()
	{
		return false;
	}

	@ConfigItem(
			name = "Current Wave Color",
			keyName = "currentWaveColor",
			description = "- Sets the current wave tile overlay colors",
			position = 8
	)
	@Alpha
	default Color currentWaveColor()
	{
		return Color.WHITE;
	}

	@ConfigItem(
			name = "Next Wave Color",
			keyName = "nextWaveColor",
			description = "- Sets the next wave tile overlay colors",
			position = 9
	)
	@Alpha
	default Color nextWaveColor()
	{
		return Color.GREEN;
	}

	@RequiredArgsConstructor
	@Getter
	enum DisplayMode
	{
		OFF("Off"),
		CURRENT_WAVE("Current"),
		NEXT_WAVE("Next"),
		BOTH("Both");

		private final String name;

		@Override
		public String toString()
		{
			return name;
		}
	}

	@RequiredArgsConstructor
	@Getter
	enum CurrentWaveDisplayMode
	{
		OFF("Off"),
		FLASH("Flash"),
		TOGGLE("Toggle");

		private final String name;

		@Override
		public String toString()
		{
			return name;
		}
	}
}
