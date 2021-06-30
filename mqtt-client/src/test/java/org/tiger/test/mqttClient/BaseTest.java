/**
 *
 * Project Name:	mqtt-client
 * File Name:	BaseTest.java
 *
 * Author:      Wang Huiyuan
 * Create Date: 2021年6月14日
 * Version:		1.0
 * Remark：
 */
package org.tiger.test.mqttClient;

import java.util.Date;

import org.eclipse.paho.mqttv5.client.IMqttToken;
import org.eclipse.paho.mqttv5.client.MqttAsyncClient;
import org.eclipse.paho.mqttv5.common.MqttException;
import org.eclipse.paho.mqttv5.common.MqttMessage;
import org.eclipse.paho.mqttv5.common.MqttSubscription;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author WangHuiyuan
 *
 */
public class BaseTest {
	
	private static Logger logger = LoggerFactory.getLogger(BaseTest.class);

	MqttAsyncClient client;

	@Before
	public void setUp() throws Exception {
		client = MqttClient.connectDefaultAndGetClient();
	}

	@Test
	public void tesConnect() throws InterruptedException {
		Thread.sleep(3600 * 1000);
	}
	
	@Test
	public void testPublishAndReceive() throws MqttException, InterruptedException {

		String allClientTopic = "platform/mqtt/test/*";
		String myTopic = "platform/mqtt/test/whytest";

		int timeout = 5000;

		MqttV5Receiver mqttV5Receiver = new MqttV5Receiver(MqttClient.clientId, System.out);
		MqttAsyncClient asyncClient = MqttClient.connectAndGetClient(MqttClient.broker, MqttClient.clientId,
				mqttV5Receiver, null, timeout);

		for (int qos = 0; qos <= 2; qos++) {
			logger.info("Testing Publish and Receive at QoS: " + qos);
			// Subscribe to a topic
			logger.info(String.format("Subscribing to: %s at QoS %d", allClientTopic, qos));
			MqttSubscription subscription = new MqttSubscription(allClientTopic, qos);
			IMqttToken subscribeToken = asyncClient.subscribe(subscription);
			subscribeToken.waitForCompletion(timeout);

			// Publish a message to the topic
			String messagePayload = "Test Payload at: " + new Date().toString();
			MqttMessage testMessage = new MqttMessage(messagePayload.getBytes(), qos, false, null);
			logger.info(String.format("Publishing Message %s to: %s at QoS: %d", testMessage.toDebugString(), myTopic, qos));
			IMqttToken deliveryToken = asyncClient.publish(myTopic, testMessage);
			deliveryToken.waitForCompletion(timeout);

			logger.info("Waiting [{}] milliseconds for delivery and validating message.", MqttV5Receiver.waitMilliseconds);
			wait(MqttV5Receiver.waitMilliseconds);
//			boolean received = mqttV5Receiver.validateReceipt(allClientTopic, qos, testMessage);
//			Assert.assertTrue(received);

			// Unsubscribe from the topic
			logger.info("Unsubscribing from : " + allClientTopic);
			IMqttToken unsubscribeToken = asyncClient.unsubscribe(allClientTopic);
			unsubscribeToken.waitForCompletion(timeout);
		}
		MqttClient.disconnectAndCloseClient(asyncClient, timeout);
	}

}
