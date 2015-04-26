#Get going with JRedis - here's how:

# Introduction #

So how do you use JRedis?  Pull the code ([r16](https://code.google.com/p/jredis/source/detail?r=16)) or download from [github](http://github.com/alphazero/jredis/downloads).

# Details #

JRedis is a specification and a reference implementation.  Currently there is one implementation providing (blocking semantics on method calls) for a passive client (that uses the caller's thread to do its job).

This initial client can not be shared across threads, but you can certainly either put it behind a synchronized gate, or, fire up a whole bunch for each one of your threads, as you prefer.

**If you do share an instance from behind a facade, do note** that redis connections are stateful, and if you plan on using the facility to switch between dbs using jredis.select(db) [SELECT db](redis.md) it is almost guaranteed to be bad idea to share a single connection across multiple threads.  However, if you will not be using select, then there should be no problems with sharing a single connection from behind a facade.  If switching dbs is required, then you will need to create dedicated JRedis instances per thread.  (This is very much a server issue and not JRedis specific.)

Alright, that said, here is a barebones JRedis app -- HelloAgain:

```
package org.jredis.examples;

import org.jredis.ClientRuntimeException;
import org.jredis.Command;
import org.jredis.JRedis;
import org.jredis.RedisException;
import org.jredis.connector.ProviderException;
import org.jredis.ri.alphazero.JRedisClient;
import org.jredis.ri.alphazero.support.Encode;

/**
 * Note this program will set a (hopefully non-coliding!) key in your DB 13.
 * 
 * @author Joubin Houshyar
 *
 */
public class HelloAgain {
	public static final String key = "jredis::examples::HelloAgain::message";
	public static void main(String[] args) {
		String password = "";
		if(args.length > 0) password  = args[0];
		new HelloAgain().run(password);
	}

	private void run(String password) {
		try {
			JRedis	jredis = new JRedisClient();

			if(!password.equals("")) 
				jredis.auth(password);
			
			jredis.ping().select(13);
			
			if(!jredis.exists(key)) {
				jredis.set(key, "Hello Again!");
				System.out.format("Hello!  You should run me again!\n");
				return;
			}
			
			String msg = Encode.toStr ( jredis.get(key) );
			
			System.out.format("%s\n", msg);
		}
		catch (RedisException error){
			if (error.getCommand()==Command.PING){
				System.out.format("I'll need that password!  Try again with password as command line arg for this program.\n");
			}
		}
		catch (ProviderException bug){
			System.out.format("Oh no, an 'un-documented feature':  %s\nKindly report it.", bug.getMessage());
		}
		catch (ClientRuntimeException problem){
			System.out.format("%s\n", problem.getMessage());
		}
	}
}

```

The essentials:

1) **Get a connection implementing JRedis interface**. (You'll want to code to this interface to minimize the impact of changes behind the scene) like this:
```
    JRedis      jredis = new JRedisClient();
```

2) **Do you have a `requirepass jredis` in your 'redis.conf'** ?  Then do this:
```
   jredis.auth(password);
```

3) Use the JRedis api, which is an analog of the Redis command set.

Want to **bind a value to a key** (map semantics)?

Use the Redis 'String' commands:
```
    jredis.set(myKey, myValue); 
```

**What can 'myKey' be?**
Any `java.lang.String` value that does **not** contain \r, \n, and space.  Other than that, Redis and JRedis support UTF-8 keys:

```
			String asciiKey = "ascii-key";
			String utf8key_Russian = "фывапро";
			String utf8key_Chinese = "漢字[汉字]";
												String utf8key_Persian = "مهندس";
			String variousKeys[] = {asciiKey, utf8key_Russian, utf8key_Chinese, utf8key_Persian};
			String value = "some data";
			
			for(String key : variousKeys){
				System.out.format("using %s as key for SET ...", key);
				redis.set(key, value);
				System.out.format("...and we get:\n\t  %s => '%s'\n", key, value);
			}

```



**What can 'myValue' be?**

JRedis is to the metal.  So you can pass `byte[]`.  In fact, if you are after high performance, you'll want to avoid passing java.lang.String, unless that is precisely what you want stored.  Redis itself will accept **anything** for the value.  You can pass up to **1MB** of \r\n or zeros, if you feel like it.  Its a `blob`.

So, do you have a Java (`Serializable`) object you want to add as a member of a set?

Here is how:

```
			// lets make a 100 SimpleBean instances and add them to our
			// 'object_set' key (which is a Redis SET)

			int objcnt = 100;
			System.out.format ("Creating and saving %d Java objects to redis ...", objcnt);

			for(int i=1; i<objcnt; i++){
				// instance it
				SimpleBean	obj = new SimpleBean ("bean #" + i);

				// get the next available object id from our Redis counter using INCR command
				int id = redis.incr("SimpleBean::next_id")

				// we can bind it a unique key using map (Redis "String") semantics now
				String key = "objects::SimpleBean::" + id;

				// voila: java object db
				redis.set(key, obj);
				
				// and lets add it to this set too since this is so much fun
				redis.sadd("object_set", obj);
			}

			System.out.format (" and done.\n");

```

**And how do I get my values back to proper types**?

So, to repeat, Redis treats values as blobs, just `byte[]`s, so JRedis api reflects that and returns either `byte[]` or `List<byte[]>` (for set and list ops).

So, to help out there is (as of [r16](https://code.google.com/p/jredis/source/detail?r=16) but this will be improved so remember this bit is in flux), `Encode`.

Here's how we get our objects back using `Encode.decode(byte[] bytes)`, which which have imported using `import static` to make things easier:

```
 			// lets get all those objects in that object set
 			// (Remember: JRedis is NOT maintaining a type system for you, so
  			// if you have other kinds of blobs in that set, the object stream is not going to like it

 			List<SimpleBean>  members = decode (redis.smembers("object_set"));

 			for(SimpleBean obj : members) {
 				System.out.format("a member of 'object-set' => %s\n", obj.toString());
 			}
```

And there you are.

`byte[]`s go in, and `byte[]`s come out.  If you want to convert to `Number` and `String`, check out the methods in `DefaultCodec` class.

And its as simple as that.

/Enjoy!