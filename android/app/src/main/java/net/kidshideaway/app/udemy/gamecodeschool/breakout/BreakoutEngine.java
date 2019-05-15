package net.kidshideaway.app.udemy.gamecodeschool.breakout;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.graphics.RectF;
import android.media.AudioManager;
import android.media.SoundPool;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;

public class BreakoutEngine extends SurfaceView implements Runnable{

    private Thread gameThread = null;
    private SurfaceHolder ourHolder;
    private volatile boolean playing;
    private boolean paused = true;
    private Canvas canvas;
    private Paint paint;
    private int screenX;
    private int screenY;
    private long fps;
    private long timeThisFrame;

    Bat bat;
    Ball ball;
    Brick[] bricks = new Brick[200];
    int numBricks = 0;
    SoundPool soundPool;
    int beep1ID = -1;
    int beep2ID = -1;
    int beep3ID = -1;
    int loseLifeID = -1;
    int explodeID = -1;
    int score = 0;
    int lives = 3;

    public BreakoutEngine(Context context, int x, int y)
    {
        // calls default constructor
        super(context);
        // Initialize ourHolder and paint objects
        ourHolder = getHolder();
        paint = new Paint();
        // Init screen
        screenX = x;
        screenY = y;
        bat = new Bat(screenX, screenY);
        ball = new Ball();
        soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC,0);

        try{
            AssetManager assetManager = context.getAssets();
            AssetFileDescriptor descriptor;

            descriptor = assetManager.openFd("beep1.ogg");
            beep1ID = soundPool.load(descriptor, 0);
            descriptor = assetManager.openFd("beep2.ogg");
            beep2ID = soundPool.load(descriptor, 0);
            descriptor = assetManager.openFd("beep3.ogg");
            beep3ID = soundPool.load(descriptor, 0);
            descriptor = assetManager.openFd("loseLife.ogg");
            loseLifeID = soundPool.load(descriptor, 0);
            descriptor = assetManager.openFd("explode.ogg");
            explodeID = soundPool.load(descriptor, 0);

        }
        catch(IOException e)
        {
            Log.e("error","failed to load sound files");
        }
        restart();
    }

    public void pause()
    {
        playing = false;
        try {
            gameThread.join();
        } catch(InterruptedException e){
            Log.e("Error:", "joining thread");
        }
    }

    public void resume()
    {
        playing = true;
        gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    public void run() {

        while (playing)
        {
            //caputure current time
            long startFrameTime = System.currentTimeMillis();
            //update the frame
            if(!paused){
                update();
            }
            draw();
            timeThisFrame = System.currentTimeMillis() - startFrameTime;
            if(timeThisFrame >= 1)
            {
                fps=1000/timeThisFrame;
            }
        }

    }
    private void update(){
        bat.update(fps);
        ball.update(fps);

        for(int i =0; i<numBricks; i++)
        {
            if(bricks[i].getVisibility())
            {
                if(RectF.intersects(bricks[i].getRect(), ball.getRect())){
                    bricks[i].setInvisible();
                    ball.reverseYVelocity();
                    score = score + 10;
                    //soundPool.play(explodeID, 1,1,0,0,1);
                }
            }
        }
        if(RectF.intersects(bat.getRect(),ball.getRect())){
            ball.setRandomVelocity();
            ball.reverseYVelocity();
            ball.clearObstacleY(bat.getRect().top-2);
            //soundPool.play(beep1ID,1,1,0,0,1);
        }

        if(ball.getRect().bottom > screenY){
            ball.reverseYVelocity();
            ball.clearObstacleY(screenY - 2);
            lives --;
            //soundPool.play(loseLifeID, 1, 1, 0, 0 ,1);
            if(lives == 0)
            {
                paused = true;
                lives = 5;  // should really display a game over message.
                restart();
            }
        }

        // Bounce the ball back when it hits the top of screen
        if(ball.getRect().top < 0){
            ball.reverseYVelocity();
            ball.clearObstacleY(12);
            //soundPool.play(beep2ID, 1, 1, 0, 0, 1);
        }

        // If the ball hits left wall bounce
        if(ball.getRect().left < 0){
            ball.reverseXVelocity();
            ball.clearObstacleX(2);
            //soundPool.play(beep3ID, 1, 1, 0, 0, 1);
        }

        // If the ball hits right wall bounce
        if(ball.getRect().right > screenX - 10){
            ball.reverseXVelocity();
            ball.clearObstacleX(screenX - 22);
            //soundPool.play(beep3ID, 1, 1, 0, 0, 1);
        }

        // Pause if cleared screen
        if(score == numBricks * 10){
            paused = true;
            restart();
        }
    }

    void restart(){
        ball.reset(screenX, screenY);
        int brickWidth = screenX /8;
        int brickHeight = screenY / 10;
        numBricks = 0;
        for(int column=0; column<8; column++)
        {
            for(int row=0; row<3; row++)
            {
                bricks[numBricks] = new Brick(row, column, brickWidth, brickHeight);
                numBricks++;
            }
        }
    }

    private void draw() {
        int alpha_color = 255;
        int red_color = 0;
        int green_color = 0;
        int blue_color = 0;

        if(lives ==5)
        {   //blues
            red_color = 66;
            green_color = 137;
            blue_color = 244;
        }
        else if(lives ==4)
        {   //purples
            red_color = 146;
            green_color = 28;
            blue_color = 255;
        }
        else if(lives ==3)
        {   //greens
            red_color = 20;
            green_color = 255;
            blue_color = 125;
        }
        else if(lives ==2)
        {   //oranges
            red_color = 255;
            green_color = 136;
            blue_color = 25;
        }
        else if(lives ==1)
        {   //reds
            red_color = 255;
            green_color = 25;
            blue_color = 25;
        }
        else if(lives < 1)
        {   //black
            red_color = 0;
            green_color = 0;
            blue_color = 0;
        }

        int black_RGB = 0;
        int white_RGB = 255;

        if(ourHolder.getSurface().isValid())
        {
                canvas = ourHolder.lockCanvas();
                canvas.drawColor(Color.argb(alpha_color, black_RGB, black_RGB, black_RGB));
                paint.setColor(Color.argb(alpha_color, red_color, green_color, blue_color));
                for(int i=0; i< numBricks; i++) {
                    if (bricks[i].getVisibility()) {
                        canvas.drawRect(bricks[i].getRect(), paint);
                    }
                }

                paint.setColor(Color.argb(alpha_color, white_RGB, white_RGB, white_RGB));
                canvas.drawRect(bat.getRect(), paint);
                canvas.drawRect(ball.getRect(), paint);

                // Draw HUD
                paint.setColor(Color.argb(alpha_color,white_RGB,white_RGB,white_RGB));
                paint.setTextSize(70);
                canvas.drawText("Score: " + score + " Lives: " + lives, 10, 80, paint);

                // Draw HUD
                paint.setColor(Color.argb(alpha_color,white_RGB,white_RGB,white_RGB));
                paint.setTextSize(70);
                canvas.drawText("Score: " + score + " Lives: " + lives, 10, 80, paint);

                ourHolder.unlockCanvasAndPost(canvas);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent){
        switch (motionEvent.getAction() & MotionEvent.ACTION_MASK){
            case MotionEvent.ACTION_DOWN:
                paused =false;
                if(motionEvent.getX() > screenX /2){
                    bat.setMovementState(bat.RIGHT);
                }
                else{
                    bat.setMovementState(bat.LEFT);
                }
                break;
            case MotionEvent.ACTION_UP:
                bat.setMovementState(bat.STOPPED);
                break;
        }
        return true;
    }
}
