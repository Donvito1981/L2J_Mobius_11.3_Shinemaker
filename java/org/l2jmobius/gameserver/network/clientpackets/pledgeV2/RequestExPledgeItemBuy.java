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
package org.l2jmobius.gameserver.network.clientpackets.pledgeV2;

import org.l2jmobius.commons.network.ReadablePacket;
import org.l2jmobius.gameserver.data.xml.ClanShopData;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.holders.ClanShopProductHolder;
import org.l2jmobius.gameserver.network.GameClient;
import org.l2jmobius.gameserver.network.clientpackets.ClientPacket;
import org.l2jmobius.gameserver.network.serverpackets.pledgeV2.ExPledgeItemBuy;

/**
 * @author Mobius
 */
public class RequestExPledgeItemBuy implements ClientPacket
{
	private int _itemId;
	private int _count;
	
	@Override
	public void read(ReadablePacket packet)
	{
		_itemId = packet.readInt();
		_count = packet.readInt();
	}
	
	@Override
	public void run(GameClient client)
	{
		if ((_count < 1) || (_count > 10000))
		{
			client.sendPacket(new ExPledgeItemBuy(1));
			return;
		}
		
		final Player player = client.getPlayer();
		if ((player == null) || (player.getClan() == null))
		{
			client.sendPacket(new ExPledgeItemBuy(1));
			return;
		}
		
		final ClanShopProductHolder product = ClanShopData.getInstance().getProduct(_itemId);
		if (product == null)
		{
			client.sendPacket(new ExPledgeItemBuy(1));
			return;
		}
		
		if (player.getClan().getLevel() < product.getClanLevel())
		{
			client.sendPacket(new ExPledgeItemBuy(2));
			return;
		}
		
		final long slots = product.getTradeItem().getItem().isStackable() ? 1 : product.getTradeItem().getCount() * _count;
		final long weight = product.getTradeItem().getItem().getWeight() * product.getTradeItem().getCount() * _count;
		if (!player.getInventory().validateWeight(weight) || !player.getInventory().validateCapacity(slots))
		{
			client.sendPacket(new ExPledgeItemBuy(3));
			return;
		}
		
		if ((player.getAdena() < (product.getAdena() * _count)) || (player.getFame() < (product.getFame() * _count)))
		{
			client.sendPacket(new ExPledgeItemBuy(3));
			return;
		}
		
		if (product.getAdena() > 0)
		{
			player.reduceAdena("ClanShop", product.getAdena() * _count, player, true);
		}
		if (product.getFame() > 0)
		{
			player.setFame(player.getFame() - (product.getFame() * _count));
			player.broadcastUserInfo();
		}
		
		player.addItem("ClanShop", _itemId, product.getCount() * _count, player, true);
		client.sendPacket(new ExPledgeItemBuy(0));
	}
}