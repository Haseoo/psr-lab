package pl.kielce.tu.firebase;

import com.google.api.core.ApiFuture;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import lombok.SneakyThrows;
import pl.kielce.tu.firebase.data.BusCourse;
import pl.kielce.tu.firebase.data.BusDriver;
import pl.kielce.tu.firebase.data.BusRoute;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class Main {
    public static boolean isNotNullNorEmpty(String s) {
        return s != null && !s.isEmpty() && !s.isBlank();
    }

    private static Integer getId(String name) {
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
    public static void main(String[] args) throws IOException {
        var serviceAccount = new FileInputStream("C:\\Users\\dawid\\Desktop/key.json");

        var options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .setDatabaseUrl("https://psr-lab-default-rtdb.europe-west1.firebasedatabase.app")
                .build();

        FirebaseApp.initializeApp(options);
        var dbRef = FirebaseDatabase.getInstance().getReference();
        var drivers = dbRef.child("drivers");
        var routes = dbRef.child("routes");

        var option = -1;
        var rnd = new Random();

        while (option != 15) {
            if (option != -1) {
                System.in.read();
            }
            option = selectOperation();
            if (option == 1) {
                var driver = new BusDriver();
                driver.setId(rnd.nextInt(7000));
                fillBusDriver(driver);
                drivers.child(driver.getId().toString())
                        .setValueAsync(driver)
                        .get();
            } else if (option == 2) {
                var id = getId("driver");
                var driverChild = drivers.child(id.toString());
                var driverListener = new ValueListener<>(BusDriver.class);
                driverChild.addListenerForSingleValueEvent(driverListener);
                var driverOptional = driverListener.getValue();
                if (driverOptional.isPresent()) {
                    for (BusCourse course : driverOptional.get().getCourses()) {
                        removeCourse(drivers, routes, course.getId());
                    }
                }
                driverChild.removeValueAsync().get();
            } else if (option == 3) {
                var id = getId("driver");
                var listener = new ValueListener<>(BusDriver.class);
                drivers.child(id.toString()).addListenerForSingleValueEvent(listener);
                var driverOpt = listener.getValue();
                driverOpt.ifPresentOrElse(driver -> {
                    updateDriver(drivers, id, driver);
                }, () -> System.out.println("Not found"));
            } else if (option == 4) {
                var listener = new ValueListListener<>(BusDriver.class);
                drivers.addListenerForSingleValueEvent(listener);
                for (BusDriver busDriver : listener.getValue()) {
                    System.out.println(busDriver);
                }
            }

            if (option == 5) {
                var route = new BusRoute();
                route.setId(rnd.nextInt(7000));
                fillRoute(route);
                routes.child(route.getId().toString())
                        .setValueAsync(route)
                        .get();
            } else if (option == 6) {
                var id = getId("route");
                var routeChild = routes.child(id.toString());
                var listener = new ValueListener<>(BusRoute.class);
                routeChild.addListenerForSingleValueEvent(listener);
                var routeOptional = listener.getValue();
                if (routeOptional.isPresent()) {
                    for (BusCourse course : routeOptional.get().getCourses()) {
                        removeCourse(drivers, routes, course.getId());
                    }
                }
                routeChild.removeValueAsync().get();
            } else if (option == 7) {
                var id = getId("route");
                var listener = new ValueListener<>(BusRoute.class);
                routes.child(id.toString()).addListenerForSingleValueEvent(listener);
                var routeOpt = listener.getValue();
                routeOpt.ifPresentOrElse(route -> {
                    updateRoute(routes, id, route);
                }, () -> System.out.println("Not found"));
            } else if (option == 8) {
                var listener = new ValueListListener<>(BusRoute.class);
                routes.addListenerForSingleValueEvent(listener);
                for (BusRoute route : listener.getValue()) {
                    System.out.println(route);
                }
            } else if (option == 9) {
                var driverOpt = getDriverByIdListener(drivers).getValue();
                if (driverOpt.isEmpty()) {
                    System.out.println("Not found");
                    continue;
                }
                var routeOpt = getRouteByIdListener(routes).getValue();
                if (routeOpt.isEmpty()) {
                    System.out.println("Not found");
                    continue;
                }
                var driver = driverOpt.get();
                var route = routeOpt.get();
                var course = new BusCourse();
                course.setId(rnd.nextInt(7000));
                fillCourse(course);
                driver.getCourses().add(course);
                route.getCourses().add(course);
                drivers.child(driver.getId().toString()).setValueAsync(driver).get();
                routes.child(route.getId().toString()).setValueAsync(route).get();
                System.out.println("Added course with id " + course.getId());
            } else if (option == 10) {
                var id = getId("course");
                removeCourse(drivers, routes, id);

            } else if (option == 11) {
                ValueListener<BusDriver> listener = getDriverByIdListener(drivers);
                listener.getValue().ifPresentOrElse(System.out::println,
                        () -> System.out.println("Not found"));
            } else if (option == 12) {
                ValueListener<BusRoute> listener = getRouteByIdListener(routes);
                listener.getValue().ifPresentOrElse(System.out::println,
                        () -> System.out.println("Not found"));
            } else if (option == 13) {
                var form = getRouteFrom();
                var listener = new ValueListListener<>(BusRoute.class);
                routes.addListenerForSingleValueEvent(listener);
                listener.getValue().stream()
                        .filter(route -> route.getFrom().equals(form))
                        .forEach(System.out::println);
            } else if (option == 14) {
                var listener = new ValueListListener<>(BusDriver.class);
                drivers.addListenerForSingleValueEvent(listener);
                var toUpdate = listener.getValue().stream()
                        .filter(driver -> driver.getAge() > 50 && driver.getAge() < 80)
                        .collect(Collectors.toList());
                var futures = new ArrayList<ApiFuture<Void>>();
                for (BusDriver busDriver : toUpdate) {
                    var increase = busDriver.getAge() + 50;
                    busDriver.setSalary((double) Math.round(busDriver.getSalary() * increase) / 100);
                    futures.add(drivers.child(busDriver.getId().toString()).setValueAsync(busDriver));
                }
                for (ApiFuture<Void> future : futures) {
                    future.get();
                }
            }

        }
    }

    private static void removeCourse(DatabaseReference drivers, DatabaseReference routes, Integer id)
            throws InterruptedException, ExecutionException {
        var driversLsn = new ValueListListener<>(BusDriver.class);
        drivers.addListenerForSingleValueEvent(driversLsn);
        var driversToUpdate = driversLsn.getValue().stream()
                .filter(driver -> driver.getCourses().stream()
                        .anyMatch(course -> course.getId().equals(id)))
                .collect(Collectors.toList());
        var routesLsn = new ValueListListener<>(BusRoute.class);
        routes.addListenerForSingleValueEvent(routesLsn);
        var routesToUpdate = routesLsn.getValue().stream()
                .filter(driver -> driver.getCourses().stream()
                        .anyMatch(course -> course.getId().equals(id)))
                .collect(Collectors.toList());
        for (BusDriver busDriver : driversToUpdate) {
            busDriver.getCourses().removeIf(c -> c.getId().equals(id));
            drivers.child(busDriver.getId().toString()).setValueAsync(busDriver).get();
        }

        for (BusRoute busRoute : routesToUpdate) {
            busRoute.getCourses().removeIf(c -> c.getId().equals(id));
            routes.child(busRoute.getId().toString()).setValueAsync(busRoute).get();
        }
    }

    private static ValueListener<BusDriver> getDriverByIdListener(DatabaseReference drivers) {
        var id = getId("driver");
        var listener = new ValueListener<>(BusDriver.class);
        drivers.child(id.toString()).addListenerForSingleValueEvent(listener);
        return listener;
    }

    private static ValueListener<BusRoute> getRouteByIdListener(DatabaseReference routes) {
        var id = getId("route");
        var listener = new ValueListener<>(BusRoute.class);
        routes.child(id.toString()).addListenerForSingleValueEvent(listener);
        return listener;
    }

    @SneakyThrows
    private static void updateDriver(DatabaseReference drivers, Integer id, BusDriver driver) {
        fillBusDriver(driver);
        drivers.child(id.toString()).setValueAsync(driver).get();
    }

    @SneakyThrows
    private static void updateRoute(DatabaseReference routes, Integer id, BusRoute route) {
        fillRoute(route);
        routes.child(id.toString()).setValueAsync(route).get();
    }
}
