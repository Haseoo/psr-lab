package pl.kielce.tu.weaii.psr.cassandra;

import com.datastax.oss.driver.api.core.CqlSession;

import java.io.IOException;
import java.util.*;

public class Main {
    public static boolean isNotNullNorEmpty(String s) {
        return s != null && !s.isEmpty() && !s.isBlank();
    }

    private static int selectOperation() {
        int option = -1;
        System.out.println("Available operations:");
        System.out.println("1. Add member");
        System.out.println("2. Delete member");
        System.out.println("3. Update member");
        System.out.println("4. List members");
        System.out.println("5. Add book");
        System.out.println("6. Delete book");
        System.out.println("7. Update book");
        System.out.println("8. List books");
        System.out.println("9. Borrow a book");
        System.out.println("10. Return a book");
        System.out.println("11. Get member by id");
        System.out.println("12. Get book by id");
        System.out.println("13. Get books of author");
        System.out.println("14. Get average book borrow count");
        System.out.println("15. Exit");
        Scanner scan = new Scanner(System.in);
        while (option < 0 || option > 15) {
            System.out.print("Select operation: ");
            option = scan.nextInt();
        }
        return option;
    }

    private static Integer getId(String name) {
        Scanner scan = new Scanner(System.in);
        System.out.printf("Enter id of %s\n", name);
        return scan.nextInt();
    }

    private static String getAuthorStr() {
        Scanner scan = new Scanner(System.in);
        System.out.printf("Enter author\n");
        return scan.nextLine();
    }

    private static Map<String, String> getMember() {
        var map = new NonEmptyHashData();
        Scanner scan = new Scanner(System.in);
        System.out.println("Enter name");
        map.put("name", scan.nextLine());
        System.out.println("Enter surname");
        map.put("surname", scan.nextLine());
        return map;
    }

    private static Map<String, String> getBook(List<String> authors) {
        var map = new NonEmptyHashData();
        Scanner scan = new Scanner(System.in);
        System.out.println("Enter title");
        map.put("title", scan.nextLine());
        System.out.println("Enter category");
        map.put("category", scan.nextLine());
        System.out.println("Enter number of authors (<0 to skip)");
        var op = Integer.parseInt(scan.nextLine());
        if (op >= 0) {
            map.put("books", "authors");
            for(var i = 0; i < op; i++) {
                System.out.println("Enter author");
                authors.add(scan.nextLine());
            }
        }
        return map;
    }

    public static void main(String[] args) throws IOException {
        try (CqlSession session = CqlSession.builder().build()) {
            var keyspaceManager = new KeyspaceBuilderManager(session);
            keyspaceManager.useKeyspace();
            keyspaceManager.createKeyspace();
            var tableManager = new TableManager(session);
            tableManager.createBooksTable();
            tableManager.createMembersTable();

            var option = -1;
            var rnd = new Random();

            while(option != 15) {
                if (option != -1) {
                    System.in.read();
                }
                option = selectOperation();

                if (option == 1) {
                    var memberData = getMember();
                    tableManager.addMember(memberData, rnd.nextInt(700));

                }

                else if (option == 2) {
                    var id = getId("member");
                    if (tableManager.deleteById(id, "members")) {
                        System.out.println("success");
                    } else {
                        System.out.println("failed");
                    }
                }

                else if (option == 3) {
                    var id = getId("member");
                    var memberData = getMember();
                    if (tableManager.updateMember(id, memberData)) {
                        System.out.println("success");
                    } else {
                        System.out.println("Update failed");
                    }
                }

                else if (option == 4) {
                    tableManager.printAllMembers();
                }

                else if (option == 5) {
                    var bookAuthors = new ArrayList<String>();
                    var bookData = getBook(bookAuthors);
                    tableManager.addBook(bookData, bookAuthors, rnd.nextInt(700));
                }

                else if (option == 6) {
                    var id = getId("book");
                    tableManager.deleteAllBookCheckouts(id);
                    if (tableManager.deleteById(id, "books")) {
                        System.out.println("success");
                    } else {
                        System.out.println("failed");
                    }
                }

                else if (option == 7) {
                    var id = getId("book");
                    var authors = new ArrayList<String>();
                    var bookData = getBook(authors);
                    if (tableManager.updateBook(id, bookData, authors)) {
                        System.out.println("success");
                    } else {
                        System.out.println("Update failed");
                    }
                }

                else if (option == 8) {
                    tableManager.printAllBooks();
                }

                else if (option == 9) {
                    var memberId = getId("member");
                    var bookId = getId("book");
                    tableManager.borrowABook(memberId, bookId);
                }

                else if (option == 10) {
                    var memberId = getId("member");
                    var bookId = getId("book");
                    if (tableManager.deleteCheckOut(memberId, bookId)) {
                        System.out.println("success");
                    } else {
                        System.out.println("failed");
                    }
                }

                else if (option == 11) {
                    tableManager.printMemberById(getId("member"));
                }

                else if (option == 12) {
                    tableManager.printBookById(getId("book"));
                }

                else if (option == 13) {
                    var author = getAuthorStr();
                    tableManager.printBookWithAuthorsContainsString(author);
                }

                else if (option == 14) {
                    System.out.println(tableManager.getAverageBorrowCount());
                }
            }
        }
    }
}
