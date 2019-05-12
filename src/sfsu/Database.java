package sfsu;

import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.Semaphore;
import java.util.concurrent.*;

public class Database
{
	private Semaphore readLock = new Semaphore(1);
	private Semaphore writeLock = new Semaphore(1);
	private static int readCount = 0;
    
    private final Map<String, String> database = new ConcurrentHashMap<String, String>();
     
    public void put(String key, String value)
    {
        try
        {
    		//Acquire/wait Section
            writeLock.acquire();
            //Read
            database.put(key, value);
            System.out.println("DB: In Putting a new Key " + key + " with its value " + value + " for thread"+Thread.currentThread().getName());
        } catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally
		{
			//Release/signal section
			writeLock.release();
            System.out.println("DB: Out Putting a new Key " + key + " with its value " + value + " for thread"+Thread.currentThread().getName());
        }
 	}
	 
	public String get(String key)
	{
		String value = new String();
        
        try
	    {
            //Acquire/wait Section
            readLock.acquire();
            readCount++;
            if (readCount == 1) {
                writeLock.acquire();
            }
            readLock.release();
            //Read
	    	value = database.get(key);
	        System.out.println("DB: In Getting value for key " + key + " by thread "+Thread.currentThread().getName() + " value is " + value);
	    } catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    finally
	    {
        //Release/signal section
	    	try {
				readLock.acquire();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    	readCount--;
	        if(readCount == 0) 
	        {
	            writeLock.release();
	        }
	        readLock.release();
	        System.out.println("DB: Out Getting value for key " + key + " by thread "+Thread.currentThread().getName() + " value is " + value);
	    }
        return value;
	}
	
	public void delete(String key)
	{
		    try
		    {
	            writeLock.acquire();
		        System.out.println("DB: In Deleting key " + key + " by thread "+Thread.currentThread().getName());
		        database.remove(key);
		    } catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		    finally
		    {
		    	//Release/signal section
		    	writeLock.release();
		        System.out.println("DB: Out Deleting key " + key + " by thread "+Thread.currentThread().getName());
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