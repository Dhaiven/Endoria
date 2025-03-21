package game.pkg_Util.pkg_Message.options;

public record Side(Integer start, Integer size) {

    public Integer end() {
        return start + size;
    }

    public Boolean contains(Integer var) {
        return start <= var && var <= end();
    }

    public Boolean contains(Double var) {
        return start <= var && var <= end();
    }
}
