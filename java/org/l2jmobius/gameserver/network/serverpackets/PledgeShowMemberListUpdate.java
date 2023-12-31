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

import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.clan.ClanMember;
import org.l2jmobius.gameserver.network.ServerPackets;

/**
 * @author -Wooden-
 */
public class PledgeShowMemberListUpdate extends ServerPacket
{
	private final int _pledgeType;
	private final String _name;
	private final int _level;
	private final int _classId;
	private final int _objectId;
	
	public PledgeShowMemberListUpdate(Player player)
	{
		this(player.getClan().getClanMember(player.getObjectId()));
	}
	
	public PledgeShowMemberListUpdate(ClanMember member)
	{
		_name = member.getName();
		_level = member.getLevel();
		_classId = member.getClassId();
		_objectId = member.isOnline() ? member.getObjectId() : 0;
		_pledgeType = member.getPledgeType();
	}
	
	@Override
	public void write()
	{
		ServerPackets.PLEDGE_SHOW_MEMBER_LIST_UPDATE.writeId(this);
		writeString(_name);
		writeInt(_level);
		writeInt(_classId);
		writeInt(0); // _sex
		writeInt(0); // _race
		writeInt(_objectId);
		writeInt(_pledgeType);
		writeInt(0); // _hasSponsor
		writeByte(0);
	}
}
