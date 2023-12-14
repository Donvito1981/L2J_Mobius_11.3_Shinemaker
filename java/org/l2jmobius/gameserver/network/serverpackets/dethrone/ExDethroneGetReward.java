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
import org.l2jmobius.gameserver.model.variables.PlayerVariables;
import org.l2jmobius.gameserver.network.ServerPackets;
import org.l2jmobius.gameserver.network.SystemMessageId;
import org.l2jmobius.gameserver.network.serverpackets.ServerPacket;

/**
 * @author CostyKiller
 */
public class ExDethroneGetReward extends ServerPacket
{
	private final Player _player;
	private final boolean _rewarded;
	private final Map<Integer, StatSet> _previousConquestPlayerList;
	
	public ExDethroneGetReward(Player player, boolean rewarded)
	{
		_rewarded = rewarded;
		_player = player;
		_previousConquestPlayerList = RankManager.getInstance().getPreviousConquestRankList();
	}
	
	@Override
	public void write()
	{
		ServerPackets.EX_DETHRONE_INFO.writeId(this);
		// Check rank for previous season
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
		}
		if (_rewarded)
		{
			_player.sendPacket(SystemMessageId.YOU_HAVE_ALREADY_RECEIVED_THE_REWARD);
		}
		else if (personalPoints > Config.CONQUEST_PERSONAL_REWARD_MIN_POINTS) // Personal Reward.
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
			
			switch (rewardRank)
			{
				case 1:
				{
					for (ItemHolder reward : Config.CONQUEST_REWARDS_RANK_1)
					{
						_player.addItem("CONQUEST_REWARDS", reward.getId(), reward.getCount(), _player, true);
					}
					break;
				}
				case 2:
				{
					for (ItemHolder reward : Config.CONQUEST_REWARDS_RANK_2)
					{
						_player.addItem("CONQUEST_REWARDS", reward.getId(), reward.getCount(), _player, true);
					}
					break;
				}
				case 3:
				{
					for (ItemHolder reward : Config.CONQUEST_REWARDS_RANK_3)
					{
						_player.addItem("CONQUEST_REWARDS", reward.getId(), reward.getCount(), _player, true);
					}
					break;
				}
				case 4:
				{
					for (ItemHolder reward : Config.CONQUEST_REWARDS_RANK_4)
					{
						_player.addItem("CONQUEST_REWARDS", reward.getId(), reward.getCount(), _player, true);
					}
					break;
				}
				case 5:
				{
					for (ItemHolder reward : Config.CONQUEST_REWARDS_RANK_5)
					{
						_player.addItem("CONQUEST_REWARDS", reward.getId(), reward.getCount(), _player, true);
					}
					break;
				}
				case 6:
				{
					for (ItemHolder reward : Config.CONQUEST_REWARDS_RANK_6)
					{
						_player.addItem("CONQUEST_REWARDS", reward.getId(), reward.getCount(), _player, true);
					}
					break;
				}
				case 7:
				{
					for (ItemHolder reward : Config.CONQUEST_REWARDS_RANK_7)
					{
						_player.addItem("CONQUEST_REWARDS", reward.getId(), reward.getCount(), _player, true);
					}
					break;
				}
				case 8:
				{
					for (ItemHolder reward : Config.CONQUEST_REWARDS_RANK_8)
					{
						_player.addItem("CONQUEST_REWARDS", reward.getId(), reward.getCount(), _player, true);
					}
					break;
				}
				case 9:
				{
					for (ItemHolder reward : Config.CONQUEST_REWARDS_RANK_9)
					{
						_player.addItem("CONQUEST_REWARDS", reward.getId(), reward.getCount(), _player, true);
					}
					break;
				}
				case 10:
				{
					for (ItemHolder reward : Config.CONQUEST_REWARDS_RANK_10)
					{
						_player.addItem("CONQUEST_REWARDS", reward.getId(), reward.getCount(), _player, true);
					}
					break;
				}
			}
			
			// Conqueror Server Reward
			if (personalPoints > Config.CONQUEST_SERVER_REWARD_MIN_POINTS)
			{
				for (ItemHolder reward : Config.CONQUEST_REWARDS_RANK_PARTICIPANT)
				{
					_player.addItem("CONQUEST_REWARDS", reward.getId(), reward.getCount(), _player, true);
				}
			}
			
			_player.getVariables().set(PlayerVariables.CONQUEST_REWARDS_RECEIVED, true);
		}
	}
}
