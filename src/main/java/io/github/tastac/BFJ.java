package io.github.tastac;

import io.github.tastac.api.DataRetriever;
import io.github.tastac.api.components.BFKill;
import io.github.tastac.tools.MapGenerator;

import javax.xml.crypto.Data;
import java.awt.*;

public class BFJ{

    /**
     *      NOTICE: THIS CLASS IS FOR TESTING PURPOSES
     */

    public static void main(String[] args) {
        new BFJ().run();
    }

    public void run(){
        System.out.println("Start");

        MapGenerator mapGenerator = new MapGenerator();

        Graphics2D g2d = mapGenerator.map.createGraphics();

        for(BFKill kill : DataRetriever.getKillsByBFPlayerSource(DataRetriever.getPlayerByUsername("Mesryn"))){

            int
                    sx = (int)kill.getSourcePos().x,
                    sz = (int)kill.getSourcePos().z,
                    ex = (int)kill.getTargetPos().x,
                    ez = (int)kill.getTargetPos().z;

            mapGenerator.drawLine(sx, sz, ex, ez, 0xFF6060FF);

            mapGenerator.drawOval(sx, sz, 5, 5, 0xFF00FF00, true, true);
            mapGenerator.drawOval(ex, ez, 5, 5, 0xFFFF0000, true, true);
        }

        mapGenerator.outputMapPNG();

        System.out.println("End");
    }

}
