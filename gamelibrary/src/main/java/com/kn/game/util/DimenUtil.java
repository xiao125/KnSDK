package com.kn.game.util;

import android.content.res.Resources;
import android.util.DisplayMetrics;

import com.kn.game.configurator.Game;

/**
 * Created
 */

public final class DimenUtil {

    public static int getScreenWidth() {
        final Resources resources = Game.getApplicationContext().getResources();
        final DisplayMetrics dm = resources.getDisplayMetrics();
        return dm.widthPixels;
    }

    public static int getScreenHeight() {
        final Resources resources = Game.getApplicationContext().getResources();
        final DisplayMetrics dm = resources.getDisplayMetrics();
        return dm.heightPixels;
    }
}
