package pe.edu.pe.pucp.proyecto.dao;


public interface IDAO <T, ID> {
    T load(ID id);
    T save(T t);
    T update(T t);
    void remove(T t);
}

