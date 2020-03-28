package io.github.tastac;

import io.github.tastac.api.BFDataRetriever;
import io.github.tastac.api.BFKill;
import io.github.tastac.tools.MapGenerator;

import java.awt.*;

public class BFJ{

    /**
     *      NOTICE: THIS CLASS IS FOR TESTING PURPOSES
     */

    public static void main(String[] args) {
        new BFJ().run();
    }

    public void run(){
        MapGenerator mapGenerator = new MapGenerator();

        Graphics2D g2d = mapGenerator.map.createGraphics();

        for(BFKill kill : BFDataRetriever.getKillsBySourceID(137)){
            mapGenerator.drawLine((int)kill.getSourcePos().x,  (int)kill.getSourcePos().z, (int)kill.getTargetPos().x, (int)kill.getTargetPos().z, 0xFFFFFFFF);

            mapGenerator.drawOval((int)kill.getSourcePos().x, (int)kill.getSourcePos().z, 5, 5, 0xFF00FF00, true, true);
            mapGenerator.drawOval((int)kill.getTargetPos().x, (int)kill.getTargetPos().z, 5, 5, 0xFFFF0000, true, true);
        }

        mapGenerator.outputMapPNG();

        System.out.println("End");
    }

}
