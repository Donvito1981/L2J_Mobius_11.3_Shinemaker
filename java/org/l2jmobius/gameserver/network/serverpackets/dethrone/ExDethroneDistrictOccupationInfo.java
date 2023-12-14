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
package org.l2jmobius.gameserver.network.serverpackets.dethrone;

import org.l2jmobius.Config;
import org.l2jmobius.gameserver.instancemanager.GlobalVariablesManager;
import org.l2jmobius.gameserver.network.ServerPackets;
import org.l2jmobius.gameserver.network.serverpackets.ServerPacket;

/**
 * @author CostyKiller
 */
public class ExDethroneDistrictOccupationInfo extends ServerPacket
{
	private final int _category;
	
	public ExDethroneDistrictOccupationInfo(int category)
	{
		_category = category;
	}
	
	@Override
	public void write()
	{
		ServerPackets.EX_DETHRONE_DISTRICT_OCCUPATION_INFO.writeId(this);
		
		writeByte(_category);
		
		if (_category == 0) // Water Area
		{
			writeInt(3); // array size for conquest regions Water Area (currently 3)
			
			// Zone 1 ASA
			writeInt(1); // zone name id for ASA
			writeInt(Config.SERVER_ID); // server id for ASA ZONE CONQUERER
			writeInt(1); // array size for ASA servers (only one server atm)
			
			writeInt(1); // server rank 1
			writeInt(Config.SERVER_ID); // rank 1 server id
			writeInt(GlobalVariablesManager.getInstance().getInt("CONQUEST_ZONE_ASA_POINTS", 0)); // rank 1 server points
			writeInt(0);
			
			// writeInt(2); // server rank 2
			// writeInt(0); // rank 2 server id
			// writeInt(0); // rank 2 server points
			// writeInt(0);
			
			// Zone 2 ANIMA
			writeInt(2); // zone name id for ANIMA
			writeInt(Config.SERVER_ID); // server id for ANIMA ZONE CONQUERER
			writeInt(1); // array size for ANIMA servers (only one server atm)
			
			writeInt(1); // server rank 1
			writeInt(Config.SERVER_ID); // rank 1 server id
			writeInt(GlobalVariablesManager.getInstance().getInt("CONQUEST_ZONE_ANIMA_POINTS", 0)); // rank 1 server points
			writeInt(0);
			
			// writeInt(2); // server rank 2
			// writeInt(0); // rank 2 server id
			// writeInt(0); // rank 2 server points
			// writeInt(0);
			
			// Zone 3 NOX
			writeInt(3); // zone name id for NOX
			writeInt(Config.SERVER_ID); // server id for NOX ZONE CONQUERER
			writeInt(1); // array size for NOX servers (only one server atm)
			
			writeInt(1); // server rank 1
			writeInt(Config.SERVER_ID); // rank 1 server id
			writeInt(GlobalVariablesManager.getInstance().getInt("CONQUEST_ZONE_NOX_POINTS", 0)); // rank 1 server points
			writeInt(0);
			
			// writeInt(2); // server rank 2
			// writeInt(0); // rank 2 server id
			// writeInt(0); // rank 2 server points
			// writeInt(0);
		}
		else // Fire Area
		{
			writeInt(2); // array size for conquest regions Fire Area (currently 2)
			
			// Zone 4 VITA
			writeInt(4); // zone name id for VITA
			writeInt(Config.SERVER_ID); // server id for VITA ZONE CONQUERER
			writeInt(1); // array size for VITA servers (only one server atm)
			
			writeInt(1); // server rank 1
			writeInt(Config.SERVER_ID); // rank 1 server id
			writeInt(GlobalVariablesManager.getInstance().getInt("CONQUEST_ZONE_VITA_POINTS", 0)); // rank 1 server points
			writeInt(0);
			
			// writeInt(2); // server rank 2
			// writeInt(0); // rank 2 server id
			// writeInt(0); // rank 2 server points
			// writeInt(0);
			
			// Zone 5 IGNIS
			writeInt(5); // zone name id for IGNIS
			writeInt(Config.SERVER_ID); // server id for IGNIS ZONE CONQUERER
			writeInt(1); // array size for IGNIS servers (only one server atm)
			
			writeInt(1); // server rank 1
			writeInt(Config.SERVER_ID); // rank 1 server id
			writeInt(GlobalVariablesManager.getInstance().getInt("CONQUEST_ZONE_IGNIS_POINTS", 0)); // rank 1 server points
			writeInt(0);
			
			// writeInt(2); // server rank 2
			// writeInt(0); // rank 2 server id
			// writeInt(0); // rank 2 server points
			// writeInt(0);
		}
	}
}
