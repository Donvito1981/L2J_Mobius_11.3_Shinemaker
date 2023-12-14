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
import org.l2jmobius.gameserver.model.holders.ItemHolder;
import org.l2jmobius.gameserver.network.ServerPackets;
import org.l2jmobius.gameserver.network.serverpackets.ServerPacket;

/**
 * @author CostyKiller
 */
public class ExDethronePrevSeasonInfo extends ServerPacket
{
	private final Player _player;
	private final Long _previousSeasonServerPoints;
	private final Long _previousSeasonServerSoulOrbs;
	private final Map<Integer, StatSet> _previousConquestPlayerList;
	
	public ExDethronePrevSeasonInfo(Player player, Long prevServerPoints, Long prevServerSoulOrbs)
	{
		_player = player;
		_previousSeasonServerPoints = prevServerPoints;
		_previousSeasonServerSoulOrbs = prevServerSoulOrbs;
		_previousConquestPlayerList = RankManager.getInstance().getPreviousConquestRankList();
	}
	
	@Override
	public void write()
	{
		// Size 83 bytes on official
		ServerPackets.EX_DETHRONE_PREV_SEASON_INFO.writeId(this);
		
		// Previous Results
		// Terr. Owner
		int rank = 0;
		long personalPoints = 0;
		if (!_previousConquestPlayerList.isEmpty())
		{
			for (Entry<Integer, StatSet> entry : _previousConquestPlayerList.entrySet())
			{
				if (entry.getValue().getInt("charId") == _player.getObjectId())
				{
					rank = entry.getKey();
					personalPoints = entry.getValue().getLong("conquestPersonalPoints");
				}
			}
			writeSizedString(_previousConquestPlayerList.get(1).getString("conquest_name")); // sConquerorName
			writeInt(rank); // My Rank
			writeInt(_previousConquestPlayerList.size()); // My Rank percent
		}
		else
		{
			writeSizedString(""); // sConquerorName
			writeInt(0); // My Rank
			writeInt(0); // My Rank percent
		}
		
		writeInt(Config.SERVER_ID); // Conqueror Server
		writeInt(1); // total rankers only 1 server atm
		writeInt(Config.SERVER_ID); // server id
		writeLong(_previousSeasonServerPoints); // Server points
		
		writeLong(_previousSeasonServerSoulOrbs); // Total Soul Orbs
		writeLong(_previousSeasonServerPoints); // Server points
		
		// Personal Reward
		if (personalPoints > Config.CONQUEST_PERSONAL_REWARD_MIN_POINTS)
		{
			int rewardRank = 0;
			// rank percent formula
			double rankPercent = ((rank * 100) / (RankManager.getInstance().getPreviousConquestRankList().size()));
			
			if ((rankPercent > 0) && (rankPercent < 5))
			{
				rewardRank = 1;
			}
			else if ((rankPercent > 5) && (rankPercent < 10))
			{
				rewardRank = 2;
			}
			else if ((rankPercent > 10) && (rankPercent < 20))
			{
				rewardRank = 3;
			}
			else if ((rankPercent > 20) && (rankPercent < 30))
			{
				rewardRank = 4;
			}
			else if ((rankPercent > 30) && (rankPercent < 40))
			{
				rewardRank = 5;
			}
			else if ((rankPercent > 40) && (rankPercent < 50))
			{
				rewardRank = 6;
			}
			else if ((rankPercent > 50) && (rankPercent < 60))
			{
				rewardRank = 7;
			}
			else if ((rankPercent > 60) && (rankPercent < 70))
			{
				rewardRank = 8;
			}
			else if ((rankPercent > 70) && (rankPercent < 80))
			{
				rewardRank = 9;
			}
			else if (rankPercent > 80)
			{
				rewardRank = 10;
			}
			
			// writeInt(1); // 0 - no reward available / 1 - number of rewards available
			// writeInt(57);
			// writeLong(10000);
			switch (rewardRank)
			{
				case 1:
				{
					writeInt(Config.CONQUEST_REWARDS_RANK_1.size());
					for (ItemHolder reward : Config.CONQUEST_REWARDS_RANK_1)
					{
						writeInt(reward.getId());
						writeLong(reward.getCount());
					}
					break;
				}
				case 2:
				{
					writeInt(Config.CONQUEST_REWARDS_RANK_2.size());
					for (ItemHolder reward : Config.CONQUEST_REWARDS_RANK_2)
					{
						writeInt(reward.getId());
						writeLong(reward.getCount());
					}
					break;
				}
				case 3:
				{
					writeInt(Config.CONQUEST_REWARDS_RANK_3.size());
					for (ItemHolder reward : Config.CONQUEST_REWARDS_RANK_3)
					{
						writeInt(reward.getId());
						writeLong(reward.getCount());
					}
					break;
				}
				case 4:
				{
					writeInt(Config.CONQUEST_REWARDS_RANK_4.size());
					for (ItemHolder reward : Config.CONQUEST_REWARDS_RANK_4)
					{
						writeInt(reward.getId());
						writeLong(reward.getCount());
					}
					break;
				}
				case 5:
				{
					writeInt(Config.CONQUEST_REWARDS_RANK_5.size());
					for (ItemHolder reward : Config.CONQUEST_REWARDS_RANK_5)
					{
						writeInt(reward.getId());
						writeLong(reward.getCount());
					}
					break;
				}
				case 6:
				{
					writeInt(Config.CONQUEST_REWARDS_RANK_6.size());
					for (ItemHolder reward : Config.CONQUEST_REWARDS_RANK_6)
					{
						writeInt(reward.getId());
						writeLong(reward.getCount());
					}
					break;
				}
				case 7:
				{
					writeInt(Config.CONQUEST_REWARDS_RANK_7.size());
					for (ItemHolder reward : Config.CONQUEST_REWARDS_RANK_7)
					{
						writeInt(reward.getId());
						writeLong(reward.getCount());
					}
					break;
				}
				case 8:
				{
					writeInt(Config.CONQUEST_REWARDS_RANK_8.size());
					for (ItemHolder reward : Config.CONQUEST_REWARDS_RANK_8)
					{
						writeInt(reward.getId());
						writeLong(reward.getCount());
					}
					break;
				}
				case 9:
				{
					writeInt(Config.CONQUEST_REWARDS_RANK_9.size());
					for (ItemHolder reward : Config.CONQUEST_REWARDS_RANK_9)
					{
						writeInt(reward.getId());
						writeLong(reward.getCount());
					}
					break;
				}
				case 10:
				{
					writeInt(Config.CONQUEST_REWARDS_RANK_10.size());
					for (ItemHolder reward : Config.CONQUEST_REWARDS_RANK_10)
					{
						writeInt(reward.getId());
						writeLong(reward.getCount());
					}
					break;
				}
			}
		}
		else
		{
			writeInt(0); // 0 - no reward available
		}
		
		// Conqueror Server Reward
		if (personalPoints > Config.CONQUEST_SERVER_REWARD_MIN_POINTS)
		{
			// writeInt(2); // 0 - no reward available / 2 - number of rewards available
			// writeInt(57);
			// writeLong(10000);
			// writeInt(57);
			// writeLong(20000);
			
			writeInt(Config.CONQUEST_REWARDS_RANK_PARTICIPANT.size());
			for (ItemHolder reward : Config.CONQUEST_REWARDS_RANK_PARTICIPANT)
			{
				writeInt(reward.getId());
				writeLong(reward.getCount());
			}
		}
		else
		{
			writeInt(0); // 0 - no reward available
		}
		
		// Conquest personal reward ranking only available if player has more than 1 personal points
		// Conquest server reward only available if player has more than 1k personal points
		writeBoolean(personalPoints > 1); // Reward button available (grey false - green true)
	}
}
