package com.gooddies.utils;


import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author sad
 */
public abstract class HangableMethod {
    private Thread thread;
    private Exception exception;
    private long awaitMilliseconds = 10000;

    public HangableMethod() {
    }

    public HangableMethod(long awaitMilliseconds) {
        this.awaitMilliseconds = awaitMilliseconds;
    }

    public abstract void doTask() throws Exception;

    public boolean run() throws Exception {
        final ReentrantLock lock = new ReentrantLock();
        final Condition condition = lock.newCondition();
        thread = new Thread() {
            @Override
            public void run() {
                try {
                    doTask();
                } catch (Exception ex) {
                    if(!(ex instanceof InterruptedException)){
                        ex.printStackTrace();
                        exception = ex;
                    }
                }
                lock.lock();
                try {
                    condition.signal();
                } finally {
                    lock.unlock();
                }
            }
        };
        thread.start();
        lock.lockInterruptibly();
        try {
            boolean result=condition.await(awaitMilliseconds, TimeUnit.MILLISECONDS);
            if(result==false){
                killThread();
                return false;
            }
        } catch (InterruptedException ex) {
            killThread();
            return false;
        } finally {
            lock.unlock();
        }
        if (exception != null) {
            throw exception;
        }
        return true;
    }

    private void killThread(){
        if(thread!=null){
            thread.stop(new InterruptedException("Killing job"));
            thread=null;
        }
    }
    
    public Exception getException() {
        return exception;
    }

    public boolean tryToRun(int attempts) throws Exception {
        for (int i = 0; i < attempts; i++) {
            try {
                exception = null;
                if (run()) {
                    return true;
                }
            } catch (Exception ex) {
                //should be ignored
            }
        }
        return false;
    }
}
