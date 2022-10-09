import java.time.format.DateTimeFormatter;
import java.util.*;
import java.time.LocalDateTime;

public class FrontEnd {
    private UserInterface ui;
    private BackEnd backend;
    private User user;

    public FrontEnd(UserInterface ui, BackEnd backend) {
        this.ui = ui;
        this.backend = backend;
    }
    
    public boolean auth(String authInfo){
        // TODO sub-problem 1
        String[] idPswd = authInfo.split("\\n");
        user = new User(idPswd[0], backend.getPassword(idPswd[0]));
        String password = backend.getPassword(idPswd[0]);
        if(password.equals(idPswd[1]))
            return true;
        else
            return false;
    }

    public void post(Pair<String, String> titleContentPair) {
        // TODO sub-problem 2
        Post newPost = new Post(backend.getLastPostId(backend.getServerStorageDir())+1, LocalDateTime.now(), titleContentPair.key, titleContentPair.value);
        backend.makePost(user, newPost);
    }
    
    public void recommend(){
        // TODO sub-problem 3
        ArrayList<String> friends = backend.getFriends(user);
        LinkedList<Post> allPosts = new LinkedList<>();
        for(String i : friends) {
            allPosts.addAll(backend.getUserPost(i));
        }
        LinkedList<Post> recommendPosts = new LinkedList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        for(int i=0; i< allPosts.size()-1;i++){
            for(int j=i+1; j<allPosts.size();j++){
                Post p1 = allPosts.get(i);
                Post p2 = allPosts.get(j);
                BackEnd.Sort compare = new BackEnd.Sort();
                int z = compare.compare(Post.parseDateTimeString(p2.getDate(), formatter), Post.parseDateTimeString(p1.getDate(), formatter));
                if(z>0){
                    Collections.swap(allPosts, i, j);
                }
            }
        }
        for(Post i : allPosts) {
            if(recommendPosts.size()<10) {
                recommendPosts.add(i);
                ui.println(i.toString());
            }
            else
                break;
        }
    }

    public static class Coordinate{
        Post post;
        int num;

        public Coordinate(Post post, int count) {
            this.post = post;
            this.num = count;
        }
    }

    public void search(String command) {
        // TODO sub-problem 4
        command = command.replace("search", "");
        Scanner sc = new Scanner(command);
        HashSet<String> keywords = new HashSet<>();
        LinkedList<Coordinate> posts = new LinkedList<>();
        LinkedList<Post> sortedPosts = new LinkedList<>();
        while (sc.hasNext()) {
            keywords.add(sc.next());
        }

        posts = backend.getPost(backend.getServerStorageDir(), keywords);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");

        for(int i=0; i<posts.size();i++) {
            for (int j = 0; j < posts.size()-i; j++) {
                if (posts.get(i).num < posts.get(j).num) {
                    Collections.swap(posts, i, j);
                } else if (posts.get(i).num == posts.get(j).num) {
                    BackEnd.Sort compare = new BackEnd.Sort();
                    int z = compare.compare(Post.parseDateTimeString(posts.get(i).post.getDate(), formatter), Post.parseDateTimeString(posts.get(j).post.getDate(), formatter));
                    if (z > 0) {
                        Collections.swap(posts, i, j);
                    }
                }
            }
        }

        for(Coordinate post : posts){
            if(sortedPosts.size()<10){
                sortedPosts.add(post.post);
                ui.println(post.post.getSummary());
            }
            else
                break;
        }


    }
    
    User getUser(){
        return user;
    }
}
