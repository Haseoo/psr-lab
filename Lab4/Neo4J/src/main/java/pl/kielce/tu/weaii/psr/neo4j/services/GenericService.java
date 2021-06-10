package pl.kielce.tu.weaii.psr.neo4j.services;

import org.neo4j.ogm.session.Session;

abstract class GenericService<T>  {

	protected Session session;
	
	protected GenericService(Session session){
		this.session = session;
	}
	
	public T read(Long id) {
        return session.load(getEntityType(), id);
    }
	
	public Iterable<T> readAll() {
    	return session.loadAll(getEntityType());
    }

	public void delete(Long id) {
		var entity = session.load(getEntityType(), id);
		if (entity !=  null) {
        	session.delete(entity);
		}
    }
    
	public void deleteAll() {
    	session.deleteAll(getEntityType());
    }
    
	public void createOrUpdate(T entity) {
        session.save(entity);
    }

    abstract Class<T> getEntityType();
}