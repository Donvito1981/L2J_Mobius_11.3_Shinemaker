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
package org.l2jmobius.gameserver.network.clientpackets.huntingzones;

import org.l2jmobius.commons.network.ReadablePacket;
import org.l2jmobius.commons.threads.ThreadPool;
import org.l2jmobius.gameserver.data.xml.TimedHuntingZoneData;
import org.l2jmobius.gameserver.instancemanager.InstanceManager;
import org.l2jmobius.gameserver.instancemanager.QuestManager;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.holders.TimedHuntingZoneHolder;
import org.l2jmobius.gameserver.model.itemcontainer.Inventory;
import org.l2jmobius.gameserver.model.olympiad.OlympiadManager;
import org.l2jmobius.gameserver.model.variables.PlayerVariables;
import org.l2jmobius.gameserver.model.zone.ZoneId;
import org.l2jmobius.gameserver.network.GameClient;
import org.l2jmobius.gameserver.network.SystemMessageId;
import org.l2jmobius.gameserver.network.clientpackets.ClientPacket;
import org.l2jmobius.gameserver.network.serverpackets.huntingzones.TimedHuntingZoneEnter;

/**
 * @author Mobius
 */
public class ExTimedHuntingZoneEnter implements ClientPacket
{
	private int _zoneId;
	
	@Override
	public void read(ReadablePacket packet)
	{
		_zoneId = packet.readInt();
	}
	
	@Override
	public void run(GameClient client)
	{
		final Player player = client.getPlayer();
		if (player == null)
		{
			return;
		}
		
		if (!player.isInsideZone(ZoneId.PEACE))
		{
			player.sendPacket(SystemMessageId.YOU_CAN_ENTER_THE_AREA_ONLY_FROM_PEACE_ZONE);
			return;
		}
		if (player.isInCombat())
		{
			player.sendMessage("You can only enter in time-limited hunting zones while not in combat.");
			return;
		}
		if (player.getReputation() < 0)
		{
			player.sendMessage("You can only enter in time-limited hunting zones when you have positive reputation.");
			return;
		}
		if (player.isInDuel())
		{
			player.sendMessage("Cannot use time-limited hunting zones during a duel.");
			return;
		}
		if (player.isInOlympiadMode() || OlympiadManager.getInstance().isRegistered(player))
		{
			player.sendPacket(SystemMessageId.SESSION_ZONES_ARE_UNAVAILABLE_WHILE_YOU_ARE_IN_QUEUE_FOR_THE_OLYMPIAD);
			return;
		}
		if (player.isRegisteredOnEvent() || (player.getBlockCheckerArena() > -1))
		{
			player.sendMessage("Cannot use time-limited hunting zones while registered on an event.");
			return;
		}
		if (player.isInInstance() || player.isInTimedHuntingZone())
		{
			player.sendMessage("Cannot use time-limited hunting zones while in an instance.");
			return;
		}
		
		final TimedHuntingZoneHolder holder = TimedHuntingZoneData.getInstance().getHuntingZone(_zoneId);
		if (holder == null)
		{
			return;
		}
		
		if ((player.getLevel() < holder.getMinLevel()) || (player.getLevel() > holder.getMaxLevel()))
		{
			player.sendMessage("Your level does not correspond the zone equivalent.");
			return;
		}
		
		final int instanceId = holder.getInstanceId();
		if ((instanceId > 0) && holder.isSoloInstance() && (InstanceManager.getInstance().getInstanceTime(player, instanceId) > System.currentTimeMillis()))
		{
			player.sendMessage("This transcendent instance has not reset yet.");
			return;
		}
		
		if (player.isMounted())
		{
			if (holder.useWorldPrefix())
			{
				player.sendPacket(SystemMessageId.YOU_CANNOT_TELEPORT_TO_THE_WORLD_HUNTING_ZONE_WHILE_RIDING_A_MOUNT);
			}
			else
			{
				player.sendMessage("Cannot use time-limited hunting zones while mounted.");
			}
			return;
		}
		if (holder.useWorldPrefix())
		{
			if (player.isCursedWeaponEquipped())
			{
				player.sendPacket(SystemMessageId.YOU_CANNOT_TELEPORT_TO_THE_WORLD_HUNTING_ZONE_WHILE_OWNING_A_CURSED_WEAPON);
				return;
			}
		}
		
		final long currentTime = System.currentTimeMillis();
		long endTime = currentTime + player.getTimedHuntingZoneRemainingTime(_zoneId);
		final long lastEntryTime = player.getVariables().getLong(PlayerVariables.HUNTING_ZONE_ENTRY + _zoneId, 0);
		if ((lastEntryTime + holder.getResetDelay()) < currentTime)
		{
			if (endTime == currentTime)
			{
				endTime += holder.getInitialTime();
				player.getVariables().set(PlayerVariables.HUNTING_ZONE_ENTRY + _zoneId, currentTime);
			}
		}
		
		if (endTime > currentTime)
		{
			if (holder.getEntryItemId() == Inventory.ADENA_ID)
			{
				if (player.getAdena() > holder.getEntryFee())
				{
					player.reduceAdena("TimedHuntingZone", holder.getEntryFee(), player, true);
				}
				else
				{
					player.sendPacket(SystemMessageId.NOT_ENOUGH_ADENA);
					return;
				}
			}
			else if (!player.destroyItemByItemId("TimedHuntingZone", holder.getEntryItemId(), holder.getEntryFee(), player, true))
			{
				player.sendPacket(SystemMessageId.YOU_DO_NOT_HAVE_ENOUGH_REQUIRED_ITEMS);
				return;
			}
			
			player.getVariables().set(PlayerVariables.HUNTING_ZONE_TIME + _zoneId, endTime - currentTime);
			
			if (instanceId == 0)
			{
				if (holder.useWorldPrefix())
				{
					player.sendPacket(SystemMessageId.YOU_LL_BE_TAKEN_TO_THE_WORLD_HUNTING_ZONE_IN_3_SEC);
					player.stopMove(null);
					ThreadPool.schedule(() -> player.teleToLocation(holder.getEnterLocation()), 3000);
				}
				else
				{
					player.teleToLocation(holder.getEnterLocation());
				}
			}
			else // Instanced zones.
			{
				if (holder.useWorldPrefix())
				{
					player.sendPacket(SystemMessageId.YOU_LL_BE_TAKEN_TO_THE_WORLD_HUNTING_ZONE_IN_3_SEC);
					player.stopMove(null);
					ThreadPool.schedule(() -> QuestManager.getInstance().getQuest("TimedHunting").notifyEvent("ENTER " + _zoneId, null, player), 3000);
				}
				else
				{
					QuestManager.getInstance().getQuest("TimedHunting").notifyEvent("ENTER " + _zoneId, null, player);
				}
			}
			
			// Send time icon.
			player.sendPacket(new TimedHuntingZoneEnter(player, _zoneId));
		}
		else
		{
			player.sendPacket(SystemMessageId.CURRENTLY_YOU_HAVE_THE_MAX_AMOUNT_OF_TIME_FOR_THE_HUNTING_ZONE_SO_YOU_CANNOT_ADD_ANY_MORE_TIME);
		}
	}
}