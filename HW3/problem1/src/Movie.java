
import java.util.HashMap;
import java.util.Map;

public class Movie implements Comparable {
    private String title;
    public Movie(String title) { this.title = title;}
    @Override
    public String toString() {
        return title;
    }

    @Override
    public int compareTo(Object o) {
        return title.compareTo(o.toString());
    }
}
