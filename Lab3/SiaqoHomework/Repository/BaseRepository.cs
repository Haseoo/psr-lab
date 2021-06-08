using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Sqo;
using Sqo.Documents;

namespace SiaqoHomework.Repository
{
    public abstract class BaseRepository<T>
    {
        protected BaseRepository(Siaqodb db)
        {
            _db = db;
        }

        private readonly Siaqodb _db;
        public void Store(T obj)
        {
            _db.StoreObject(obj);
        }

        public T GetById(int oid)
        {
            return _db.LoadObjectByOID<T>(oid);
        }

        protected IEnumerable<T> Query()
        {
            return _db.Query<T>();
        }

        public IList<T> GetAll()
        {
            return _db.LoadAll<T>();
        }

        public void Delete(int oid)
        {
            var obj = GetById(oid);
            if (obj != null)
            {
                _db.Delete(obj);
            }
        }
    }
}
