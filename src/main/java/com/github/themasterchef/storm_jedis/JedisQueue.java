/**
 * JedisQueue.java
 */

package com.github.themasterchef.storm_jedis;

import java.util.List;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisDataException;


/**
 * A very simplified FIFO queue structure, implemented with Jedis.
 * 
 * @author Christopher Kilding
 * @date 29/11/2012
 */
public class JedisQueue {

  private transient Jedis jedis;
  
  private final String pattern;
  
  /**
   * @param jedis
   * @param pattern
   */
  public JedisQueue(Jedis jedis, String pattern) {
    this.jedis = jedis;
    this.pattern = pattern;
  }

  /**
   * Clear out the values for this.pattern
   */
  public void clear() {
    this.jedis.del(this.pattern);
    
  }

  /**
   * @return whether this.pattern maps to no values
   *
   */
  public boolean isEmpty() { 
    return (this.size() == 0);
  }


  /**
   * @return the number of values for this.pattern
   */
  public int size() {
    return new Integer(this.jedis.llen(this.pattern).toString());
  }


  /**
   * @return
   *  serialized form of the Jedis queue
   */
  public List<String> toArray() {
    return this.jedis.lrange(this.pattern, 0, -1);
  }



  /**
   * @param elems the element(s) to add
   */
  public void enqueue(String... elems) {
    this.jedis.rpush(this.pattern, elems);
  }

  /**
   * @return the next item in the queue, popped
   *
   */
  public List<String> dequeue() {
    List<String> out = null;
    try {
      out = this.jedis.blpop(0, this.pattern);
    }
    catch (JedisDataException e) {
      // It wasn't a list of strings
    }
   
    return out;
  }

}
