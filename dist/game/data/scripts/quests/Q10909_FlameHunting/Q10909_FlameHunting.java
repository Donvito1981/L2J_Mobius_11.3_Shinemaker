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
package quests.Q10909_FlameHunting;

import java.util.HashSet;
import java.util.Set;

import org.l2jmobius.commons.util.CommonUtil;
import org.l2jmobius.gameserver.enums.QuestSound;
import org.l2jmobius.gameserver.model.Party;
import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.holders.NpcLogListHolder;
import org.l2jmobius.gameserver.model.quest.Quest;
import org.l2jmobius.gameserver.model.quest.QuestState;
import org.l2jmobius.gameserver.model.quest.State;
import org.l2jmobius.gameserver.network.NpcStringId;

import quests.Q10904_JourneyToTheConquestWorld.Q10904_JourneyToTheConquestWorld;

/**
 * @author Mobius
 */
public class Q10909_FlameHunting extends Quest
{
	
	// NPC
	private static final int CASGARD = 34668;
	
	// Monsters
	private static final int[] MONSTERS_FIERY_FIELD =
	{
		// Fiery Field (no PvP) (Lv. 116)
		27801, // Seo Agel
		27802, // Craigo Agel
		27803, // Catshi Agel Wizard
		27804, // Catshi Agel Archer
		27805, // Catshi Agel Warrior
		27806, // Catshi Agel Knight
	};
	private static final int[] MONSTERS_GARDEN_OF_FLAMES =
	{
		// Garden of Flames (no PvP) (Lv. 120)
		27794, // Delia Gof
		27795, // Aselon Gof
		27796, // Retel Gof
		27797, // Catshi Gof Wizard
		27798, // Catshi Gof Archer
		27799, // Catshi Gof Warrior
	};
	private static final int[] MONSTERS_VITA =
	{
		// Vita Area 1 (Lv. 124)
		27808, // Renard Vita
		27810, // Catshi Vita Wizard
		27811, // Catshi Vita Archer
		27812, // Catshi Vita Warrior
		27813, // Catshi Vita Knight
		27814, // Vita Sorceress
		27815, // Vita Seeker
		27816, // Vita Reaper
	};
	private static final int[] MONSTERS_IGNIS =
	{
		// Ignis Area 1 (Lv. 128)
		27817, // Beor Ignis
		27819, // Catshi Ignis Wizard
		27820, // Catshi Ignis Archer
		27821, // Ignis Sorceress
		27822, // Ignis Seeker
		27823, // Ignis Reaper
		27824, // Ignis Swordsman
		27825, // Ignis Guardian
	};
	// Misc
	private static final int MIN_LEVEL = 110;
	private static final String KILL_COUNT_VAR_FIERY_FIELD = "KillCountFF";
	private static final String KILL_COUNT_VAR_GARDEN_OF_FLAMES = "KillCountGoF";
	private static final String KILL_COUNT_VAR_VITA = "KillCountVita";
	private static final String KILL_COUNT_VAR_IGNIS = "KillCountIgnis";
	
	// Monsters Kill Target
	private static final int MAX_KILLED_MOBS_FIERY_FIELD = 600;
	private static final int MAX_KILLED_MOBS_GARDEN_OF_FLAMES = 600;
	private static final int MAX_KILLED_MOBS_VITA = 600;
	private static final int MAX_KILLED_MOBS_IGNIS = 600;
	
	public Q10909_FlameHunting()
	{
		super(10909);
		addStartNpc(CASGARD);
		addTalkId(CASGARD);
		addKillId(MONSTERS_FIERY_FIELD);
		addKillId(MONSTERS_GARDEN_OF_FLAMES);
		addKillId(MONSTERS_VITA);
		addKillId(MONSTERS_IGNIS);
		addCondCompletedQuest(Q10904_JourneyToTheConquestWorld.class.getSimpleName(), "34668-00.html");
		addCondMinLevel(MIN_LEVEL, "34668-00.html");
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, Player player)
	{
		final QuestState qs = getQuestState(player, false);
		if (qs == null)
		{
			return null;
		}
		
		String htmltext = null;
		switch (event)
		{
			case "34668-02.html":
			{
				if ((player.getLevel() >= MIN_LEVEL))
				{
					qs.startQuest();
					qs.setMemoState(1);
					htmltext = event;
				}
				break;
			}
			case "34668-05.html":
			{
				if (qs.isCond(2))
				{
					addExpAndSp(player, 26617381116L, 23955643);
					qs.exitQuest(false, true);
					htmltext = event;
				}
				else
				{
					htmltext = "34668-04.html";
				}
				break;
			}
		}
		return htmltext;
	}
	
	@Override
	public String onTalk(Npc npc, Player player)
	{
		final QuestState qs = getQuestState(player, true);
		String htmltext = getNoQuestMsg(player);
		switch (qs.getState())
		{
			case State.CREATED:
			{
				if (npc.getId() == CASGARD)
				{
					htmltext = "34668-01.htm";
				}
				break;
			}
			case State.STARTED:
			{
				switch (npc.getId())
				{
					case CASGARD:
					{
						if ((qs.isCond(1) & qs.isMemoState(1)) || qs.isCond(2))
						{
							htmltext = "34668-03.htm";
						}
						break;
					}
				}
				break;
			}
			case State.COMPLETED:
			{
				htmltext = getAlreadyCompletedMsg(player);
				break;
			}
		}
		return htmltext;
	}
	
	@Override
	public String onKill(Npc npc, Player killer, boolean isSummon)
	{
		final Party party = killer.getParty();
		if (party != null)
		{
			for (Player member : party.getMembers())
			{
				final QuestState qs = getQuestState(member, false);
				if (qs != null)
				{
					processKill(npc, member);
				}
			}
		}
		else
		{
			processKill(npc, killer);
		}
		return super.onKill(npc, killer, isSummon);
	}
	
	private void processKill(Npc npc, Player killer)
	{
		final QuestState qs = getRandomPartyMemberState(killer, 1, 3, npc);
		if (qs != null)
		{
			final Player player = qs.getPlayer();
			
			int killCountFieryField = qs.getInt(KILL_COUNT_VAR_FIERY_FIELD);
			int killCountGardenOfFlames = qs.getInt(KILL_COUNT_VAR_GARDEN_OF_FLAMES);
			int killCountVita = qs.getInt(KILL_COUNT_VAR_VITA);
			int killCountIgnis = qs.getInt(KILL_COUNT_VAR_IGNIS);
			
			if (CommonUtil.contains(MONSTERS_FIERY_FIELD, npc.getId()))
			{
				if (killCountFieryField < MAX_KILLED_MOBS_FIERY_FIELD)
				{
					qs.set(KILL_COUNT_VAR_FIERY_FIELD, killCountFieryField + 1);
					playSound(player, QuestSound.ITEMSOUND_QUEST_ITEMGET);
					checkKillProgress(killer);
				}
			}
			if (CommonUtil.contains(MONSTERS_GARDEN_OF_FLAMES, npc.getId()))
			{
				if (killCountGardenOfFlames < MAX_KILLED_MOBS_GARDEN_OF_FLAMES)
				{
					qs.set(KILL_COUNT_VAR_GARDEN_OF_FLAMES, killCountGardenOfFlames + 1);
					playSound(player, QuestSound.ITEMSOUND_QUEST_ITEMGET);
					checkKillProgress(killer);
				}
			}
			if (CommonUtil.contains(MONSTERS_VITA, npc.getId()))
			{
				if (killCountVita < MAX_KILLED_MOBS_VITA)
				{
					qs.set(KILL_COUNT_VAR_VITA, killCountVita + 1);
					playSound(player, QuestSound.ITEMSOUND_QUEST_ITEMGET);
					checkKillProgress(killer);
				}
			}
			if (CommonUtil.contains(MONSTERS_IGNIS, npc.getId()))
			{
				if (killCountIgnis < MAX_KILLED_MOBS_IGNIS)
				{
					qs.set(KILL_COUNT_VAR_IGNIS, killCountIgnis + 1);
					playSound(player, QuestSound.ITEMSOUND_QUEST_ITEMGET);
					checkKillProgress(killer);
				}
			}
			sendNpcLogList(player);
		}
	}
	
	private void checkKillProgress(Player player)
	{
		final QuestState qs = getQuestState(player, false);
		if ((qs.getInt(KILL_COUNT_VAR_FIERY_FIELD) >= MAX_KILLED_MOBS_FIERY_FIELD) && (qs.getInt(KILL_COUNT_VAR_GARDEN_OF_FLAMES) >= MAX_KILLED_MOBS_GARDEN_OF_FLAMES) && (qs.getInt(KILL_COUNT_VAR_VITA) >= MAX_KILLED_MOBS_VITA) && (qs.getInt(KILL_COUNT_VAR_IGNIS) >= MAX_KILLED_MOBS_IGNIS))
		{
			qs.setCond(2, true);
			playSound(player, QuestSound.ITEMSOUND_QUEST_MIDDLE);
		}
	}
	
	@Override
	public Set<NpcLogListHolder> getNpcLogList(Player player)
	{
		final QuestState qs = getQuestState(player, false);
		if ((qs != null) && qs.isCond(1))
		{
			final Set<NpcLogListHolder> holder = new HashSet<>();
			holder.add(new NpcLogListHolder(NpcStringId.MONSTERS_IN_THE_FIERY_FIELD.getId(), true, qs.getInt(KILL_COUNT_VAR_FIERY_FIELD)));
			holder.add(new NpcLogListHolder(NpcStringId.MONSTERS_IN_THE_GARDEN_OF_FLAMES.getId(), true, qs.getInt(KILL_COUNT_VAR_GARDEN_OF_FLAMES)));
			holder.add(new NpcLogListHolder(NpcStringId.MONSTERS_IN_THE_VITA_ZONE.getId(), true, qs.getInt(KILL_COUNT_VAR_VITA)));
			holder.add(new NpcLogListHolder(NpcStringId.MONSTERS_IN_THE_IGNIS_ZONE.getId(), true, qs.getInt(KILL_COUNT_VAR_IGNIS)));
			return holder;
		}
		return super.getNpcLogList(player);
	}
}