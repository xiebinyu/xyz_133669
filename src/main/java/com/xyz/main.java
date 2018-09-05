package com.xyz;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xyz.utils.HttpRequest;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class main {

  static TreeMap<Integer, TreeMap<Integer, Object>> list = new TreeMap<>();

  public static void main(String[] args) throws Exception {
    // 拉取数据
    String result = HttpRequest.post("http://www.133669.com/lottery/trendChart/lotteryOpenNum.do?lotCode=FFC&recentDay=1&rows=200&timestamp=1536113699172", "");
    JSONArray array = JSONArray.parseArray(result);
    // 连续次数
    int[] lx = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    int length = array.size();
    String lastQiHao = null;
    String latestQiHao = null;
    List<String> msgs = new ArrayList<>();
    for (int i = length - 1; i >= 0; i--) {
      JSONObject json = (JSONObject) array.get(i);
      // 期号
      String qiHao = json.getString("qiHao");
      // 号码
      String haoMa = json.getString("haoMa");
//      String haoMaOK = haoMa.substring(0, 14);
      String haoMaOK = haoMa;
      for (int num = 0; num < 10; num++) {
        int lxTimes = lx[num];
        boolean flag = false;
        // 前五位包含该数字
        if (haoMaOK.contains(String.valueOf(num))) {
          if (lxTimes >= 0) {
            lx[num] = lxTimes + 1;
          } else {
            // 逆转，清零
            flag = true;
            lx[num] = 1;
          }
        } else {
          // 后五位包含该数字
          if (lxTimes > 0) {
            // 逆转，清零
            flag = true;
            lx[num] = -1;
          } else {
            lx[num] = lxTimes - 1;
          }
        }
        // 连续三次及以上进行记录
        if (flag && (lxTimes >= 3 || lxTimes <= -3)) {
          Map map = getMap(num);
          Integer times = (Integer) map.get(lxTimes);
          if (times == null) times = 0;
          // 连续次数+1
          map.put(lxTimes, times + 1);
          if (lxTimes > 5 || lxTimes < -5) msgs.add("期号：" + qiHao + "，号码：" + num + "，次数：" + lxTimes);
        }
      }
      // 第一期期号
      if (lastQiHao == null) {
        lastQiHao = json.getString("qiHao");
      }
      // 最新期号
      if (i == 0) {
        latestQiHao = json.getString("qiHao");
      }
    }
    String qihao = lastQiHao + "-" + latestQiHao;
    File csvFile = new File("D:/133669/" + qihao + ".csv");
    BufferedWriter csvWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(csvFile), "GB2312"), 1024);
    for (Map.Entry<Integer, TreeMap<Integer, Object>> map : list.entrySet()) {
      writeRow(map.getKey(), map.getValue(), csvWriter);
    }
    csvWriter.newLine();
    csvWriter.write("详情：");
    for (String msg : msgs) {
      csvWriter.newLine();
      csvWriter.write(msg);
    }
    csvWriter.flush();
  }

  public static TreeMap<Integer, Object> getMap(Integer index) {
    TreeMap<Integer, Object> map = list.get(index);
    if (map == null) {
      map = new TreeMap<>();
      list.put(index, map);
    }
    return map;
  }

  public static String getNo() {
    try {
      BufferedReader reader = new BufferedReader(new FileReader("D:/133669.csv"));//换成你的文件名
      String line = reader.readLine();
      String[] datas = line.split(",");
      return datas[0];
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

  /**
   * 写一行数据
   * @param map 数据列表
   * @param csvWriter
   * @throws IOException
   */
  private static void writeRow(int index, TreeMap<Integer, Object> map, BufferedWriter csvWriter) throws IOException {
    String rowStr = "\"" + index + "\",";
    for (Map.Entry<Integer, Object> tree : map.entrySet()) {
      StringBuffer sb = new StringBuffer();
      Integer key = tree.getKey();
      String keyStr = " " + String.valueOf(key);
      rowStr += sb.append("\"").append(keyStr + "=" + tree.getValue()).append("\",").toString();
    }
    csvWriter.write(rowStr);
    csvWriter.newLine();
  }
}
