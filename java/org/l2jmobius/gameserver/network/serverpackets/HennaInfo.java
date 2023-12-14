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
package org.l2jmobius.gameserver.network.serverpackets;

import java.util.ArrayList;
import java.util.List;

import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.item.Henna;
import org.l2jmobius.gameserver.model.stats.BaseStat;
import org.l2jmobius.gameserver.network.ServerPackets;

/**
 * This server packet sends the player's henna information.
 * @author Zoey76
 */
public class HennaInfo extends ServerPacket
{
	private final Player _player;
	private final List<Henna> _hennas = new ArrayList<>();
	
	public HennaInfo(Player player)
	{
		_player = player;
		for (int i = 1; i < 4; i++)
		{
			if (player.getHenna(i) != null)
			{
				_hennas.add(player.getHenna(i));
			}
		}
	}
	
	@Override
	public void write()
	{
		ServerPackets.HENNA_INFO.writeId(this);
		writeShort(_player.getHennaValue(BaseStat.INT)); // equip INT
		writeShort(_player.getHennaValue(BaseStat.STR)); // equip STR
		writeShort(_player.getHennaValue(BaseStat.CON)); // equip CON
		writeShort(_player.getHennaValue(BaseStat.MEN)); // equip MEN
		writeShort(_player.getHennaValue(BaseStat.DEX)); // equip DEX
		writeShort(_player.getHennaValue(BaseStat.WIT)); // equip WIT
		writeShort(_player.getHennaValue(BaseStat.LUC)); // equip LUC
		writeShort(_player.getHennaValue(BaseStat.CHA)); // equip CHA
		writeInt(3 - _player.getHennaEmptySlots()); // Slots
		writeInt(_hennas.size()); // Size
		for (Henna henna : _hennas)
		{
			writeInt(henna.getDyeId());
			writeInt(henna.isAllowedClass(_player.getClassId()));
		}
		final Henna premium = _player.getHenna(4);
		if (premium != null)
		{
			int duration = premium.getDuration();
			if (duration > 0)
			{
				final long currentTime = System.currentTimeMillis();
				duration = (int) Math.max(0, _player.getVariables().getLong("HennaDuration4", currentTime) - currentTime) / 1000;
			}
			writeInt(premium.getDyeId());
			writeInt(duration); // Premium Slot Dye Time Left
			writeInt(premium.isAllowedClass(_player.getClassId()));
		}
		else
		{
			writeInt(0); // Premium Slot Dye ID
			writeInt(0); // Premium Slot Dye Time Left
			writeInt(0); // Premium Slot Dye ID isValid
		}
	}
}
