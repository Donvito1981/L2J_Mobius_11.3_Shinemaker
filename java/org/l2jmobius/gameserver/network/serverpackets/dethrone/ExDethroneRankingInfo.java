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
public class ExDethroneRankingInfo extends ServerPacket
{
	private final Player _player;
	private final int _bCurrentSeason;
	private final int _cRankingScope;
	private final Map<Integer, StatSet> _currentConquestPlayerList;
	private final Map<Integer, StatSet> _previousConquestPlayerList;
	
	public ExDethroneRankingInfo(Player player, int bCurrentSeason, int cRankingScope)
	{
		_bCurrentSeason = bCurrentSeason;
		_cRankingScope = cRankingScope;
		_player = player;
		_currentConquestPlayerList = RankManager.getInstance().getCurrentConquestRankList();
		_previousConquestPlayerList = RankManager.getInstance().getPreviousConquestRankList();
	}
	
	@Override
	public void write()
	{
		ServerPackets.EX_DETHRONE_RANKING_INFO.writeId(this);
		writeByte(_bCurrentSeason);
		writeByte(_cRankingScope);
		
		if (_bCurrentSeason == 1) // Current Cycle
		{
			if (_cRankingScope == 0) // Top-100
			{
				if (!_currentConquestPlayerList.isEmpty())
				{
					writeInt(_currentConquestPlayerList.size() > 100 ? 100 : _currentConquestPlayerList.size()); // ranking percents seems is calculated inside client (must be same number as ranking list size)
					writeInt(_currentConquestPlayerList.size() > 100 ? 100 : _currentConquestPlayerList.size()); // ranking list size
					for (Entry<Integer, StatSet> entry : _currentConquestPlayerList.entrySet())
					{
						writeInt(entry.getKey()); // server rank
						writeInt(Config.SERVER_ID);
						writeSizedString(RankManager.getInstance().getPlayerConquestGlobalRankName(entry.getKey())); // conquest char name
						// writeSizedString(player.getString("name")); // real char name
						writeLong(entry.getValue().getLong("conquestPersonalPoints"));
					}
				}
				else
				{
					writeInt(0);
					writeInt(0);
				}
			}
			else // Personal Ranking
			{
				if (!_currentConquestPlayerList.isEmpty())
				{
					boolean found = false;
					for (Entry<Integer, StatSet> entry : _currentConquestPlayerList.entrySet())
					{
						if (entry.getValue().getInt("charId") == _player.getObjectId())
						{
							found = true;
							final int id = entry.getKey();
							final int first = id > 10 ? (id - 9) : 1;
							final int last = _currentConquestPlayerList.size() >= (id + 10) ? id + 10 : id + (_currentConquestPlayerList.size() - id);
							if (first == 1)
							{
								writeInt(_currentConquestPlayerList.size()); // rank percents
								writeInt(last - (first - 1));
							}
							else
							{
								writeInt(_currentConquestPlayerList.size()); // rank percents
								writeInt(last - first);
							}
							for (int id2 = first; id2 <= last; id2++)
							{
								final StatSet plr = _currentConquestPlayerList.get(id2);
								writeInt(id2); // server rank
								writeInt(Config.SERVER_ID);
								writeSizedString(RankManager.getInstance().getPlayerConquestGlobalRankName(id2)); // conquest char name
								// writeSizedString(plr.getString("name")); // real char name
								writeLong(plr.getLong("conquestPersonalPoints"));
							}
						}
					}
					if (!found)
					{
						writeInt(0);
						writeInt(0);
					}
				}
				else
				{
					writeInt(0);
					writeInt(0);
				}
			}
		}
		else // Previous Cycle
		{
			if (_cRankingScope == 0) // Top-100
			{
				if (!_previousConquestPlayerList.isEmpty())
				{
					writeInt(_previousConquestPlayerList.size()); // ranking percents seems is calculated inside client (must be same number as ranking list size)
					writeInt(_previousConquestPlayerList.size()); // ranking list size
					for (Entry<Integer, StatSet> entry : _previousConquestPlayerList.entrySet())
					{
						final StatSet info = entry.getValue();
						writeInt(entry.getKey()); // server rank
						writeInt(Config.SERVER_ID);
						writeSizedString(info.getString("conquest_name")); // conquest char name
						writeLong(info.getLong("conquestPersonalPoints"));
					}
					
					// writeInt(1); // Rank percent
					// writeInt(Config.SERVER_ID); // WorldId
					// writeSizedString("Player1"); // name
					// writeLong(30000);
					//
					// writeInt(2); // Rank percent
					// writeInt(Config.SERVER_ID); // WorldId
					// writeSizedString("Player2"); // name
					// writeLong(15000);
				}
				else
				{
					writeInt(0);
					writeInt(0);
				}
			}
			else // Personal Ranking
			{
				if (!_previousConquestPlayerList.isEmpty())
				{
					boolean found = false;
					for (Entry<Integer, StatSet> entry : _previousConquestPlayerList.entrySet())
					{
						if (entry.getValue().getInt("charId") == _player.getObjectId())
						{
							found = true;
							final int id = entry.getKey();
							final int first = id > 10 ? (id - 9) : 1;
							final int last = _previousConquestPlayerList.size() >= (id + 10) ? id + 10 : id + (_previousConquestPlayerList.size() - id);
							
							if (first == 1)
							{
								writeInt(_previousConquestPlayerList.size()); // rank percents
								writeInt(last - (first - 1));
							}
							else
							{
								writeInt(_previousConquestPlayerList.size()); // rank percents
								writeInt(last - first);
							}
							for (int id2 = first; id2 <= last; id2++)
							{
								final StatSet plr = _previousConquestPlayerList.get(id2);
								writeInt(id2); // server rank
								writeInt(Config.SERVER_ID);
								writeSizedString(plr.getString("conquest_name")); // conquest char name
								writeLong(plr.getLong("conquestPersonalPoints"));
							}
						}
					}
					if (!found)
					{
						writeInt(0);
						writeInt(0);
					}
					
					// writeInt(0); // ???
					// writeInt(2); // ranking list size
					//
					// writeInt(1); // Rank Slot
					// writeInt(Config.SERVER_ID); // WorldId
					// writeSizedString("Player1"); // name
					// writeLong(6000);
					//
					// writeInt(2); // Rank Slot
					// writeInt(Config.SERVER_ID); // WorldId
					// writeSizedString("Player2"); // name
					// writeLong(5000);
				}
				else
				{
					writeInt(0);
					writeInt(0);
				}
			}
		}
	}
}
