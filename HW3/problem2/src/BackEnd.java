import com.sun.source.tree.BinaryTree;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class BackEnd extends ServerResourceAccessible {
    // Use getServerStorageDir() as a default directory
    // TODO sub-program 1 ~ 4 :
    int fileNumber = 0;

    public String getPassword(String id){
        File testFile = new File(getServerStorageDir()+id+"/password.txt");
        String password= new String();
        try {
            Scanner pswdScanner = new Scanner(testFile);
            while(pswdScanner.hasNext()){
                password = password + pswdScanner.nextLine();
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return password;
    }
    public int getId(File file){
        String[] idtmp = file.getName().split("\\.");
        int id = Integer.parseInt(idtmp[0]);
        return id;
    }

    public LocalDateTime getTime(File file) throws FileNotFoundException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        Scanner sc = new Scanner(file);
        String str = sc.nextLine();
        LocalDateTime time = LocalDateTime.parse(str, formatter);
        return time;
    }

    public String getTitle(File file) throws FileNotFoundException {
        Scanner sc = new Scanner(file);
        sc.nextLine();
        String title = String.valueOf(sc.nextLine());
        return title;
    }

    public String getContents(File file) throws FileNotFoundException {
        Scanner sc = new Scanner(file);
        sc.nextLine();
        sc.nextLine();
        String content = "";
        String s;
        while(sc.hasNext()){
            s = sc.nextLine();
            content= content + s +"\n";
        }
        return content;
    }

    public Post filetoPost(File file) throws FileNotFoundException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        Scanner sc = new Scanner(file);
        String[] idtmp = file.getName().split("\\.");
        int id = Integer.parseInt(idtmp[0]);
        String str = sc.nextLine();
        LocalDateTime time = LocalDateTime.parse(str, formatter);
        String title = String.valueOf(sc.nextLine());
        String buffer = sc.nextLine();
        String content = "";
        while(sc.hasNext()){
            String s = sc.nextLine();
            content= content + s +"\n";
        }
        return new Post(id, time, title, content);
    }


    public boolean isPostEmpty(String dir){
        File postFile = new File(dir);
        try{
            for(File file : postFile.listFiles()){
                if(file.isFile()&&!file.getName().equals("friend.txt")&&!file.getName().equals("password.txt")) {
                    return false;
                }
                else if(file.isDirectory()){
                    return isPostEmpty(file.getCanonicalPath().toString());
                }
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return true;
    }

    public int getLastPostId(String dir){
        File postFile = new File(dir);
        if(isPostEmpty(dir))
            return 0;
        try{
            for(File file : Objects.requireNonNull(postFile.listFiles())){
                if(file.isFile()&&!file.getName().equals("friend.txt")&&!file.getName().equals("password.txt")) {
                    if(fileNumber<Integer.parseInt(file.getName().split("\\.")[0])){
                        fileNumber=Integer.parseInt(file.getName().split("\\.")[0]);
                    }
                }
                else if(file.isDirectory()) {
                    fileNumber = +getLastPostId(file.getCanonicalPath().toString());
                }
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return fileNumber;
    }

    public void makePost(User user, Post newPost){
        String target = getServerStorageDir()+user.id+"/post/"+newPost.getId()+".txt";
        File newFile = new File(target);
        try{
            newFile.createNewFile();
            FileWriter fileWriter = new FileWriter(target);
            fileWriter.write(newPost.getDate()+"\n");
            fileWriter.write(newPost.getTitle()+"\n\n");
            String[] content = newPost.getContent().split("\n");
            for(String st : content){
                if(!st.equals("\n"))
                    fileWriter.write(st+"\n");
            }
            if(fileWriter!=null)
                try {fileWriter.close();} catch (IOException ioException) {
                    ioException.printStackTrace();
                };
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public ArrayList<String> getFriends(User user){
        File postFile = new File(getServerStorageDir());
        ArrayList<String> friends = new ArrayList<String>();
        try{
            File file = new File(getServerStorageDir()+"/"+user.id+"/friend.txt");
            Scanner sc = new Scanner(file);
            String friend;
            while(sc.hasNext()){
                friends.add(sc.nextLine());
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return friends;
    }

    static class Sort implements Comparator<LocalDateTime>{
        @Override
        public int compare(LocalDateTime o1, LocalDateTime o2) {
            boolean isAfter = o1.toLocalDate().isAfter(o2.toLocalDate());
            if(o1.toLocalDate().isEqual(o2.toLocalDate())){
                isAfter=o1.toLocalTime().isAfter(o2.toLocalTime());
            }
            if(isAfter==true){
                return 1;
            }
            else
                return -1;
        }
    }

    public LinkedList<Post> getUserPost(String userId){
        File postFile = new File(getServerStorageDir()+userId+"/post/");
        File[] posts = postFile.listFiles();
        LinkedList<Post> allPosts = new LinkedList<>();
        LinkedList<Post> returnPosts = new LinkedList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");

        try{
            //making list of all post
            assert posts != null;
            for(File file : posts){

                /*

                Scanner sc = new Scanner(file);
                String[] idtmp = file.getName().split("\\.");
                int id = Integer.parseInt(idtmp[0]);
                String str = sc.nextLine();
                LocalDateTime time = LocalDateTime.parse(str, formatter);
                String title = String.valueOf(sc.nextLine());
                sc.nextLine();
                String content = "";
                while(sc.hasNext()){
                    String s = sc.nextLine();
                    content= content + s +"\n";
                }

                 */

                /*
                int id = getId(file);
                LocalDateTime time = getTime(file);
                String title = getTitle(file);
                String content = getContents(file);

                 */
                //Post onePost = new Post(id, time, title, content);
                Post onePost = filetoPost(file);
                allPosts.add(onePost);
                //System.out.println(onePost.toString());

            }
                //System.out.println(userId);

            for(int i=0; i< allPosts.size()-1;i++){
                for(int j=0; j<allPosts.size()-i;j++){
                    LocalDateTime time1 = LocalDateTime.parse(allPosts.get(i).getDate(), formatter);
                    LocalDateTime time2 = LocalDateTime.parse(allPosts.get(j).getDate(),formatter);
                    Post p1 = allPosts.get(i);
                    Post p2 = allPosts.get(j);
                    Sort compare = new Sort();
                    int z = compare.compare(LocalDateTime.parse(p2.getDate(), formatter), LocalDateTime.parse(p1.getDate(), formatter));
                    if(z>0){
                        Collections.swap(allPosts, i, j);
                    }
                }
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return allPosts;

    }

    public int countKeyword(String keyword){

        return 0;
    }

    public LinkedList<FrontEnd.Coordinate> getPost(String dir, HashSet<String> keyword){
        File postFile = new File(dir);
        LinkedList<FrontEnd.Coordinate> allPost = new LinkedList<>();
        try{
            for(File file : Objects.requireNonNull(postFile.listFiles())){
                if(file.isDirectory()){
                    allPost.addAll(getPost(file.getCanonicalPath().toString(), keyword));
                }
                else if(file.isFile()&&!file.getName().equals("friend.txt")&&!file.getName().equals("password.txt")) {
                    Scanner sc = new Scanner(file);
                    String[] title = getTitle(file).split(" ");
                    String[] content = getContents(file).split(" ");

                    int count=0;

                    for(String i : keyword){
                        String tmp ="";

                        for(String j : title){
                            if(j.contains(i))
                                count ++;
                        }
                        for(String j : content){
                            if(j.contains(i))
                                count++;
                        }
                    }

                    if(count!=0){
                        Post post = filetoPost(file);
                        allPost.add(new FrontEnd.Coordinate(post, count));
                    }
                }
            }

        }
        catch (Exception e){
            e.printStackTrace();
        }
        return allPost;
    }

}