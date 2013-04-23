# storm-jedis
A set of Storm helpers to plug your application into the Redis in-memory data store, built on the Jedis Java Redis client.

## Classes
- `JedisQueue`, an implementation of a standard queue data structure backed by Redis. Note that the `dequeue` method somewhat unconventionally returns a `List<String>` as this is what the underlying Jedis implementation returns: this is due to the ability of Redis to store many values for a single key.
- `RedisQueueSpout`, a Storm spout which will plug your topology into Redis using a specified `pattern` (aka key), at which it will look for input data every time Storm polls it. The spout emits a single field with the ID `message` to whichever bolt follows it.

## Use the source
The storm-jedis dependencies are handled by Maven, so if you wish to work with the source you will need to have Maven download these first.