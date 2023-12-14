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
package handlers.dailymissionhandlers;

import org.l2jmobius.gameserver.data.sql.ClanTable;
import org.l2jmobius.gameserver.enums.DailyMissionStatus;
import org.l2jmobius.gameserver.handler.AbstractDailyMissionHandler;
import org.l2jmobius.gameserver.model.DailyMissionDataHolder;
import org.l2jmobius.gameserver.model.DailyMissionPlayerEntry;
import org.l2jmobius.gameserver.model.SiegeClan;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.clan.Clan;
import org.l2jmobius.gameserver.model.events.Containers;
import org.l2jmobius.gameserver.model.events.EventType;
import org.l2jmobius.gameserver.model.events.impl.sieges.OnCastleSiegeOwnerChange;
import org.l2jmobius.gameserver.model.events.listeners.ConsumerEventListener;

/**
 * @author UnAfraid
 */
public class SiegeDailyMissionHandler extends AbstractDailyMissionHandler
{
	private final int _missionId;
	private final int _minLevel;
	private final int _maxLevel;
	private final int _minClanLevel;
	private final int _minClanMasteryLevel;
	private int _ownerId;
	
	public SiegeDailyMissionHandler(DailyMissionDataHolder holder)
	{
		super(holder);
		_missionId = holder.getId();
		_minLevel = holder.getParams().getInt("minLevel", 0);
		_maxLevel = holder.getParams().getInt("maxLevel", Integer.MAX_VALUE);
		_minClanLevel = holder.getParams().getInt("minClanLevel", 0);
		_minClanMasteryLevel = holder.getParams().getInt("minClanMasteryLevel", 0);
	}
	
	@Override
	public void init()
	{
		Containers.Global().addListener(new ConsumerEventListener(this, EventType.ON_CASTLE_SIEGE_OWNER_CHANGE, (OnCastleSiegeOwnerChange event) -> onSiegeOwnerChange(event), this));
	}
	
	@Override
	public boolean isAvailable(Player player)
	{
		final DailyMissionPlayerEntry entry = getPlayerEntry(player.getObjectId(), false);
		if (entry != null)
		{
			switch (entry.getStatus())
			{
				case AVAILABLE:
				{
					return true;
				}
			}
		}
		return false;
	}
	
	private void onSiegeOwnerChange(OnCastleSiegeOwnerChange event)
	{
		if (_missionId == 2103) // Castle Siege: Successful Attack
		{
			_ownerId = event.getSiege().getCastle().getOwnerId();
			event.getSiege().getAttackerClans().forEach(this::processSiegeClan);
		}
		else if (_missionId == 2104)// Castle Siege: Successful Defense
		{
			_ownerId = event.getSiege().getCastle().getOwnerId();
			event.getSiege().getDefenderClans().forEach(this::processSiegeClan);
		}
	}
	
	private boolean checkClan(Player player)
	{
		if (player == null)
		{
			return false;
		}
		
		final int clanMastery = player.getClan().hasMastery(14) ? 14 : player.getClan().hasMastery(15) ? 15 : player.getClan().hasMastery(16) ? 16 : 0;
		return ((player.getClan().getLevel() >= _minClanLevel) && (clanMastery >= _minClanMasteryLevel));
	}
	
	private void processSiegeClan(SiegeClan siegeClan)
	{
		final Clan clan = ClanTable.getInstance().getClan(siegeClan.getClanId());
		if (clan != null)
		{
			if (_ownerId == clan.getId())
			{
				clan.getOnlineMembers(0).forEach(player ->
				{
					if ((player.getLevel() < _minLevel) || (player.getLevel() > _maxLevel) || !checkClan(player))
					{
						return;
					}
					
					final DailyMissionPlayerEntry entry = getPlayerEntry(player.getObjectId(), true);
					entry.setStatus(DailyMissionStatus.AVAILABLE);
					storePlayerEntry(entry);
				});
			}
		}
	}
}
