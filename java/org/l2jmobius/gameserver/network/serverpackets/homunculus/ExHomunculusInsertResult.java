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
package org.l2jmobius.gameserver.network.serverpackets.homunculus;

import org.l2jmobius.gameserver.network.ServerPackets;
import org.l2jmobius.gameserver.network.SystemMessageId;
import org.l2jmobius.gameserver.network.serverpackets.ServerPacket;

/**
 * @author Mobius
 */
public class ExHomunculusInsertResult extends ServerPacket
{
	private final int _type;
	
	public ExHomunculusInsertResult(int type)
	{
		_type = type;
	}
	
	@Override
	public void write()
	{
		ServerPackets.EX_HOMUNCULUS_INSERT_RESULT.writeId(this);
		writeInt(1); // 1 - success
		if (_type == 0)
		{
			writeInt(SystemMessageId.THE_HOMUNCULUS_TAKES_YOUR_BLOOD_HP.getId());
		}
		else if (_type == 1)
		{
			writeInt(SystemMessageId.THE_HOMUNCULUS_TAKES_YOUR_SPIRIT_SP.getId());
		}
		else
		{
			writeInt(SystemMessageId.THE_HOMUNCULUS_TAKES_YOUR_TEARS_VP.getId());
		}
	}
}
