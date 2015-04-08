// Display the voronoi graph produced in Map.as
// Author: amitp@cs.stanford.edu
// License: MIT

package jMapGen;


public class IslandMapGen
{
	// Island shape is controlled by the islandRandom seed and the
	// type of island. The islandShape function uses both of them to
	// determine whether any point should be water or land.
	public long mapSeed = 0;

	// The map data
	public Map map;
	//public Roads roads;
	public Lava lava;

	IslandDefinition def;

	public IslandMapGen(IslandDefinition is, long seed) 
	{
		mapSeed = seed;
		map = new Map(is.SIZE, seed);
		def = is;
		createNewIsland();
	}

	public void createNewIsland() 
	{
		map.newIsland(mapSeed, def);
		map.go();
	}
}
