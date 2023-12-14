/*
 * This file is part of the L2J Mobius project.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.l2jmobius.gameserver.network.serverpackets.dethroneability;

import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.variables.PlayerVariables;
import org.l2jmobius.gameserver.network.ServerPackets;
import org.l2jmobius.gameserver.network.serverpackets.ServerPacket;

/**
 * @author Liamxroy, CostyKiller
 */
public class ExAbilityFireOpenUI extends ServerPacket
{
	private final Player _player;
	
	public ExAbilityFireOpenUI(Player player)
	{
		_player = player;
	}
	
	@Override
	public void write()
	{
		ServerPackets.EX_ENHANCED_ABILITY_OF_FIRE_OPEN_UI.writeId(this);
		
		writeInt(checkAbilitySetLevels()); // int SetEffectLevel (IntProperty) Full Ability Level 0 to 10
		
		// Fire Source
		writeInt(_player.getVariables().getInt(PlayerVariables.CONQUEST_ABILITY_FIRE_SOURCE_LEVEL, 0)); // Level
		writeInt(_player.getVariables().getInt(PlayerVariables.CONQUEST_ABILITY_FIRE_SOURCE_EXP, 0)); // Exp % x 1000 (100% = 1032032)
		writeInt(0); // allow to reset counter
		writeInt(0); // upgrades number
		
		// Life Source
		writeInt(_player.getVariables().getInt(PlayerVariables.CONQUEST_ABILITY_LIFE_SOURCE_LEVEL, 0)); // Level
		writeInt(_player.getVariables().getInt(PlayerVariables.CONQUEST_ABILITY_LIFE_SOURCE_EXP, 0)); // Exp % x 1000 (100% = 109890)
		writeInt(_player.getVariables().getInt(PlayerVariables.CONQUEST_ABILITY_LIFE_SOURCE_RESET, 1)); // reset counter
		writeInt(_player.getVariables().getInt(PlayerVariables.CONQUEST_ABILITY_LIFE_SOURCE_UPGRADES, 500)); // upgrades number (max = 500)
		
		// Flame Spark
		writeInt(_player.getVariables().getInt(PlayerVariables.CONQUEST_ABILITY_FLAME_SPARK_LEVEL, 0)); // Level
		writeInt(_player.getVariables().getInt(PlayerVariables.CONQUEST_ABILITY_FLAME_SPARK_EXP, 0)); // Exp % x 1000 (100% = 15067)
		writeInt(_player.getVariables().getInt(PlayerVariables.CONQUEST_ABILITY_FLAME_SPARK_RESET, 1)); // reset counter
		writeInt(_player.getVariables().getInt(PlayerVariables.CONQUEST_ABILITY_FLAME_SPARK_UPGRADES, 60)); // upgrades number (max = 60)
		
		// Fire Totem
		writeInt(_player.getVariables().getInt(PlayerVariables.CONQUEST_ABILITY_FIRE_TOTEM_LEVEL, 0)); // Level
		writeInt(_player.getVariables().getInt(PlayerVariables.CONQUEST_ABILITY_FIRE_TOTEM_EXP, 0)); // Exp % x 1000 (100% = 21973)
		writeInt(_player.getVariables().getInt(PlayerVariables.CONQUEST_ABILITY_FIRE_TOTEM_RESET, 1)); // reset counter
		writeInt(_player.getVariables().getInt(PlayerVariables.CONQUEST_ABILITY_FIRE_TOTEM_UPGRADES, 100)); // upgrades number (max = 100)
		
		// Battle Soul
		writeInt(_player.getVariables().getInt(PlayerVariables.CONQUEST_ABILITY_BATTLE_SOUL_LEVEL, 0)); // Level
		writeInt(_player.getVariables().getInt(PlayerVariables.CONQUEST_ABILITY_BATTLE_SOUL_EXP, 0)); // Exp % x 1000 (100% = 21973)
		writeInt(_player.getVariables().getInt(PlayerVariables.CONQUEST_ABILITY_BATTLE_SOUL_RESET, 1)); // reset counter
		writeInt(_player.getVariables().getInt(PlayerVariables.CONQUEST_ABILITY_BATTLE_SOUL_UPGRADES, 100)); // upgrades number (max = 100)
	}
	
	public int checkAbilitySetLevels()
	{
		int setLevel = 0;
		if ((_player.getVariables().getInt(PlayerVariables.CONQUEST_ABILITY_FIRE_SOURCE_LEVEL, 0) == 10) && //
			(_player.getVariables().getInt(PlayerVariables.CONQUEST_ABILITY_LIFE_SOURCE_LEVEL, 0) == 10) && //
			(_player.getVariables().getInt(PlayerVariables.CONQUEST_ABILITY_FLAME_SPARK_LEVEL, 0) == 10) && //
			(_player.getVariables().getInt(PlayerVariables.CONQUEST_ABILITY_FIRE_TOTEM_LEVEL, 0) == 10) && //
			(_player.getVariables().getInt(PlayerVariables.CONQUEST_ABILITY_BATTLE_SOUL_LEVEL, 0) == 10))
		{
			setLevel = 10;
		}
		else if ((_player.getVariables().getInt(PlayerVariables.CONQUEST_ABILITY_FIRE_SOURCE_LEVEL, 0) >= 6) && //
			(_player.getVariables().getInt(PlayerVariables.CONQUEST_ABILITY_LIFE_SOURCE_LEVEL, 0) >= 6) && //
			(_player.getVariables().getInt(PlayerVariables.CONQUEST_ABILITY_FLAME_SPARK_LEVEL, 0) >= 6) && //
			(_player.getVariables().getInt(PlayerVariables.CONQUEST_ABILITY_FIRE_TOTEM_LEVEL, 0) >= 6) && //
			(_player.getVariables().getInt(PlayerVariables.CONQUEST_ABILITY_BATTLE_SOUL_LEVEL, 0) >= 6))
		{
			setLevel = 6;
		}
		else if ((_player.getVariables().getInt(PlayerVariables.CONQUEST_ABILITY_FIRE_SOURCE_LEVEL, 0) >= 3) && //
			(_player.getVariables().getInt(PlayerVariables.CONQUEST_ABILITY_LIFE_SOURCE_LEVEL, 0) >= 3) && //
			(_player.getVariables().getInt(PlayerVariables.CONQUEST_ABILITY_FLAME_SPARK_LEVEL, 0) >= 3) && //
			(_player.getVariables().getInt(PlayerVariables.CONQUEST_ABILITY_FIRE_TOTEM_LEVEL, 0) >= 3) && //
			(_player.getVariables().getInt(PlayerVariables.CONQUEST_ABILITY_BATTLE_SOUL_LEVEL, 0) >= 3))
		{
			setLevel = 3;
		}
		return setLevel;
	}
}
