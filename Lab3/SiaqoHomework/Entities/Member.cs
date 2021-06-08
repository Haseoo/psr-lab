using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace SiaqoHomework.Entities
{
    public class Member
    {
        public int OID { get; set; }
        public string Name { get; set; }
        public string Surname { get; set; }
        public List<Book> CheckOuts { get; set; }

        public override string ToString()
        {
            return $"{nameof(OID)}: {OID}," +
                   $" {nameof(Name)}: {Name}," +
                   $" {nameof(Surname)}: {Surname}," +
                   $" {nameof(CheckOuts)}:" +
                   (CheckOuts.Count == 0 ? "" : CheckOuts
                       .Select(c => c.OID.ToString())
                       .Aggregate((i, j) => i + "," + j));
        }
    }
}
