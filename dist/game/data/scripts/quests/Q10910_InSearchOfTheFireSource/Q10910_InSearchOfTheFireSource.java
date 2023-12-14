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
package quests.Q10910_InSearchOfTheFireSource;

import java.util.HashSet;
import java.util.Set;

import org.l2jmobius.gameserver.enums.QuestSound;
import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.events.Containers;
import org.l2jmobius.gameserver.model.events.EventType;
import org.l2jmobius.gameserver.model.events.ListenerRegisterType;
import org.l2jmobius.gameserver.model.events.annotations.Id;
import org.l2jmobius.gameserver.model.events.annotations.RegisterEvent;
import org.l2jmobius.gameserver.model.events.annotations.RegisterType;
import org.l2jmobius.gameserver.model.events.impl.creature.player.OnPlayerSummonSacredFire;
import org.l2jmobius.gameserver.model.events.listeners.ConsumerEventListener;
import org.l2jmobius.gameserver.model.holders.NpcLogListHolder;
import org.l2jmobius.gameserver.model.quest.Quest;
import org.l2jmobius.gameserver.model.quest.QuestState;
import org.l2jmobius.gameserver.model.quest.State;
import org.l2jmobius.gameserver.network.NpcStringId;

import quests.Q10904_JourneyToTheConquestWorld.Q10904_JourneyToTheConquestWorld;

/**
 * @author Mobius
 */
public class Q10910_InSearchOfTheFireSource extends Quest
{
	// NPCs
	private static final int ISHRIN = 34669;
	private static final int FIRE_POWER_ZEALOT = 34663;
	private static final int SACRED_FIRE = 34658;
	// Items
	private static final int SACRED_FIRE_SUMMON_SCROLL = 82614;
	// Misc
	private static final int MIN_LEVEL = 110;
	private static final String SUMMON_COUNT_VAR = "SummonCount";
	
	public Q10910_InSearchOfTheFireSource()
	{
		super(10910);
		addStartNpc(ISHRIN);
		addTalkId(ISHRIN, FIRE_POWER_ZEALOT);
		addCondCompletedQuest(Q10904_JourneyToTheConquestWorld.class.getSimpleName(), "34663-00.html");
		addCondMinLevel(MIN_LEVEL, "34663-00.html");
		addSummonSpawnId(SACRED_FIRE);
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
			case "34669-02.html":
			{
				if ((player.getLevel() >= MIN_LEVEL))
				{
					qs.startQuest();
					htmltext = event;
					Containers.Global().addListener(new ConsumerEventListener(player, EventType.ON_PLAYER_SUMMON_SACRED_FIRE, (OnPlayerSummonSacredFire eventListener) -> onPlayerSummonSacredFire(eventListener), this));
				}
				break;
			}
			case "34669-04.html":
			{
				if (qs.isCond(3))
				{
					addExpAndSp(player, 8872460372L, 7985214);
					qs.exitQuest(false, true);
					Containers.Global().removeListener(new ConsumerEventListener(player, EventType.ON_PLAYER_SUMMON_SACRED_FIRE, (OnPlayerSummonSacredFire eventListener) -> onPlayerSummonSacredFire(eventListener), this));
					htmltext = event;
				}
				break;
			}
			case "34663-02.htm":
			case "34663-03.htm":
			case "34663-04.htm":
			case "34663-05.htm":
			{
				htmltext = event;
				break;
			}
			case "34663-06.html":
			{
				if (qs.isCond(1))
				{
					qs.setCond(2);
					htmltext = event;
				}
				break;
			}
			case "34663-09.html":
			{
				if (qs.isCond(3))
				{
					htmltext = event;
				}
				else
				{
					htmltext = "34663-08.html";
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
				if (npc.getId() == ISHRIN)
				{
					htmltext = "34669-01.htm";
				}
				break;
			}
			case State.STARTED:
			{
				switch (npc.getId())
				{
					case ISHRIN:
					{
						if (qs.isCond(1) || qs.isCond(2))
						{
							htmltext = "34669-02.html";
						}
						else if (qs.isCond(3))
						{
							htmltext = "34669-03.htm";
						}
						break;
					}
					case FIRE_POWER_ZEALOT:
					{
						if (qs.isCond(1))
						{
							htmltext = "34663-01.htm";
						}
						else if (qs.isCond(2) || qs.isCond(3))
						{
							htmltext = "34663-07.htm";
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
	
	@RegisterEvent(EventType.ON_PLAYER_SUMMON_SACRED_FIRE)
	@RegisterType(ListenerRegisterType.ITEM)
	@Id(SACRED_FIRE_SUMMON_SCROLL)
	public void onPlayerSummonSacredFire(OnPlayerSummonSacredFire event)
	{
		if (event.getNpcId() != SACRED_FIRE)
		{
			return;
		}
		final Player player = event.getPlayer();
		if (player == null)
		{
			return;
		}
		final QuestState qs = getQuestState(player, true);
		int summonCount = qs.getInt(SUMMON_COUNT_VAR);
		if (summonCount < 5)
		{
			qs.set(SUMMON_COUNT_VAR, summonCount + 1);
			playSound(player, QuestSound.ITEMSOUND_QUEST_ITEMGET);
			sendNpcLogList(player);
		}
		if (summonCount >= 4)
		{
			qs.setCond(3, true);
		}
	}
	
	@Override
	public Set<NpcLogListHolder> getNpcLogList(Player player)
	{
		final QuestState qs = getQuestState(player, false);
		if ((qs != null) && qs.isCond(2))
		{
			final Set<NpcLogListHolder> holder = new HashSet<>();
			holder.add(new NpcLogListHolder(NpcStringId.SUMMON_THE_SACRED_FIRE.getId(), true, qs.getInt(SUMMON_COUNT_VAR)));
			return holder;
		}
		return super.getNpcLogList(player);
	}
}