package thunder.util;
/* Credits to: GamesWithGabe
While the code may have changed significantly since inception, the boilerplate code
was heavily inspired by GamesWithGabe's "Coding a 2D game Engine in Java"
*/
public class Time {
    public static float timeStarted = System.nanoTime();

    public static float getTime() {
        return (float) ((System.nanoTime() - timeStarted) * 1E-9);
    }
}
