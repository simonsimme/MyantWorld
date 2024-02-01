package org.evensen.ants;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MyAntWorld implements AntWorld{

    int width;
    int height;
    List<FoodSource> foodSources;

    private static final Random foodspawn = new Random();

     float[][] foodFermones;
    float[][] forgingFermones;
    boolean[][] boleanMatrix;
    CostumeDispersalPolicy dispersalPolicy;
    FoodSource genfood()
    {
        int foodAmount = foodspawn.nextInt(50,150);
        int radie = foodAmount/10 +5;

        int randomX = foodspawn.nextInt(0, this.width+1);
        int randomY = foodspawn.nextInt(0, this.height+1);
        return new FoodSource(new Position(randomX,randomY),radie, foodAmount);
    }


    public MyAntWorld(final int worldWidth, final int worldHeight, final int i, CostumeDispersalPolicy dispersalPolicy) {
        this.width = worldWidth;
        this.height = worldHeight;
        this.foodFermones = new float[this.width][this.height];
        this.forgingFermones = new float[this.width][this.height];
        this.boleanMatrix = new boolean[this.width][this.height];
        this.foodSources = new ArrayList<>();
        this.dispersalPolicy = dispersalPolicy;
        for (int a = 0; a < i; a++)
        {
            this.foodSources.add(genfood());
        }
        bloeanFoodUpdate();
    }
    public void bloeanFoodUpdate()
    {
     this.boleanMatrix = new boolean[this.width][this.height];
     for (int i = 0; i< this.width;i++)
     {
         for (int j = 0; j<this.height;j++)
         {
             for (int f=0;f<this.foodSources.size();f++)
             {
                 Position pos = new Position(i,j);
                 if (pos.isWithinRadius(this.foodSources.get(f).pos,  this.foodSources.get(f).radie))
                 {
                     this.boleanMatrix[i][j] = true;
                 }
             }
         }
     }

    }

        @Override
        public int getWidth() {
            return this.width;
        }

        @Override
        public int getHeight() {
            return this.height;
        }

        public float[][] getFoodFermones()
        {
            return this.foodFermones;
        }
        public float[][] getForgingFermones()
        {
            return this.forgingFermones;
        }

        @Override
        public boolean isObstacle(final Position p) {
            if (!p.isInBounds(this.width, this.height))
            {
                return true;
            }

        return false;
        }

        @Override
        public void dropForagingPheromone(final Position p,  float amount) {
            int x =(int) p.getX();
            int y =(int) p.getY();

            if(this.forgingFermones[x][y] + amount > 1)
            {
                this.forgingFermones[x][y] = 1;
            }else
            {
                this.forgingFermones[x][y] += amount;
            }

           // this.forgingFermones[x][y] += amount;
        }

        @Override
        public void dropFoodPheromone(final Position p, float amount) {
            int x =(int) p.getX();
            int y =(int) p.getY();

            if(this.foodFermones[x][y] + amount > 1)
            {
               this.foodFermones[x][y] = 1;
            }else
            {
                this.foodFermones[x][y] += amount;
            }


           // this.foodFermones[x][y] += amount;
        }

        @Override
        public void dropFood(final Position p) {

        }

        @Override
        public void pickUpFood(final Position p) {

                for( int i = 0; i < this.foodSources.size(); i++)
                {
                    if (p.isWithinRadius(this.foodSources.get(i).pos, this.foodSources.get(i).radie+1.0f)) {
                        this.foodSources.get(i).takeFood();
                        if(this.foodSources.get(i).foodAmount == 0)
                        {
                            this.foodSources.set(i, genfood());
                           // this.foodSources.remove(i);
                            bloeanFoodUpdate();
                        }
                    }
                }

        }

        @Override
        public float getDeadAntCount(final Position p) {
            return 0;
        }

        @Override
        public float getForagingStrength(final Position p) {

            return this.forgingFermones[(int)p.getX()][(int) p.getY()];
        }

        @Override
        public float getFoodStrength(final Position p) {

               return this.foodFermones[(int)p.getX()][(int) p.getY()];

        }

        @Override
        public boolean containsFood(final Position p) {
            if (isObstacle(p))
            {
                return false;
            }
            return this.boleanMatrix[(int) p.getX()][(int) p.getY()];
        }

        @Override
        public long getFoodCount() {
            return 0;
        }

        @Override
        public boolean isHome(final Position p) {
            if (p.isWithinRadius(new Position(this.width,this.height/2), 15))
            {
                return true;
            }
            return false;
        }


    @Override
    public void dispersePheromones() {
        for (int i = 0; i < this.foodSources.size(); i++) {
            dropFoodPheromone(this.foodSources.get(i).pos, this.foodSources.get(i).foodAmount);
        }

        for (int i = 0; i < this.width; i++) {
            for (int j = 0; j < this.height; j++) {
                Position pos = new Position(i, j);
                float[] newValues = this.dispersalPolicy.getDispersedValue(this, pos);
                this.foodFermones[i][j] = newValues[0];
                this.forgingFermones[i][j] = newValues[1];
            }
        }
    }

    public void selfContainedDisperse() {


            float[][] tmpFood = new float[this.width][this.height];
            float[][] tmpForging = new float[this.width][this.height];
            for(int x = 0; x< this.width; x++)
            {
                for (int y =0; y < this.height; y++ )
                {
                    Position pos = new Position(x,y);
                    float nplfood =0;
                    float nplforging =0;
                    float f = 0.95f;
                    float k = 1f;
                    if(!isObstacle(pos))
                    {
                        for (int i = x-1; i < x+2; i++) {
                            for (int j = y-1; j < y+2; j++) {
                                if(i >= 0 && j >= 0 && i < this.width && j < this.height && pos != new Position(i,j))
                                {
                                    nplfood += this.foodFermones[i][j];
                                    nplforging += this.forgingFermones[i][j];
                                }else if (i < 0 && pos != new Position(i,j) || j < 0 && pos != new Position(i,j)|| i > this.width  && pos != new Position(i,j)|| j > this.height && pos != new Position(i,j))
                                {
                                    if(i +1 == 0 && j >= 0 && j < this.height)
                                    {
                                        nplfood += this.foodFermones[i+1][j];
                                        nplforging += this.forgingFermones[i+1][j];
                                    }
                                    if(i - this.width == 1 && j - this.height < 0)
                                    {
                                        nplfood += this.foodFermones[i-1][j];
                                        nplforging += this.forgingFermones[i-1][j];
                                    }
                                    if(j +1 == 1 && i >= 0 && i < this.width)
                                    {
                                        nplfood += this.foodFermones[i][j+1];
                                        nplforging += this.forgingFermones[i][j+1];
                                    }
                                    if(j - this.height == 1 &&i - this.width < 0 )
                                    {
                                        nplfood += this.foodFermones[i][j-1];
                                        nplforging += this.forgingFermones[i][j-1];
                                    }
                                    if(j < 0 && i < 0)
                                    {
                                        nplfood += this.foodFermones[i+1][j+1];
                                        nplforging += this.forgingFermones[i+1][j+1];
                                    }
                                    if(j - this.height == 1 && i - this.width == 1)
                                    {
                                        nplfood += this.foodFermones[i-1][j-1];
                                        nplforging += this.forgingFermones[i-1][j-1];
                                    }
                                    if(j - this.height == 1 &&j < 0)
                                    {
                                        nplfood += this.foodFermones[i+1][j-1];
                                        nplforging += this.forgingFermones[i+1][j-1];
                                    }
                                    if( i < 0 &&j - this.height == 1)
                                    {
                                        nplfood += this.foodFermones[i-1][j+1];
                                        nplforging += this.forgingFermones[i-1][j+1];
                                    }


                                }

                            }
                            nplfood = ((1-k) *nplfood) / 8 +(k * this.foodFermones[x][y]);
                            nplforging = ((1-k) *nplforging) / 8 +(k * this.forgingFermones[x][y]);


                        }

                        tmpFood[x][y] = nplfood * f;
                        tmpForging[x][y] = nplforging * f;
                    }

                }
            }
            for (int i = 0; i < this.width; i++) {
                for (int j = 0; j < this.height; j++) {
                    this.foodFermones[i][j] = tmpFood[i][j];
                    this.forgingFermones[i][j] = tmpForging[i][j];

                }

            }

        }



        @Override
        public void setObstacle(final Position position, final boolean value) {

        }

        @Override
        public void hitObstacle(final Position position, final float strength) {

        }

}

