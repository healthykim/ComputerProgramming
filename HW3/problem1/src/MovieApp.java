
import java.util.*;

public class MovieApp {
    private HashMap<Movie, String[]> movies = new HashMap<>();
    private LinkedList<User> users = new LinkedList<>();
    private HashMap<User, Map> ratings= new HashMap<>();
    private HashMap<User, LinkedList<Movie>> histories = new HashMap<>();

    public boolean addMovie(String title, String[] tags) {
        // TODO sub-problem 1
        Movie movie = new Movie(title);
        if(findMovie(title)==null) {
            movies.put(movie, tags);
            return true;
        }
        else
           return false;
    }

    public boolean addUser(String name) {
        // TODO sub-problem 1
        User user = new User(name);
        for(User i : users){
            if(i.toString().equals(name)){
                return false;
            }
        }
        users.add(user);
        return true;
    }

    public Movie findMovie(String title) {
        // TODO sub-problem 1
        for(Movie i : movies.keySet()){
            if(i.toString().equals(title)){
                return i;
            }
        }
        return null;
    }

    public User findUser(String username) {
        // TODO sub-problem 1
        for(User i : users){
            if(i.toString().equals(username)){
                return i;
            }
        }
        return null;
    }

    public List<Movie> findMoviesWithTags(String[] tags) {
        // TODO sub-problem 2
        LinkedList matchMovies = new LinkedList<Movie>();
        if(tags.length==0){
            return matchMovies;
        }
        for(Movie movie : movies.keySet()) {
            matchMovies.add(movie);
            String tagString = new String();
            for (int i = 0; i < movies.get(movie).length; i++) {
                tagString = tagString + movies.get(movie)[i];
            }
            for (String tag : tags) {
                if (!tagString.contains(tag)) {
                    matchMovies.remove(movie);
                }
            }
        }
        Collections.sort(matchMovies);
        return matchMovies;

    }

    public boolean rateMovie(User user, String title, int rating) {
        // TODO sub-problem 3
        HashMap titleRating = new HashMap<String, Integer>();
        titleRating.put(title, rating);
        int i = getUserRating(user, title);
        if(user == null||title == null || findUser(user.toString())==null||findMovie(title)==null||rating>10||rating<1){
            return false;
        }
        if(i==0){
            if(ratings.get(user)==null){
                ratings.put(user, titleRating);
            }
            else {
                ratings.get(user).put(title, rating);
            }
            return true;
        }
        else {
            ratings.get(user).replace(title, rating);

            return true;
        }
    }

    public int getUserRating(User user, String title) {
        // TODO sub-problem 3
        if(user==null||title==null||findUser(user.toString())==null||findMovie(title)==null)
            return -1;

        else if(ratings.get(user)==null||ratings.get(user).get(title)==null) {
            return 0;
        }
        else {
            return (int) ratings.get(user).get(title);
        }

    }

    public List<Movie> findUserMoviesWithTags(User user, String[] tags) {
        // TODO sub-problem 4
        if(user==null){
            return new LinkedList<>();
        }
        else {
            if(!findMoviesWithTags(tags).isEmpty()) {
                if(histories.get(user)==null){
                    histories.put(user,new LinkedList<>());
                }
                histories.get(user).addAll(findMoviesWithTags(tags));
                /*
                for (String i : tags) {
                    if (histories.get(user) == null) {
                        histories.put(user, new LinkedList<>());
                    }
                    histories.get(user).add(i);
                }

                 */
            }
            return findMoviesWithTags(tags);
        }
    }

    public class MoviewithAverage{
        Movie movie;
        float average;

        public MoviewithAverage(Movie j, float average) {
            this.movie = j;
            this.average = average;
        }
    }

    public List<Movie> recommend(User user) {
        // TODO sub-problem 4

        if(user==null)
            return new LinkedList<>();
        else{
            /*
            String[] tags = new String[histories.get(user).size()];
            int idx =0;
            for(String j : histories.get(user)){
                tags[idx] = j;
                idx++;
                System.out.println(j);
            }

             */
            LinkedList<Movie> candidates = histories.get(user);
            /*
            for(String y : tags){
                String[] j = new String[1];
                j[0] =y;
                for(Movie m : findMoviesWithTags(j)){
                    if(!candidates.contains(m))
                        candidates.add(m);
                }
            }

             */
            /*
            LinkedList<Movie> candidates = new LinkedList<>();
            for(Movie x : findMoviesWithTags(tags)){
                candidates.add(x);
            }

             */

            //LinkedList<Movie> candidates = (LinkedList<Movie>) findMoviesWithTags(tags);
            LinkedList<MoviewithAverage> moviewithAverages = new LinkedList<>();
            LinkedList<Movie> returnMovies = new LinkedList<>();
            for(Movie j : candidates) {
                float average=0;
                int rated = 0;
                for (User i : users) {
                    average = average + getUserRating(i,j.toString());
                    if(getUserRating(i, j.toString())>0){
                        rated++;
                    }
                }
                if(rated == 0){
                    average = 0;
                }
                else
                    average=average/rated;
                moviewithAverages.add(new MoviewithAverage(j, average));
            }
            for(int i=0; i<moviewithAverages.size()-1; i++){
                for(int j=i; j<moviewithAverages.size();j++){
                    if(moviewithAverages.get(i).average<moviewithAverages.get(j).average){
                        Collections.swap(moviewithAverages, i, j);
                    }
                }
            }
            for(MoviewithAverage i : moviewithAverages){
                if(returnMovies.size()<3) {
                    returnMovies.add(i.movie);
                }
                else
                    break;
            }
            return returnMovies;
        }
        //sort by descending ratings -> if same, lexicographical


    }
}
