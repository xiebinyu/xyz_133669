package com.xyz.timer;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

@Component
public class Timer {

  @Scheduled(cron="0/10 * * * * ?") //每分钟执行一次,这是cron表达式
  public void statusCheck() {
    Date date = new Date();
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    String dateTime = sdf.format(date);
    System.out.println(dateTime);
  }
}
