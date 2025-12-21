package scheduling;

import java.util.concurrent.PriorityBlockingQueue;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class TiredExecutor {

    private final TiredThread[] workers;
    private final PriorityBlockingQueue<TiredThread> idleMinHeap = new PriorityBlockingQueue<>();
    private final AtomicInteger inFlight = new AtomicInteger(0);

    public TiredExecutor(int numThreads) {
        workers = new TiredThread[numThreads]; 
        for(int i=0; i<numThreads;i++){
            workers[i] = new TiredThread(i,Math.random()+0.5);
            workers[i].start();
            idleMinHeap.add(workers[i]);
        }
    }

    public void submit(Runnable task) {
        try{
            //add ot in flight and take from heap thread
        inFlight.addAndGet(1);
        TiredThread toSubmTiredThread = idleMinHeap.take();
        //wraaeps the task and add to heap in the end 
        Runnable wrapedTask = () -> {
            try{
                task.run();
            } finally{
                int reamin = inFlight.decrementAndGet();
                idleMinHeap.offer(toSubmTiredThread);
                if(reamin ==0){
                    synchronized(this){
                        this.notifyAll();
                    }
                }
            };
        };
        //submit to thread
        toSubmTiredThread.newTask(wrapedTask);
        }catch(InterruptedException e){

        }
    }

    public void submitAll(Iterable<Runnable> tasks) {
        for (Runnable task : tasks){
            submit(task);
        }
        synchronized(this){
            while(inFlight.get()>0){
                try{
                    wait();
                }catch(InterruptedException e){
                    System.out.print(e);
                }
                
            }
        }
    }

    public void shutdown() throws InterruptedException {
        for(TiredThread worker : workers){
            if(worker.isBusy()){
                throw new InterruptedException("thread interapted while working");
            }
            worker.shutdown();
        }
    }

    public synchronized String getWorkerReport() {
        StringBuilder report = new StringBuilder();
        for(TiredThread worker: workers){
            report.append(
                "worker " + worker.getWorkerId() + " is now busy(true/false) -" + worker.isBusy() + 
                " has worked " + worker.getTimeUsed() + " time "
                +"has rested for " + worker.getTimeIdle()
                 +" and with fatigue score of " + worker.getFatigue()+ '\n' 
            );
            
            
        }
        return report.toString();
    }
}
