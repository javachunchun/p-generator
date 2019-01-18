package com.codegen.util;

public class LiveCache<T> {
    // 缓存时间
    private final int cacheMillis;
    // 缓存对象
    private final T element;
    // 缓存对象创建时间
    private final long createTime;
 
    public LiveCache(int cacheMillis, T element) {
        this.cacheMillis = cacheMillis;
        this.element = element;
        this.createTime = System.currentTimeMillis();
    }
 
    // 获取缓存对象
    public T getElement() {
        long currentTime = System.currentTimeMillis();
        if(cacheMillis > 0 && currentTime - createTime > cacheMillis) {
            return null;
        } else {
            return element;
        }
    }
 
    // 获取缓存对象，忽略缓存时间有效性
    public T getElementIfNecessary() {
        return element;
    }
 
    public static void main(String[] args) throws InterruptedException {
        int cacheMilis = 1000 ;
        LiveCache<String> liveCache = new LiveCache<>(cacheMilis, "我是啊啊啊") ;

        Thread.sleep(2000);
        String element = liveCache.getElement();

        String elementIfNecessary = liveCache.getElementIfNecessary();

        System.out.println(element+"=="+elementIfNecessary);

    }

}