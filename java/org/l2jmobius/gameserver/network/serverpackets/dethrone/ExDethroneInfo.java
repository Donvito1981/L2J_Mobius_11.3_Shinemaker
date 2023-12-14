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
package org.l2jmobius.gameserver.network.serverpackets.dethrone;

import java.util.Map;
import java.util.Map.Entry;

import org.l2jmobius.Config;
import org.l2jmobius.gameserver.instancemanager.RankManager;
import org.l2jmobius.gameserver.model.StatSet;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.network.ServerPackets;
import org.l2jmobius.gameserver.network.serverpackets.ServerPacket;

/**
 * @author CostyKiller
 */
public class ExDethroneInfo extends ServerPacket
{
	private final Player _player;
	private final String _playerName;
	private final int _attackPoint;
	private final int _life;
	private final long _personalDethronePoint;
	private final long _serverDethronePoint;
	private final long _topServerDethronePoint;
	private final Map<Integer, StatSet> _previousConquestPlayerList;
	
	public ExDethroneInfo(Player player, String playerName, int attackPoint, int life, long personalDethronePoint, long serverDethronePoint)
	{
		_player = player;
		_playerName = playerName;
		_attackPoint = attackPoint;
		_life = life;
		_personalDethronePoint = personalDethronePoint;
		_serverDethronePoint = serverDethronePoint;
		_topServerDethronePoint = serverDethronePoint;
		_previousConquestPlayerList = RankManager.getInstance().getPreviousConquestRankList();
	}
	
	@Override
	public void write()
	{
		ServerPackets.EX_DETHRONE_INFO.writeId(this);
		
		writeSizedString(_playerName);
		
		writeInt(_attackPoint); // nAttackPoint 100
		writeInt(_life); // nLife 20
		
		writeInt(RankManager.getInstance().getPlayerConquestGlobalRank(_player)); // nRank
		writeInt(RankManager.getInstance().getCurrentConquestRankList().size()); // rank percents
		writeLong(_personalDethronePoint); // nPersonalDethronePoint
		
		int rank = 0;
		long prevPersonalPoints = 0;
		if (!_previousConquestPlayerList.isEmpty())
		{
			for (Entry<Integer, StatSet> entry : _previousConquestPlayerList.entrySet())
			{
				final StatSet info = entry.getValue();
				if (info.getInt("charId") == _player.getObjectId())
				{
					rank = entry.getKey();
					prevPersonalPoints = info.getLong("conquestPersonalPoints");
				}
			}
		}
		
		writeInt(rank); // nPrevRank
		writeInt(_previousConquestPlayerList.size()); // rank percents
		writeLong(prevPersonalPoints); // nPrevDethronePoint
		
		writeInt(1); // nServerRank
		writeLong(_serverDethronePoint); // nServerDethronePoint
		
		// Terr. Owner
		writeInt(Config.SERVER_ID); // nConquerorWorldID (Server Id of the conqueror player)
		// writeSizedString(RankManager.getInstance().getCurrentConquestRankList().get(1).getString("name")); // sConquerorName // real char name
		writeSizedString(RankManager.getInstance().getPlayerConquestGlobalRankName(1)); // sTopRankerName; // conquest char name
		
		// Conqueror Server
		writeInt(Config.SERVER_ID); // nOccupyingServerWorldID
		
		// Conquest Status
		// set from SeasonInfo Packet
		
		// Rank 1
		writeInt(Config.SERVER_ID); // nTopRankerWorldID
		// writeSizedString(RankManager.getInstance().getCurrentConquestRankList().get(1).getString("name")); // sTopRankerName; // real char name
		writeSizedString(RankManager.getInstance().getPlayerConquestGlobalRankName(1)); // sTopRankerName; // conquest char name
		
		writeInt(Config.SERVER_ID); // Leading Server nTopServerWorldID
		writeLong(_topServerDethronePoint); // Server Points nTopServerDethronePoint
	}
}
