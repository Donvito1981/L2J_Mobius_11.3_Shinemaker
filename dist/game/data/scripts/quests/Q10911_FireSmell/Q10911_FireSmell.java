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
package quests.Q10911_FireSmell;

import org.l2jmobius.gameserver.enums.QuestSound;
import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.events.Containers;
import org.l2jmobius.gameserver.model.events.EventType;
import org.l2jmobius.gameserver.model.events.ListenerRegisterType;
import org.l2jmobius.gameserver.model.events.annotations.Id;
import org.l2jmobius.gameserver.model.events.annotations.RegisterEvent;
import org.l2jmobius.gameserver.model.events.annotations.RegisterType;
import org.l2jmobius.gameserver.model.events.impl.conquest.OnConquestFlowerCollect;
import org.l2jmobius.gameserver.model.events.listeners.ConsumerEventListener;
import org.l2jmobius.gameserver.model.quest.Quest;
import org.l2jmobius.gameserver.model.quest.QuestState;
import org.l2jmobius.gameserver.model.quest.State;

import quests.Q10904_JourneyToTheConquestWorld.Q10904_JourneyToTheConquestWorld;

/**
 * @author CostyKiller
 */
public class Q10911_FireSmell extends Quest
{
	// NPCs
	private static final int CASGARD = 34668;
	private static final int ISHRIN = 34669;
	// Flowers
	private static final int FIRE_FLOWER = 34655;
	private static final int LIFE_FLOWER = 34656;
	private static final int POWER_FLOWER = 34657;
	// Items
	private static final int BRIGHT_SCARLET_PETAL = 82662;
	private static final int ARDENT_SCARLET_PETAL = 82663;
	// Misc
	private static final int MIN_LEVEL = 110;
	private static final int BRIGHT_PETALS_NEEDED = 30;
	private static final int ARDENT_PETALS_NEEDED = 30;
	
	public Q10911_FireSmell()
	{
		super(10911);
		addStartNpc(CASGARD);
		addTalkId(CASGARD, ISHRIN);
		addCondCompletedQuest(Q10904_JourneyToTheConquestWorld.class.getSimpleName(), "34668-00.html");
		addCondMinLevel(MIN_LEVEL, "34668-00.html");
		registerQuestItems(BRIGHT_SCARLET_PETAL, ARDENT_SCARLET_PETAL);
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
					htmltext = event;
					Containers.Global().addListener(new ConsumerEventListener(player, EventType.ON_CONQUEST_FLOWER_COLLECT, (OnConquestFlowerCollect eventListener) -> onConquestFlowerCollect(eventListener), this));
				}
				break;
			}
			case "34669-02.htm":
			case "34669-03.htm":
			case "34669-04.htm":
			{
				htmltext = event;
				break;
			}
			case "34669-05.html":
			{
				if (qs.isCond(1))
				{
					qs.setCond(2);
					htmltext = event;
				}
				break;
			}
			case "34669-08.html":
			{
				if (qs.isCond(3))
				{
					addExpAndSp(player, 8872460372L, 7985214);
					Containers.Global().removeListener(new ConsumerEventListener(player, EventType.ON_CONQUEST_FLOWER_COLLECT, (OnConquestFlowerCollect eventListener) -> onConquestFlowerCollect(eventListener), this));
					qs.exitQuest(false, true);
					htmltext = event;
				}
				else
				{
					htmltext = "34669-07.html";
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
						htmltext = "34668-02.html";
						break;
					}
					case ISHRIN:
					{
						if (qs.isCond(1))
						{
							htmltext = "34669-01.htm";
						}
						else if (qs.isCond(2) || qs.isCond(3))
						{
							htmltext = "34669-06.htm";
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
	
	@RegisterEvent(EventType.ON_CONQUEST_FLOWER_COLLECT)
	@RegisterType(ListenerRegisterType.GLOBAL_PLAYERS)
	@Id(FIRE_FLOWER)
	@Id(LIFE_FLOWER)
	@Id(POWER_FLOWER)
	private void onConquestFlowerCollect(OnConquestFlowerCollect event)
	{
		final Player player = event.getPlayer();
		if ((player.getLevel() < MIN_LEVEL))
		{
			return;
		}
		if (event.getNpcId() == FIRE_FLOWER)
		{
			if (getQuestItemsCount(player, ARDENT_SCARLET_PETAL) < ARDENT_PETALS_NEEDED)
			{
				giveItems(player, ARDENT_SCARLET_PETAL, 1);
				playSound(player, QuestSound.ITEMSOUND_QUEST_ITEMGET);
				checkQuestProgress(player);
			}
		}
		else if ((event.getNpcId() == LIFE_FLOWER) || (event.getNpcId() == POWER_FLOWER))
		{
			if (getQuestItemsCount(player, BRIGHT_SCARLET_PETAL) < BRIGHT_PETALS_NEEDED)
			{
				giveItems(player, BRIGHT_SCARLET_PETAL, 1);
				playSound(player, QuestSound.ITEMSOUND_QUEST_ITEMGET);
				checkQuestProgress(player);
			}
		}
	}
	
	private void checkQuestProgress(Player player)
	{
		final QuestState qs = getQuestState(player, false);
		if ((getQuestItemsCount(player, BRIGHT_SCARLET_PETAL) >= BRIGHT_PETALS_NEEDED) && (getQuestItemsCount(player, ARDENT_SCARLET_PETAL) >= ARDENT_PETALS_NEEDED))
		{
			qs.setCond(3, true);
			playSound(player, QuestSound.ITEMSOUND_QUEST_MIDDLE);
		}
	}
}