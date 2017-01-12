package com.expedia.www.my.tiny.web.event.counter.domain;

public class Event<T extends Comparable<T>> implements Comparable<Event<T>>{
    private final T event;

    public Event(T event) {
        this.event = event;
    }

    public T getEvent() {
        return event;
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean equals(Object obj) {
        try{
            return this.event.equals(((Event<T>)obj).event);
        }catch (Exception x){
            return false;
        }
    }

    @Override
    public int hashCode() {
        return event.hashCode();
    }

    @Override
    public int compareTo(Event<T> o) {
        return this.event.compareTo(o.event);
    }

    @Override
    public String toString() {
        return String.valueOf(event);
    }
}
