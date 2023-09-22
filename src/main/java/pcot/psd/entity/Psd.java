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

    @Override
    public boolean equals(Object o) {
        if (Psd.class != o.getClass()) return false;

        Psd p = (Psd) o;
        return this.name.equals(p.name)
                && this.size == p.size;
    }
}
