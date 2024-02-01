package org.evensen.ants;

import java.util.Objects;

public class CostumeDispersalPolicy implements DispersalPolicy{
    @Override
    public float[] getDispersedValue(final AntWorld w, final Position p) {
        final float[] retlist = new float[2];

        final float[][] forgingFermones= w.getForgingFermones();
        final float[][] foodFermones= w.getFoodFermones();
        final int width = w.getWidth();
        final int height = w.getHeight();
        

                Position pos = p;
                float nplfood =0;
                float nplforging =0;
                float f = 0.95f;
                float k = 0.9f;
                int x = (int)p.getX();
                int y = (int)p.getY();
                if(!w.isObstacle(p))
                {
                    for (int i = x-1; i < x+2; i++) {
                        for (int j = y-1; j < y+2; j++) {
                            if(isaBoolean(i, j, width, height, pos))
                            {
                                nplfood += foodFermones[i][j];
                                nplforging += forgingFermones[i][j];
                            }else if (outsideBool(i, pos, j, width, height))
                            {
                               int nx = i;
                               int ny= j;
                                if(nx >= width || nx < 0)
                                {
                                    nx = x;
                                }
                                if(ny>= height || ny < 0)
                                {
                                  nx = x;
                                  ny = y;
                                }
                                //Position poss = new Position(nx, ny);
                                nplfood += foodFermones[nx][ny];
                                nplforging += forgingFermones[nx][ny];
                            }

                        }
                        nplfood = ((1-k) *nplfood) / 8 +(k *foodFermones[x][y]);
                        nplforging = ((1-k) *nplforging) / 8 +(k * forgingFermones[x][y]);
                        retlist[0] = nplfood;
                        retlist[1] = nplforging;


                    }
                }



        return retlist;
    }

    private static boolean isaBoolean(final int i, final int j, final int width, final int height, final Position pos) {
        return i >= 0 && j >= 0 && i < width && j < height && !Objects.equals(pos, new Position(i, j));
    }

    private static boolean outsideBool(final int i, final Position pos, final int j, final int width, final int height) {
        return i < 0 && !Objects.equals(pos, new Position(i, j)) || j < 0 && !Objects.equals(pos, new Position(i, j)) || i >
                width &&
                !Objects.equals(pos, new Position(i, j)) || j > height && !Objects.equals(pos, new Position(i, j));
    }
}
