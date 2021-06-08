using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Sqo;
using SiaqoHomework.Entities;
using SiaqoHomework.Repository;

namespace SiaqoHomework
{
    class Program
    {
        private static int SelectOperation() {
            int option = -1;
            Console.WriteLine("Available operations:");
            Console.WriteLine("1. Add member");
            Console.WriteLine("2. Delete member");
            Console.WriteLine("3. Update member");
            Console.WriteLine("4. List members");
            Console.WriteLine("5. Add book");
            Console.WriteLine("6. Delete book");
            Console.WriteLine("7. Update book");
            Console.WriteLine("8. List books");
            Console.WriteLine("9. Borrow a book");
            Console.WriteLine("10. Return a book");
            Console.WriteLine("11. Get member by id");
            Console.WriteLine("12. Get book by id");
            Console.WriteLine("13. Get books of author");
            Console.WriteLine("14. Get average book borrow count");
            Console.WriteLine("15. Exit");
            while (option < 0 || option > 15) {
                Console.WriteLine("Select operation: ");
                var sOption = Console.ReadLine();
                option = string.IsNullOrEmpty(sOption) ? -1 : int.Parse(sOption);
            }
            return option;
        }

        private static int GetId(String name) {
            Console.WriteLine($"Enter id of {name}");
            return int.Parse(Console.ReadLine() ?? "-1");
        }

        private static string GetAuthorStr() {
            Console.WriteLine("Enter author");
            return Console.ReadLine();
        }

        private static void FillMember(Member member)
        {
            Console.WriteLine("Enter name");
            var name = Console.ReadLine();
            if (!string.IsNullOrEmpty(name))
            {
                member.Name = name;
            }
            Console.WriteLine("Enter surname");
            var surname = Console.ReadLine();
            if (!string.IsNullOrEmpty(surname))
            {
                member.Surname = surname;
            }
        }

        private static void FillBook(Book book)
        {
            Console.WriteLine("Enter title");
            var title = Console.ReadLine();
            if (!string.IsNullOrEmpty(title))
            {
                book.Title = title;
            }
            Console.WriteLine("Enter category");
            var category = Console.ReadLine();
            if (!string.IsNullOrEmpty(category))
            {
                book.Category = category;
            }
            Console.WriteLine("Enter number of authors (<0 to skip)");
            var op = int.Parse(Console.ReadLine() ?? "-1");
            if (op >= 0)
            {
                var authors = new List<String>();
                for (int i = 0; i < op; i++)
                {
                    Console.WriteLine("Enter author:");
                    authors.Add(Console.ReadLine() ?? "");
                }
                book.Authors = authors;
            }
        }

        static void Main(string[] args)
        {
            var db = new Siaqodb("Data");
            var memberRepo = new MemberRepository(db);
            var bookRepo = new BookRepository(db);

            int option = -1;
            while (option != 15)
            {
                if (option != -1)
                {
                    Console.ReadLine();
                }
                option = SelectOperation();

                if (option == 1)
                {
                    var member = new Member();
                    FillMember(member);
                    member.CheckOuts = new List<Book>();
                    memberRepo.Store(member);
                }

                else if (option == 2)
                {
                    int id = GetId("member");
                    memberRepo.Delete(id);
                }

                else if (option == 3)
                {
                    int id = GetId("member");
                    var member = memberRepo.GetById(id);
                    if (member != null)
                    {
                        FillMember(member);
                        memberRepo.Store(member);
                    }
                    else
                    {
                        Console.WriteLine("Not found");
                    }
                }

                else if (option == 4)
                {
                    foreach (var member in memberRepo.GetAll())
                    {
                        Console.WriteLine(member);
                    }
                }

                else if (option == 5)
                {
                    var book = new Book();
                    FillBook(book);
                    bookRepo.Store(book);
                }

                else if (option == 6)
                {
                    int id = GetId("book");
                    bookRepo.Delete(id);
                }

                else if (option == 7)
                {
                    int id = GetId("book");
                    var book = bookRepo.GetById(id);
                    if (book != null)
                    {
                        FillBook(book);
                        bookRepo.Store(book);
                    }
                    else
                    {
                        Console.WriteLine("Not found");
                    }
                }

                else if (option == 8)
                {
                    foreach (var book in bookRepo.GetAll())
                    {
                        Console.WriteLine(book);
                    }
                }

                else if (option == 9)
                {
                    var memberId = GetId("member");
                    var member = memberRepo.GetById(memberId);
                    if (member == null)
                    {
                        Console.WriteLine("Member not found");
                        continue;
                    }

                    var bookId = GetId("book");
                    var book = bookRepo.GetById(bookId);
                    if (book == null)
                    {
                        Console.WriteLine("Book not found");
                        continue;
                    }

                    member.CheckOuts.Add(book);
                    memberRepo.Store(member);
                }

                else if (option == 10)
                {
                    var memberId = GetId("member");
                    var member = memberRepo.GetById(memberId);
                    if (member == null)
                    {
                        Console.WriteLine("Member not found");
                        continue;
                    }
                    var bookId = GetId("book");
                    member.CheckOuts.RemoveAll(book => book.OID == bookId);
                    memberRepo.Store(member);
                }

                else if (option == 11)
                {
                    int id = GetId("member");
                    Console.WriteLine(memberRepo.GetById(id));
                }

                else if (option == 12)
                {
                    int id = GetId("book");
                    Console.WriteLine(bookRepo.GetById(id));
                }

                else if (option == 13)
                {
                    string author = GetAuthorStr();
                    foreach (var book in bookRepo.GetByAuthors(author))
                    {
                        Console.WriteLine(book);
                    }
                }

                else if (option == 14)
                {
                    Console.WriteLine($"Average count is: {memberRepo.GetAverageBorrowCount()}");
                }

            }

            db.Close();
        }
    }
}
