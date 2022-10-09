package cpta;

import cpta.environment.Compiler;
import cpta.environment.Executer;
import cpta.exam.ExamSpec;
import cpta.exam.Problem;
import cpta.exam.Student;
import cpta.exam.TestCase;
import cpta.exceptions.CompileErrorException;
import cpta.exceptions.FileSystemRelatedException;
import cpta.exceptions.InvalidFileTypeException;
import cpta.exceptions.RunTimeErrorException;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;

public class Grader {
    Compiler compiler;
    Executer executer;

    public Grader(Compiler compiler, Executer executer) {
        this.compiler = compiler;
        this.executer = executer;
    }

    public Map<String,Map<String, List<Double>>> gradeSimple(ExamSpec examSpec, String submissionDirPath) {
        // TODO Problem 1-1
        HashMap<String,Map<String, List<Double>>> score = new HashMap<String,Map<String, List<Double>>>();

        for(Student student : examSpec.students){
            HashMap<String, List<Double>> map = new HashMap<>();
            for(Problem problem : examSpec.problems){
                List<Double> scoreList = new LinkedList<>();
                for(TestCase testCase : problem.testCases) {
                    File submittedFile = new File(submissionDirPath + student.id +"/"+ problem.id +"/"+ problem.targetFileName);
                    File outputFile = new File(submittedFile.getParent()+"/"+problem.id+".out");
                    Compiler compiler = new Compiler();
                    try {
                        compiler.compile(submittedFile.getPath());
                    }catch(Exception e){
                    }
                    String[] pieces = problem.targetFileName.split("\\.");
                    String fileName = pieces[0];
                    String yoFilePath = Paths.get(submittedFile.getParent(), fileName+".yo").toString();
                    Executer executer = new Executer();
                    try {
                        executer.execute(yoFilePath, problem.testCasesDirPath+"/"+testCase.inputFileName, outputFile.getPath());
                    }catch (Exception e){
                    }
                    List<String> submissionOutput =new LinkedList<>();
                    List<String> requiredOutput= new LinkedList<>();
                    try {
                        submissionOutput = Files.readAllLines(Path.of(outputFile.getPath()));
                        requiredOutput = Files.readAllLines(Path.of(problem.testCasesDirPath + "/" + testCase.outputFileName));
                    }catch (Exception e){
                    }
                    if(submissionOutput.containsAll(requiredOutput)){
                        scoreList.add(testCase.score);
                    }
                    else {
                        scoreList.add((double) 0);
                    }

                }
                map.put(problem.id, scoreList);
            }
            score.put(student.id, map);
        }
        return score;
    }

    public Map<String,Map<String, List<Double>>> gradeRobust(ExamSpec examSpec, String submissionDirPath) {
        // TODO Problem 1-2
        HashMap<String,Map<String, List<Double>>> score = new HashMap<String,Map<String, List<Double>>>();

        for(Student student : examSpec.students){
            HashMap<String, List<Double>> scoremap = new HashMap<>();
            File students = new File(submissionDirPath);

            File studentDir = new File(submissionDirPath+student.id);
            //System.out.println(submissionDirPath+student.id);
            boolean wrongNameDir = false;
            if(!studentDir.exists()) {
                for (File wrongDir : new File(submissionDirPath).listFiles()) {
                    if (wrongDir.getName().contains(student.id)) {
                        if (wrongDir.getName().indexOf(student.id) == 0) {
                            //System.out.println("finding..");
                            studentDir = new File(submissionDirPath+wrongDir.getName());
                            //System.out.println("found!"+submissionDirPath+wrongDir.getName());
                            wrongNameDir = true;
                        }
                        if(wrongNameDir)
                            break;
                    }
                    if(wrongNameDir)
                        break;
                }
            }
            else if(studentDir.exists()){
                wrongNameDir = true;
            }
            if(!wrongNameDir){
                //System.out.println("no student id directory");
                for(Problem problem : examSpec.problems) {
                    List<Double> zeroPoints = new LinkedList();
                    for(TestCase testCase : problem.testCases) {
                        zeroPoints.add((double)0);
                    }
                    scoremap.put(problem.id, zeroPoints);
                }
                score.put(student.id, scoremap);
                continue;
            }

            for(Problem problem : examSpec.problems){
                File problemDir = new File(studentDir+"/"+problem.id); //problem 1, 2, 3

                boolean thereIsDir = false;
                for(File dir : studentDir.listFiles()){
                    if(problem.id.equals(dir.getName())){
                        thereIsDir = true;
                        break;
                    }
                }

                if(!thereIsDir){
                    //System.out.println("there was no proper directory"+problem.id);
                    List<Double> zeroPoints = new LinkedList();
                    for(TestCase testCase : problem.testCases) {
                        zeroPoints.add((double)0);
                    }
                    scoremap.put(problem.id, zeroPoints);
                    score.put(student.id, scoremap);
                    continue;
                }


                for(File file : problemDir.listFiles()) {
                    if(file.isDirectory()){
                        //System.out.println(file.getName()+" is directory");
                        for(File wrongDirfile : file.listFiles()) {
                            Path properDir = Paths.get(problemDir.getPath()+"/"+wrongDirfile.getName());
                            //Path wrongDirFile = Paths.get(wrongDirfile.getPath());
                            try {
                                Files.copy((Path.of(wrongDirfile.getPath())), properDir, StandardCopyOption.REPLACE_EXISTING);
                                //Files.move(Path.of(wrongDirfile.getPath()), properDir);
                            } catch (Exception e) {
                            }
                        }
                    }
                }


//System.out.println(student.id+"compile");
                List<Double> scoreList = new LinkedList<>();
                boolean ishalf = false;

                for(File submittedFile : problemDir.listFiles()){
                    boolean thereIssugo = false;
                    if(submittedFile.getName().contains(".yo")){

                        String[] pieces = submittedFile.getName().split("\\.");
                        String filenameWOyo = pieces[0];

                        for(File submittedFile2 : problemDir.listFiles()) {
                            if (submittedFile2.getName().equals(filenameWOyo + ".sugo")) {
                                thereIssugo = true;
                            }
                        }
                        if(!thereIssugo) {
                            ishalf = true;
                        //System.out.println(student.id+" will get half score.");
                        }
                    }
                    if(ishalf)
                        break;
                }

                if(problem.wrappersDirPath != null) {
                    File wrappersDir = new File(problem.wrappersDirPath);
                    for (File wrapfile : wrappersDir.listFiles()) {
                        String filename = wrapfile.getName(); //.split("\\.");
                            //System.out.println(wrapfile.getName());
                        Path dest = Paths.get(problemDir.getPath() + "/" + filename);
                        try {
                            if (wrapfile.exists()) {
                            //System.out.println("copy~~");
                                Files.copy(Path.of(wrapfile.getPath()), dest);
                            }
                        } catch (Exception e) {
                        }
                    }
                }

                boolean compileError = false;

                for(File compileFile : problemDir.listFiles()){
                    //System.out.println(compileFile.getName());
                    if(compileFile.getName().contains(".sugo")) {
                        Compiler compiler = new Compiler();
                        try {
                            compiler.compile(compileFile.getPath());
                        } catch (CompileErrorException e) {
                            compileError = true;
                            //System.out.println(compileFile.getName()+" compile Error");
                        } catch (FileSystemRelatedException | InvalidFileTypeException e) {
                            e.printStackTrace();
                        }
                        if (compileError == true) {
                            break;
                        }
                    }
                    else{
                        //System.out.println(compileFile.getName()+ "is not sugo");
                    }
                }

                String[] tmp = problem.targetFileName.split("\\.");
                String filename = tmp[0];

                File targetFile = new File(problemDir.getPath()+"/"+filename+".yo");
                //System.out.println(problemDir.getPath()+"/"+filename+".yo");
                boolean noFile = false;
                if(!targetFile.exists()){
                    //System.out.println(student.id+" "+problem.id+" no target yo file");
                    noFile = true;
                }

                for(TestCase testCase : problem.testCases){
                    if(compileError||noFile){
                            scoreList.add((double) 0);
                    }
                    else{
                        boolean runTimeError = false;
                        String testNumber = testCase.inputFileName.split("\\.")[0];
                        Executer executer = new Executer();
                        File outputFile = new File(problemDir.getPath()+"/"+filename+"_"+testNumber+".out");
                        try {
                            executer.execute(targetFile.getPath(), problem.testCasesDirPath+"/"+testCase.inputFileName, outputFile.getPath());
                        } catch (RunTimeErrorException e) {
                            scoreList.add((double)0);
                            runTimeError = true;
                        } catch (InvalidFileTypeException | FileSystemRelatedException e) {
                            e.printStackTrace();
                        }
                        if(outputFile.exists()&&runTimeError==false){
                            //System.out.println(problem.id+" is being scored..");
                            List<String> submissionOutput = new LinkedList<>();
                            List<String> requiredOutput = new LinkedList<>();
                            try {
                                submissionOutput = Files.readAllLines(Path.of(outputFile.getPath()));
                                requiredOutput = Files.readAllLines(Path.of(problem.testCasesDirPath + "/" + testCase.outputFileName));
                            } catch (Exception e) {
                            }
                            if(giveScore(submissionOutput,requiredOutput,problem.judgingTypes)){
                                if(ishalf)
                                    scoreList.add(testCase.score/2);
                                else
                                    scoreList.add(testCase.score);
                            }
                            else
                                scoreList.add((double) 0);
                            /*
                            if (problem.judgingTypes.contains("trailing-whitespaces")) {
                                if (problem.judgingTypes.contains("case-insensitive")) {
                                    if (checkWithWhitespace(submissionOutput, requiredOutput, true)) {
                                        if(ishalf)
                                            scoreList.add(testCase.score/2);
                                        else
                                            scoreList.add(testCase.score);
                                    }
                                    else
                                        scoreList.add((double) 0);
                                } else {
                                    if (checkWithWhitespace(submissionOutput, requiredOutput, false)) {
                                        if(ishalf)
                                            scoreList.add(testCase.score/2);
                                        else
                                            scoreList.add(testCase.score);
                                    }
                                    else
                                        scoreList.add((double) 0);
                                }
                            } else if (problem.judgingTypes.contains("ignore-whitespaces")) {
                                if (problem.judgingTypes.contains("case-insensitive")) {
                                    if (checkWithoutWhitespace(submissionOutput, requiredOutput, true)) {
                                        if(ishalf)
                                            scoreList.add(testCase.score/2);
                                        else
                                            scoreList.add(testCase.score);
                                    }
                                    else
                                        scoreList.add((double) 0);
                                } else {
                                    if (checkWithoutWhitespace(submissionOutput, requiredOutput, false)) {
                                        if(ishalf)
                                            scoreList.add(testCase.score/2);
                                        else
                                            scoreList.add(testCase.score);
                                    }
                                    else
                                        scoreList.add((double) 0);
                                }
                            }
                            else if (problem.judgingTypes.contains("case-insensitive")) {
                                if (checkWithoutWhitespace(submissionOutput, requiredOutput, true)) {
                                    if (ishalf)
                                        scoreList.add(testCase.score / 2);
                                    else
                                        scoreList.add(testCase.score);
                                } else
                                    scoreList.add((double) 0);
                            }

                             */
                        }


                    }
                }
                scoremap.put(problem.id, scoreList);
            }
            score.put(student.id, scoremap);
        }

        return score;
    }



    public boolean checkWithWhitespace(List<String> submissionOutput, List<String> requiredOutput, boolean CASE_INSENSITIVE){

        String firstWhitespace ="";
        if(submissionOutput.isEmpty()){
            return false;
        }
        for(int i=0; i<submissionOutput.get(submissionOutput.size()-1).length(); i++){
            char a = submissionOutput.get(submissionOutput.size()-1).charAt(i);
            if(a==' '||a=='\n'||a=='\t'){
                firstWhitespace = firstWhitespace+a;
            }
            else
                break;
        }


        String tmp = submissionOutput.get(submissionOutput.size()-1).trim();
        submissionOutput.remove(submissionOutput.size()-1);
        submissionOutput.add(firstWhitespace + tmp);


        String firstWhitespace2 ="";
        for(int i=0; i<requiredOutput.get(requiredOutput.size()-1).length(); i++){
            char a = requiredOutput.get(requiredOutput.size()-1).charAt(i);
            if(a==' '||a=='\n'||a=='\t'){
                firstWhitespace2 = firstWhitespace2+a;
            }
            else
                break;
        }


        String tmp2 = requiredOutput.get(requiredOutput.size()-1).trim();
        requiredOutput.remove(requiredOutput.size()-1);
        requiredOutput.add(firstWhitespace2 + tmp2);

        /*
        submissionOutput.get(submissionOutput.size()-1).replace("\t", "");
        submissionOutput.get(submissionOutput.size()-1).replace("\n", "");
        System.out.println(submissionOutput);

        requiredOutput.get(submissionOutput.size()-1).replace(" ", "");
        requiredOutput.get(submissionOutput.size()-1).replace("\t", "");
        requiredOutput.get(submissionOutput.size()-1).replace("\n", "");

         */

/*
        if((requiredOutput.lastIndexOf("\n")|requiredOutput.lastIndexOf("\t")|requiredOutput.lastIndexOf(" "))== requiredOutput.size()-1)
            requiredOutput.remove(requiredOutput.size());
        if((submissionOutput.lastIndexOf("\n")|submissionOutput.lastIndexOf("\t")|submissionOutput.lastIndexOf(" "))== submissionOutput.size()-1)
            submissionOutput.remove(requiredOutput.size());

 */

/*
        if(CASE_INSENSITIVE){
            for(String i : requiredOutput){
                for(String j: submissionOutput){
                    if (!i.equalsIgnoreCase(j)){
                        return false;
                    }
                }
            }
            return true;
        }
        else {
            if (submissionOutput.containsAll(requiredOutput)) {
                return true;
            } else {
                return false;
            }
        }
 */
        return checkCase(submissionOutput, requiredOutput, CASE_INSENSITIVE);

    }

    public boolean checkWithoutWhitespace(List<String> submissionOutput, List<String> requiredOutput, boolean CASE_INSENSITIVE){
        //System.out.println("hehe");
        if(submissionOutput.isEmpty()){
            return false;
        }

        String submissionOutput2 = "";
        String requiredOutput2 = "";
        for(String i: submissionOutput){
                //i = i.replaceAll("\\s+", "");
                i = i.replaceAll("\\p{Z}", "");
                i = i.replaceAll("\\t", "");
                i = i.replaceAll("\\n", "");
                submissionOutput2 = submissionOutput2 + i;
        }

        for(String i: requiredOutput){
                //i = i.replaceAll("[\\s+|\\u00A0]", "");
                i = i.replaceAll("\\p{Z}", "");
                i = i.replaceAll("\\t", "");
                i = i.replaceAll("\\n", "");
                requiredOutput2 = requiredOutput2 + i;
        }

        if(CASE_INSENSITIVE){
            if (!requiredOutput2.equalsIgnoreCase(submissionOutput2)){
                return false;
            }
            else
                return true;
        }

        else {
            if (submissionOutput2.equals(requiredOutput2)) {
                return true;
            } else {
                return false;
            }
        }

        /*
        List<String> submissionOutput2 = new LinkedList<>();
        submissionOutput2.addAll(submissionOutput);
        List<String> requiredOutput2 = new LinkedList<>();
        requiredOutput2.addAll(requiredOutput);
        for(String i: requiredOutput2){
            requiredOutput.remove(i);
            i = i.replaceAll("[\\s+|\\u00A0]", "");
            i = i.replaceAll("\\p{Z}", "");
            i = i.replaceAll("\\t", "");
            i = i.replaceAll("\\n", "");
            requiredOutput.add(i);

        }
        for(String i: submissionOutput2){
            submissionOutput.remove(i);
            i = i.replaceAll("\\s+", "");
            i = i.replaceAll("\\p{Z}", "");
            i = i.replaceAll("\\t", "");
            i = i.replaceAll("\\n", "");
            submissionOutput.add(i);
        }
        //System.out.println(submissionOutput);

         */
/*
        if(CASE_INSENSITIVE){
            for(String i : requiredOutput){
                for(String j: submissionOutput){
                    if (!i.equalsIgnoreCase(j)){
                        return false;
                    }
                }
            }
            return true;
        }
        else {
            if (submissionOutput.containsAll(requiredOutput)) {
                return true;
            } else {
                return false;
            }
        }

 */
    }

    public boolean checkCase (List<String> submissionOutput, List<String> requiredOutput, boolean CASE_INSENSITIVE){
        if(submissionOutput.isEmpty()){
            return false;
        }
        String submissionOutput2 = "";
        String requiredOutput2 = "";
        for(String i : requiredOutput){
            requiredOutput2 = requiredOutput2 + i;
        }
        for(String i : submissionOutput){
            submissionOutput2 = submissionOutput2 + i;
        }

        if(CASE_INSENSITIVE){

            if(submissionOutput2.equalsIgnoreCase(requiredOutput2)){
                return true;
            }
            else
                return false;

            /*
            for(String i : requiredOutput){
                for(String j: submissionOutput){
                    if (!i.equalsIgnoreCase(j)){
                        return false;
                    }
                }
            }
            for(String i : submissionOutput){
                for(String j: requiredOutput){
                    if (!i.equalsIgnoreCase(j)){
                        return false;
                    }
                }
            }

             */
        }
        else {
            if (submissionOutput2.equals(requiredOutput2)) {
                return true;
            } else {
                return false;
            }
        }
    }

    public boolean giveScore(List<String> submissionOutput, List<String> requiredOutput, Set<String> judgingTypes){
        if(judgingTypes == null || judgingTypes.isEmpty()){
            checkCase(submissionOutput,requiredOutput,false);
        }

        else if(judgingTypes.contains(Problem.TRAILING_WHITESPACES)&&judgingTypes.contains(Problem.IGNORE_WHITESPACES)&&judgingTypes.contains(Problem.CASE_INSENSITIVE)){
            return checkWithoutWhitespace(submissionOutput, requiredOutput, true)&&checkWithWhitespace(submissionOutput, requiredOutput, true);
        }

        else if(judgingTypes.contains(Problem.TRAILING_WHITESPACES)){
            if(judgingTypes.contains(Problem.IGNORE_WHITESPACES)){
                return checkWithoutWhitespace(submissionOutput, requiredOutput, false)&&checkWithWhitespace(submissionOutput, requiredOutput, false);

            }else if(judgingTypes.contains(Problem.CASE_INSENSITIVE)){
                return checkWithWhitespace(submissionOutput,requiredOutput,true);
            }
            else{
                //System.out.println("what?");
                return checkWithWhitespace(submissionOutput, requiredOutput, false);
            }
        }

        else if(judgingTypes.contains(Problem.IGNORE_WHITESPACES)){
            if(judgingTypes.contains(Problem.CASE_INSENSITIVE)){
                return checkWithoutWhitespace(submissionOutput,requiredOutput,true);
            }
            else{
                return checkWithoutWhitespace(submissionOutput, requiredOutput, false);
            }
        }

        else if(judgingTypes.contains(Problem.CASE_INSENSITIVE)){
            return checkCase(submissionOutput,requiredOutput,true);
        }
        return checkCase(submissionOutput,requiredOutput,false);
    }
}

