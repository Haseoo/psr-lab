using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace SiaqoHomework.Entities
{
    public class Book
    {
        public int OID { get; set; }
        public string Title { get; set; }
        public string Category { get; set; }
        public List<String> Authors { get; set; }


        public override string ToString()
        {
            return $"{nameof(OID)}: {OID}," +
                   $" {nameof(Title)}: {Title}," +
                   $" {nameof(Category)}: {Category}," +
                   $" {nameof(Authors)}:" + 
                   (Authors.Count == 0 ? "" :
                   Authors.Aggregate((i, j) => i + "," + j));
        }
    }
}
