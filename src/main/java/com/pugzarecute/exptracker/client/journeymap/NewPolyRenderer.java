package com.pugzarecute.exptracker.client.journeymap;

import com.pugzarecute.exptracker.EXPTracker;
import com.pugzarecute.exptracker.server.HuntDataService;
import journeymap.client.api.IClientAPI;
import journeymap.client.api.display.DisplayType;
import journeymap.client.api.display.Overlay;
import journeymap.client.api.display.PolygonOverlay;
import journeymap.client.api.display.Waypoint;
import journeymap.client.api.model.*;
import journeymap.client.api.util.PolygonHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;

import java.util.List;

public class NewPolyRenderer {

    public static void drawPlayerBox(IClientAPI api, BlockPos offset_player, ResourceKey<Level> world) {
        ChunkPos initial = new ChunkPos(offset_player);
        List<ChunkPos> big_square = HuntDataService.genChunkSet(initial);
        ChunkPos waypointX = new ChunkPos(initial.x+2,initial.z+2);

        List<MapPolygonWithHoles> polys = PolygonHelper.createChunksPolygon(big_square,offset_player.getY());


        //Waypoint

        MapImage gun = new MapImage(new ResourceLocation("exptracker:hunt/hunt_big.png"),32,32);
        Waypoint waypoint = new Waypoint(EXPTracker.MODID,"Tracked Player",world,new BlockPos(waypointX.getMinBlockX(),offset_player.getY(),waypointX.getMinBlockZ())).setIcon(gun).setColor(0xff3636).setEditable(false);
        try {
            cleanup(api);
            api.show(waypoint);
            genOverlay(api,world, polys);
        } catch (Exception ignored) {
        }
    }
    private static  void genOverlay(IClientAPI api, ResourceKey<Level> world, List<MapPolygonWithHoles> shapes) throws Exception {
        TextProperties text_style = new TextProperties()
                .setBackgroundColor(0x000000)
                .setBackgroundOpacity(0.3f)
                .setColor(0xffffff)
                .setOpacity(1f)
                .setFontShadow(true);

        ShapeProperties shape_style = new ShapeProperties()
                .setStrokeWidth(2)
                .setStrokeColor(0x0).setStrokeOpacity(0.9f)
                .setFillColor(0x4800ff).setFillOpacity(0.5f);

        if(api.playerAccepts(EXPTracker.MODID, DisplayType.Polygon)){
            int i = 0;

            for(MapPolygonWithHoles polygonWithHoles: shapes){
                String tmp_id= "tracked_player_poly" +i++;
                Overlay overlay = new PolygonOverlay(EXPTracker.MODID,tmp_id,world,shape_style,polygonWithHoles).setOverlayGroupName("tracked_player_poly").setLabel("").setTextProperties(text_style);
                api.show(overlay);
            }
        }
    }
    public static void cleanup(IClientAPI api){
        for (Waypoint w:api.getWaypoints(EXPTracker.MODID)){
            api.remove(w);
        }
        api.removeAll(EXPTracker.MODID);
    }
}
