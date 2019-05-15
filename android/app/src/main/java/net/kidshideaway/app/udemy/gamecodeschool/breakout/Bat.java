package net.kidshideaway.app.udemy.gamecodeschool.breakout;

import android.graphics.RectF;

public class Bat {
    private RectF root;
    private float length;
    private float x;
    private float paddleSpeed;
    final int STOPPED = 0;
    final int LEFT =  1;
    final int RIGHT = 2;
    private int paddleMoving = STOPPED;
    private RectF rect;

    Bat(int screenX, int screenY)
    {
        length = 200;
        float height = 30;
        x = screenX / 2;
        float y = screenY - 20;
        rect = new RectF(x, y, x + length, y + height);
        paddleSpeed = 350;

    }
    RectF getRect() {
        return rect;
    }
    void setMovementState(int state){
        paddleMoving = state;
    }

    void update(long fps)
    {
        if(paddleMoving == LEFT)
        {
            x = x - paddleSpeed / fps;
        }
        if(paddleMoving == RIGHT){
            x = x + paddleSpeed / fps;
        }
        rect.left = x;
        rect.right = x + length;
    }
}
