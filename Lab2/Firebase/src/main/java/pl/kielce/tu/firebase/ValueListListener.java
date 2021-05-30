package pl.kielce.tu.firebase;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import lombok.SneakyThrows;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

public class ValueListListener<T> implements ValueEventListener {
    private final Class<T> type;
    private final Semaphore mutex;
    private final List<T> list;

    @SneakyThrows
    public ValueListListener(Class<T> type) {
        this.type = type;
        list = new ArrayList<>();
        mutex = new Semaphore(1);
        mutex.acquire();
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        try {
            for (DataSnapshot child : dataSnapshot.getChildren()) {
                list.add(child.getValue(type));
            }
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
    List<T> getValue() {
        mutex.acquire();
        var retList = this.list;
        mutex.release();
        return retList;
    }
}
