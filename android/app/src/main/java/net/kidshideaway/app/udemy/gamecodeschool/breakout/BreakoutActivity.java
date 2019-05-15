package net.kidshideaway.app.udemy.gamecodeschool.breakout;

import android.app.Activity;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;

public class BreakoutActivity extends Activity {

    BreakoutEngine breakoutEngine;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get a Display object to access screen details
        Display display = getWindowManager().getDefaultDisplay();
        // Load athe resolution in to a point object
        Point size = new Point();
        display.getSize(size);

        // Initialize gameView and set it as the view
        breakoutEngine = new BreakoutEngine(this, size.x, size.y);
        setContentView(breakoutEngine);
    }

    // this method executes when the player starts the game
    @Override
    protected void onResume() {
        super.onResume();
        // Tell the gameView resume method to execute
        breakoutEngine.resume();
    }

    // This method executes when the player quites the game
    @Override
    protected void onPause() {
        super.onPause();
        // Tell the gameView pause method to execute
        breakoutEngine.pause();
    }

}