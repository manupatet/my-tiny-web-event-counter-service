package com.expedia.www.my.tiny.web.event.counter.service;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.expedia.www.my.tiny.web.event.counter.domain.EventFrequency;

public class EventCounter<T extends Comparable<T>> {
    
    private final static Logger logger = LogManager.getLogger(EventCounter.class);
    private final int timeUnit; // Time interval 

    private final int minutes;
    private final Queue<Map<T, AtomicInteger>> timeChunks;//This keeps a record of what to throw away in time 'timeUnit'.
    private final ReentrantLock lock;

    private ConcurrentSkipListSet<EventFrequency<T>> sortedEvents;
    private Map<T, EventFrequency<T>> eventFreqMap;
    private long currentMinute = 0; // Could be made volatile, but for speed
    private Map<T, AtomicInteger> HEAD; //The current map of events

    /**
     * Constructor that defines how many minutes of history M the class will track for each event.
     *
     * @param time
     */
    public EventCounter(final int time, final int timeChunk) {
        this.minutes = time;
        this.timeUnit = timeChunk;
        this.timeChunks = new LinkedList<>();
        this.currentMinute = currentMinute();
        this.lock = new ReentrantLock();
        this.HEAD = new ConcurrentHashMap<>(50, (float) 0.75, 20);
        this.timeChunks.offer(this.HEAD);
        this.sortedEvents = new ConcurrentSkipListSet<>();
        this.eventFreqMap = new ConcurrentHashMap<>(50, (float) 0.75, 20);
    }

    /**
     * Method to accept events streaming in. Should be able to handle hundreds or thousands of calls per second.
     *
     * Accuracy is sacrificed for speed, here.
     *
     * @param eventType
     */
    public void recordEvent(final T eventType) {
        final long diff = currentMinute() - this.currentMinute; 
        if (diff > 0) {
            // Toggle the condition as soon as possible, so no more threads enter the block
            this.currentMinute = currentMinute();

            // A few threads would be in by now, but don't worry, with tryLock, they'll fall through without hurting
            this.lock.tryLock();
            if (this.lock.isHeldByCurrentThread()) {
                try {
                    this.HEAD = pushElement();
                } finally {
                    this.lock.unlock();
                }
            } else {
                logger.info("Baah! "+Thread.currentThread().getName()+" fell through the crack [" + this.currentMinute + ", " + eventType + "]");
            }
        }

        recordEventInner(eventType);

    }

    private void recordEventInner(T eventType){

        if (!this.HEAD.containsKey(eventType)) {
            this.HEAD.put(eventType, new AtomicInteger(0));
        }
        this.HEAD.get(eventType).incrementAndGet();

        if (!this.eventFreqMap.containsKey(eventType)) {
            EventFrequency<T> freq = new EventFrequency<T>(eventType);
            this.eventFreqMap.put(eventType, freq);
        }

        //Should ideally happen atomically
        EventFrequency<T> ef = this.eventFreqMap.get(eventType);
        this.sortedEvents.remove(ef);
        ef.incrementAndGet();
        this.sortedEvents.add(ef);
        this.eventFreqMap.replace(eventType, ef);//For atomicity
    }

    /**
     * Method to give that returns counts for the specified eventType. Should provide a count of all events that have
     * occurred between the current time and current time – M minutes. Should be able to handle hundreds of calls per
     * second.
     *
     * Time complexity of this method is O(1)
     *
     * @param eventType
     * @return
     */
    public long getCount(final T eventType) {
        try{
            return eventFreqMap.get(eventType).get();
        } catch(Exception x){
            return 0;
        }
    }

    /**
     * Method that persists into an external data store a sorted set of the K most frequent events within the last M
     * minutes. Optionally may be called internally within the class on a recurring schedule.
     *
     * @param K
     */
    public String persistTopKCounts(final int K) {
        int i= 0;
        List<String> buf = new LinkedList<>(); 
        for(EventFrequency<T> entry: sortedEvents){
            if(i == K){
                break;
            }
            buf.add(entry.toString());
            i++;
        }

        return buf.toString();
    }

    /**
     * There's only going to be one thread accessing it each minute (TIMEUNIT).
     *
     * @return Map new HEAD
     */
    private Map<T, AtomicInteger> pushElement() {
        final Map<T, AtomicInteger> map = new ConcurrentHashMap<>(50, (float) 0.75, 20);
        this.timeChunks.offer(map);
        String s = "";
        if (this.timeChunks.size() > this.minutes) {
            s = " Trimming queue [" + this.timeChunks.size() + "] down to " + this.minutes + ". ";
            Map<T, AtomicInteger> discarded = this.timeChunks.poll();

            //Deep copy of datastructs.
            ConcurrentSkipListSet<EventFrequency<T>> se = new ConcurrentSkipListSet<>();
            Map<T, EventFrequency<T>> efm = new ConcurrentHashMap<>(50, (float) 0.75, 20);

            for(T evt: this.eventFreqMap.keySet()){
                EventFrequency<T> ef = eventFreqMap.get(evt);
                EventFrequency<T> efNew = new EventFrequency<T>(evt);
                efNew.set(ef.get());
                efm.put(evt, efNew);
                se.add(efNew);
            }

            for(T key: discarded.keySet()){
                int v = efm.get(key).get();
                int d = discarded.get(key).get();//freq count to be deducted from universal map
                if(v - d > 0){
                    EventFrequency<T> ef = efm.get(key);
                    se.remove(ef);
                    ef.set(v-d);
                    efm.replace(key, ef);
                    se.add(ef);
                }else{
                    EventFrequency<T> x = efm.remove(key);
                    se.remove(x);
                }
            }
            this.eventFreqMap = efm;
            this.sortedEvents = se;
        }

        logger.info("Time ++."+s);
        return map;
    }

    /**
     * Method returns time rounded to time unit TIMEUNIT
     *
     * @return millis rounded to the last minute
     */
    private long currentMinute() {
        final long cm = System.currentTimeMillis();
        return cm - cm % timeUnit;
    }
}