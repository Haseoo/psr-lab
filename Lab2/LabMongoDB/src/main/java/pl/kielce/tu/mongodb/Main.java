package pl.kielce.tu.mongodb;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import lombok.SneakyThrows;
import org.bson.Document;
import pl.kielce.tu.mongodb.data.BusCourse;
import pl.kielce.tu.mongodb.data.BusDriver;
import pl.kielce.tu.mongodb.data.BusRoute;

import java.util.Random;
import java.util.Scanner;

import static com.mongodb.client.model.Filters.*;

public class Main {
    public static boolean isNotNullNorEmpty(String s) {
        return s != null && !s.isEmpty() && !s.isBlank();
    }

    private static int getId(String name) {
        Scanner scan = new Scanner(System.in);
        System.out.printf("Enter id of %s\n", name);
        return scan.nextInt();
    }

    private static String getRouteFrom() {
        Scanner scan = new Scanner(System.in);
        System.out.println("Enter place name");
        return scan.nextLine();
    }

    private static int selectOperation() {
        int option = -1;
        System.out.println("Available operations:");
        System.out.println("1. Add driver");
        System.out.println("2. Delete driver");
        System.out.println("3. Update driver");
        System.out.println("4. List drivers");
        System.out.println("5. Add route");
        System.out.println("6. Delete route");
        System.out.println("7. Update route");
        System.out.println("8. List routes");
        System.out.println("9. Add course");
        System.out.println("10. Delete course");
        System.out.println("11. Get driver by id");
        System.out.println("12. Get route by id");
        System.out.println("13. Get routes by start point");
        System.out.println("14. Give drivers a rise");
        System.out.println("15. Exit");
        Scanner scan = new Scanner(System.in);
        while (option < 0 || option > 15) {
            System.out.print("Select operation: ");
            option = scan.nextInt();
        }
        return option;
    }

    private static void fillBusDriver(BusDriver busDriver) {
        Scanner scan = new Scanner(System.in);
        String line;
        System.out.println("Enter name");
        line = scan.nextLine();
        if (isNotNullNorEmpty(line)) {
            busDriver.setName(line);
        }

        System.out.println("Enter surname");
        line = scan.nextLine();
        if (isNotNullNorEmpty(line)) {
            busDriver.setSurname(line);
        }

        System.out.println("Enter age");
        line = scan.nextLine();
        if (isNotNullNorEmpty(line)) {
            busDriver.setAge(Integer.parseInt(line));
        }

        System.out.println("Enter salary");
        line = scan.nextLine();
        if (isNotNullNorEmpty(line)) {
            busDriver.setSalary(Double.parseDouble(line));
        }
    }

    private static void fillRoute(BusRoute busRoute) {
        Scanner scan = new Scanner(System.in);
        String line;
        System.out.println("Enter from");
        line = scan.nextLine();
        if (isNotNullNorEmpty(line)) {
            busRoute.setFrom(line);
        }

        System.out.println("Enter to");
        line = scan.nextLine();
        if (isNotNullNorEmpty(line)) {
            busRoute.setTo(line);
        }
    }

    private static void fillCourse(BusCourse course) {
        Scanner scan = new Scanner(System.in);
        String line;
        System.out.println("Enter departure time");
        line = scan.nextLine();
        if (isNotNullNorEmpty(line)) {
            course.setDepartureTime(line);
        }
    }


    @SneakyThrows
    public static void main(String[] args) {
        var uri = new MongoClientURI("mongodb://student01:student01@localhost:27017/bus");
        var mongoClient = new MongoClient(uri);
        var db = mongoClient.getDatabase("bus");
        var drivers = db.getCollection("drivers");
        var routes = db.getCollection("routes");
        var rnd = new Random();
        var mapper = new ObjectMapper();
        int option = -1;
        while (option != 15) {
            option = selectOperation();

            if(option == 1) {
                var driver = new BusDriver();
                driver.setId(rnd.nextInt(7000));
                fillBusDriver(driver);
                drivers.insertOne(Document.parse(mapper.writeValueAsString(driver)));
            }

            else if (option == 2) {
                var id = getId("driver");
                var driver = drivers.find(eq("_id", id)).first();
                if (driver != null && !driver.isEmpty()) {
                    deleteCourses(drivers, routes, driver);
                    drivers.deleteOne(eq("_id", id));
                }
            }

            else if(option == 3) {
                var id = getId("driver");
                var document = drivers.find(eq("_id", id)).first();
                if(document == null || document.isEmpty()) {
                    System.out.println("Not found");
                    continue;
                }
                var driver = mapper.readValue(document.toJson(), BusDriver.class);
                fillBusDriver(driver);
                var doc = Document.parse(mapper.writeValueAsString(driver));
                drivers.updateOne(eq("_id", driver.getId()),
                        new Document("$set", doc));
            }

            else if (option == 4) {
                for (Document document : drivers.find()) {
                    System.out.println(document.toJson());
                }
            }

            else if(option == 5) {
                var route = new BusRoute();
                route.setId(rnd.nextInt(7000));
                fillRoute(route);
                routes.insertOne(Document.parse(mapper.writeValueAsString(route)));
            }

            else if(option == 6) {
                var id = getId("route");
                var route = routes.find(eq("_id", id)).first();
                if (route != null && !route.isEmpty()) {
                    deleteCourses(drivers, routes, route);
                    routes.deleteOne(eq("_id", id));
                }
            }

            else if(option == 7) {
                var id = getId("route");
                var document = routes.find(eq("_id", id)).first();
                if(document == null || document.isEmpty()) {
                    System.out.println("Not found");
                    continue;
                }
                var route = mapper.readValue(document.toJson(), BusRoute.class);
                fillRoute(route);
                var doc = Document.parse(mapper.writeValueAsString(route));
                routes.updateOne(eq("_id", route.getId()),
                        new Document("$set", doc));
            }

            else if (option == 8) {
                for (Document document : routes.find()) {
                    System.out.println(document.toJson());
                }
            }

            else if(option == 9) {
                var course = new BusCourse();
                var driverId = getId("driver");
                var driverDocument = drivers.find(eq("_id", driverId)).first();
                if(driverDocument == null || driverDocument.isEmpty()) {
                    System.out.println("Not found");
                    continue;
                }
                var routeId = getId("route");
                var routeDocument = routes.find(eq("_id", routeId)).first();
                if(routeDocument == null || routeDocument.isEmpty()) {
                    System.out.println("Not found");
                    continue;
                }
                course.setId(rnd.nextInt(7000));
                fillCourse(course);
                var courseDoc = Document.parse(mapper.writeValueAsString(course));
                drivers.updateOne(driverDocument, Updates.push("courses", courseDoc));
                routes.updateOne(routeDocument, Updates.push("courses", courseDoc));
                System.out.printf("Added course with id %s%n", course.getId());
            }

            else if(option == 10) {
                var courseId = getId("course");
                deleteCourse(drivers, routes, courseId);
            }

            else if(option == 11) {
                var id = getId("driver");
                var document = drivers.find(eq("_id", id)).first();
                if(document == null || document.isEmpty()) {
                    System.out.println("Not found");
                    continue;
                }
                System.out.println(document.toJson());
            }

            else if(option == 12) {
                var id = getId("route");
                var document = routes.find(eq("_id", id)).first();
                if(document == null || document.isEmpty()) {
                    System.out.println("Not found");
                    continue;
                }
                System.out.println(document.toJson());
            }

            else if(option == 13) {
                var place = getRouteFrom();
                System.out.println(place);
                for (Document route : routes.find(eq("from", place))) {
                    System.out.println(route.toJson());
                }
            }

            else if (option == 14) {
                var driversToUpdate = drivers.find(and(gt("age", 50), lt("age", 80)));
                for (Document document : driversToUpdate) {
                    var increase = document.get("age", Integer.class) + 50;
                    var salary = document.get("salary", Double.class);
                    var newSalary = (double) Math.round(salary * increase) / 100;
                    drivers.updateOne(document, Updates.set("salary", newSalary));
                }
            }

            System.in.read();
        }
        mongoClient.close();
    }

    private static void deleteCourses(MongoCollection<Document> drivers, MongoCollection<Document> routes, Document route) {
        var courses = route.getList("courses", Document.class);
        for (Document course : courses) {
            deleteCourse(drivers, routes, course.get("_id", Integer.class));
        }
    }

    private static void deleteCourse(MongoCollection<Document> drivers, MongoCollection<Document> routes, int courseId) {
        var match =  new BasicDBObject("_id", courseId);
        var routesWithCourse = routes.find(elemMatch("courses", match));
        var driversWithCourse = drivers.find(elemMatch("courses", match));
        for (Document document : routesWithCourse) {
            routes.updateOne(document, Updates.pull("courses", match));
        }
        for (Document document : driversWithCourse) {
            drivers.updateOne(document, Updates.pull("courses", match));
        }
    }
}
