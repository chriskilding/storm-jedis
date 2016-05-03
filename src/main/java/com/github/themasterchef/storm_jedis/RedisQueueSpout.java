/**
 * RedisPubSubSpout.java
 */

package com.github.themasterchef.storm_jedis;

import java.util.List;
import java.util.Map;
import redis.clients.jedis.Jedis;
import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.base.BaseRichSpout;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;
import backtype.storm.utils.Utils;

/**
 * A spout to interface with a Redis server.
 * 
 * Adapted from the https://github.com/sorenmacbeth/storm-redis-pubsub project.
 * 
 * Due to the way Storm polls spouts at regular intervals for data, instead of getting data off them via events, it is more
 * efficient to use Redis as a FIFO queue; the source will push data onto the queue, and each time nextTuple() is called, this class
 * will pop the next item to be processed off the queue.
 * 
 * @author Christopher Kilding
 * @date 28/11/2012
 */
public class RedisQueueSpout extends BaseRichSpout {

  static final long            serialVersionUID = 737015318988609460L;

  private SpoutOutputCollector _collector;

  /** The host on which Redis is located. */
  private final String         host;

  /** The port at which Redis may be found. */
  private final int            port;

  /** The "pattern" i.e. root key below which all messages are stored. */
  private final String         pattern;

  /** JedisQueue instance. Transient as its inner Jedis member cannot be serialized. */
  private transient JedisQueue jq;

  public RedisQueueSpout(String host, int port, String pattern) {
    this.host = host;
    this.port = port;
    this.pattern = pattern;
  }

  @SuppressWarnings("rawtypes")
  public void open(Map conf, TopologyContext context, SpoutOutputCollector collector) {
    _collector = collector;
    Jedis newJedis = new Jedis(host, port);
    newJedis.connect();
    this.jq = new JedisQueue(newJedis, pattern);
  }

  public void close() {
  }

  public void nextTuple() {
    List<String> ret = this.jq.dequeue();
    if (ret == null) {
      Utils.sleep(5L);
    }
    else {
      _collector.emit(new Values(ret));
    }
  }

  @Override
  public void ack(Object msgId) {
    // TODO Auto-generated method stub

  }

  @Override
  public void fail(Object msgId) {
    // TODO Auto-generated method stub

  }

  /**
   * This spout simply returns one thing: a message. Due to the nature of the key-value store, this will be a List<String> of
   * the latest thing that was stored underneath this.pattern.
   * 
   * @param declarer
   * 
   * @see backtype.storm.topology.IComponent#declareOutputFields(backtype.storm.topology.OutputFieldsDeclarer)
   */
  public void declareOutputFields(OutputFieldsDeclarer declarer) {
    declarer.declare(new Fields("message"));
  }

}
