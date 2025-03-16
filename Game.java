import game.GameEngineV2;

public final class Game {

    public static void main(String[] args) {
        new Thread(new GameEngineV2()).start();
    }
}
