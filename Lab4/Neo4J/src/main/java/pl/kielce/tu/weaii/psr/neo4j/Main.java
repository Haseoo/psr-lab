package pl.kielce.tu.weaii.psr.neo4j;

import org.neo4j.ogm.config.Configuration;
import org.neo4j.ogm.session.SessionFactory;
import pl.kielce.tu.weaii.psr.neo4j.entities.Animal;
import pl.kielce.tu.weaii.psr.neo4j.entities.Keeper;
import pl.kielce.tu.weaii.psr.neo4j.services.AnimalService;
import pl.kielce.tu.weaii.psr.neo4j.services.KeeperService;

import java.io.IOException;
import java.util.Scanner;
import java.util.stream.StreamSupport;

public class Main {
    public static boolean isNotNullNorEmpty(String s) {
        return s != null && !s.isEmpty() && !s.isBlank();
    }

    private static Long getId(String name) {
        Scanner scan = new Scanner(System.in);
        System.out.printf("Enter id of %s\n", name);
        return scan.nextLong();
    }

    private static Integer getAge() {
        Scanner scan = new Scanner(System.in);
        System.out.println("Enter age");
        return scan.nextInt();
    }

    private static int selectOperation() {
        int option = -1;
        System.out.println("Available operations:");
        System.out.println("1. Add keeper");
        System.out.println("2. Delete keeper");
        System.out.println("3. Update keeper");
        System.out.println("4. List keepers");
        System.out.println("5. Add animal");
        System.out.println("6. Delete animal");
        System.out.println("7. Update animal");
        System.out.println("8. List animals");
        System.out.println("9. Add animal keeper");
        System.out.println("10. Remove animal keeper");
        System.out.println("11. Get keeper by id");
        System.out.println("12. Get animal by id");
        System.out.println("13. Get keepers older than");
        System.out.println("14. Give keeper a rise");
        System.out.println("15. Exit");
        var scan = new Scanner(System.in);
        while (option < 0 || option > 15) {
            System.out.print("Select operation: ");
            option = scan.nextInt();
        }
        return option;
    }

    public static void fillKeeper(Keeper keeper) {
        var scan = new Scanner(System.in);
        System.out.println("Enter name:");
        var line = scan.nextLine();
        if (isNotNullNorEmpty(line)) {
            keeper.setName(line);
        }

        System.out.println("Enter surname:");
        line = scan.nextLine();
        if (isNotNullNorEmpty(line)) {
            keeper.setSurname(line);
        }

        System.out.println("Enter age:");
        line = scan.nextLine();
        if (isNotNullNorEmpty(line)) {
            keeper.setAge(Integer.parseInt(line));
        }

        System.out.println("Enter salary:");
        line = scan.nextLine();
        if (isNotNullNorEmpty(line)) {
            keeper.setSalary(Double.parseDouble(line));
        }
    }

    public static void fillAnimal(Animal animal) {
        var scan = new Scanner(System.in);
        System.out.println("Enter name:");
        var line = scan.nextLine();
        if (isNotNullNorEmpty(line)) {
            animal.setName(line);
        }

        System.out.println("Enter location code:");
        line = scan.nextLine();
        if (isNotNullNorEmpty(line)) {
            animal.setLocationCode(line);
        }
    }



        public static void main(String[] args) throws IOException {
        var configuration = new Configuration.Builder()
                .uri("bolt://localhost:7687")
                .credentials("neo4j", "zaq12wsx")
                .build();
        var sessionFactory = new SessionFactory(configuration, "pl.kielce.tu.weaii.psr.neo4j");
        var session = sessionFactory.openSession();

        var animalService = new AnimalService(session);
        var keeperService = new KeeperService(session);
        int option = -1;

        while(option != 15) {
            if (option != -1) {
                System.in.read();
            }
            option = selectOperation();

            if (option == 1) {
                var keeper = new Keeper();
                fillKeeper(keeper);
                keeperService.createOrUpdate(keeper);
            }

            else if (option == 2) {
                var id = getId("keeper");
                keeperService.delete(id);
            }

            else if (option == 3) {
                var id = getId("keeper");
                var keeper = keeperService.read(id);
                if (keeper == null) {
                    System.out.println("not found");
                    continue;
                }
                fillKeeper(keeper);
                keeperService.createOrUpdate(keeper);
            }

            else if (option == 4) {
                for (Keeper keeper : keeperService.readAll()) {
                    System.out.println(keeper);
                }
            }

            else if (option == 5) {
                var animal = new Animal();
                fillAnimal(animal);
                animalService.createOrUpdate(animal);
            }

            else if (option == 6) {
                var id = getId("animal");
                animalService.delete(id);
            }

            else if (option == 7) {
                var id = getId("animal");
                var animal = animalService.read(id);
                if (animal == null) {
                    System.out.println("not found");
                    continue;
                }
                fillAnimal(animal);
                animalService.createOrUpdate(animal);
            }

            else if (option == 8) {
                for (Animal animal : animalService.readAll()) {
                    System.out.println(animal);
                }
            }

            else if (option == 9) {
                var kId = getId("keeper");
                var keeper = keeperService.read(kId);
                if (keeper == null) {
                    System.out.println("keeper not found");
                    continue;
                }
                var aId = getId("animal");
                var animal = animalService.read(aId);
                if (animal == null) {
                    System.out.println("animal not found");
                    continue;
                }
                keeper.getAnimalTakenCareOf().add(animal);
                keeperService.createOrUpdate(keeper);
            }

            else if (option == 10) {
                var kId = getId("keeper");
                var keeper = keeperService.read(kId);
                if (keeper == null) {
                    System.out.println("keeper not found");
                    continue;
                }
                var aId = getId("animal");
                keeper.getAnimalTakenCareOf().removeIf(a -> a.getId().equals(aId));
                keeperService.createOrUpdate(keeper);
            }

            else if (option == 11) {
                var id = getId("keeper");
                var keeper = keeperService.read(id);
                System.out.println(keeper != null ? keeper.toString() : "Not found");
            }

            else if (option == 12) {
                var id = getId("animal");
                var animal = animalService.read(id);
                System.out.println(animal != null ? animal.toString() : "Not found");
            }

            else if (option == 13) {
                var age = getAge();
                StreamSupport.stream(keeperService.readAll().spliterator(), false)
                        .filter(k -> k.getAge() > age)
                        .forEach(System.out::println);
            }

            else if (option == 14) {
                for (Keeper keeper : keeperService.readAll()) {
                    if (keeper.getAnimalTakenCareOf().size() > 3) {
                        keeper.setSalary(keeper.getSalary() * 1.1);
                        keeperService.createOrUpdate(keeper);
                    }
                }
            }

        }

        sessionFactory.close();

    }
}
