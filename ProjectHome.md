![http://jredis.googlecode.com/files/jredis-logo-2.png](http://jredis.googlecode.com/files/jredis-logo-2.png)

# NOTE: JRedis is maintained at [Github](http://github.com/alphazero/jredis) -- current contact info is alphazero@sensesay.net.  Thanks, Google, and goodbye! #

# Project links #
  * [repository](http://github.com/alphazero/jredis)
  * [issue-reporting](http://github.com/alphazero/jredis/issues)
  * [twitter-announces](https://twitter.com/sunof27)

# Project Updates #

Until 2.0.0 final release some API changes may be inevitable.  Classic JRedis API/semantics will not be affected.  API will be augmented.

(If you are using internal components to roll your own clients the changes may have substantial impact.)

You'll need to follow either the master branch on github, or, visit the JRedis google group (or follow me on [twitter](http://twitter.com/sunOf27))

# Current Master #

**2.0.0 Compatible.**

(Full feature set support within the next couple of weeks).
[2.0.0 compatible (github)](http://github.com/alphazero/jredis)

# Current Release #

**1.0 RC2**

Full compliance with [Redis](http://code.google.com/p/redis/) [version 1.2.6](http://redis.googlecode.com/files/redis-1.2.6.tar.gz) and command set (except SYNC/MONITOR).  Synchronous and Asynchronous semantics, and a selection of connector types ranging from single threaded client to multiplexing pipeline.

See release [announce](http://groups.google.com/group/jredis/browse_thread/thread/821e1656d3daea6a) for details.

(Note the 1.0-RC2 is ONLY for Redis 1.2.n servers.  You will need to build from "master" branch source for Redis 1.3.n/2.0 Redis compatible JRedis builds.)

## Release Notes: ##
  * [1.0-RC2](http://github.com/alphazero/jredis/raw/1.2.n/Release/RELEASE-NOTES-1.0-RC2.txt)
  * [Prior update release notes and full RC2 build jars](http://github.com/alphazero/jredis/tree/1.2.n/Release/)

  * [General release Notes](http://github.com/alphazero/jredis/raw/1.2.n/Release/RELEASE-NOTES.txt)

## Release client lib (binary only) ##
http://github.com/downloads/alphazero/jredis/jredis-1.0-rc2.jar

## Release Project Archives ##
  * http://github.com/alphazero/jredis/tarball/JRedis-1.0-RC2 [tarball](tarball.md)
  * http://github.com/alphazero/jredis/zipball/JRedis-1.0-RC2 [zip](zip.md)

# Development #

  * Git master branch (tracks [Redis master branch](http://github.com/antirez/redis)) and ONLY for Redis 1.3.n and higher: http://github.com/alphazero/jredis/tree/master
  * Git 1.2.n branch (Redis 1.2.n compliance): http://github.com/alphazero/jredis/tree/1.2.n
  * **Issue Reporting**: http://github.com/alphazero/jredis/issues

**Note: SVN repo under google code is dated & obsolete**:

# Quick Start #

  * Simple client**: http://code.google.com/p/jredis/wiki/JRedisQuickStart
  * Examples**: http://github.com/alphazero/jredis/tree/1.2.n/examples/src/main/java/org/jredis/examples/


---

[![](http://eclipse-cs.sourceforge.net/images/logo_jprofiler01.gif)](http://www.ej-technologies.com/products/jprofiler/overview.html)

Many thanks to the folks at [ej-technlogies](http://www.ej-technologies.com/) for their support of the JRedis project.    This project uses [JProfiler](http://www.ej-technologies.com/products/jprofiler/overview.html), which provides constructively informative insights and quantitative performance measures of the runtime characteristics of JRedis connectors.  _ausgezeichnet!_


---

![http://jredis.googlecode.com/files/LICENSE.png](http://jredis.googlecode.com/files/LICENSE.png)

(image made with [wordle](http://www.wordle.net))