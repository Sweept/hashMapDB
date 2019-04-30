package sfsu;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.*; 

 
public class Database
{
    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
     
    private final Lock readLock = readWriteLock.readLock();
 
    private final Lock writeLock = readWriteLock.writeLock();
 
    private final Map<String, String> database = new ConcurrentHashMap<String, String>();
     
    public void put(String key, String value)
    {
        writeLock.lock();
        try
        {
            database.put(key, value);
            System.out.println("Putting a new Key " + key + " with its value " + value + " for thread"+Thread.currentThread().getName());
        }
		finally{
		    writeLock.unlock();
		}
	}
	 
	public String get(String key)
	{
	    readLock.lock();
	    try
	    {
	    	String value = database.get(key);
	        System.out.println("Getting value for key " + key + " by thread "+Thread.currentThread().getName() + " value is " + value);
	        return value;
	    }
	    finally{
	        readLock.unlock();
	    }
	}
	
	public String delete(String key)
	{
		writeLock.lock();
	    try
	    {
	        System.out.println("Deleting key " + key + " by thread "+Thread.currentThread().getName());
	        return database.remove(key);
	    }
	    finally{
	    	writeLock.unlock();
	    }
	}

	public static void main(String[] args)
	{
		 Database mydb = new Database();
	     mydb.put("1", "6");
	     mydb.put("2", "7");
	     mydb.put("3", "8");
	 
	     System.out.println("Printing the First Element : "+mydb.get("1"));
	     System.out.println("Printing the First Element : "+mydb.get("2"));
	     System.out.println("Printing the First Element : "+mydb.get("3"));
	}
}