package pl.kielce.tu.firebase;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import lombok.SneakyThrows;

import java.util.Optional;
import java.util.concurrent.Semaphore;

public class ValueListener<T> implements ValueEventListener {
    private final Class<T> type;
    private final Semaphore mutex;
    private T obj;

    @SneakyThrows
    public ValueListener(Class<T> type) {
        this.type = type;
        mutex = new Semaphore(1);
        mutex.acquire();
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        try {
            obj = dataSnapshot.getValue(type);
        } finally {
            mutex.release();
        }
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {
        try {
            System.out.println("An error has occurred" + databaseError.getMessage());
            System.out.println(databaseError.getDetails());
        } finally {
            mutex.release();
        }
    }

    @SneakyThrows
    Optional<T> getValue() {
        mutex.acquire();
        var ret = Optional.ofNullable(obj);
        mutex.release();
        return ret;
    }
}
