package com.value.buildingblocks.resilience;

import com.netflix.hystrix.HystrixThreadPoolKey;
import com.netflix.hystrix.HystrixThreadPoolProperties;
import com.netflix.hystrix.strategy.HystrixPlugins;
import com.netflix.hystrix.strategy.concurrency.HystrixConcurrencyStrategy;
import com.netflix.hystrix.strategy.concurrency.HystrixRequestVariable;
import com.netflix.hystrix.strategy.concurrency.HystrixRequestVariableLifecycle;
import com.netflix.hystrix.strategy.eventnotifier.HystrixEventNotifier;
import com.netflix.hystrix.strategy.executionhook.HystrixCommandExecutionHook;
import com.netflix.hystrix.strategy.metrics.HystrixMetricsPublisher;
import com.netflix.hystrix.strategy.properties.HystrixPropertiesStrategy;
import com.netflix.hystrix.strategy.properties.HystrixProperty;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CallableWrapperHystrixConcurrencyStrategy extends HystrixConcurrencyStrategy {
  private static final Logger log = LoggerFactory.getLogger(CallableWrapperHystrixConcurrencyStrategy.class);
  
  protected HystrixConcurrencyStrategy existingConcurrencyStrategy;
  
  private List<CallableWrapperSupplier<?>> callableWrapperSuppliers;
  
  public CallableWrapperHystrixConcurrencyStrategy(List<CallableWrapperSupplier<?>> callableWrapperSuppliers) {
    this.callableWrapperSuppliers = callableWrapperSuppliers;
    this.existingConcurrencyStrategy = HystrixPlugins.getInstance().getConcurrencyStrategy();
    HystrixEventNotifier eventNotifier = HystrixPlugins.getInstance().getEventNotifier();
    HystrixMetricsPublisher metricsPublisher = HystrixPlugins.getInstance().getMetricsPublisher();
    HystrixPropertiesStrategy propertiesStrategy = HystrixPlugins.getInstance().getPropertiesStrategy();
    HystrixCommandExecutionHook commandExecutionHook = HystrixPlugins.getInstance().getCommandExecutionHook();
    HystrixPlugins.reset();
    HystrixPlugins.getInstance().registerConcurrencyStrategy(this);
    HystrixPlugins.getInstance().registerEventNotifier(eventNotifier);
    HystrixPlugins.getInstance().registerMetricsPublisher(metricsPublisher);
    HystrixPlugins.getInstance().registerPropertiesStrategy(propertiesStrategy);
    HystrixPlugins.getInstance().registerCommandExecutionHook(commandExecutionHook);
  }
  
  public BlockingQueue<Runnable> getBlockingQueue(int maxQueueSize) {
    return this.existingConcurrencyStrategy.getBlockingQueue(maxQueueSize);
  }
  
  public <T> HystrixRequestVariable<T> getRequestVariable(HystrixRequestVariableLifecycle<T> rv) {
    return this.existingConcurrencyStrategy.getRequestVariable(rv);
  }
  
  public ThreadPoolExecutor getThreadPool(HystrixThreadPoolKey threadPoolKey, HystrixProperty<Integer> corePoolSize, HystrixProperty<Integer> maximumPoolSize, HystrixProperty<Integer> keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue) {
    return this.existingConcurrencyStrategy.getThreadPool(threadPoolKey, corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
  }
  
  public ThreadPoolExecutor getThreadPool(HystrixThreadPoolKey threadPoolKey, HystrixThreadPoolProperties threadPoolProperties) {
    return this.existingConcurrencyStrategy.getThreadPool(threadPoolKey, threadPoolProperties);
  }
  
  public <T> Callable<T> wrapCallable(Callable<T> callable) {
    callable = super.wrapCallable(callable);
    for (CallableWrapperSupplier<?> callableWrapperSupplier : this.callableWrapperSuppliers) {
      log.debug("Using CallableWrapperSupplier {}", callableWrapperSupplier);
      callable = callableWrapperSupplier.wrapCallable(callable);
      log.debug("Callable is now {}", callable);
    } 
    return callable;
  }
}
