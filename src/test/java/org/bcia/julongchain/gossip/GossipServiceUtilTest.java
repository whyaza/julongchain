package org.bcia.julongchain.gossip;

import org.apache.gossip.GossipService;
import org.bcia.julongchain.common.exception.GossipException;
import org.junit.Assert;
import org.junit.Test;

import java.util.UUID;

/**
 * 类描述
 *
 * @author wanliangbing
 * @date 2018/06/08
 * @company Dingxuan
 */
public class GossipServiceUtilTest {

  @Test
  /** 测试启动gossip服务 */
  public void newGossipService() throws GossipException {
    String address = "localhost:7052";
    GossipServiceUtil.newGossipService(address).start();
  }

  @Test
  /** 测试上传数据 */
  public void addData() throws GossipException {
    String address = "localhost:7052";
    String group = "group";
    GossipService gossipService = GossipServiceUtil.newGossipService(address);
    gossipService.start();
    String data = "hello";
    GossipServiceUtil.addData(gossipService, group, 1l, data);
  }

  @Test
  /** 测试上传数据，另一个节点读取数据 */
  public void getData() throws GossipException {

    String address_addData = "localhost:7052";
    String address_readData = "localhost:7053";
    String address_readData2 = "localhost:7054";

    String group = "group111";

    GossipService gossipService_add = GossipServiceUtil.newGossipService(address_addData);

    GossipService gossipService_read =
        GossipServiceUtil.newGossipService(address_readData, address_addData);

    GossipService gossipService_read2 =
        GossipServiceUtil.newGossipService(address_readData2, address_addData);

    gossipService_add.start();
    gossipService_read.start();
    gossipService_read2.start();

    String data = "hello gossip";

    GossipServiceUtil.addData(gossipService_add, group, 1l, data);

    try {
      Thread.sleep(1000);
    } catch (Exception e) {
      e.printStackTrace();
    }

    String string = (String) GossipServiceUtil.getData(gossipService_read, group, 1l);
    System.out.println(string);

    String string2 = (String) GossipServiceUtil.getData(gossipService_read2, group, 1l);
    System.out.println(string2);

    Assert.assertEquals(string, data);
  }

  @Test
  /** 测试上传数据，另一个节点读取数据 */
  public void getDataFromRemote() throws GossipException {

    String id2 = UUID.randomUUID().toString();

    String address_readData = "192.168.1.50:7053";
    String group = "group";

    GossipService gossipService =
        GossipServiceUtil.newGossipService(address_readData, "192.168.1.110:7052");
    gossipService.start();

    try {
      Thread.sleep(2000);
    } catch (Exception e) {
      e.printStackTrace();
    }

    String string = (String) GossipServiceUtil.getData(gossipService, group, 1l);
    System.out.println(string);

    Assert.assertEquals(string, "hello");
  }

  public static void main(String[] args) {
    System.out.println("hello world");
  }
}
