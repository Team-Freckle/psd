package pcot.psd.entity;

public class Psd {
    protected String name;
    protected int size;

    public String getName() {
        return name;
    }

    public int getSize() {
        return size;
    }

    public boolean isDifferent(Psd o) {
        return this.name.equals(o.name);
    }
}
