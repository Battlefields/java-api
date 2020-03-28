package io.github.tastac.bfj.tools;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class MapGenerator {

    static final int xMapOffset = 241, yMapOffset = 224;
    public BufferedImage map;
    public Graphics2D g2d;

    public MapGenerator(){
        this.map = loadMapTexture();
        g2d = map.createGraphics();
        //ALPHA | g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
    }

    private BufferedImage loadMapTexture(){
        File f = new File(ClassLoader.getSystemResource("map.png").getFile());
        BufferedImage image = new BufferedImage(1264, 1232, BufferedImage.TYPE_INT_RGB);
        try{ image = ImageIO.read(f); }catch(IOException e){
            System.err.println("Unable to load map texture: ");
            e.printStackTrace();
        }
        return image;
    }

    public void outputMapPNG(){
        File f = new File(".\\output.png");
        try{
            ImageIO.write(map, "png", f);
        }catch(IOException e){
            System.err.println("Unable to output map image: ");
            e.printStackTrace();
        }

    }

    public void outputMapJPG() {
        File f = new File(".\\output.jpg");
        try {
            ImageIO.write(map, "jpg", f);
        } catch (IOException e) {
            System.err.println("Unable to output map image: ");
            e.printStackTrace();
        }
    }

    //TOOLS

    public void drawLine(int startX, int startY, int endX, int endY, int color){
        g2d.setColor(new Color(color));
        g2d.drawLine(startX + xMapOffset, startY + yMapOffset, endX + xMapOffset, endY + yMapOffset);
    }

    public void drawOval(int x, int y, int width, int height, int color, boolean filled, boolean centered){
        g2d.setColor(new Color(color));

        int posX;
        int posY;

        if(centered){
            posX = (int)(x - (float)(width/2) + xMapOffset);
            posY = (int)(y - (float)(height/2) + yMapOffset);
        }else {
            posX = x + xMapOffset;
            posY = y + yMapOffset;
        }

        if(filled){
            g2d.fillOval(posX, posY, width, height);
        }else{
            g2d.drawOval(posX, posY, width, height);
        }
    }

    public void drawRect(int x, int y, int width, int height, int color, boolean filled, boolean centered){
        g2d.setColor(new Color(color));

        int posX;
        int posY;

        if(centered){
            posX = (int)(x - (float)(width/2) + xMapOffset);
            posY = (int)(y - (float)(height/2) + yMapOffset);
        }else {
            posX = x + xMapOffset;
            posY = y + yMapOffset;
        }

        if(filled){
            g2d.fillRect(posX, posY, width, height);
        }else{
            g2d.drawRect(posX, posY, width, height);
        }
    }

}
