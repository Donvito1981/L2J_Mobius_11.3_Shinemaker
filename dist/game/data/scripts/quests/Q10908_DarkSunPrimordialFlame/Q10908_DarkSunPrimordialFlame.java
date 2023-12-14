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
package quests.Q10908_DarkSunPrimordialFlame;

import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.quest.Quest;
import org.l2jmobius.gameserver.model.quest.QuestState;
import org.l2jmobius.gameserver.model.quest.State;

import quests.Q10904_JourneyToTheConquestWorld.Q10904_JourneyToTheConquestWorld;

/**
 * @author CostyKiller
 */
public class Q10908_DarkSunPrimordialFlame extends Quest
{
	// NPCs
	private static final int CASGARD = 34668;
	private static final int CHLOE = 34600;
	// Items
	private static final int SACRED_FIRE_SUMMON_SCROLL = 82614;
	private static final int CHLOE_INVITATION = 82661;
	// Misc
	private static final int MIN_LEVEL = 110;
	
	public Q10908_DarkSunPrimordialFlame()
	{
		super(10908);
		addStartNpc(CHLOE);
		addTalkId(CHLOE, CASGARD);
		addCondCompletedQuest(Q10904_JourneyToTheConquestWorld.class.getSimpleName(), "34600-00.html");
		addCondMinLevel(MIN_LEVEL, "34600-00.html");
		registerQuestItems(CHLOE_INVITATION);
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
			case "34600-02.html":
			{
				if ((player.getLevel() >= MIN_LEVEL))
				{
					qs.startQuest();
					giveItems(player, CHLOE_INVITATION, 1);
					htmltext = event;
				}
				break;
			}
			case "34668-02.html":
			{
				if (qs.isCond(1) && (hasQuestItems(player, CHLOE_INVITATION)))
				{
					takeItems(player, -1, CHLOE_INVITATION);
					qs.setCond(2);
					htmltext = event;
				}
				else
				{
					htmltext = "34668-00.html";
				}
				break;
			}
			case "34668-04.html":
			{
				if (qs.isCond(2))
				{
					giveItems(player, SACRED_FIRE_SUMMON_SCROLL, 1);
					qs.exitQuest(false, true);
					htmltext = event;
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
				if (npc.getId() == CHLOE)
				{
					htmltext = "34600-01.htm";
				}
				break;
			}
			case State.STARTED:
			{
				switch (npc.getId())
				{
					case CHLOE:
					{
						if (qs.isCond(1))
						{
							htmltext = "34600-02.html";
						}
						break;
					}
					case CASGARD:
					{
						if (qs.isCond(1))
						{
							htmltext = "34668-01.htm";
						}
						else if (qs.isCond(2))
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
}