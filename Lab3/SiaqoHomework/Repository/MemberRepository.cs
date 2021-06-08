using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using SiaqoHomework.Entities;
using Sqo;

namespace SiaqoHomework.Repository
{
    class MemberRepository : BaseRepository<Member>
    {
        public MemberRepository(Siaqodb db) : base(db)
        {
        }

        public double GetAverageBorrowCount()
        {
            return base.Query().Select(member => member.CheckOuts.Count).Average();
        }
    }
}
