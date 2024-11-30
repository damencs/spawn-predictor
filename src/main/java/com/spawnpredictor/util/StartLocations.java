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
package com.spawnpredictor.util;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

@Getter
@Slf4j
public enum StartLocations
{
	ROTATION_1(1, 2),
	ROTATION_2(2, 13),
	ROTATION_3(3, 8),
	ROTATION_4(4, 12),
	ROTATION_5(5, 7),
	ROTATION_6(6, 3),
	ROTATION_7(7, 6),
	ROTATION_8(8, 6),
	ROTATION_9(9, 14),
	ROTATION_10(10, 5),
	ROTATION_11(11, 0),
	ROTATION_12(12, 9),
	ROTATION_13(13, 1),
	ROTATION_14(14, 10),
	ROTATION_15(15, 4);

	// r = Rotation Column Number (See -> translateRotation)
	private final int rotation;

	// rsVal = Rotation Start Value (Where the first Bat will spawn) based off the spawn circle -> https://imgur.com/a/EOfNGEa
	private final int rotationStartValue;

	@Getter
	private static final HashMap<Integer, Integer> lookupMap;

	@Getter
	private static final Map<Integer, Integer> rotationResolver = Map.ofEntries(
			// Rotation Column Number, Rotation Number
			Map.entry(1, 4),
			Map.entry(2, 2),
			Map.entry(3, 9),
			Map.entry(4, 11),
			Map.entry(5, 13),
			Map.entry(6, 1),
			Map.entry(7, 6),
			Map.entry(8, 15),
			Map.entry(9, 10),
			Map.entry(10, 8),
			Map.entry(11, 5),
			Map.entry(12, 3),
			Map.entry(13, 12),
			Map.entry(14, 14),
			Map.entry(15, 7)
	);

	static
	{
		lookupMap = new HashMap<>();

		EnumSet.allOf(StartLocations.class).forEach(n -> lookupMap.put(n.getRotation(), n.getRotationStartValue()));
	}

	StartLocations(int rotation, int rotationStartValue)
	{
		this.rotation = rotation;
		this.rotationStartValue = rotationStartValue;
	}

	/**
	 * @param r Column Number based off the 'Rotation Times Table' on the Wiki
	 * @return Inputted 'Rotation Column Number' as a proper rotation value
	 */
	public static int translateRotation(int r)
	{
		return translateRotation(r, false);
	}

	public static int translateRotation(int r, boolean reverseLookup)
	{
		if (r == -1)
		{
			log.debug("Rotation not valid to translate, r: {}", r);
			return -1;
		}

		if (!reverseLookup)
		{
			return rotationResolver.get(r);
		}
		else
		{
			return rotationResolver.entrySet().stream().filter(entry -> entry.getValue().equals(r)).map(Map.Entry::getKey).findFirst().orElse(-1);
		}
	}
}
