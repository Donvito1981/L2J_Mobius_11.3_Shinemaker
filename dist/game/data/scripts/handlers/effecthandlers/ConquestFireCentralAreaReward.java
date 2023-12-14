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
package handlers.effecthandlers;

import org.l2jmobius.Config;
import org.l2jmobius.gameserver.instancemanager.ZoneManager;
import org.l2jmobius.gameserver.model.StatSet;
import org.l2jmobius.gameserver.model.actor.Creature;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.effects.AbstractEffect;
import org.l2jmobius.gameserver.model.events.AbstractScript;
import org.l2jmobius.gameserver.model.item.instance.Item;
import org.l2jmobius.gameserver.model.skill.Skill;
import org.l2jmobius.gameserver.model.variables.PlayerVariables;
import org.l2jmobius.gameserver.model.zone.ZoneType;
import org.l2jmobius.gameserver.network.SystemMessageId;

/**
 * @author CostyKiller
 */
public class ConquestFireCentralAreaReward extends AbstractEffect
{
	private static final ZoneType FIRE_SOURCE_CENTRAL_AREA_ZONE = ZoneManager.getInstance().getZoneByName("central_area_fire_source");
	
	@Override
	public boolean isInstant()
	{
		return true;
	}
	
	public ConquestFireCentralAreaReward(StatSet params)
	{
	}
	
	@Override
	public void instant(Creature effector, Creature effected, Skill skill, Item item)
	{
		if (!effected.isPlayable())
		{
			return;
		}
		
		if (effected.isPlayer() && FIRE_SOURCE_CENTRAL_AREA_ZONE.isInsideZone(effected))
		{
			final Player player = effected.getActingPlayer();
			if (AbstractScript.getRandom(100) < Config.CONQUEST_ABILITY_FIRE_SOURCE_UPGRADE_CHANCE)
			{
				player.getVariables().set(PlayerVariables.CONQUEST_ABILITY_FIRE_SOURCE_EXP, player.getVariables().getInt(PlayerVariables.CONQUEST_ABILITY_FIRE_SOURCE_EXP, 0) + Config.CONQUEST_ABILITY_FIRE_SOURCE_EXP_AMOUNT);
				player.sendPacket(SystemMessageId.YOU_HAVE_RECEIVED_FIRE_SOURCE_POINTS);
				AbstractScript.showOnScreenMsg(player, "You have received Fire Source points.", 5000);
			}
		}
	}
}
