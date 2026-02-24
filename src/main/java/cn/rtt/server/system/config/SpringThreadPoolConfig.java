package cn.rtt.server.system.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

@Configuration
@EnableAsync
public class SpringThreadPoolConfig implements AsyncConfigurer {

  /**
   * 核心线程数
   */
  private static final int CORE_POOL_SIZE = Runtime.getRuntime().availableProcessors() * 2;

  /**
   * 队列长度
   */
  private static final int QUEUE_CAPACITY = 1024;

  /**
   * 等待时间
   */
  private static final int AWAIT_TERMINATION = 60;

  /**
   * 空闲线程存活时间
   */
  private static final int KEEP_ALIVE_TIME = 60;

  /**
   * 这种形式的线程池配置是需要在使用的方法上面 @Async
   */
  @Bean
  @Override
  public Executor getAsyncExecutor() {
    ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskCustomExecutor();
    // 配置核心线程池数量
    taskExecutor.setCorePoolSize(CORE_POOL_SIZE);
    // 配置最大线程池数量
    taskExecutor.setMaxPoolSize(CORE_POOL_SIZE * 2);
    /// 线程池所使用的缓冲队列
    taskExecutor.setQueueCapacity(QUEUE_CAPACITY);
    // 等待时间 （默认为0，此时立即停止），并没等待xx秒后强制停止
    taskExecutor.setAwaitTerminationSeconds(AWAIT_TERMINATION);
    // 空闲线程存活时间
    taskExecutor.setKeepAliveSeconds(KEEP_ALIVE_TIME);
    // 等待任务在关机时完成--表明等待所有线程执行完
    taskExecutor.setWaitForTasksToCompleteOnShutdown(true);
    // 线程池名称前缀
    taskExecutor.setThreadNamePrefix("system-thread-pool-");
    // 线程池拒绝策略
    taskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
    // 线程池初始化
    taskExecutor.initialize();
    return taskExecutor;
  }
}
