package com.example.newapp.global;

import java.util.Observable;

public class Response<T, E extends String> extends Observable {
    private T data;
    private E error;

    public void setData(T data) {
        synchronized (this) {
            this.data = data;
        }
        setChanged();
        notifyObservers();
    }

    public void setError(E error) {
        synchronized (this){
            this.error = error;
        }
        setChanged();
        notifyObservers();
    }

    public T getData() {
        return data;
    }

    public E getError() {
        return error;
    }
}
