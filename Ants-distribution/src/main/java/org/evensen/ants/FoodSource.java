package org.evensen.ants;

public class FoodSource {
    Position pos;
    int radie;

    int foodAmount;
    public FoodSource(Position pos, int radie, int foodAmount)
    {
        this.pos = pos;
        this.radie = radie;
        this.foodAmount = foodAmount;
    }

    public boolean containsFood()
    {
        return this.foodAmount > 0;
    }

    public void takeFood()
    {
        if (!containsFood())
        {
            return;
        }
        this.foodAmount -=1;
    }
}
