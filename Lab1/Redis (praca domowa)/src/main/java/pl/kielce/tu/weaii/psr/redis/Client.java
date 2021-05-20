package pl.kielce.tu.weaii.psr.redis;

import lombok.SneakyThrows;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.ScanParams;
import redis.clients.jedis.ScanResult;

import java.util.*;

public class Client {
    private static int getId(String name) {
        Scanner scan = new Scanner(System.in);
        System.out.printf("Enter id of %s\n", name);
        return scan.nextInt();
    }

    private static String getGroup() {
        Scanner scan = new Scanner(System.in);
        System.out.println("Enter group");
        return scan.nextLine();
    }

    private static double getGrade() {
        Scanner scan = new Scanner(System.in);
        var grade = 0.0;
        do {
            System.out.println("Enter grade");
            grade = scan.nextDouble();
        } while (grade < 2 || grade > 5);
        return grade;
    }

    private static Map<String, String> getStudentData() {
        Map<String, String> data = new HashMap<>();
        Scanner scan = new Scanner(System.in);

        System.out.println("Enter name");
        data.put("name", scan.nextLine());
        System.out.println("Enter surname");
        data.put("surname", scan.nextLine());
        System.out.println("Enter group");
        data.put("group", scan.nextLine());
        System.out.println("Enter birth year");
        data.put("birthYear", scan.nextLine());
        System.out.println(data);
        return data;
    }

    private static Map<String, String> getModuleData() {
        Map<String, String> data = new HashMap<>();
        Scanner scan = new Scanner(System.in);

        System.out.println("Enter name");
        data.put("name", scan.nextLine());
        System.out.println("Enter ects");
        data.put("ects", scan.nextLine());
        System.out.println(data);
        return data;
    }

    private static int selectOperation() {
        int option = -1;
        System.out.println("Available operations:");
        System.out.println("1. Add student");
        System.out.println("2. Delete student");
        System.out.println("3. Update student");
        System.out.println("4. List students");
        System.out.println("5. Add module");
        System.out.println("6. Delete module");
        System.out.println("7. Update module");
        System.out.println("8. List modules");
        System.out.println("9. Grade student");
        System.out.println("10. Delete grade");
        System.out.println("11. Find student by id");
        System.out.println("12. Find module by id");
        System.out.println("13. Print students from group");
        System.out.println("14. Print grades of student");
        System.out.println("15. Print student average grade");
        System.out.println("16. Print student average grade (cluster-side)");
        System.out.println("17. Exit");
        Scanner scan = new Scanner(System.in);
        while (option < 0 || option > 17) {
            System.out.print("Select operation: ");
            option = scan.nextInt();
        }
        return option;
    }

    @SneakyThrows
    public static void main(String[] args) {
        var client = new Jedis();
        var rnd = new Random();

        System.out.println("Welcome to the best USOS ever!");
        int option = -1;


        while (option != 17) {
            option = selectOperation();
            if (option == 1) {
                var dataMap = getStudentData();
                var id = String.format("student#%s", rnd.nextInt(2137));
                putEntity(client, id, dataMap);
            } else if (option == 2) {
                var id = String.format("student#%s",  getId("student"));
                client.del(id);
                client.del(String.format("grade#%s", id));

            } else if (option == 3) {
                var id = String.format("student#%s", getId("student"));
                var student = client.hgetAll(id);
                if (student == null || student.size() == 0) {
                    System.out.println("Student not found");
                } else {
                    var dataMap = getStudentData();
                    updateEntity(student, dataMap);
                    putEntity(client, id, student);
                }
            } else if (option == 4) {
                printHashWithPattern(client, "student#*");
            } else if (option == 5) {
                var dataMap = getModuleData();
                var id = String.format("module#%s", rnd.nextInt(2137));
                putEntity(client, id, dataMap);
            } else if (option == 6) {
                var id = String.format("module#%s", getId("module"));
                client.del(id);
                var scanParams = new ScanParams().match("grade#*");
                String cursor = ScanParams.SCAN_POINTER_START;
                boolean cycleIsFinished = false;
                while (!cycleIsFinished) {
                    ScanResult<String> scan = client.scan(cursor, scanParams);
                    for (String s : scan.getResult()) {
                        var grade = client.hgetAll(s);
                        if (grade != null && grade.containsKey(id)) {
                            client.hdel(s, id);
                        }
                    }
                    cursor = scan.getCursor();
                    if (cursor.equals("0")) {
                        cycleIsFinished = true;
                    }
                }
            } else if (option == 7) {
                var id = String.format("module#%s", getId("module"));
                var module = client.hgetAll(id);
                if (module == null || module.size() == 0) {
                    System.out.println("Module not found");
                } else {
                    var dataMap = getModuleData();
                    updateEntity(module, dataMap);
                    putEntity(client, id, module);
                }
            } else if (option == 8) {
                printHashWithPattern(client, "module#*");
            } else if (option == 9) {
                var sid = String.format("student#%s", getId("student"));
                var student = client.hgetAll(sid);
                if(student == null || student.isEmpty()) {
                    System.out.println("Student not found");
                    continue;
                }
                var mid = String.format("module#%s", getId("module"));
                var module = client.hgetAll(mid);
                if(module == null || module.isEmpty()) {
                    System.out.println("Module not found");
                    continue;
                }
                var grade = getGrade();
                var gradeId = String.format("grade#%s", sid);
                client.hset(gradeId, mid, Double.toString(grade));
            } else if (option == 10) {
                var sid = String.format("student#%s", getId("student"));
                var mid = String.format("module#%s", getId("module"));
                var gid = String.format("grade#%s",sid);
                var grade= client.hgetAll(gid);
                if (grade != null && !grade.containsKey(mid)) {
                    client.hdel(gid, mid);
                }
            } else if (option == 11) {
                var studentId = String.format("student#%s", getId("student"));
                var student = client.hgetAll(studentId);
                if (student != null && !student.isEmpty()) {
                    System.out.println(student);
                } else {
                    System.out.println("Now found!");
                }
            } else if (option == 12) {
                var moduleId = String.format("module#%s", getId("module"));
                var module = client.hgetAll(moduleId);
                if (module != null && !module.isEmpty()) {
                    System.out.println(module);
                } else {
                    System.out.println("Now found!");
                }
            } else if(option == 13) {
                var group = getGroup();
                var scanParams = new ScanParams().match("student#*");
                String cursor = ScanParams.SCAN_POINTER_START;
                boolean cycleIsFinished = false;
                while (!cycleIsFinished) {
                    ScanResult<String> scan = client.scan(cursor, scanParams);
                    for (String s : scan.getResult()) {
                        var student = client.hgetAll(s);
                        if (student.containsKey("group") && student.get("group").equals(group)) {
                            System.out.printf("%s %s%n", s, student);
                        }
                    }
                    cursor = scan.getCursor();
                    if (cursor.equals("0")) {
                        cycleIsFinished = true;
                    }
                }
            } else if (option == 14) {
                var sid = String.format("student#%s", getId("student"));
                var gid = String.format("grade#%s", sid);
                System.out.printf("Grades of %s%n", sid);
                for (Map.Entry<String, String> gradeEntry : client.hgetAll(gid).entrySet()) {
                    System.out.printf("%s -> %s%n", client.hgetAll(gradeEntry.getKey()).get("name"),
                            gradeEntry.getValue());
                }
            } else if (option == 15) {
                var sid = String.format("student#%s", getId("student"));
                var gid = String.format("grade#%s", sid);
                var ectsSum = 0;
                var sum = 0.0;
                for (Map.Entry<String, String> gradeEntry : client.hgetAll(gid).entrySet()) {
                    var module = client.hgetAll(gradeEntry.getKey());
                    var ects = Integer.parseInt(module.get("ects"));
                    sum += Double.parseDouble(gradeEntry.getValue()) * ects;
                    ectsSum += ects;
                }
                if (ectsSum == 0) {
                    System.out.println("No grades!");
                } else {
                    System.out.printf("Average grade of student is %s\n", sum / (double) ectsSum);
                }
            } else if (option == 16) {
                var sid = String.format("student#%s", getId("student"));
                var gid = String.format("grade#%s", sid);
                System.out.println(client.eval(StringUtils.LUA_SCRIPT, 1, "g", gid));
            }
            System.in.read();
        }
        client.close();
    }

    private static void updateEntity(Map<String, String> module, Map<String, String> dataMap) {
        for (Map.Entry<String, String> entry : dataMap.entrySet()) {
            if (StringUtils.isNullNorEmpty(entry.getValue())) {
                module.put(entry.getKey(), entry.getValue());
            }
        }
    }

    private static void putEntity(Jedis client, String id, Map<String, String> entityValues) {
        for (Map.Entry<String, String> studentEntry : entityValues.entrySet()) {
            client.hset(id, studentEntry.getKey(), studentEntry.getValue());
        }
    }

    private static void printHashWithPattern(Jedis client, String pattern) {
        var scanParams = new ScanParams().match(pattern);
        String cursor = ScanParams.SCAN_POINTER_START;
        boolean cycleIsFinished = false;
        while (!cycleIsFinished) {
            ScanResult<String> scan = client.scan(cursor, scanParams);
            for (String s : scan.getResult()) {
                System.out.printf("%s %s%n", s, client.hgetAll(s));
            }
            cursor = scan.getCursor();
            if (cursor.equals("0")) {
                cycleIsFinished = true;
            }
        }
    }
}
