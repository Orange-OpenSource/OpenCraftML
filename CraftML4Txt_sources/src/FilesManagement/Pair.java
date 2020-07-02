package FilesManagement;

public class Pair implements Comparable<Pair> {
    public final String index;
    public final float value;

    public Pair(String index, float value) {
        this.index = index;
        this.value = value;
    }

    @Override
    public int compareTo(Pair other) {
        //multiplied to -1 as the author need descending sort order
        return -1 * Float.valueOf(this.value).compareTo(other.value);
    }
}