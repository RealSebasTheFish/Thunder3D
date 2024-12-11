import thunder.Window;
/* Credits to: GamesWithGabe
While the code may have changed significantly since inception, the boilerplate code
was heavily inspired by GamesWithGabe's "Coding a 2D game Engine in Java"
*/
public class Main {
    public static void main(String[] args) {
        Window window = Window.get(new FirstScene());
        window.run();
    }
}