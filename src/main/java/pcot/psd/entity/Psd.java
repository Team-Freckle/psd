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
        if (!(o instanceof Psd p))
            return false;

        return this.name.equals(p.name);
    }
}
