/*
 *   Copyright 2009 Joubin Mohammad Houshyar
 * 
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *    
 *   http://www.apache.org/licenses/LICENSE-2.0
 *    
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package org.jredis.examples;
import static org.jredis.Encode.decode;
import static org.jredis.Encode.encode;
import static org.jredis.Encode.toInt;
import static org.jredis.Encode.toStr;

import java.util.List;
import java.util.Random;

import org.jredis.ClientRuntimeException;
import org.jredis.Command;
import org.jredis.JRedis;
import org.jredis.RedisException;
import org.jredis.connector.ProviderException;
import org.jredis.ri.alphazero.JRedisClient;
import org.jredis.ri.alphazero.util.Log;


/**
 * [TODO: document me!]
 *
 * @author  Joubin Houshyar (alphazero@sensesay.net)
 * @version alpha.0, Apr 12, 2009
 * @since   alpha.0
 * 
 */
public class UsageExample {
	
	public static final String password = "jredis";
	public static void main(String[] args) throws RedisException {
		(new UsageExample()).start();
	}

	private void start() throws RedisException {
		
		JRedis redis = new JRedisClient();		
		goodForm (redis);
		
	}

	/**
	 * 
	 * @param redis
	 */
	public void goodTopLevelJRedisForm (JRedis redis) 
	{
		try { 
			redis.ping();
		}
		catch (RedisException e) {
			try {
				redis.auth(password);
			}
			catch (RedisException e1) { throw new RuntimeException ("auth attempt failed too", e);}
			catch (ProviderException bug) { throw new RuntimeException ("buggy client code", bug);}
			catch (ClientRuntimeException problem) { throw new RuntimeException ("is that server running?", problem);}
		}
		finally {
			// do your stuff ..
			useIT (redis);
			
			// then ciao!
			redis.quit();
		}
	}
	
	public void goodForm (JRedis redis) {
		try {
			redis.auth ("jredis");
			
			// alright, lets try it out
			useIT (redis);
		}
		catch (RedisException e) { throw new RuntimeException ("auth no doubt", e);}
		catch (ProviderException bug) { throw new RuntimeException ("buggy client code", bug);}
		catch (ClientRuntimeException problem) { throw new RuntimeException ("is that server running?", problem);}
	}	
	
	/**
	 * Provides example usage (and test) of the Redis SET {@link Command}s.
	 * 
	 * @param redis a connected instance of {@link JRedisClient}
	 */
	private void useIT(JRedis redis) {
		
		try {
			playWithMaps (redis);
			playWithSets (redis);
		}
		catch (ProviderException bug) {
			bug.printStackTrace();
			Log.problem ("redis client code has a bug: " + bug.getLocalizedMessage());
		}
		catch (ClientRuntimeException problem) {
			System.out.println ("bad network, server not running, that sort of stuff" + problem.getLocalizedMessage());
			problem.printStackTrace();
			System.exit(-1);
		}
	}
	
	/**
	 * 
	 * @param redis
	 * @return
	 */
	private boolean playWithMaps (JRedis redis) {
		boolean worked = false;
//		System.out.println ("\n\n *** playing with Maps (redis \"strings\") ..\n");
		try {
			
			/*  
			 * first lets set up 
			 */
			
			// Redis can handle UTF-8 keys 
			//
			String asciiKey = "ascii-key";
			String utf8key_russian = "фывапро";
			String utf8key_chinese = "漢字[汉字]";
												String utf8key_Persian = "مهندس";
			String variousKeys[] = {asciiKey, utf8key_russian, utf8key_chinese, utf8key_Persian};
			String value = "some data";
			
			for(String key : variousKeys){
//				System.out.format("using %s as key for SET ...", key);
				redis.set(key, value);
				if(!toStr(redis.get(key)).equals(value))
					System.err.println ("well, that didn't work -- when using key " + key);
//				System.out.format("...and we get:\n\t  %s => '%s'\n", key, value);
			}
			
			// id
			
			String key4 = "idx";
			String key3 = "woof";
			
			// we'll use db 2
			redis.select (2);
			redis.flushdb ();
			

			// *SET* *GET*
			
			// set with UTF-8 keys
			
			// set with a big value 
			
			int len = 1024 * 768;
			byte[]	bigstuff = new byte[len];
			
//			System.out.format ("Creating a %d byte buffer and filling it with random bytes ...", len);
			Random random = new Random(System.currentTimeMillis());
			random.nextBytes(bigstuff);
			
//			System.out.format ("and saving it ...");
			redis.set("bar", bigstuff);
//			System.out.format (" and done.\n\n");
			
//			System.out.format ("Now lets get it back and check it against the original ...");

			byte[] data = redis.get("bar");

			
			for (int i=0; i<data.length; i++)
				if(data[i] != bigstuff[i]) 
					System.out.format("data we got back differs at index %d -- bug\n", i);
//			System.out.format (" and done.\n\n");

			// *SET*
			// using the Encode conversion methods to show how to deal with the byte[] interface
			
			long idx = redis.incr("test::idx");
			redis.set(key4, idx);
			
			String strValue = null;
			strValue = toStr(redis.get(key4));
			if(idx != Integer.parseInt(strValue))
				System.out.format("%d should be represented as %s but its not -- bug\n", idx, strValue);

			int		intValue = -1;
			intValue = toInt(redis.get(key4));
			if(idx != intValue)
				System.out.format("%d should be equal to %d but its not -- bug\n", idx, intValue);
			
			// *SET*
			// yep, we can store java objects 
			// lets create an object and store it
			//
//			System.out.format ("Creating a serializable java object and saving it to redis ...");

			SimpleBean	byteMe = new SimpleBean ("cafebabe");
			System.out.format ("\n\n\t => %s\n\n", byteMe);
			redis.set (key3, byteMe);
			
//			System.out.format ("and done.  \nNow lets get it back and compare to the original ...");
			
			SimpleBean	again = decode (redis.get(key3));
			
			if (!again.equals(byteMe)) 
//			{
//				System.out.format ("\n\n\t => %s\n\n", again);
//				System.out.format ("(Its our own %s! (isn't this fun?))\n\n", again.getName());
//			}
//			else
				System.err.format("Here's a problem:  these are supposed to be the same object! %s & %s\n", byteMe, again);

			// lets create and save a bunch
			// 
			
			int objcnt = 100;
//			System.out.format ("Creating and saving %d Java objects to redis ...", objcnt);
			for(int i=1; i<objcnt; i++){
				SimpleBean	obj = new SimpleBean ("bean #" + i);
				redis.set("objects::byteme::" + redis.incr("byteme::next_id"), encode(obj));
				redis.sadd("object_set", obj);
			}
//			System.out.format (" and done.\n");
			
			List<SimpleBean>  members = decode (redis.smembers("object_set"));
			for(SimpleBean obj : members) {
				System.out.format("a member of 'object-set' => %s\n", obj.toString());
			}
			List<String>  keys = redis.keys("objects::byteme*");
//			System.out.format ("Getting our %d objects back from redis (we'll use KEYS here as well) ...", objcnt);
			for(String key : keys) {
				again = decode(redis.get(key));
			}
//			System.out.format (" and done.\n");

			// *INCR*
			// Lets count up
			
			int max = 100;
			String cntrkey = "__NEXT_ID__";
			long	   cntr = -1;
			
//			System.out.format("\ncounting up to %d, INCR on key %s ...", max, cntrkey);
			for (long i=1; i<max; i++)
				if((cntr = redis.incr(cntrkey)) != i) {
					System.err.format("Here's a problem:  these are supposed to be the same object! %s & %s\n", cntr, i);
					throw new RuntimeException ("i: " + i);
				}
				
//			System.out.format (" and done.  \nNow count back down using DECR ...");
			// back to zero
			for (long i=cntr-1; i>0; i--)
				if((cntr = redis.decr(cntrkey)) != i) {
					System.err.format("Here's a problem:  these are supposed to be the same object! %s & %s\n", cntr, i);
					System.err.format("Here's a problem:  these are supposed to be the same object! %s & %s\n", cntr, i);
					throw new RuntimeException ("i: " + i);
				}
			
//			System.out.println (" and done.");
				
			worked = true;  // all ok
		}
		catch (RedisException e) { 
			System.err.format("did something wrong using sets => %s\n", e.getLocalizedMessage());
			e.printStackTrace();
		}
//		System.out.println ("... and everything is fine!");
		return worked;
	}
	
	/**
	 * 
	 * @param redis
	 * @return
	 */
	private boolean playWithSets (JRedis redis) {
		boolean worked = false;
		
//		System.out.println ("\n\n *** playing with sets ..");
		try {
			int max = 100;
			
			/*  
			 * first lets set up 
			 */
			
			// ** SADD **
			// lets add some stuff to set1
			// value_n, where n is an integer 0 to max
			String setkey = "testing:we-are-123";
			for (int i=0; i<max; i++)  
				redis.sadd(setkey, ("value_" + i).getBytes());
			
			// lets add some stuff to set1
			// value_n, where n is an odd integer 1 to max
			String evenset = "testing:we-are-even";
			for (int i=0; i<max; i=i+2) 
				redis.sadd(evenset, ("value_" + i).getBytes());
			
			// lets add some stuff to set1
			// value_n, where n is an even integer 0 to max
			String oddset = "testing:we-are-odd";
			for (int i=1; i<max; i=i+2) 
				redis.sadd(oddset, ("value_" + i).getBytes());
			
			/* 
			 * ok - now lets do some (more) set ops 
			 */
			
			// ** SCARD **
			// what's the cardinality of this set? better be max
			if(redis.scard(setkey) != max) 
				System.out.format("? says %s has cardinality of %d and not %d as expected -- bug\n", setkey, redis.scard(setkey), max);
				
			// ** SINTER **
			// what do these sets have in common?
			
			// (p.s. its a pain to convert the results from byte[] to String
			// if you know its a string, use a helper to wrap the call
			// next release TODO will address this with enhanced JRedis interface)
			
			List<String> strList = null;
			strList = toStr (redis.sinter(setkey, evenset));
			if(strList.size() == 0) 
				System.out.format("? says %s and %s have nothing in common -- bug\n", setkey, evenset);

			// ** SINTERSTORE **
			// set intersect and store
			// same as before, but now we're saving the results ...
			
			String common_1 = "testing:even-ones";
			String common_2 = "testing:odd-ones";
			redis.sinterstore(common_1, setkey, evenset);
			redis.sinterstore(common_2, setkey, oddset);
			
			// ** SMEMBERS **
			// lets print it out
			List<String>  reslist = toStr(redis.smembers(common_1));
			if(reslist.size() != redis.scard(common_1)) 
//			{
//				System.out.format("\n%s: {", common_1);
//				for(String odd : reslist)
//					System.out.format("%s ", odd);
//				System.out.println("}\n");
//			}
//			else
				System.out.format("smembers returns a list that has less members than scard on same -- bug\n");
			
			// evens and odds should have nothing in common .. 
			// ** SINTER **
			redis.sinter(common_1, common_2);
			if(redis.sinter(common_1, common_2).size() != 0) 
				System.out.format("huh? -- bug\n");
			
			// now say we remove from intset whatever it has in common with evenset
			// (we're using 2 calls to redis so this is not atomic, o/c, keep that in mind)
			// ** SINTER ** SREM ** RENAME
			for(byte[] m : redis.sinter(setkey, evenset))
				redis.srem(setkey, m);
			
			// JRedis will return the new key for convenience
			// if something went wrong, there will be an exception
			setkey = redis.rename(setkey, "isnt-it-odd?");
			
			
			// now lets check what they have in common
			// 
			strList = toStr(redis.sinter(setkey, evenset));
			if(strList.size() != 0) 
				for(String m : strList) 
					System.out.format("%s is *still* in both - bug!\n", m);

			// any keyspace command can be used on a set key
			// sets can be renamed - 
			// ** MOVE ** SELECT ** RENAME **
			
			/* redis.rename("nosuchkeyinmydb2!", "set1"); // <= this will throw an exception - try it  */
			
			redis.move(oddset, 3);
			redis.select(3);
			
			/* redis.get(oddset); // <== will throw an exception -- try it */
			if(!redis.keys(oddset).contains(oddset)) {
				System.out.format("set %s was supposed to have been moved to db 3 so where is it?  bug!\n", oddset);
			}
			oddset = redis.rename(oddset, "oddly-i-am-moved");
			

			// ** SORT **
			// Sort is a special form in JRedis.  You just add the clauses
			// you want on the Query and then call exec();
			
			long rescount = redis.scard(oddset); // we'll take the whole thing
			List<byte[]> results = redis.sort(oddset).LIMIT(0, rescount).DESC().ALPHA().exec();
//			System.out.format("\n%s %s %s %d %s =>: {", "SORT ", oddset, "LIMIT 0 ", rescount, "DESC ALPHA");
			for (String m : toStr(results)){
				System.out.format ("%s ", m);
			}
			System.out.println("}\n");

			worked = true;  // all ok
		}
		catch (RedisException e) { 
			System.err.format("did something wrong using sets => %s\n", e.getLocalizedMessage());
			e.printStackTrace();
		}
//		System.out.println ("... and everything is fine!");
		return worked;
	}
}
