/**
 * RedisPubSubSpout.java
 */

package storm.jedis;


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
 * Adapted from the https://github.com/sorenmacbeth/storm-redis-pubsub project.
 * 
 * @author Christopher Kilding
 * @date 28/11/2012
 */
public class RedisQueueSpout extends BaseRichSpout {
      
  static final long serialVersionUID = 737015318988609460L;
  
  private SpoutOutputCollector _collector;

  private final String host;
  private final int port;
  private final String pattern;
  
  /** Jedis instance. Transient as you DON'T want to store it. */
  private transient JedisQueue jq;
  
  public RedisQueueSpout(String host, int port, String pattern) {
    this.host = host;
    this.port = port;
    this.pattern = pattern;    
  }
  
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
        if(ret==null) {
            Utils.sleep(5L);
        } else {
            System.out.println(ret);

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

  public void declareOutputFields(OutputFieldsDeclarer declarer) {
    declarer.declare(new Fields("message"));
  }

}