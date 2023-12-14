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

import java.util.LinkedList;
import java.util.List;

import org.l2jmobius.Config;
import org.l2jmobius.commons.threads.ThreadPool;
import org.l2jmobius.gameserver.enums.StatModifierType;
import org.l2jmobius.gameserver.model.StatSet;
import org.l2jmobius.gameserver.model.actor.Creature;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.effects.AbstractEffect;
import org.l2jmobius.gameserver.model.effects.EffectType;
import org.l2jmobius.gameserver.model.holders.ItemSkillHolder;
import org.l2jmobius.gameserver.model.item.instance.Item;
import org.l2jmobius.gameserver.model.skill.Skill;
import org.l2jmobius.gameserver.network.serverpackets.InventoryUpdate;

/**
 * @author NviX
 */
public class RestoreSymbolSeal extends AbstractEffect
{
	private final int _amount;
	private final StatModifierType _mode;
	
	public RestoreSymbolSeal(StatSet params)
	{
		_amount = params.getInt("amount", 0);
		_mode = params.getEnum("mode", StatModifierType.class, StatModifierType.PER);
	}
	
	@Override
	public EffectType getEffectType()
	{
		return EffectType.RESTORE_SYMBOL_SEAL;
	}
	
	@Override
	public boolean isInstant()
	{
		return true;
	}
	
	@Override
	public void instant(Creature effector, Creature effected, Skill skill, Item item)
	{
		if (effected.isDead() || effected.isDoor())
		{
			return;
		}
		
		final Player player = effected.getActingPlayer();
		final int basicAmount = _amount;
		double amount = 0;
		switch (_mode)
		{
			case DIFF:
			{
				amount = Math.min(basicAmount, Config.MAX_SYMBOL_SEAL_POINTS - player.getSymbolSealPoints());
				break;
			}
			case PER:
			{
				amount = Math.min((Config.MAX_SYMBOL_SEAL_POINTS * basicAmount) / 100, Config.MAX_SYMBOL_SEAL_POINTS - player.getSymbolSealPoints());
				break;
			}
		}
		
		if (amount > 0)
		{
			final double newSymbolSealPoints = amount + effected.getActingPlayer().getSymbolSealPoints();
			player.setSymbolSealPoints((int) newSymbolSealPoints);
			player.updateSymbolSealSkills();
			player.sendSkillList();
			player.broadcastUserInfo();
			
			// Send item list to update Dye Powder with red icon in inventory.
			ThreadPool.schedule(() ->
			{
				final List<Item> items = new LinkedList<>();
				ITEMS: for (Item i : effected.getActingPlayer().getInventory().getItems())
				{
					if (i.getTemplate().hasSkills())
					{
						for (ItemSkillHolder s : i.getTemplate().getAllSkills())
						{
							if (s.getSkill().hasEffectType(EffectType.RESTORE_SYMBOL_SEAL))
							{
								items.add(i);
								continue ITEMS;
							}
						}
					}
				}
				
				if (!items.isEmpty())
				{
					final InventoryUpdate iu = new InventoryUpdate();
					iu.addItems(items);
					effected.getActingPlayer().sendInventoryUpdate(iu);
				}
			}, 1000);
		}
	}
}
