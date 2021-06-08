using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using SiaqoHomework.Entities;
using Sqo;

namespace SiaqoHomework.Repository
{
    public class BookRepository : BaseRepository<Book>
    {
        public BookRepository(Siaqodb db) : base(db)
        {
        }

        public List<Book> GetByAuthors(String authorStr)
        {
            return base.Query()
                .Where(book => book.Authors
                    .Exists(author => author.Contains(authorStr)))
                .ToList();
        }
    }
}
