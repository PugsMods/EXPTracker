package com.pugzarecute.exptracker.server;

import com.pugzarecute.exptracker.networking.EXPTrackerPacketHandler;
import com.pugzarecute.exptracker.networking.HuntDataPacketS2C;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.PacketDistributor;
import org.apache.commons.lang3.Range;
import org.checkerframework.checker.units.qual.C;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class HuntDataService extends Thread {
    private final Player hunted;
    private final Player hunter;
    private ChunkPos lastSentPos;


    public HuntDataService(Player hunter, Player hunted) {
        this.hunted = hunted;
        this.hunter = hunter;
    }

    public void run() {
        Random random = ThreadLocalRandom.current();

        int x = (int)hunted.getX() - random.nextInt(0, 63);;
        int y = (int)hunted.getY() - random.nextInt(-15, 15);;
        int z = (int)hunted.getZ() - random.nextInt(0, 63);;
        lastSentPos = new ChunkPos(new BlockPos(x,y,z));

        EXPTrackerPacketHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) hunter), new HuntDataPacketS2C(false, x, y, z));

        while (hunter.getPersistentData().getBoolean("exptracker.currently_hunting")) {

            x = (int)hunted.getX() - random.nextInt(0, 63);;
            y = (int)hunted.getY() - random.nextInt(-15, 15);;
            z = (int)hunted.getZ() - random.nextInt(0, 63);;

            BlockPos initXYZ = new BlockPos(x, y, z);

            List<ChunkPos> old_square = genChunkSet(lastSentPos);

            List<ChunkPos> new_square = genChunkSet(new ChunkPos(initXYZ));

            while(!(cCP_repeat(hunted.chunkPosition(),new_square))){
                x = (int)hunted.getX() - random.nextInt(0, 63);;
                y = (int)hunted.getY() - random.nextInt(-15, 15);;
                z = (int)hunted.getZ() - random.nextInt(0, 63);;

                 initXYZ = new BlockPos(x, y, z);

                 new_square = genChunkSet(new ChunkPos(initXYZ));
                System.out.println("LOOP");
            }



            ChunkPos pChunkPos = hunted.chunkPosition();

            if(!(cCP_repeat(pChunkPos,old_square))){
            EXPTrackerPacketHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) hunter), new HuntDataPacketS2C(false, x, y, z));
            this.lastSentPos = new_square.stream().toList().get(0);
            }


            try {
                Thread.sleep(10 * 1000);
            } catch (InterruptedException e) {
                System.out.println("insomnia");
            }
        }
        EXPTrackerPacketHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) hunter), new HuntDataPacketS2C(true, 0, 0, 0));
        }

    private static boolean cCP_repeat(ChunkPos a, List<ChunkPos>b){
        for (ChunkPos c:b){
            if (a.equals(c)) return true;
        }
        return false;
    }
        public static List<ChunkPos> genChunkSet(ChunkPos initial){
            List<ChunkPos> new_square = new ArrayList<>();
            new_square.add(initial); //top left
            new_square.add(new ChunkPos(initial.x+1,initial.z)); // top right
            new_square.add(new ChunkPos(initial.x,initial.z+1)); //bottom left
            new_square.add(new ChunkPos(initial.x+1,initial.z+1)); //bottom right
            new_square.add(new ChunkPos(initial.x+2,initial.z+1)); // top right
            new_square.add(new ChunkPos(initial.x+1,initial.z+2)); // top right
            new_square.add(new ChunkPos(initial.x+2,initial.z)); // top right
            new_square.add(new ChunkPos(initial.x,initial.z+2)); //bottom left
            new_square.add(new ChunkPos(initial.x+2,initial.z+2)); //bottom right

            new_square.add(new ChunkPos(initial.x+3,initial.z));
            new_square.add(new ChunkPos(initial.x+3,initial.z+1)); // top right
            new_square.add(new ChunkPos(initial.x+3,initial.z+2)); // top right
            new_square.add(new ChunkPos(initial.x+3,initial.z+3)); //bottom left
            new_square.add(new ChunkPos(initial.x,initial.z+3)); //bottom left
            new_square.add(new ChunkPos(initial.x+1,initial.z+3)); //bottom left
            new_square.add(new ChunkPos(initial.x+2,initial.z+3)); //bottom left
            //new_square.add(new ChunkPos(initial.x+2,initial.z+2)); //bottom right
            return new_square;
        }
    }

