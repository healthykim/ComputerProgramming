package server;

import course.*;
import utils.Config;
import utils.ErrorCode;
import utils.Pair;

import javax.swing.*;
import java.io.*;
import java.lang.reflect.Array;
import java.security.Key;
import java.util.*;

public class Server {

    public LinkedList<Course> loadCourses(){
        LinkedList<Course> courses = new LinkedList<>();
        File Dir_2020_Spring = new File("data/Courses/2020_Spring");
        File[] under2020_Spring = Dir_2020_Spring.listFiles();

        for(File Dir : under2020_Spring) {
            String college = null;
            int courseId =0;
            String department = null;
            String academicDegree = null;
            int academicYear = 0;
            String courseName = null;
            int credit = 0;
            String location = null;
            String instructor = null;
            int quota =0;

            college = Dir.getName();
            for (File file : Dir.listFiles()) {
                String tmpForId = file.getName();
                String[] pieceForId = tmpForId.split("\\.");
                courseId = Integer.parseInt(pieceForId[0]);
                try {
                    Scanner scanner = new Scanner(file);
                    String st = "";
                    while (scanner.hasNextLine()) {
                        st = st + scanner.nextLine();
                    }
                    String[] pieces = st.split("[|]");
                    department = pieces[0];
                    academicDegree = pieces[1];
                    academicYear = Integer.parseInt(pieces[2]);
                    courseName = pieces[3];
                    credit = Integer.parseInt(pieces[4]);
                    location = pieces[5];
                    instructor = pieces[6];
                    quota = Integer.parseInt(pieces[7]);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Course course = new Course(courseId, college, department, academicDegree, academicYear, courseName, credit, location, instructor, quota);
                courses.add(course);
            }
        }
        return courses;
    }


    public List<Course> search(Map<String,Object> searchConditions, String sortCriteria){
        // TODO Problem 2-1

        LinkedList<Course> courses = loadCourses();

        //search
        if(searchConditions.isEmpty()||searchConditions==null){
            return sortByCriteria(courses, sortCriteria);
        }
        else {
            LinkedList<Course> searchedCourses = new LinkedList<>();
            searchedCourses.addAll(courses);
            int ayCondition = 0;
            String deptCondition = null;
            String nameCondition = null;
            if(searchConditions.get("ay")!=null){
                ayCondition = (int) searchConditions.get("ay");
                for(Course course : courses){
                    if(course.academicYear != ayCondition)
                        if(searchedCourses.contains(course))
                            searchedCourses.remove(course);
                }
            }
            if(searchConditions.get("dept")!=null){
                deptCondition = (String) searchConditions.get("dept");
                for(Course course : courses){
                    if(!course.department.equals(deptCondition))
                        if(searchedCourses.contains(course))
                            searchedCourses.remove(course);
                }
            }
            if(searchConditions.get("name")!=null){
                nameCondition = (String) searchConditions.get("name");
                List<String> searchnameKeywords = Arrays.asList(nameCondition.split(" "));
                for(Course course : courses){
                    List<String> coursenameKeywords = Arrays.asList(course.courseName.split(" "));
                    if(!coursenameKeywords.containsAll(searchnameKeywords)){
                        if(searchedCourses.contains(course))
                            searchedCourses.remove(course);
                    }
                }
            }

            return sortByCriteria(searchedCourses, sortCriteria);
        }
    }

    public LinkedList<Course> sortByCriteria(LinkedList<Course> searchedCourses, String sortCriteria){
        String sortct;
        if(sortCriteria==null)
            sortct = "id";

        else if(sortCriteria.isEmpty())
            sortct = "id";
        else sortct = sortCriteria;


        if(sortct.equals("id")) Collections.sort(searchedCourses, new CompareId());
        else if(sortct.equals("name")) Collections.sort(searchedCourses, new CompareName());
        else if(sortct.equals("dept")) Collections.sort(searchedCourses, new CompareDept());
        else if(sortct.equals("ay")) Collections.sort(searchedCourses, new CompareAy());
        return searchedCourses;
    }

    public static class CompareId implements Comparator<Course>{

        @Override
        public int compare(Course o1, Course o2) {
            if(o1.courseId<o2.courseId) return -1;
            else if(o1.courseId>o2.courseId) return 1;
            else if(o1.courseId==o2.courseId) return 0;
            return 0;
        }
    }
    public class CompareName implements Comparator<Course>{

        @Override
        public int compare(Course o1, Course o2) {
            if(o1.courseName.compareTo(o2.courseName)<0) return -1;
            else if(o1.courseName.compareTo(o2.courseName)>0) return 1;
            else if(o1.courseName.compareTo(o2.courseName)==0) {
                if(o1.courseId<o2.courseId) return -1;
                else if(o1.courseId>o2.courseId) return 1;
                else if(o1.courseId==o2.courseId) return 0;
            }
            return 0;
        }
    }
    public class CompareDept implements Comparator<Course>{

        @Override
        public int compare(Course o1, Course o2) {
            if(o1.department.compareTo(o2.department)<0) return -1;
            else if(o1.department.compareTo(o2.department)>0) return 1;
            else if(o1.department.compareTo(o2.department)==0)  {
                if(o1.courseId<o2.courseId) return -1;
                else if(o1.courseId>o2.courseId) return 1;
                else if(o1.courseId==o2.courseId) return 0;
            }
            return 0;
        }
    }
    public class CompareAy implements Comparator<Course>{

        @Override
        public int compare(Course o1, Course o2) {
            if(o1.academicYear<o2.academicYear) return -1;
            else if(o1.academicYear>o2.academicYear) return 1;
            else if(o1.academicYear==o2.academicYear) {
                if(o1.courseId<o2.courseId) return -1;
                else if(o1.courseId>o2.courseId) return 1;
                else if(o1.courseId==o2.courseId) return 0;
            }
            return 0;
        }
    }


    public int bid(int courseId, int mileage, String userId){
        // TODO Problem 2-2
        //error detection
        //1. User not found
        File userDir = new File("data/Users/"+userId);
        List<Bidding> biddingList = new LinkedList<>();
        List<Integer> errors = new LinkedList<>();

        if(!userDir.exists()){
            //System.out.println("there is no Dir at "+userDir.getPath());
            errors.add(ErrorCode.USERID_NOT_FOUND);
        }

        //2. No bid file
        File bidTxt = new File("data/Users/"+userId+"/bid.txt");
        if(userDir.exists()&&!bidTxt.exists()){
            //System.out.println("no bid file");
            return ErrorCode.IO_ERROR;
        }

        //if there is no error that can be occur in the retrieveBids, then call that.
        Pair<Integer, List<Bidding>> previousBids = retrieveBids(userId);

        //to check the mileage usage exceeded and proper change on bid txt
        boolean reset = false;
        boolean ignore = false;
        boolean newbid = false;
        boolean rebid = false;
        if(mileage ==0){
            for(Bidding bidding : previousBids.value){
                if(bidding.courseId == courseId)
                    reset = true;
            }
            if(!reset)
                ignore = true;
        }
        else{
            for(Bidding bidding : previousBids.value) {
                if (bidding.courseId == courseId) {
                    rebid = true;
                }
            }
            if(!rebid)
                newbid = true;
        }

        //3. mileage check
        // - negative?
        if(mileage<0)
            errors.add(ErrorCode.NEGATIVE_MILEAGE);
        // - Over the Max Mileage that can be bid to course?
        if(mileage> Config.MAX_MILEAGE_PER_COURSE)
            errors.add(ErrorCode.OVER_MAX_COURSE_MILEAGE);
        // - Mileage usage exceeded
        if(previousBids.key==ErrorCode.SUCCESS) {
            if(rebid == true){
                int totalUsage = 0;
                for (Bidding bidding : previousBids.value) {
                    if(bidding.courseId == courseId){
                    }
                    else {
                        totalUsage = totalUsage + bidding.mileage;
                    }
                }
                if (totalUsage + mileage > Config.MAX_MILEAGE)
                    errors.add(ErrorCode.OVER_MAX_MILEAGE);
            }
            else {
                int totalUsage = 0;
                for (Bidding bidding : previousBids.value) {
                    totalUsage = totalUsage + bidding.mileage;
                }

                if (totalUsage + mileage > Config.MAX_MILEAGE)
                    errors.add(ErrorCode.OVER_MAX_MILEAGE);
            }
        }
        else{
            errors.add(previousBids.key);
        }

        //5. No such CourseId
        LinkedList<Course> courses = loadCourses();
        boolean iscourseId=false;
        for(Course course : courses){
            if(course.courseId == courseId)
                iscourseId = true;
        }
        if(!iscourseId)
            errors.add(ErrorCode.NO_COURSE_ID);

        if(errors.isEmpty()){
            //change the bid.txt file appropriately
            if(reset){
                //System.out.println("reset...");
                String newContent ="";
                try {
                    Scanner scanner = new Scanner(bidTxt);
                    while (scanner.hasNextLine()){
                        String st = scanner.nextLine();
                        if(st.contains(courseId+"|")){
                            String[] temp = st.split("[|]");
                            int removingmileage = Integer.parseInt(temp[1]);
                            previousBids.value.remove(new Bidding(courseId, removingmileage));
                        }
                        else
                            newContent = newContent + st +"\n";
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                try {
                    FileWriter fileWriter = new FileWriter(bidTxt);
                    fileWriter.write(newContent);
                    fileWriter.flush();
                    fileWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                errors.add(ErrorCode.SUCCESS);
            }

            else if(ignore) {
                //System.out.println("ignore...");
                errors.add(ErrorCode.SUCCESS);
            }
            else if(rebid){
                //System.out.println("re bidding...");
                String newContent ="";
                try {
                    Scanner scanner = new Scanner(bidTxt);
                    while (scanner.hasNextLine()){
                        String st = scanner.nextLine();
                        if(st.contains(courseId+"|")) {
                            newContent = newContent + courseId + "|" + mileage + "\n";
                            String[] temp = st.split("[|]");
                            int removingmileage = Integer.parseInt(temp[1]);
                            previousBids.value.remove(new Bidding(courseId, removingmileage));
                            previousBids.value.add(new Bidding(courseId, mileage));
                        }
                        else{
                            newContent = newContent + st +"\n";
                        }
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                try {
                    FileWriter fileWriter = new FileWriter(bidTxt);
                    fileWriter.write(newContent);
                    fileWriter.flush();
                    fileWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                errors.add(ErrorCode.SUCCESS);
            }
            else if(newbid){
                //System.out.println("new bidding...");
                BufferedWriter bw = null;
                try {
                    bw = new BufferedWriter(new FileWriter(bidTxt, true));
                    PrintWriter pw = new PrintWriter(bw, true);
                    pw.write("\n"+courseId + "|" + mileage +"\n");
                    previousBids.value.add(new Bidding(courseId, mileage));
                    pw.flush();
                    pw.close();

                } catch (IOException e) {
                    e.printStackTrace();
                }

                errors.add(ErrorCode.SUCCESS);
            }
        }
        return Collections.min(errors);

    }

    public Pair<Integer,List<Bidding>> retrieveBids(String userId){
        // TODO Problem 2-2
        File users = new File("data/Users/");
        File userDir = new File("data/Users/"+userId);
        List<Bidding> biddingList = new LinkedList<>();
        List<Integer> errorList = new LinkedList<>();

        boolean valid = false;
        for(File file : users.listFiles()){
            if(file.getName().equals(userId)) {
                valid = true;
                break;
            }
        }
        if(valid==false){
            errorList.add(ErrorCode.USERID_NOT_FOUND);
        }



        if(!userDir.exists()){
            //System.out.println("there is no Dir at "+userDir.getPath());
            errorList.add(ErrorCode.USERID_NOT_FOUND);
        }


        File bidTxt = new File("data/Users/"+userId+"/bid.txt");
        if(!bidTxt.exists()){
            errorList.add(ErrorCode.IO_ERROR);
        }

        if(errorList.isEmpty()) {
            LinkedList<String> bidInfos = new LinkedList<>();
            try {
                Scanner scanner = new Scanner(bidTxt);
                while (scanner.hasNextLine()) {
                    bidInfos.add(scanner.nextLine());
                }
                for (String bidInfo : bidInfos) {
                    if(!bidInfo.isEmpty()) {
                        String[] pieces = bidInfo.split("[|]");
                        Bidding bidding = new Bidding(Integer.parseInt(pieces[0]), Integer.parseInt(pieces[1]));
                        biddingList.add(bidding);
                    }
                }
                errorList.add(ErrorCode.SUCCESS);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        //System.out.println(biddingList);
        return new Pair<>(Collections.min(errorList), biddingList);

    }

    public boolean confirmBids() {
        // TODO Problem 2-3
        HashMap<String, List<Bidding>> UserBiddingInfo = new HashMap<>();
        HashMap<Course, HashMap<String, Integer>> CourseBiddingInfo = new HashMap<>();
        HashMap<Course, List<String>> confirmedInfo = new HashMap<>();

        List<String> users = new LinkedList<>();
        List<Course> courses = loadCourses();
        File UsersDir = new File("data/Users");
        File[] under_UsersDir = UsersDir.listFiles();
        for (File User : under_UsersDir) {
            String UserId = User.getName();
            users.add(UserId);
            List<Bidding> biddinginfo = retrieveBids(UserId).value;
            UserBiddingInfo.put(UserId, biddinginfo);
        }
        for (Course course : courses) {
            HashMap<String, Integer> userBidding = new HashMap<>();
            for (String user : users) {
                for (Bidding bidding : UserBiddingInfo.get(user)) {
                    if (bidding.courseId == course.courseId) {
                        userBidding.put(user, bidding.mileage);
                    }
                }
            }
            CourseBiddingInfo.put(course, userBidding);
        }
        for (Course course : courses) {
            List<String> confirmedUsers = new LinkedList<>();
            if (CourseBiddingInfo.get(course).size() <= course.quota) {
                confirmedUsers.addAll(CourseBiddingInfo.get(course).keySet());
            }
            else {
                Set<Integer> mileagelisttemp = new HashSet<>(CourseBiddingInfo.get(course).values());
                List<Integer>mileagelist = new LinkedList<>();
                mileagelist.addAll(mileagelisttemp);
                Collections.sort(mileagelist, Comparator.reverseOrder());
                //Set<Integer> mileagelist = new TreeSet<>();
                //mileagelist.addAll(mileagelisttemp);

                int length = 0;
                boolean isCut = false;
                Set<String> cuttedUsers = new HashSet<>();
                for (Integer i : mileagelist) {
                    if (length < course.quota) {
                        int numOfuser = 0;
                        numOfuser = numKey(CourseBiddingInfo.get(course), i);
                        if (length > course.quota - numOfuser) {
                            cuttedUsers.addAll(findKey(CourseBiddingInfo.get(course), i));
                            isCut = true;
                            break;
                        } else {
                            confirmedUsers.addAll(findKey(CourseBiddingInfo.get(course), i));
                            length = length + numOfuser;
                        }
                    }
                }

                if (isCut) {
                    int remainQuota = course.quota - length;
                    HashMap<String, Integer> userUsage = new HashMap<>();
                    for (String user : cuttedUsers) {
                        int totalUsage = 0;
                        for (Bidding bidding : retrieveBids(user).value) {
                            totalUsage = totalUsage + bidding.mileage;
                        }
                        userUsage.put(user, totalUsage);
                    }
                    Set<Integer> totalmileagelisttmp = new HashSet<>(userUsage.values());
                    List<Integer> totalmileagelist = new LinkedList<>(totalmileagelisttmp);
                    Collections.sort(totalmileagelist);

                    //Set<Integer> totalmileagelist = new TreeSet<>();
                    //totalmileagelist.addAll(totalmileagelisttmp);
                    //System.out.println(userUsage+"!!!!!!!!!!!!!");

                    int length2 = 0;
                    boolean isCut2 = false;
                    List<String> cuttedUsers2 = new LinkedList<>();
                    for (Integer i : totalmileagelist) {
                        if (length2 < remainQuota) {
                            int numOfuser = 0;
                            numOfuser = numKey(userUsage, i);
                            if (length2 > remainQuota - numOfuser) {
                                cuttedUsers2.addAll(findKey(userUsage, i));
                                break;
                            } else {
                                confirmedUsers.addAll(findKey(userUsage, i));
                                length2 = length2 + numOfuser;
                                //System.out.println(findKey(userUsage, i)+"is included");
                            }
                        }
                    }
                    if (isCut2) {
                        int remainQuota2 = course.quota - length - length2;
                        Collections.sort(cuttedUsers2);
                        int length3 = 0;
                        for (String i : cuttedUsers2) {
                            if (length3 > remainQuota2) {
                                break;
                            } else {
                                confirmedUsers.add(i);
                            }
                        }
                    }
                }


            }
            //System.out.println(course.courseId + " " + confirmedUsers);
            confirmedInfo.put(course, confirmedUsers);
            //System.out.println(course.courseId + " " + confirmedInfo.get(course));
        }

        for (String user : users) {
            File Userbidtxt = new File("data/Users/" + user + "/bid.txt");
            if (Userbidtxt.exists()) {
                Userbidtxt.delete();
            } //else
                //System.out.println("delete failed");
        }


        for(Course course : courses){
            //System.out.println(course.courseId);
            List<String> confirmedusers = confirmedInfo.get(course);
            //System.out.println(confirmedusers);
            if(confirmedusers==null){
                //System.out.println("confirmedusers is null");
            }
            else {
                for (String confirmeduser : confirmedusers) {
                    try {
                        File file = new File("data/Users/" + confirmeduser + "/courses.txt");
                        if(!file.exists()) {
                            file.createNewFile();
                        }
                        BufferedWriter bw = new BufferedWriter(new FileWriter(file, true));
                        String s = course.courseId+"|"+course.college+"|"+course.department+"|"+course.academicDegree+"|"
                                +course.academicYear+"|"+course.courseName+"|"+course.credit+"|"+course.location+"|"+course.instructor
                                +"|"+course.quota+"\n";
                        bw.write(s);
                        bw.flush();
                        bw.close();
                        //System.out.println(confirmeduser+" write");
                    } catch (IOException e) {
                        //System.out.println("No such file");
                        return false;
                    }
                }
            }
        }

        return true;
    }
    public <K,V> int numKey(Map<K, V> map, V value) {
        int numOfkey=0;
        for (K key : map.keySet()) {
            if (value.equals(map.get(key))) {
                 numOfkey = numOfkey+1;
            }
        }
        return numOfkey;
    }
    public <K,V> LinkedList<K> findKey(Map<K, V> map, V value) {
        LinkedList<K> list = new LinkedList<K>();
        for (K key : map.keySet()) {
            if (value.equals(map.get(key))) {
                list.add(key);
            }
        }
        return list;
    }

    public Pair<Integer,List<Course>> retrieveRegisteredCourse(String userId){
        // TODO Problem 2-3
        File dir = new File("data/Users/" + userId );
        if(!dir.exists()){
            return new Pair<>(ErrorCode.USERID_NOT_FOUND,new ArrayList<>());
        }
        File file = new File("data/Users/" + userId + "/courses.txt");
        if(!file.exists()){
            //System.out.println("no such file");
            return new Pair<>(ErrorCode.SUCCESS,new ArrayList<>());
        }
        ArrayList<Course> courses = new ArrayList<>();
        try {
            Scanner sc = new Scanner(file);
            String college = null;
            int courseId =0;
            String department = null;
            String academicDegree = null;
            int academicYear = 0;
            String courseName = null;
            int credit = 0;
            String location = null;
            String instructor = null;
            int quota =0;
            if(sc.hasNextLine()){
                Scanner scanner = new Scanner(file);
                String st = "";
                while (scanner.hasNextLine()) {
                    st = scanner.nextLine();
                    String[] pieces = st.split("[|]");
                    courseId = Integer.parseInt(pieces[0]);
                    college = pieces[1];
                    department= pieces[2];
                    academicDegree = pieces[3];
                    academicYear = Integer.parseInt(pieces[4]);
                    courseName = pieces[5];
                    credit = Integer.parseInt(pieces[6]);
                    location = pieces[7];
                    instructor = pieces[8];
                    quota = Integer.parseInt(pieces[9]);
                    Course course = new Course(courseId, college, department, academicDegree, academicYear, courseName, credit, location, instructor, quota);
                    courses.add(course);
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return new Pair<>(ErrorCode.IO_ERROR,new ArrayList<>());
        }

        return new Pair<>(ErrorCode.SUCCESS,courses);
    }
}
