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
package ai.areas.Conquest.SacredFire;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.concurrent.ScheduledFuture;
import java.util.logging.Level;

import org.l2jmobius.Config;
import org.l2jmobius.commons.database.DatabaseFactory;
import org.l2jmobius.commons.threads.ThreadPool;
import org.l2jmobius.gameserver.instancemanager.GlobalVariablesManager;
import org.l2jmobius.gameserver.instancemanager.ZoneManager;
import org.l2jmobius.gameserver.model.World;
import org.l2jmobius.gameserver.model.WorldObject;
import org.l2jmobius.gameserver.model.actor.Creature;
import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.actor.instance.Guard;
import org.l2jmobius.gameserver.model.events.EventDispatcher;
import org.l2jmobius.gameserver.model.events.EventType;
import org.l2jmobius.gameserver.model.events.ListenerRegisterType;
import org.l2jmobius.gameserver.model.events.annotations.Id;
import org.l2jmobius.gameserver.model.events.annotations.RegisterEvent;
import org.l2jmobius.gameserver.model.events.annotations.RegisterType;
import org.l2jmobius.gameserver.model.events.impl.OnServerStart;
import org.l2jmobius.gameserver.model.events.impl.creature.player.OnPlayerSummonSacredFire;
import org.l2jmobius.gameserver.model.events.impl.item.OnItemUse;
import org.l2jmobius.gameserver.model.holders.ItemHolder;
import org.l2jmobius.gameserver.model.holders.SkillHolder;
import org.l2jmobius.gameserver.model.variables.PlayerVariables;
import org.l2jmobius.gameserver.model.zone.ZoneType;
import org.l2jmobius.gameserver.network.SystemMessageId;
import org.l2jmobius.gameserver.network.serverpackets.SystemMessage;

import ai.AbstractNpcAI;

/**
 * Conquest Sacred Fire AI.
 * @author CostyKiller
 */
public class SacredFire extends AbstractNpcAI
{
	private static final boolean DEBUG = false; // Set it true to display related messages into server console.
	
	// NPCs
	private static final int SACRED_FIRE = 34658;
	private static final int FLAME_GUARDIAN = 34664;
	
	// Items
	private static final int SACRED_FIRE_SUMMON_SCROLL = 82614;
	private static final int FLAME_GUARDIAN_SCROLL = 82619;
	private static final int FLAME_ESSENCE = 82618;
	
	// NPC vars
	private static final long LIFETIME = 1500000; // 25 Minutes is equivalent to 1500000 Milliseconds
	private static final long SACRED_FIRE_COUNT_UPDATE_TIME = 60000; // 60 Seconds is equivalent to 60000 Milliseconds
	private static final int SPAWNED = 1; // Spawned effect
	private static final int DESTROYED = 2; // Destroyed effect
	
	// SQL Queries
	private static final String DELETE_QUERY = "DELETE FROM character_variables WHERE var LIKE ?";
	
	// Steal attempt requirements
	private static final int REQUIRED_PERSONAL_POINTS = 900;
	private static final int REQUIRED_SP = 400000;
	private static final int STEAL_CHANCE = 50; // 50%
	
	// Rewards
	private static final int SEALED_FIRE_SOURCE = 82658;
	
	// Skills
	private static final SkillHolder ESSENCE_OF_FIRE_ENERGY_BUFF = new SkillHolder(34492, 1);
	private static final SkillHolder FLAME_GUARDIAN_ENERGY_BUFF = new SkillHolder(34493, 1);
	
	// Zones
	private static final ZoneType FIRE_SOURCE_COMMON_AREA_ZONE = ZoneManager.getInstance().getZoneByName("common_area_fire_source");
	private static final ZoneType CONQUEST_ZONE = ZoneManager.getInstance().getZoneByName("conquest");
	
	// Sacred Fire States
	// 0 - you can summon the flame / 1 - summoning the flame / 2- summoning flame is finished get reward / 3 - flame is extinguished
	private static final int SUMMONING_STATE = 1;
	private static final int REWARDING_STATE = 2;
	private static final int EXTINGUISHED_STATE = 3;
	
	// Tasks
	private static ScheduledFuture<?> _scheduledSuccessfulDefenseRewardTask;
	private static ScheduledFuture<?> _scheduledSacredFireCountSetupTask;
	
	private SacredFire()
	{
		addStartNpc(SACRED_FIRE);
		addFirstTalkId(SACRED_FIRE);
		addTalkId(SACRED_FIRE);
		addExitZoneId(CONQUEST_ZONE.getId());
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, Player player)
	{
		String htmltext = null;
		final long personalConquestPoints = player.getVariables().getLong(PlayerVariables.CONQUEST_PERSONAL_POINTS, 0);
		switch (event)
		{
			case "34658.html":
			case "34658-01.html":
			case "34658-02.html":
			case "34658-03.html":
			case "34658-31.html":
			{
				htmltext = event;
				break;
			}
			case "attemptSteal":
			{
				if (npc.getVariables().getInt("OWNER_OID", 0) == player.getObjectId())
				{
					player.sendMessage("You can't steal your own Sacred Fire.");
				}
				else if (!((personalConquestPoints > REQUIRED_PERSONAL_POINTS) && (player.getSp() > REQUIRED_SP)))
				{
					player.sendMessage("You can't steal this Sacred Fire, because requirements are not met.");
				}
				else
				{
					// — Each attempt costs 900 Conquest points and 400,000 SP.
					// — The price is paid even if the attempt ends in a failure.
					player.getVariables().set(PlayerVariables.CONQUEST_PERSONAL_POINTS, player.getVariables().getLong(PlayerVariables.CONQUEST_PERSONAL_POINTS) - REQUIRED_PERSONAL_POINTS);
					player.setSp(player.getSp() - REQUIRED_SP);
					
					// Messages.
					final SystemMessage sm = new SystemMessage(SystemMessageId.PERSONAL_CONQUEST_POINTS_S1);
					sm.addLong(REQUIRED_PERSONAL_POINTS);
					final SystemMessage sm2 = new SystemMessage(SystemMessageId.YOUR_SP_HAS_DECREASED_BY_S1);
					sm2.addLong(REQUIRED_SP);
					player.sendPacket(sm);
					player.sendPacket(sm2);
					
					if (getRandom(100) < STEAL_CHANCE)
					{
						if ((_scheduledSuccessfulDefenseRewardTask != null) && !_scheduledSuccessfulDefenseRewardTask.isCancelled() && !_scheduledSuccessfulDefenseRewardTask.isDone())
						{
							_scheduledSuccessfulDefenseRewardTask.cancel(false);
							_scheduledSuccessfulDefenseRewardTask = null;
						}
						if ((_scheduledSacredFireCountSetupTask != null) && !_scheduledSacredFireCountSetupTask.isCancelled() && !_scheduledSacredFireCountSetupTask.isDone())
						{
							_scheduledSacredFireCountSetupTask.cancel(false);
							_scheduledSacredFireCountSetupTask = null;
						}
						npc.setDisplayEffect(DESTROYED);
						npc.decayMe();
						player.sendMessage("You have stolen this Sacred Fire.");
						
						// TODO: check if stealing the sacred fire makes it disappear or spawns it for the stealer.
						// spawnSacredFire(player);
						
						// TODO: Check with increased range if more guardians attack.
						World.getInstance().forEachVisibleObjectInRange(player, Guard.class, 1000, guard ->
						{
							if ((guard.getId() == FLAME_GUARDIAN) && !guard.isDead() && !guard.isDecayed() && !guard.isInCombat() && npc.getEffectList().isAffectedBySkill(FLAME_GUARDIAN_ENERGY_BUFF.getSkillId()))
							{
								addAttackPlayerDesire(guard, player);
							}
						});
						
						// Get the previous owner objectId from world and update Sacred Fire vars.
						final Player previousOwner = World.getInstance().getPlayer(npc.getVariables().getInt("OWNER_OID", 0));
						
						// Set previous owner variable of the stolen Sacred Fire state to 3 (flame extinguished).
						previousOwner.getVariables().set("SACRED_FIRE_SLOT_" + npc.getVariables().getInt("SLOT", 0), EXTINGUISHED_STATE);
						
						// Update previous owner current Sacred Fires count.
						previousOwner.getVariables().set(PlayerVariables.CONQUEST_SACRED_FIRE_SLOT_COUNT, previousOwner.getVariables().getInt(PlayerVariables.CONQUEST_SACRED_FIRE_SLOT_COUNT, 0) - 1);
						previousOwner.getVariables().remove("SACRED_FIRE_SLOT_" + npc.getVariables().getInt("SLOT", 0));
						
						if (player.isInventoryUnder90(false))
						{
							if (!Config.CONQUEST_SACRED_FIRE_REWARDS.isEmpty())
							{
								// Common items rewards.
								giveItems(player, getRandomEntry(Config.CONQUEST_SACRED_FIRE_REWARDS));
							}
						}
						else
						{
							player.sendPacket(SystemMessageId.YOUR_INVENTORY_S_WEIGHT_SLOT_LIMIT_HAS_BEEN_EXCEEDED_SO_YOU_CAN_T_RECEIVE_THE_REWARD_PLEASE_FREE_UP_SOME_SPACE_AND_TRY_AGAIN);
						}
					}
					else
					{
						player.sendMessage("You have failed to steal this Sacred Fire.");
					}
				}
				break;
			}
			case "increaseHp":
			{
				if (npc.getVariables().getInt("OWNER_OID", 0) != player.getObjectId())
				{
					player.sendMessage("You can't upgrade this Sacred Fire.");
				}
				else if (npc.getVariables().hasVariable("INCREASED_HP") && npc.getVariables().getBoolean("INCREASED_HP"))
				{
					htmltext = "34658-21.html"; // Already used flame essence.
				}
				else if (takeItems(player, FLAME_ESSENCE, 1))
				{
					npc.setDisplayEffect(SPAWNED);
					npc.getVariables().set("INCREASED_HP", true);
					ESSENCE_OF_FIRE_ENERGY_BUFF.getSkill().applyEffects(npc, npc);
					htmltext = "34658-20.html"; // Sacred Fire buff.
				}
				else
				{
					player.sendMessage("You don't have the required items.");
				}
				break;
			}
			case "preventSteal":
			{
				if (npc.getVariables().getInt("OWNER_OID", 0) != player.getObjectId())
				{
					player.sendMessage("You can't protect this Sacred Fire.");
				}
				else if (npc.getVariables().hasVariable("PROTECTED") && (npc.getVariables().getBoolean("PROTECTED", false) == true))
				{
					htmltext = "34658-31.html"; // Already used protect fire.
				}
				else if (takeItems(player, FLAME_GUARDIAN_SCROLL, 1))
				{
					npc.setDisplayEffect(SPAWNED);
					npc.getVariables().set("PROTECTED", true);
					FLAME_GUARDIAN_ENERGY_BUFF.getSkill().applyEffects(npc, npc);
					htmltext = "34658-30.html";
				}
				else
				{
					player.sendMessage("You don't have the required items.");
				}
				break;
			}
		}
		return htmltext;
	}
	
	@Override
	public String onFirstTalk(Npc npc, Player player)
	{
		return npc.getId() + ".html";
	}
	
	@Override
	public String onExitZone(Creature creature, ZoneType zone)
	{
		final Player player = creature.getActingPlayer();
		if (player != null)
		{
			// Decay spawned sacred fires.
			for (int i = Config.CONQUEST_MAX_SACRED_FIRE_SLOTS_COUNT; i >= 1; i--)
			{
				final WorldObject sacredFire = World.getInstance().findObject(player.getVariables().getInt("SACRED_FIRE_SLOT_" + i + "_OID", 0));
				if (sacredFire != null)
				{
					((Npc) sacredFire).setDisplayEffect(DESTROYED);
					((Npc) sacredFire).decayMe();
					if ((_scheduledSuccessfulDefenseRewardTask != null) && !_scheduledSuccessfulDefenseRewardTask.isCancelled() && !_scheduledSuccessfulDefenseRewardTask.isDone())
					{
						_scheduledSuccessfulDefenseRewardTask.cancel(false);
						_scheduledSuccessfulDefenseRewardTask = null;
					}
					if ((_scheduledSacredFireCountSetupTask != null) && !_scheduledSacredFireCountSetupTask.isCancelled() && !_scheduledSacredFireCountSetupTask.isDone())
					{
						_scheduledSacredFireCountSetupTask.cancel(false);
						_scheduledSacredFireCountSetupTask = null;
					}
					
					// Check if the player exits the zone and remove Sacred Fire progress
					player.getVariables().remove("SACRED_FIRE_SLOT_" + i);
					player.getVariables().remove("SACRED_FIRE_SLOT_" + i + "_SUMMON_TIME");
					player.getVariables().remove("SACRED_FIRE_SLOT_" + i + "_OID");
					player.getVariables().remove(PlayerVariables.CONQUEST_SACRED_FIRE_SLOT_COUNT);
					player.getVariables().storeMe();
					if (DEBUG)
					{
						LOGGER.info("Sacred Fire Decay. -> " + player.getObjectId() + " - " + "SACRED_FIRE_SLOT_" + i);
					}
				}
			}
			if (DEBUG)
			{
				LOGGER.info("Sacred Fire Vars have been reset, since you exit the conquest zone");
			}
		}
		return super.onEnterZone(creature, zone);
	}
	
	@RegisterEvent(EventType.ON_ITEM_USE)
	@RegisterType(ListenerRegisterType.ITEM)
	@Id(SACRED_FIRE_SUMMON_SCROLL)
	public void onItemUse(OnItemUse event)
	{
		final Player player = event.getPlayer();
		if (!GlobalVariablesManager.getInstance().getBoolean("CONQUEST_AVAILABLE", false))
		{
			player.sendMessage("You can't summon Sacred Fires if you're not in proper zone and conquest is not available.");
			player.getInventory().addItem("Sacred Fire Summon Scroll refund", SACRED_FIRE_SUMMON_SCROLL, 1, player, player);
			return;
		}
		
		spawnSacredFire(player);
	}
	
	private void spawnSacredFire(Player player)
	{
		// Sacred Fire can only be summoned in the common area of the Fire Source in Conquest's Fire Area (after entering the area your character will turn into a rabbit).
		// Check zone and transformation (rabbit transform id is 105).
		if (FIRE_SOURCE_COMMON_AREA_ZONE.isInsideZone(player) && (player.getTransformationId() == 105))
		{
			if (player.getVariables().getInt(PlayerVariables.CONQUEST_SACRED_FIRE_SLOT_COUNT, 0) < Config.CONQUEST_MAX_SACRED_FIRE_SLOTS_COUNT)
			{
				// Get Sacred Fire slot.
				int sacredFireSlot = Config.CONQUEST_MAX_SACRED_FIRE_SLOTS_COUNT;
				for (int i = sacredFireSlot; i >= 1; i--)
				{
					if ((player.getVariables().getInt("SACRED_FIRE_SLOT_" + i, 0) == 0) || (player.getVariables().getInt("SACRED_FIRE_SLOT_" + i, 0) == 3))
					{
						sacredFireSlot = i;
						if (DEBUG)
						{
							LOGGER.info("Sacred Fire Slot Calculation. -> " + player.getObjectId() + " - " + "SACRED_FIRE_SLOT_" + i);
						}
					}
				}
				
				final Npc sacredFire = addSpawn(SACRED_FIRE, player, true, LIFETIME, false, player.getInstanceId());
				sacredFire.setDisplayEffect(SPAWNED);
				sacredFire.getVariables().set("OWNER_OID", player.getObjectId());
				sacredFire.getVariables().set("SLOT", sacredFireSlot);
				sacredFire.getVariables().set("SUMMON_TIME", System.currentTimeMillis());
				
				checkSummonSacredFireListener(player);
				
				// Update owner current Sacred Fires count.
				player.getVariables().set(PlayerVariables.CONQUEST_SACRED_FIRE_SLOT_COUNT, player.getVariables().getInt(PlayerVariables.CONQUEST_SACRED_FIRE_SLOT_COUNT, 0) + 1);
				
				// Set Sacred Fire state to 1 (summoning).
				player.getVariables().set("SACRED_FIRE_SLOT_" + sacredFire.getVariables().getInt("SLOT", 0), SUMMONING_STATE);
				player.getVariables().set("SACRED_FIRE_SLOT_" + sacredFire.getVariables().getInt("SLOT", 0) + "_OID", sacredFire.getObjectId());
				player.getVariables().set("SACRED_FIRE_SLOT_" + sacredFire.getVariables().getInt("SLOT", 0) + "_SUMMON_TIME", sacredFire.getVariables().getLong("SUMMON_TIME", 0));
				player.getVariables().storeMe();
				
				// Tasks
				_scheduledSuccessfulDefenseRewardTask = ThreadPool.schedule(() ->
				{
					// Rewards
					if (player.isInventoryUnder90(false))
					{
						// Set Sacred Fire state to 2 (summoning flame is finished get reward).
						player.getVariables().set("SACRED_FIRE_SLOT_" + sacredFire.getVariables().getInt("SLOT", 0), REWARDING_STATE);
						player.getVariables().storeMe();
						final SystemMessage sm = new SystemMessage(SystemMessageId.FIRE_SOURCE_POINTS_S1_PERSONAL_CONQUEST_POINTS_S2_SERVER_CONQUEST_POINTS_S3);
						
						// Points rewards.
						if (Config.CONQUEST_SACRED_FIRE_REWARD_FIRE_SOURCE_POINTS != 0)
						{
							player.addItem("CONQUEST_SACRED_FIRE_REWARDS", SEALED_FIRE_SOURCE, Config.CONQUEST_SACRED_FIRE_REWARD_FIRE_SOURCE_POINTS, player, false);
						}
						
						// Message update.
						sm.addInt(Config.CONQUEST_SACRED_FIRE_REWARD_FIRE_SOURCE_POINTS != 0 ? Config.CONQUEST_SACRED_FIRE_REWARD_FIRE_SOURCE_POINTS : 0);
						if (Config.CONQUEST_SACRED_FIRE_REWARD_PERSONAL_POINTS != 0)
						{
							player.getVariables().set(PlayerVariables.CONQUEST_PERSONAL_POINTS, player.getVariables().getLong(PlayerVariables.CONQUEST_PERSONAL_POINTS) + Config.CONQUEST_SACRED_FIRE_REWARD_PERSONAL_POINTS);
						}
						
						// Message update.
						sm.addLong(Config.CONQUEST_SACRED_FIRE_REWARD_PERSONAL_POINTS != 0 ? Config.CONQUEST_SACRED_FIRE_REWARD_PERSONAL_POINTS : 0);
						if (Config.CONQUEST_SACRED_FIRE_REWARD_SERVER_POINTS != 0)
						{
							GlobalVariablesManager.getInstance().set("CONQUEST_SERVER_POINTS", GlobalVariablesManager.getInstance().getLong("CONQUEST_SERVER_POINTS", 0) + Config.CONQUEST_SACRED_FIRE_REWARD_SERVER_POINTS);
						}
						
						// Message update.
						sm.addLong(Config.CONQUEST_SACRED_FIRE_REWARD_SERVER_POINTS != 0 ? Config.CONQUEST_SACRED_FIRE_REWARD_SERVER_POINTS : 0);
						player.sendPacket(sm);
						
						// Common items rewards.
						if (Config.CONQUEST_SACRED_FIRE_REWARDS.size() > 0)
						{
							for (ItemHolder reward : Config.CONQUEST_SACRED_FIRE_REWARDS)
							{
								player.addItem("CONQUEST_SACRED_FIRE_REWARDS", reward.getId(), reward.getCount(), player, true);
							}
						}
					}
					else
					{
						// Set Sacred Fire state to 3 (flame is extinguished).
						player.getVariables().set("SACRED_FIRE_SLOT_" + sacredFire.getVariables().getInt("SLOT", 0), EXTINGUISHED_STATE);
						player.getVariables().storeMe();
						player.sendPacket(SystemMessageId.YOUR_INVENTORY_S_WEIGHT_SLOT_LIMIT_HAS_BEEN_EXCEEDED_SO_YOU_CAN_T_RECEIVE_THE_REWARD_PLEASE_FREE_UP_SOME_SPACE_AND_TRY_AGAIN);
					}
					if (DEBUG)
					{
						LOGGER.info("Sacred Fire Reward Task. -> " + player.getObjectId() + " - " + "SACRED_FIRE_SLOT_" + sacredFire.getVariables().getInt("SLOT", 0));
					}
					
					_scheduledSacredFireCountSetupTask = ThreadPool.schedule(() ->
					{
						// Update owner current Sacred Fires count.
						player.getVariables().set(PlayerVariables.CONQUEST_SACRED_FIRE_SLOT_COUNT, player.getVariables().getInt(PlayerVariables.CONQUEST_SACRED_FIRE_SLOT_COUNT, 0) - 1);
						
						// Remove previous vars.
						player.getVariables().remove("SACRED_FIRE_SLOT_" + sacredFire.getVariables().getInt("SLOT", 0));
						player.getVariables().remove("SACRED_FIRE_SLOT_" + sacredFire.getVariables().getInt("SLOT", 0) + "_SUMMON_TIME");
						player.getVariables().remove("SACRED_FIRE_SLOT_" + sacredFire.getVariables().getInt("SLOT", 0) + "_OID");
						player.getVariables().storeMe();
						
						if (DEBUG)
						{
							LOGGER.info("Sacred Fire Count Setup Task. -> " + player.getObjectId() + " - " + "SACRED_FIRE_SLOT_" + sacredFire.getVariables().getInt("SLOT", 0));
						}
					}, SACRED_FIRE_COUNT_UPDATE_TIME);
				}, LIFETIME);
			}
			else
			{
				player.sendMessage("You already have max number of Sacred Fires.");
				player.getInventory().addItem("Sacred Fire Summon Scroll refund", SACRED_FIRE_SUMMON_SCROLL, 1, player, player);
			}
		}
		else
		{
			player.sendMessage("You can't summon Sacred Fires here.");
			player.getInventory().addItem("Sacred Fire Summon Scroll refund", SACRED_FIRE_SUMMON_SCROLL, 1, player, player);
		}
	}
	
	private void removeSacredFireVars()
	{
		try (Connection con = DatabaseFactory.getConnection();
			PreparedStatement st = con.prepareStatement(DELETE_QUERY))
		{
			st.setString(1, "SACRED_FIRE_SLOT_" + "%");
			st.execute();
			LOGGER.info("Sacred Fire Vars have been reset.");
		}
		catch (Exception e)
		{
			LOGGER.log(Level.WARNING, getClass().getSimpleName() + ": Couldn't delete Sacred Fire variables.", e);
		}
	}
	
	@RegisterEvent(EventType.ON_SERVER_START)
	@RegisterType(ListenerRegisterType.GLOBAL)
	public void onServerStart(OnServerStart event)
	{
		removeSacredFireVars();
	}
	
	private void checkSummonSacredFireListener(Player player)
	{
		// Notify to scripts.
		if (EventDispatcher.getInstance().hasListener(EventType.ON_PLAYER_SUMMON_SACRED_FIRE))
		{
			EventDispatcher.getInstance().notifyEventAsync(new OnPlayerSummonSacredFire(player, SACRED_FIRE));
		}
	}
	
	public static void main(String[] args)
	{
		new SacredFire();
	}
}
