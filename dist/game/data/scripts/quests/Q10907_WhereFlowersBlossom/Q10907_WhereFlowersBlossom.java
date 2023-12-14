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
package quests.Q10907_WhereFlowersBlossom;

import org.l2jmobius.Config;
import org.l2jmobius.gameserver.enums.QuestSound;
import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.quest.Quest;
import org.l2jmobius.gameserver.model.quest.QuestState;
import org.l2jmobius.gameserver.model.quest.State;

import quests.Q10904_JourneyToTheConquestWorld.Q10904_JourneyToTheConquestWorld;

/**
 * @author CostyKiller
 */
public class Q10907_WhereFlowersBlossom extends Quest
{
	// NPC
	private static final int ENTROPY = 34599;
	// Monsters
	private static final int[] MONSTERS =
	{
		19818, // Soul Flower - Asa Area 1 (Lv. 116)
		19819, // Soul Flower - Asa Area 2 (Lv. 120)
		19820, // Soul Flower - Asa Area 3 (Lv. 124)
		19824, // Soul Flower - Anima Area 1 (Lv. 116)
		19825, // Soul Flower - Anima Area 2 (Lv. 120)
		19826, // Soul Flower - Anima Area 3 (Lv. 124)
		19821, // Soul Flower - Nox Area 1 (Lv. 116)
		19822, // Soul Flower - Nox Area 2 (Lv. 120)
		19823, // Soul Flower - Nox Area 3 (Lv. 124)
		19840, // Soul Flower - Vita (Lv. 124)
		19841, // Soul Flower - Ignis (Lv. 128)
	};
	// Item
	private static final int SOUL_FLOWER_PETAL = 82178;
	// Misc
	private static final int MIN_LEVEL = 110;
	private static final int SOUL_FLOWER_PETAL_NEEDED = 120;
	
	public Q10907_WhereFlowersBlossom()
	{
		super(10907);
		addStartNpc(ENTROPY);
		addTalkId(ENTROPY);
		addKillId(MONSTERS);
		addCondCompletedQuest(Q10904_JourneyToTheConquestWorld.class.getSimpleName(), "34599-00.html");
		addCondMinLevel(MIN_LEVEL, "34599-00.html");
		registerQuestItems(SOUL_FLOWER_PETAL);
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
			case "34599-02.html":
			{
				if ((player.getLevel() >= MIN_LEVEL))
				{
					qs.startQuest();
					qs.setMemoState(1);
					htmltext = event;
				}
				break;
			}
			case "34599-05.html":
			{
				if (qs.isCond(2))
				{
					takeItems(player, SOUL_FLOWER_PETAL, 120);
					addExpAndSp(player, 8872460372L, 7985214);
					qs.exitQuest(false, true);
					htmltext = event;
				}
				else
				{
					htmltext = "34599-04.html";
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
				if (npc.getId() == ENTROPY)
				{
					htmltext = "34599-01.htm";
				}
				break;
			}
			case State.STARTED:
			{
				switch (npc.getId())
				{
					case ENTROPY:
					{
						if ((qs.isCond(1) & qs.isMemoState(1)) || qs.isCond(2))
						{
							htmltext = "34599-03.htm";
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
	public String onKill(Npc npc, Player player, boolean isSummon)
	{
		executeForEachPlayer(player, npc, isSummon, true, false);
		return super.onKill(npc, player, isSummon);
	}
	
	@Override
	public void actionForEachPlayer(Player player, Npc npc, boolean isSummon)
	{
		final QuestState qs = getQuestState(player, false);
		if ((qs != null) && qs.isCond(1) && player.isInsideRadius3D(npc, Config.ALT_PARTY_RANGE))
		{
			if (getQuestItemsCount(player, SOUL_FLOWER_PETAL) < SOUL_FLOWER_PETAL_NEEDED)
			{
				giveItems(player, SOUL_FLOWER_PETAL, 1);
				playSound(player, QuestSound.ITEMSOUND_QUEST_ITEMGET);
			}
			if (getQuestItemsCount(player, SOUL_FLOWER_PETAL) >= SOUL_FLOWER_PETAL_NEEDED)
			{
				qs.setCond(2, true);
			}
		}
	}
}
