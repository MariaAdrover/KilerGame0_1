package staff;

import killergame0_1.KillerGame0_1;

public class Wall extends Static {
    // wall up --> 0
    // wall right --> 3
    // wall down --> 6
    // wall left --> 9
    protected int position;
    
    public Wall (KillerGame0_1 game, int state, int x, int y, int width, int height, int position) {
        super(game, state, x, y, width, height);
        this.position = position;
    }

    public int getPosition() {
        return position;
    }
    
}
