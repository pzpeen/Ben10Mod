package net.pzpeen.ben10mod.systems;

public class Playlist<T> {
    private final T[] items;
    private int pointer = 0;

    @SuppressWarnings("unchecked")
    public Playlist(){
        this.items = (T[]) new Object[10];
    }

    public boolean add(T item){
        if(pointer < 10){
            this.items[pointer] = item;
            pointer++;
            return true;
        }
        return false;
    }

    public boolean set(int pos, T item){
        if(pos < 10){
            this.items[pos] = item;
            return true;
        }else{
            return false;
        }

    }

    public T get(int pos){
        if(pos < 10){
            return this.items[pos];
        }else{
            return null;
        }
    }

}
