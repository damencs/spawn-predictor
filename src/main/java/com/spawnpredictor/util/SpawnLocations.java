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

@Getter
@Slf4j
public enum SpawnLocations
{
	NW(10, 50),
	C(30, 30),
	SE(50, 25),
	S(35, 15),
	SW(10, 15);

	private final int regionX;
	private final int regionY;

	SpawnLocations(final int regionX, final int regionY)
	{
		this.regionX = regionX;
		this.regionY = regionY;
	}

	/**
	 * @param sVal Spawn Value
	 * @see StartLocations
	 * @return a Cardinal Direction holding regionX and regionY
	 */
	public static SpawnLocations lookup(int sVal)
	{
		switch (sVal)
		{
			case 3:
			case 7:
			case 12:
			{
				return SpawnLocations.NW;
			}

			case 2:
			case 8:
			case 13:
			{
				return SpawnLocations.C;
			}

			case 0:
			case 5:
			case 9:
			{
				return SpawnLocations.SE;
			}

			case 6:
			case 11:
			case 14:
			{
				return SpawnLocations.S;
			}

			case 1:
			case 4:
			case 10:
			{
				return SpawnLocations.SW;
			}
		}

		log.debug("Invalid sVal -> {}", sVal);
		return null;
	}
}
