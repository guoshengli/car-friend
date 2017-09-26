package com.friend.rest;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisShardInfo;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;

public class RedisClient {
	 private Jedis jedis;//����Ƭ��ͻ�������
	    private JedisPool jedisPool;//����Ƭ���ӳ�
	    private ShardedJedis shardedJedis;//��Ƭ��ͻ�������
	    private ShardedJedisPool shardedJedisPool;//��Ƭ���ӳ�
	    
	    public RedisClient() 
	    { 
	        initialPool(); 
	        initialShardedPool(); 
	        shardedJedis = shardedJedisPool.getResource(); 
	        jedis = jedisPool.getResource(); 
	        
	        
	    } 
	 
	    /**
	     * ��ʼ������Ƭ��
	     */
	    private void initialPool() 
	    { 
	        // �ػ������� 
	        JedisPoolConfig config = new JedisPoolConfig(); 
	       // config.setMaxActive(20); 
	        config.setMaxIdle(5); 
	      //  config.setMaxWait(1000l); 
	        config.setTestOnBorrow(false); 
	        
	        jedisPool = new JedisPool(config,"127.0.0.1",6379);
	    }
	    
	    /** 
	     * ��ʼ����Ƭ�� 
	     */ 
	    private void initialShardedPool() 
	    { 
	        // �ػ������� 
	        JedisPoolConfig config = new JedisPoolConfig(); 
	      //  config.setMaxActive(20); 
	        config.setMaxIdle(5); 
	       // config.setMaxWait(1000l); 
	        config.setTestOnBorrow(false); 
	        // slave���� 
	        List<JedisShardInfo> shards = new ArrayList<JedisShardInfo>(); 
	        shards.add(new JedisShardInfo("127.0.0.1", 6379, "master")); 

	        // ����� 
	        shardedJedisPool = new ShardedJedisPool(config, shards); 
	    } 

	    public void show() {     
	        KeyOperate(); 
	        StringOperate(); 
	        ListOperate(); 
	        SetOperate();
	        SortedSetOperate();
	        HashOperate(); 
	        jedisPool.returnResource(jedis);
	        shardedJedisPool.returnResource(shardedJedis);
	    } 

	      private void KeyOperate() {
	    	  System.out.println("======================key=========================="); 
	          // ������� 
	          System.out.println("��տ����������ݣ�"+jedis.flushDB());
	          // �ж�key����� 
	          System.out.println("�ж�key999���Ƿ���ڣ�"+shardedJedis.exists("key999")); 
	          System.out.println("����key001,value001��ֵ�ԣ�"+shardedJedis.set("key001", "value001")); 
	          System.out.println("�ж�key001�Ƿ���ڣ�"+shardedJedis.exists("key001"));
	          // ���ϵͳ�����е�key
	          System.out.println("����key002,value002��ֵ�ԣ�"+shardedJedis.set("key002", "value002"));
	          System.out.println("ϵͳ�����м����£�");
	          Set<String> keys = jedis.keys("*"); 
	          Iterator<String> it=keys.iterator() ;   
	          while(it.hasNext()){   
	              String key = it.next();   
	              System.out.println(key);   
	          }
	          // ɾ��ĳ��key,��key�����ڣ�����Ը����
	          System.out.println("ϵͳ��ɾ��key002: "+jedis.del("key002"));
	          System.out.println("�ж�key002�Ƿ���ڣ�"+shardedJedis.exists("key002"));
	          // ���� key001�Ĺ���ʱ��
	          System.out.println("���� key001�Ĺ���ʱ��Ϊ5��:"+jedis.expire("key001", 5));
	          try{ 
	              Thread.sleep(2000); 
	          } 
	          catch (InterruptedException e){ 
	          } 
	          // �鿴ĳ��key��ʣ������ʱ��,��λ���롿.����������߲����ڵĶ�����-1
	          System.out.println("�鿴key001��ʣ������ʱ�䣺"+jedis.ttl("key001"));
	          // �Ƴ�ĳ��key������ʱ��
	          System.out.println("�Ƴ�key001������ʱ�䣺"+jedis.persist("key001"));
	          System.out.println("�鿴key001��ʣ������ʱ�䣺"+jedis.ttl("key001"));
	          // �鿴key�������ֵ������
	          System.out.println("�鿴key�������ֵ�����ͣ�"+jedis.type("key001"));
	          /*
	           * һЩ����������1���޸ļ�����jedis.rename("key6", "key0");
	           *             2������ǰdb��key�ƶ���������db���У�jedis.move("foo", 1)
	           */
	          jedis.set("aabb","dsfdsfsdfs");

	      }

	      private void StringOperate() {
	      }

	      private void ListOperate() {
	      }

	      private void SetOperate() {
	      }

	      private void SortedSetOperate() {
	      }
	    
	      private void HashOperate() {
	      }
	      
	      public static void main(String[] args) {
	    	  new RedisClient().KeyOperate(); 

		}

}
