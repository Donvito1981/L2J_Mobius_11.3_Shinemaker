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
package handlers.itemhandlers;

import java.util.ArrayList;
import java.util.List;

import org.l2jmobius.Config;
import org.l2jmobius.commons.util.Rnd;
import org.l2jmobius.gameserver.data.xml.HomunculusCreationData;
import org.l2jmobius.gameserver.data.xml.HomunculusData;
import org.l2jmobius.gameserver.handler.IItemHandler;
import org.l2jmobius.gameserver.model.actor.Playable;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.homunculus.Homunculus;
import org.l2jmobius.gameserver.model.homunculus.HomunculusTemplate;
import org.l2jmobius.gameserver.model.item.instance.Item;
import org.l2jmobius.gameserver.model.variables.PlayerVariables;
import org.l2jmobius.gameserver.network.SystemMessageId;
import org.l2jmobius.gameserver.network.serverpackets.homunculus.ExHomunculusSummonResult;
import org.l2jmobius.gameserver.network.serverpackets.homunculus.ExShowHomunculusBirthInfo;
import org.l2jmobius.gameserver.network.serverpackets.homunculus.ExShowHomunculusCouponUi;
import org.l2jmobius.gameserver.network.serverpackets.homunculus.ExShowHomunculusList;

/**
 * @author Liamxroy, Mobius
 */
public class CreateHomunculus implements IItemHandler
{
	@Override
	public boolean useItem(Playable playable, Item item, boolean forceUse)
	{
		if (!playable.isPlayer())
		{
			playable.sendPacket(SystemMessageId.YOUR_PET_CANNOT_CARRY_THIS_ITEM);
			return false;
		}
		
		final Player player = playable.getActingPlayer();
		final int size = player.getHomunculusList().size();
		if (size >= Config.MAX_HOMUNCULUS_COUNT)
		{
			player.sendMessage("There's no available slot for Homunculus!");
			return false;
		}
		
		double chance = 0.00;
		switch (item.getId())
		{
			case 81808: // Common Homunculus' Hourglass
			{
				chance = 7.00;
				break;
			}
			case 81809: // High-grade Homunculus' Hourglass
			{
				chance = 2.99;
				break;
			}
			case 81810: // Top-grade Homunculus' Hourglass
			{
				chance = 0.01;
				break;
			}
		}
		
		final List<Integer> ids = new ArrayList<>();
		for (Double[] itemChances : HomunculusCreationData.getInstance().getDefaultTemplate().getCreationChance())
		{
			if (itemChances[1] == chance)
			{
				ids.add(itemChances[0].intValue());
			}
			
			if ((item.getId() == 81810 /* Top-grade Homunculus' Hourglass */) && (itemChances[1] != 7.00))
			{
				ids.add(itemChances[0].intValue());
			}
		}
		
		final int homunculusId = ids.get(Rnd.get(ids.size()));
		final HomunculusTemplate template = HomunculusData.getInstance().getTemplate(homunculusId);
		if (template == null)
		{
			LOGGER.warning("CreateHomunculus: Could not find Homunculus template " + homunculusId + "!");
			return false;
		}
		
		player.destroyItem("CreateHomunculus", item, player, true);
		
		final Homunculus homunculus = new Homunculus(template, player.getHomunculusList().size(), 1, 0, 0, 0, 0, 0, 0, false);
		if (player.getHomunculusList().add(homunculus))
		{
			player.getVariables().set(PlayerVariables.HOMUNCULUS_CREATION_TIME, 0);
			player.getVariables().set(PlayerVariables.HOMUNCULUS_HP_POINTS, 0);
			player.getVariables().set(PlayerVariables.HOMUNCULUS_SP_POINTS, 0);
			player.getVariables().set(PlayerVariables.HOMUNCULUS_VP_POINTS, 0);
			player.sendPacket(new ExShowHomunculusBirthInfo(player));
			player.sendPacket(new ExHomunculusSummonResult(1));
			player.sendPacket(SystemMessageId.A_HOMUNCULUS_SLOT_IS_UNLOCKED);
			player.calculateHomunculusSlots();
		}
		player.sendPacket(new ExShowHomunculusCouponUi());
		player.broadcastUserInfo();
		player.sendPacket(new ExShowHomunculusList(player));
		return true;
	}
}
