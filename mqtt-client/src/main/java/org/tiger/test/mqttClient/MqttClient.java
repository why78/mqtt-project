/**
 *
 * Project Name:	mqtt-client
 * File Name:	MqttClient.java
 *
 * Author:      Wang Huiyuan
 * Create Date: 2021年6月12日
 * Version:		1.0
 * Remark：
 */
package org.tiger.test.mqttClient;


import org.eclipse.paho.mqttv5.client.IMqttToken;
import org.eclipse.paho.mqttv5.client.MqttAsyncClient;
import org.eclipse.paho.mqttv5.client.MqttCallback;
import org.eclipse.paho.mqttv5.client.MqttConnectionOptions;
import org.eclipse.paho.mqttv5.client.persist.MemoryPersistence;
import org.eclipse.paho.mqttv5.common.MqttException;
import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author WangHuiyuan
 *
 */
public class MqttClient {

	private static int qos = 2; //只有一次
	public static String broker = "tcp://10.0.30.212:1883";
	private static String userName = "admin";
	private static String password = "123456";
	public static String clientId = "whytest";
	public static int timeout = 5000;
 
	
	private static final Logger log = LoggerFactory.getLogger(MqttClient.class);
	
	public static MqttAsyncClient connectDefaultAndGetClient() throws MqttException {
		MqttConnectionOptions connectionOptions = new MqttConnectionOptions();
		connectionOptions.setUserName(userName);
		connectionOptions.setPassword(password.getBytes());
		
		return connectAndGetClient(broker, clientId, null, connectionOptions, timeout);
	}
	
	public static MqttAsyncClient connectAndGetClient(String serverURI, String clientId, MqttCallback callback,
			MqttConnectionOptions connectionOptions, int timeout) throws MqttException {
		MqttAsyncClient client = new MqttAsyncClient(serverURI, clientId);
		
		if(connectionOptions == null) {
			connectionOptions = new MqttConnectionOptions();
			connectionOptions.setUserName(userName);
			connectionOptions.setPassword(password.getBytes());
		}
		
		if (callback != null) {
			client.setCallback(callback);
		}
		log.info("Connecting: [serverURI: " + serverURI + ", ClientId: " + clientId + "]");
		
		
		IMqttToken connectToken;
		if (connectionOptions != null) {
			connectToken = client.connect(connectionOptions);
		} else {
			connectToken = client.connect();
		}

		connectToken.waitForCompletion(timeout);
		Assert.assertTrue(client.isConnected());
		log.info("Client: [" + clientId + "] is connected.");
		return client;
	}
	
	public static void disconnectAndCloseClient(MqttAsyncClient client, int timeout) throws MqttException {
		log.info("Disconnecting client: [" + client.getClientId() + "]");
		IMqttToken disconnectToken = client.disconnect();
		disconnectToken.waitForCompletion(timeout);
		Assert.assertFalse(client.isConnected());
		client.close();
		log.info("Client [" + client.getClientId() + "] disconnected and closed.");
	}

}
