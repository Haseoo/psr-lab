package pl.kielce.tu.weaii.psr.elasticsearch;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import pl.kielce.tu.weaii.psr.elasticsearch.entities.Animal;
import pl.kielce.tu.weaii.psr.elasticsearch.entities.Keeper;
import pl.kielce.tu.weaii.psr.elasticsearch.entities.Services.AnimalService;
import pl.kielce.tu.weaii.psr.elasticsearch.entities.Services.KeeperService;

import java.io.IOException;
import java.util.Random;
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
        var client = new RestHighLevelClient(RestClient.builder(
                new HttpHost("localhost",
                9200,
                HttpHost.DEFAULT_SCHEME_NAME)));
        var objectMapper = new ObjectMapper();

        var animalService = new AnimalService(client, objectMapper);
        var keeperService = new KeeperService(client, objectMapper);
        var rnd = new Random();

        int option = -1;

        while(option != 15) {
            if (option != -1) {
                System.in.read();
            }
            option = selectOperation();

            if (option == 1) {
                var keeper = new Keeper();
                keeper.setId((long)rnd.nextInt(700));
                fillKeeper(keeper);
                if (keeperService.store(keeper, keeper.getId())) {
                    System.out.println("Success");
                } else {
                    System.out.println("Error");
                }
            }

            else if (option == 2) {
                var id = getId("keeper");
                if (keeperService.delete(id)) {
                    System.out.println("Success");
                } else {
                    System.out.println("Error");
                }
            }

            else if (option == 3) {
                var id = getId("keeper");
                var keeperOpt = keeperService.getById(id);
                if (keeperOpt.isEmpty()) {
                    System.out.println("not found");
                    continue;
                }
                var keeper = keeperOpt.get();
                fillKeeper(keeper);
                if (keeperService.store(keeper, keeper.getId())) {
                    System.out.println("Success");
                } else {
                    System.out.println("Error");
                }
            }

            else if (option == 4) {
                for (Keeper keeper : keeperService.getAll()) {
                    System.out.println(keeper);
                }
            }

            else if (option == 5) {
                var animal = new Animal();
                fillAnimal(animal);
                animal.setId((long)rnd.nextInt(700));
                if (animalService.store(animal, animal.getId())) {
                    System.out.println("success");
                } else {
                    System.out.println("error");
                }
            }

            else if (option == 6) {
                var id = getId("animal");
                if (animalService.delete(id)) {
                    for (Keeper keeper : keeperService.getAll()) {
                        if (keeper.getAnimalTakenCareOf().removeIf(a -> a.getId().equals(id))) {
                            keeperService.store(keeper, keeper.getId());
                        }
                    }
                    System.out.println("success");
                } else {
                    System.out.println("error");
                }
            }

            else if (option == 7) {
                var id = getId("animal");
                var animalOpt = animalService.getById(id);
                if (animalOpt.isEmpty()) {
                    System.out.println("not found");
                    continue;
                }
                var animal = animalOpt.get();
                fillAnimal(animal);
                if (animalService.store(animal, animal.getId())) {
                    System.out.println("success");
                } else {
                    System.out.println("error");
                }
            }

            else if (option == 8) {
                for (Animal animal : animalService.getAll()) {
                    System.out.println(animal);
                }
            }

            else if (option == 9) {
                var kId = getId("keeper");
                var optionalKeeper = keeperService.getById(kId);
                if (optionalKeeper.isEmpty()) {
                    System.out.println("keeper not found");
                    continue;
                }
                var aId = getId("animal");
                var optionalAnimal = animalService.getById(aId);
                if (optionalAnimal.isEmpty()) {
                    System.out.println("animal not found");
                    continue;
                }
                var keeper = optionalKeeper.get();
                var animal = optionalAnimal.get();
                keeper.getAnimalTakenCareOf().add(animal);
                if (keeperService.store(keeper, keeper.getId())) {
                    System.out.println("success");
                } else {
                    System.out.println("error");
                }
            }

            else if (option == 10) {
                var kId = getId("keeper");
                var optionalKeeper = keeperService.getById(kId);
                if (optionalKeeper.isEmpty()) {
                    System.out.println("keeper not found");
                    continue;
                }
                var aId = getId("animal");
                var keeper = optionalKeeper.get();
                keeper.getAnimalTakenCareOf().removeIf(a -> a.getId().equals(aId));
                keeperService.store(keeper, keeper.getId());
            }

            else if (option == 11) {
                var id = getId("keeper");
                System.out.println(keeperService.getById(id)
                        .map(Keeper::toString).
                                orElse("not found"));
            }

            else if (option == 12) {
                var id = getId("animal");
                System.out.println(animalService.getById(id)
                        .map(Animal::toString).
                                orElse("not found"));
            }

            else if (option == 13) {
                var age = getAge();
                keeperService.getAll().stream()
                        .filter(k -> k.getAge() > age)
                        .forEach(System.out::println);
            }

            else if (option == 14) {
                for (Keeper keeper : keeperService.getAll()) {
                    if (keeper.getAnimalTakenCareOf().size() >= 3) {
                        keeper.setSalary(keeper.getSalary() * 1.5);
                        keeperService.store(keeper, keeper.getId());
                    }
                }
            }

        }
        client.close();
    }
}
