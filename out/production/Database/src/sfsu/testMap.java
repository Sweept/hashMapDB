/*
package sfsu;

import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.*;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

 
public class testMap
{
	static Semaphore readLock = new Semaphore(1);
    static Semaphore writeLock = new Semaphore(1);
    int readCount = 0;
    
    private final Map<String, String> database = new ConcurrentHashMap<String, String>();
     
    public void put(String key, String value)
    {
    	try {
    		//Acquire/wait Section
            writeLock.acquire();
            //Read
	        try
	        {
	            database.put(key, value);
	            System.out.println("Putting a new Key " + key + " with its value " + value + " for thread"+Thread.currentThread().getName());
	        }
			finally
			{
				//Release/signal section
				writeLock.release();
            }
    	}catch (InterruptedException e) {
            System.out.println(e.getMessage());
        }
	}
	 
	public String get(String key)
	{
		String value = new String();
		try {
            //Acquire/wait Section
            readLock.acquire();
            readCount++;
            if (readCount == 1) {
                writeLock.acquire();
            }
            readLock.release();

            //Read
            try
    	    {
    	    	value = database.get(key);
    	        System.out.println("Getting value for key " + key + " by thread "+Thread.currentThread().getName() + " value is " + value);
    	    }
    	    finally
    	    {
            //Release/signal section
    	    	readLock.acquire();
    	    	readCount--;
		        if(readCount == 0) 
		        {
		            writeLock.release();
		        }
		        readLock.release();
    	    }
        } catch (InterruptedException e) {
            System.out.println(e.getMessage());
        }
        return value;
	}
	
	public void delete(String key)
	{
		try {
			//Acquire/wait Section
            writeLock.acquire();
            //Read
		    try
		    {
		        System.out.println("Deleting key " + key + " by thread "+Thread.currentThread().getName());
		        database.remove(key);
		    }
		    finally
		    {
		    	//Release/signal section
		    	writeLock.release();
            } 
		}catch (InterruptedException e) {
            System.out.println(e.getMessage());
        }
	}

	public static void main(String[] args)
	{
		 testMap mydb = new testMap();
		 
	     mydb.put("1", "6");
	     mydb.put("2", "7");
	     mydb.put("3", "8");
	 
	     System.out.println("Printing the First Element : "+mydb.get("1"));
	     System.out.println("Printing the First Element : "+mydb.get("2"));
	     System.out.println("Printing the First Element : "+mydb.get("3"));
	}
}*/