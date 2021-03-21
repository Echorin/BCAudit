package com.oschain.fastchaindb.common.utils;

import org.redisson.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * @ClassName: RedissonUtils
 * @Description: Redisson工具类
 * @author: kevin
 * @date: 2019年3月29日
 * @version V1.0
 */
public class RedissonUtil {

	private static final Logger logger = LoggerFactory.getLogger(RedissonUtil.class);

	private static Long waitTime = 1L;// 等待锁时间范围1秒

	/**
	 * 获取字符串对象
	 * 
	 * @param redissonClient
	 * @param t
	 * @param objectName
	 * @return
	 */
	public static <T> RBucket<T> getRBucket(RedissonClient redissonClient, String objectName) {
		RBucket<T> bucket = redissonClient.getBucket(objectName);
		return bucket;
	}

	/**
	 * 获取Map对象
	 * 
	 * @param redissonClient
	 * @param objectName
	 * @return
	 */
	public static <K, V> RMap<K, V> getRMap(RedissonClient redissonClient, String objectName) {
		RMap<K, V> map = redissonClient.getMap(objectName);
		return map;
	}

	/**
	 * 获取有序集合
	 * 
	 * @param redissonClient
	 * @param objectName
	 * @return
	 */
	public static <V> RSortedSet<V> getRSortedSet(RedissonClient redissonClient, String objectName) {
		RSortedSet<V> sortedSet = redissonClient.getSortedSet(objectName);
		return sortedSet;
	}

	/**
	 * 获取集合
	 * 
	 * @param redissonClient
	 * @param objectName
	 * @return
	 */
	public static <V> RSet<V> getRSet(RedissonClient redissonClient, String objectName) {
		RSet<V> rSet = redissonClient.getSet(objectName);
		return rSet;
	}

	/**
	 * 获取列表
	 * 
	 * @param redissonClient
	 * @param objectName
	 * @return
	 */
	public static <V> RList<V> getRList(RedissonClient redissonClient, String objectName) {
		RList<V> rList = redissonClient.getList(objectName);
		return rList;
	}

	/**
	 * 获取队列
	 * 
	 * @param redissonClient
	 * @param objectName
	 * @return
	 */
	public static <V> RQueue<V> getRQueue(RedissonClient redissonClient, String objectName) {
		RQueue<V> rQueue = redissonClient.getQueue(objectName);
		return rQueue;
	}

	/**
	 * 获取双端队列
	 * 
	 * @param redissonClient
	 * @param objectName
	 * @return
	 */
	public static <V> RDeque<V> getRDeque(RedissonClient redissonClient, String objectName) {
		RDeque<V> rDeque = redissonClient.getDeque(objectName);
		return rDeque;
	}

	/**
	 * 获取锁对象
	 * 
	 * @param redissonClient
	 * @param objectName
	 * @return
	 */
	public static RLock getRLock(RedissonClient redissonClient, String objectName) {
		RLock rLock = redissonClient.getLock(objectName);
		return rLock;
	}

	/**
	 * 获取锁并设置过期时间
	 * 
	 * @param rLock
	 * @param expireTime（单位秒）
	 * @return
	 */
	public static boolean getRLockExpireTime(RLock rLock, Long expireTime) {
		boolean isLock = false;
		if (rLock != null) {
			try {
				isLock = rLock.tryLock(waitTime, expireTime, TimeUnit.SECONDS);
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		}
		return isLock;
	}

	/**
	 * 释放锁
	 * 
	 * @param redissonClient
	 * @param objectName
	 * @return
	 */
	public static void unRLock(RedissonClient redissonClient, String objectName) {
		RLock rLock = redissonClient.getLock(objectName);
		if (rLock != null) {
			rLock.unlock();
		}
	}

	/**
	 * 释放锁
	 * 
	 * @param rLock
	 * @return
	 */
	public static void unRLock(RLock rLock) {
		if (rLock != null) {
			rLock.unlock();
		}
	}

	/**
	 * 获取原子数
	 * 
	 * @param redissonClient
	 * @param objectName
	 * @return
	 */
	public static RAtomicLong getRAtomicLong(RedissonClient redissonClient, String objectName) {
		RAtomicLong rAtomicLong = redissonClient.getAtomicLong(objectName);
		return rAtomicLong;
	}

	/**
	 * 获取记数锁
	 * 
	 * @param redissonClient
	 * @param objectName
	 * @return
	 */
	public static RCountDownLatch getRCountDownLatch(RedissonClient redissonClient, String objectName) {
		RCountDownLatch rCountDownLatch = redissonClient.getCountDownLatch(objectName);
		return rCountDownLatch;
	}

	/**
	 * 获取消息的Topic
	 * 
	 * @param redissonClient
	 * @param objectName
	 * @return
	 */
	public static <M> RTopic<M> getRTopic(RedissonClient redissonClient, String objectName) {
		RTopic<M> rTopic = redissonClient.getTopic(objectName);
		return rTopic;
	}

//	加锁事例
//@Autowired
//private RedissonClient redissonClient;
//	String redisKey = "auditTransfer"+transferVO.getTransferId();
//	RLock lock = RedissonUtil.getRLock(redissonClient, redisKey);
//	// 获取锁并设置过期时间（单位是秒）
//	boolean isLock = RedissonUtil.getRLockExpireTime(lock, 10L);
//		if (isLock) {
//		// 1)调拨单审核
//		auditCount = iStkTransferServiceFacade.auditStkTransfer(stkTransferDto);
//		RedissonUtil.unRLock(lock);// 最后释放锁（必写，勿遗漏）
//	}else{
//		throw new BaseASException(ApiConstants.RESULT_FAIL, "单据正在处理中，请稍后！");
//	}

}
