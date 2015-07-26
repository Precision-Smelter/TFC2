package com.bioxx.tfc2.api.Trees;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;

import com.bioxx.tfc2.api.Types.ClimateTemp;
import com.bioxx.tfc2.api.Types.Moisture;

public class TreeRegistry
{
	public static TreeRegistry instance = new TreeRegistry();
	private HashMap<String, TreeConfig> treeTypeHash = new HashMap<String, TreeConfig>();
	private HashMap<String, TreeSchemManager> treeList;

	public TreeRegistry()
	{
		treeList  = new HashMap<String, TreeSchemManager>();
	}

	public void RegisterSchematic(TreeSchematic treeSchematic, String name)
	{
		if(!treeList.containsKey(name))
			treeList.put(name, new TreeSchemManager());

		treeList.get(name).addSchem(treeSchematic);
	}

	public String[] getTreeNames()
	{
		return (String[])treeTypeHash.keySet().toArray(new String[treeTypeHash.size()]);
	}

	public TreeSchematic getRandomTreeSchematic(Random R)
	{
		return treeList.get(R.nextInt(treeList.size())).getRandomSchematic(R);
	}

	/**
	 * @return Returns a random schematic for a specific tree type at any growth stage
	 */
	public TreeSchematic getRandomTreeSchematic(Random R, int treeID)
	{
		if(treeID > treeList.size() - 1) return null;
		return treeList.get(treeID).getRandomSchematic(R);
	}

	/**
	 * @return Returns a random schematic for a specific tree type at a specific growth stage
	 */
	public TreeSchematic getRandomTreeSchematic(Random R, int treeID, int growthStage)
	{
		if(treeID > treeList.size() - 1) return null;
		return treeList.get(treeID).getRandomSchematic(R, growthStage);
	}

	/**
	 * @return Returns a specific schematic
	 */
	public TreeSchematic getTreeSchematic(int treeID, int schemID, int growthStage)
	{
		if(treeID > treeList.size() - 1) return null;
		return treeList.get(treeID).getSchematic(schemID, growthStage);
	}

	public void addTreeType(TreeConfig configuration)
	{
		if(!treeTypeHash.containsKey(configuration.name))
		{
			treeTypeHash.put(configuration.name, configuration);
		}
	}

	public TreeSchemManager managerFromString(String n)
	{
		if(treeList.containsKey(n))
			return ((TreeSchemManager) treeList.get(n));
		return null;
	}

	/**
	 * @param n Name of the Tree type. Used as the Key in the hash map for lookups.
	 * @return Full TreeConfig object
	 */
	public TreeConfig treeFromString(String n)
	{
		if(treeTypeHash.containsKey(n))
			return ((TreeConfig) treeTypeHash.get(n));
		return null;
	}

	public TreeConfig getRandomTree()
	{
		int id = new Random().nextInt(treeTypeHash.keySet().toArray().length);
		return treeTypeHash.get(treeTypeHash.keySet().toArray(new String[treeTypeHash.keySet().size()])[id]);
	}

	public String getRandomTreeTypeForIsland(Random r, ClimateTemp temp, Moisture moisture)
	{
		ArrayList<String> list = new ArrayList<String>();
		Iterator iter = treeTypeHash.keySet().iterator();
		while(iter.hasNext())
		{
			String tree = (String) iter.next();
			TreeConfig tc = treeFromString(tree);
			if(tc.minTemp.getTemp() <= temp.getTemp() && tc.maxTemp.getTemp() >= temp.getTemp() && 
					tc.minMoisture.getMoisture() <= moisture.getMoisture() && tc.maxMoisture.getMoisture() >= moisture.getMoisture())
				list.add(tree);
		}
		if(list.size() == 0)
			return null;
		if(list.size() == 1)
			return list.get(0);

		return list.get(r.nextInt(list.size()));
	}
}