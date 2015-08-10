package com.bioxx.tfc2;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import com.bioxx.jMapGen.IslandMap;
import com.bioxx.jMapGen.IslandParameters.Feature;
import com.bioxx.jMapGen.Point;
import com.bioxx.jMapGen.attributes.Attribute;
import com.bioxx.jMapGen.attributes.CaveAttribute;
import com.bioxx.jMapGen.attributes.RiverAttribute;
import com.bioxx.jMapGen.graph.Center;
import com.bioxx.tfc2.World.WorldGen;
import com.bioxx.tfc2.api.Types.Moisture;

public class RenderOverlayHandler
{
	private FontRenderer fontrenderer = null;

	@SubscribeEvent
	public void renderText(RenderGameOverlayEvent.Text event)
	{
		Minecraft mc = Minecraft.getMinecraft();
		if(mc.theWorld.provider.getDimensionId() == 0 && WorldGen.instance != null)
		{
			int xM = ((int)(mc.thePlayer.posX) >> 12);
			int zM = ((int)(mc.thePlayer.posZ) >> 12);
			IslandMap map = WorldGen.instance.getIslandMap(xM, zM);
			Point islandCoord = new Point((int)(mc.thePlayer.posX), (int)(mc.thePlayer.posZ)).toIslandCoord();
			Center hex = map.getSelectedHexagon(islandCoord);
			event.left.add(EnumChatFormatting.BOLD+""+EnumChatFormatting.YELLOW+"--------Hex--------");
			event.left.add("Elevation: "+hex.getElevation()+" ("+map.convertHeightToMC(hex.getElevation())+")");
			event.left.add("Moisture: "+Moisture.fromVal(hex.moisture));
			event.left.add("Island Coord: "+islandCoord.getX() + "," + islandCoord.getY());	

			RiverAttribute attrib = (RiverAttribute)hex.getAttribute(Attribute.riverUUID);
			if(attrib != null)
			{
				event.left.add(EnumChatFormatting.BOLD+""+EnumChatFormatting.YELLOW+"-------River-------");
				event.left.add("River: " + attrib.getRiver() + " | " + (attrib.upriver != null ?  attrib.upriver.size() : 0));	
				if(attrib.upriver != null && attrib.getDownRiver() != null)
					event.left.add("Up :" + hex.getDirection(attrib.upriver.get(0)).toString() + " | Dn :" + hex.getDirection(attrib.getDownRiver()).toString());
			}

			CaveAttribute cattrib = (CaveAttribute)hex.getAttribute(Attribute.caveUUID);
			if(cattrib != null)
			{
				if(cattrib.nodes.size() > 0)
				{
					event.left.add(EnumChatFormatting.BOLD+""+EnumChatFormatting.YELLOW+"-------Cave-------");
					event.left.add("Cave: ");	
				}
			}

			event.right.add(EnumChatFormatting.BOLD+""+EnumChatFormatting.YELLOW+"--Island Parmaters--");
			event.right.add("*Moisture: "+map.getParams().getIslandMoisture());
			event.right.add("*Temperature: "+map.getParams().getIslandTemp());

			event.right.add(EnumChatFormatting.BOLD+""+EnumChatFormatting.YELLOW+"---Island Features--");
			for(Feature f : Feature.values())
			{
				if(map.getParams().hasFeature(f))
				{
					event.right.add("*"+f.toString());
				}
			}
		}
	}
}
