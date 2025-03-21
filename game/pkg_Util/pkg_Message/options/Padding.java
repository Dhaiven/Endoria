package game.pkg_Util.pkg_Message.options;

public class Padding {
    private final Integer top;
    private final Integer right;
    private final Integer bottom;
    private final Integer left;
    
    private Padding(
        Integer top,
        Integer right,
        Integer bottom,
        Integer left
    ) {
        this.top = top;
        this.right = right;
        this.bottom = bottom;
        this.left = left;
    }

    public Side x(Side x) {
        return new Side(x.start() - left, x.size() + left + right);
    }

    public Side y(Side y) {
        return new Side(y.start() - top, y.size() + top + bottom);
    }

    public static Padding zero() {
        return new Padding(0, 0, 0, 0);
    }

    public static Padding top(Integer top) {
        return new Padding(top, 0, 0, 0);
    }

    public static Padding right(Integer right) {
        return new Padding(0, right, 0, 0);
    }

    public static Padding bottom(Integer bottom) {
        return new Padding(0, 0, bottom, 0);
    }

    public static Padding left(Integer left) {
        return new Padding(0, 0, 0, left);
    }

    public static Padding topRight(Integer top, Integer right) {
        return new Padding(top, right, 0, 0);
    }

    public static Padding topLeft(Integer top, Integer left) {
        return new Padding(top, 0, 0, left);
    }

    public static Padding bottomRight(Integer bottom, Integer right) {
        return new Padding(0, right, bottom, 0);
    }

    public static Padding bottomLeft(Integer bottom, Integer left) {
        return new Padding(0, 0, bottom, left);
    }

    public static Padding horizontal(Integer number) {
        return new Padding(0, number, 0, number);
    }

    public static Padding horizontal(Integer left, Integer right) {
        return new Padding(0, right, 0, left);
    }

    public static Padding vertical(Integer number) {
        return new Padding(number, 0, number, 0);
    }

    public static Padding vertical(Integer top, Integer bottom) {
        return new Padding(top, 0, bottom, 0);
    }

    public static Padding all(Integer number) {
        return new Padding(number, number, number, number);
    }

    public static Padding all(Integer horizontal, Integer vertical) {
        return new Padding(vertical, horizontal, vertical, horizontal);
    }

    public static Padding all(Integer top, Integer right, Integer bottom, Integer left) {
        return new Padding(top, right, bottom, left);
    }
}
