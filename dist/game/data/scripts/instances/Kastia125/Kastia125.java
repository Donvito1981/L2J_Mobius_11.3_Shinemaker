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
package instances.Kastia125;

import java.util.List;

import org.l2jmobius.gameserver.ai.CtrlIntention;
import org.l2jmobius.gameserver.instancemanager.InstanceManager;
import org.l2jmobius.gameserver.instancemanager.WalkingManager;
import org.l2jmobius.gameserver.model.Location;
import org.l2jmobius.gameserver.model.actor.Attackable;
import org.l2jmobius.gameserver.model.actor.Creature;
import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.actor.instance.Monster;
import org.l2jmobius.gameserver.model.holders.ItemHolder;
import org.l2jmobius.gameserver.model.holders.SkillHolder;
import org.l2jmobius.gameserver.model.instancezone.Instance;
import org.l2jmobius.gameserver.network.NpcStringId;
import org.l2jmobius.gameserver.network.SystemMessageId;
import org.l2jmobius.gameserver.network.serverpackets.ExShowScreenMessage;
import org.l2jmobius.gameserver.network.serverpackets.SystemMessage;

import instances.AbstractInstance;

/**
 * @author Mobius, Tanatos
 */
public class Kastia125 extends AbstractInstance
{
	// NPC
	private static final int KARINIA = 34541;
	private static final int RESEARCHER = 34566;
	private static final int BOSS = 24929;
	// Monsters
	private static final int[] MONSTERS =
	{
		24926, // Kastia's Keeper
		24927, // Kastia's Overseer
		24928, // Kastia's Warder
		BOSS, // Hugos
	};
	// Item
	private static final ItemHolder KASTIAS_PACK = new ItemHolder(82352, 1);
	// Skills
	private static final SkillHolder BOSS_BERSERKER = new SkillHolder(32520, 2);
	// Misc
	private static final int TEMPLATE_ID = 317;
	
	public Kastia125()
	{
		super(TEMPLATE_ID);
		addStartNpc(KARINIA);
		addTalkId(KARINIA);
		addKillId(MONSTERS);
		addSpawnId(BOSS);
		addCreatureSeeId(MONSTERS);
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, Player player)
	{
		switch (event)
		{
			case "enterInstance":
			{
				// Cannot enter if player finished another Kastia instance.
				final long currentTime = System.currentTimeMillis();
				if ((currentTime < InstanceManager.getInstance().getInstanceTime(player, 298)) //
					|| (currentTime < InstanceManager.getInstance().getInstanceTime(player, 299)) //
					|| (currentTime < InstanceManager.getInstance().getInstanceTime(player, 300)) //
					|| (currentTime < InstanceManager.getInstance().getInstanceTime(player, 305)) //
					|| (currentTime < InstanceManager.getInstance().getInstanceTime(player, 306)) //
					|| (currentTime < InstanceManager.getInstance().getInstanceTime(player, 327)))
				{
					player.sendPacket(new SystemMessage(SystemMessageId.YOU_CANNOT_ENTER_AS_C1_IS_IN_ANOTHER_INSTANCE_ZONE).addString(player.getName()));
					return null;
				}
				
				enterInstance(player, npc, TEMPLATE_ID);
				if (player.getInstanceWorld() != null)
				{
					startQuestTimer("check_status", 10000, null, player);
				}
				return null;
			}
			case "check_status":
			{
				final Instance world = player.getInstanceWorld();
				if (!isInInstance(world))
				{
					return null;
				}
				switch (world.getStatus())
				{
					case 0:
					{
						world.setStatus(1);
						showOnScreenMsg(world, NpcStringId.LV_1_2, ExShowScreenMessage.TOP_CENTER, 10000, true);
						moveMonsters(world.spawnGroup("wave_1"));
						startQuestTimer("check_status", 10000, null, player);
						break;
					}
					case 1:
					{
						if (world.getAliveNpcCount(MONSTERS) == 0)
						{
							world.setStatus(2);
							showOnScreenMsg(world, NpcStringId.LV_2_2, ExShowScreenMessage.TOP_CENTER, 10000, true);
							moveMonsters(world.spawnGroup("wave_2"));
						}
						startQuestTimer("check_status", 10000, null, player);
						break;
					}
					case 2:
					{
						if (world.getAliveNpcCount(MONSTERS) == 0)
						{
							world.setStatus(3);
							showOnScreenMsg(world, NpcStringId.LV_3_2, ExShowScreenMessage.TOP_CENTER, 10000, true);
							moveMonsters(world.spawnGroup("wave_3"));
						}
						startQuestTimer("check_status", 10000, null, player);
						break;
					}
					case 3:
					{
						if (world.getAliveNpcCount(MONSTERS) == 0)
						{
							world.setStatus(4);
							showOnScreenMsg(world, NpcStringId.LV_4_2, ExShowScreenMessage.TOP_CENTER, 10000, true);
							moveMonsters(world.spawnGroup("wave_4"));
						}
						startQuestTimer("check_status", 10000, null, player);
						break;
					}
					case 4:
					{
						if (world.getAliveNpcCount(MONSTERS) == 0)
						{
							world.setStatus(5);
							showOnScreenMsg(world, NpcStringId.LV_5_2, ExShowScreenMessage.TOP_CENTER, 10000, true);
							moveMonsters(world.spawnGroup("wave_5"));
						}
						startQuestTimer("check_status", 10000, null, player);
						break;
					}
					case 5:
					{
						if (world.getAliveNpcCount(MONSTERS) == 0)
						{
							world.setStatus(6);
							showOnScreenMsg(world, NpcStringId.LV_6_2, ExShowScreenMessage.TOP_CENTER, 10000, true);
							moveMonsters(world.spawnGroup("wave_6"));
						}
						startQuestTimer("check_status", 10000, null, player);
						break;
					}
					case 6:
					{
						if (world.getAliveNpcCount(MONSTERS) == 0)
						{
							world.setStatus(7);
							showOnScreenMsg(world, NpcStringId.LV_7_2, ExShowScreenMessage.TOP_CENTER, 10000, true);
							moveMonsters(world.spawnGroup("wave_7"));
						}
						startQuestTimer("check_status", 10000, null, player);
						break;
					}
					case 7:
					{
						if (world.getAliveNpcCount(MONSTERS) == 0)
						{
							showOnScreenMsg(world, NpcStringId.YOU_HAVE_SUCCESSFULLY_COMPLETED_THE_KASTIA_S_LABYRINTH_YOU_WILL_BE_TRANSPORTED_TO_THE_SURFACE_SHORTLY_ALSO_YOU_CAN_LEAVE_THIS_PLACE_WITH_THE_HELP_OF_KASTIA_S_RESEARCHER, ExShowScreenMessage.TOP_CENTER, 10000, true);
							addSpawn(RESEARCHER, player.getLocation(), true, 0, false, world.getId());
							giveItems(player, KASTIAS_PACK);
							world.finishInstance(3);
						}
						else
						{
							startQuestTimer("check_status", 10000, null, player);
						}
						break;
					}
				}
				return null;
			}
			case "move_to_spawn":
			{
				final Instance world = npc.getInstanceWorld();
				final Location loc = world.getTemplateParameters().getLocation("spawnpoint");
				final Location moveTo = new Location(loc.getX() + getRandom(-100, 100), loc.getY() + getRandom(-100, 100), loc.getZ());
				npc.setRunning();
				addMoveToDesire(npc, moveTo, 4);
				startQuestTimer("start_moving", 5000, npc, null);
				break;
			}
			case "start_moving":
			{
				final Instance world = npc.getInstanceWorld();
				final String selectedRoute = "routetospawn";
				WalkingManager.getInstance().startMoving(npc, world.getTemplateParameters().getString(selectedRoute));
				break;
			}
		}
		return super.onAdvEvent(event, npc, player);
	}
	
	private void moveMonsters(List<Npc> monsterList)
	{
		int delay = 500;
		for (Npc monster : monsterList)
		{
			final Instance world = monster.getInstanceWorld();
			if (monster.isAttackable() && (world != null))
			{
				monster.setRandomWalking(false);
				startQuestTimer("move_to_spawn", delay, monster, null);
				((Attackable) monster).setCanReturnToSpawnPoint(false);
			}
		}
	}
	
	@Override
	public String onSpawn(Npc npc)
	{
		final Instance world = npc.getInstanceWorld();
		if (world != null)
		{
			npc.doCast(BOSS_BERSERKER.getSkill());
		}
		return super.onSpawn(npc);
	}
	
	@Override
	public String onCreatureSee(Npc npc, Creature player)
	{
		final Instance world = player.getInstanceWorld();
		if ((world != null) && (player.isPlayer()))
		{
			final double distance = npc.calculateDistance2D(player);
			if ((distance < 900))
			{
				WalkingManager.getInstance().cancelMoving(npc);
				((Monster) npc).addDamageHate(player, 0, 1000);
				npc.getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
				addAttackDesire(npc, player);
			}
		}
		return super.onCreatureSee(npc, player);
	}
	
	public static void main(String[] args)
	{
		new Kastia125();
	}
}
