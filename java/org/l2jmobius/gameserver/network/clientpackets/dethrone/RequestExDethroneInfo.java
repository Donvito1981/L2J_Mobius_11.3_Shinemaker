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
package org.l2jmobius.gameserver.network.clientpackets.dethrone;

import org.l2jmobius.gameserver.instancemanager.GlobalVariablesManager;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.variables.PlayerVariables;
import org.l2jmobius.gameserver.network.GameClient;
import org.l2jmobius.gameserver.network.clientpackets.ClientPacket;
import org.l2jmobius.gameserver.network.serverpackets.dethrone.ExDethroneInfo;

/**
 * @author Lefteris
 */
public class RequestExDethroneInfo implements ClientPacket
{
	@Override
	public void run(GameClient client)
	{
		final Player player = client.getPlayer();
		if (player == null)
		{
			return;
		}
		
		String name = "";
		if (player.getVariables().hasVariable(PlayerVariables.CONQUEST_NAME))
		{
			name = player.getVariables().getString(PlayerVariables.CONQUEST_NAME);
		}
		
		final int attackPoint = player.getVariables().getInt(PlayerVariables.CONQUEST_ATTACK_POINTS, 100);
		final int life = player.getVariables().getInt(PlayerVariables.CONQUEST_LIFE_POINTS, 20);
		final long personalDethronePoint = player.getVariables().getLong(PlayerVariables.CONQUEST_PERSONAL_POINTS, 0);
		final long serverDethronePoint = GlobalVariablesManager.getInstance().getLong("CONQUEST_SERVER_POINTS", 0);
		player.sendPacket(new ExDethroneInfo(player, name, attackPoint, life, personalDethronePoint, serverDethronePoint));
	}
}
