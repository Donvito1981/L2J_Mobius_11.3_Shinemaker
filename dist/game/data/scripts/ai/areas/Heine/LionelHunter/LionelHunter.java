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
package ai.areas.Heine.LionelHunter;

import org.l2jmobius.gameserver.data.xml.MultisellData;
import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.quest.QuestState;

import ai.AbstractNpcAI;
import quests.Q10591_NobleMaterial.Q10591_NobleMaterial;
import quests.Q10817_ExaltedOneWhoOvercomesTheLimit.Q10817_ExaltedOneWhoOvercomesTheLimit;

/**
 * Lionel Hunter AI.
 * @author Sero, CostyKiller
 */
public class LionelHunter extends AbstractNpcAI
{
	// NPC
	private static final int LIONEL_HUNTER_HEINE = 33907;
	// Multisells
	private static final int EXALTED_SHIELD_SIGIL = 3390708;
	private static final int EXALTED_WEAPONS = 3390707;
	// Items
	private static final int COMMON_BOX_PHYSICAL = 81207;
	private static final int COMMON_BOX_MAGIC = 81208;
	private static final int SPECIAL_BOX_PHYSICAL = 81209;
	private static final int SPECIAL_BOX_MAGIC = 81210;
	
	private LionelHunter()
	{
		addStartNpc(LIONEL_HUNTER_HEINE);
		addTalkId(LIONEL_HUNTER_HEINE);
		addFirstTalkId(LIONEL_HUNTER_HEINE);
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, Player player)
	{
		String htmltext = null;
		final QuestState qs2 = player.getQuestState(Q10817_ExaltedOneWhoOvercomesTheLimit.class.getSimpleName());
		final QuestState qs7 = player.getQuestState(Q10591_NobleMaterial.class.getSimpleName());
		switch (event)
		{
			case "33907.htm":
			case "33907-01.htm":
			case "33907-02.htm":
			case "33907-04.htm":
			case "33907-06.htm":
			case "33907-07.htm":
			case "33907-08.htm":
			{
				htmltext = event;
				break;
			}
			case "getSupplyBox":
			{
				if (((qs7 != null) && !qs7.isCompleted()) || (qs7 == null))
				{
					htmltext = "33907-06-no.html";
				}
				else if (qs7.isCompleted())
				{
					if (player.getVariables().getBoolean("LIONEL_REWARD_RECEIVED", false) == true)
					{
						htmltext = "33907-06-received.html";
					}
					else
					{
						if (player.isMageClass())
						{
							giveItems(player, COMMON_BOX_MAGIC, 1);
							giveItems(player, SPECIAL_BOX_MAGIC, 1);
						}
						else
						{
							giveItems(player, COMMON_BOX_PHYSICAL, 1);
							giveItems(player, SPECIAL_BOX_PHYSICAL, 1);
						}
						player.getVariables().set("LIONEL_REWARD_RECEIVED", true);
						player.getVariables().storeMe();
					}
				}
				break;
			}
			case "buyShieldSigil":
			{
				if ((qs7 != null) && qs7.isCompleted())
				{
					MultisellData.getInstance().separateAndSend(EXALTED_SHIELD_SIGIL, player, null, false);
				}
				else
				{
					htmltext = "33907-07-no.html";
				}
				break;
			}
			case "buyWeapons":
			{
				if ((qs2 != null) && qs2.isCompleted())
				{
					MultisellData.getInstance().separateAndSend(EXALTED_WEAPONS, player, null, false);
				}
				else
				{
					htmltext = "33907-08-no.html";
				}
				break;
			}
		}
		return htmltext;
	}
	
	@Override
	public String onFirstTalk(Npc npc, Player player)
	{
		return "33907.htm";
	}
	
	public static void main(String[] args)
	{
		new LionelHunter();
	}
}
