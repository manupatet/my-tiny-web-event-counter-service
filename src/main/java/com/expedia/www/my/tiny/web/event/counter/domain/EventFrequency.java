package com.expedia.www.my.tiny.web.event.counter.domain;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Encapsulating class lending itself to being compared
 * @author mpatet
 *
 * @param <T>
 */
public class EventFrequency<V extends Comparable<V>> implements Comparable<EventFrequency<V>>{
    
    private final V event;
    private final AtomicInteger count;
    
    public EventFrequency(V event) {
        this.event = event;
        count = new AtomicInteger(0);
    }
    
    @Override
    public boolean equals(Object obj) {
        try{
            @SuppressWarnings("unchecked")
            EventFrequency<V> that = (EventFrequency<V>) obj;
            return this.event.equals(that.event);
        }catch(Exception x){
            return false;
        }
    }

    @Override
    public int hashCode() {
        return event.hashCode();
    }

    @Override
    public int compareTo(EventFrequency<V> o) {
        int cmp = Integer.compare(o.count.get(), this.count.get());
        return cmp != 0? cmp: this.event.compareTo(o.event);
    }

    public int incrementAndGet(){
        return count.incrementAndGet();
    }

    public int get(){
        return count.get();
    }

    public void set(int newVal){
        count.set(newVal);
    }

    public V getEvent() {
        return event;
    }

    @Override
    public String toString() {
        return event+"_"+count;
    }
}