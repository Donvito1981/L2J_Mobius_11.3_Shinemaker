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
package ai.bosses.Eigis;

import java.util.Calendar;

import org.l2jmobius.commons.threads.ThreadPool;
import org.l2jmobius.gameserver.data.SpawnTable;
import org.l2jmobius.gameserver.data.xml.NpcData;
import org.l2jmobius.gameserver.instancemanager.DBSpawnManager;
import org.l2jmobius.gameserver.instancemanager.GlobalVariablesManager;
import org.l2jmobius.gameserver.model.Location;
import org.l2jmobius.gameserver.model.Spawn;
import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.actor.templates.NpcTemplate;

import ai.AbstractNpcAI;

/**
 * @author NasSeKa
 */
public class Eigis extends AbstractNpcAI
{
	// NPC
	private static final int EIGIS = 29385;
	// Misc
	private static final Location EIGIS_LOCATION = new Location(-23172, -222237, -3504);
	private static final String EIGIS_ALIVE_VAR = "EIGIS_ALIVE";
	
	public Eigis()
	{
		addKillId(EIGIS);
		
		final long currentTime = System.currentTimeMillis();
		final Calendar calendarEigisStart = Calendar.getInstance();
		final Calendar calendarEigisSeal = Calendar.getInstance();
		
		calendarEigisStart.set(Calendar.DAY_OF_WEEK, 1); // Sunday
		calendarEigisStart.set(Calendar.HOUR_OF_DAY, 23);
		calendarEigisStart.set(Calendar.MINUTE, 0);
		calendarEigisStart.set(Calendar.SECOND, 0);
		
		calendarEigisSeal.set(Calendar.DAY_OF_WEEK, 2); // Monday
		calendarEigisSeal.set(Calendar.HOUR_OF_DAY, 2);
		calendarEigisSeal.set(Calendar.MINUTE, 0);
		calendarEigisSeal.set(Calendar.SECOND, 0);
		
		if (((currentTime > calendarEigisStart.getTimeInMillis()) && (currentTime < calendarEigisSeal.getTimeInMillis())) && (SpawnTable.getInstance().getAnySpawn(EIGIS) == null) && GlobalVariablesManager.getInstance().getBoolean(EIGIS_ALIVE_VAR, true))
		{
			spawnEigis();
		}
		else
		{
			despawnEigis();
		}
		
		if (calendarEigisStart.getTimeInMillis() < currentTime)
		{
			calendarEigisStart.add(Calendar.WEEK_OF_YEAR, 1);
		}
		if (calendarEigisSeal.getTimeInMillis() < currentTime)
		{
			calendarEigisSeal.add(Calendar.WEEK_OF_YEAR, 1);
		}
		
		ThreadPool.scheduleAtFixedRate(() ->
		{
			spawnEigis();
		}, calendarEigisStart.getTimeInMillis() - currentTime, 604800000); // 7 days
		
		ThreadPool.scheduleAtFixedRate(() ->
		{
			despawnEigis();
		}, calendarEigisSeal.getTimeInMillis() - currentTime, 604800000); // 7 days
	}
	
	private void spawnEigis()
	{
		try
		{
			final NpcTemplate template = NpcData.getInstance().getTemplate(EIGIS);
			final Spawn spawn = new Spawn(template);
			spawn.setXYZ(EIGIS_LOCATION);
			spawn.setHeading(0);
			spawn.setRespawnDelay(0);
			DBSpawnManager.getInstance().addNewSpawn(spawn, false);
			GlobalVariablesManager.getInstance().set(EIGIS_ALIVE_VAR, true);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	private void despawnEigis()
	{
		if (SpawnTable.getInstance().getAnySpawn(EIGIS) != null)
		{
			for (Npc npc : SpawnTable.getInstance().getAnySpawn(EIGIS).getSpawnedNpcs())
			{
				npc.deleteMe();
			}
		}
	}
	
	@Override
	public String onKill(Npc npc, Player killer, boolean isSummon)
	{
		if (npc.getId() == EIGIS)
		{
			GlobalVariablesManager.getInstance().set(EIGIS_ALIVE_VAR, false);
		}
		
		return super.onKill(npc, killer, isSummon);
	}
	
	public static void main(String[] args)
	{
		new Eigis();
	}
}